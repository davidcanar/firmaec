package ec.gob.firmadigital.libreria.sign.pdf;

import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.TSAClientBouncyCastle;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import ec.gob.firmadigital.libreria.sign.RubricaSigner;
import ec.gob.firmadigital.libreria.sign.pdf.itext.SignerAdapter;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.PropertiesUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PadesEnhancedSigner extends BasePdfSigner {
   private final IExternalSignature externalSignature;
   private static final Logger LOGGER = Logger.getLogger(PadesEnhancedSigner.class.getName());

   public PadesEnhancedSigner(RubricaSigner signer) {
      this.externalSignature = new SignerAdapter(signer);
   }

   public byte[] sign(InputStream inputStream, PrivateKey privateKey, Certificate[] certificates, Properties properties) throws IOException {
      BouncyCastleUtils.initializeBouncyCastle();

      try {
         byte[] hash = this.emptySignature(inputStream, certificates, properties, this.externalSignature.getDigestAlgorithmName());
         String fieldName = this.getFieldName();
         ByteArrayOutputStream documentoPorFirmar = this.getDocumentoPorFirmar();
         byte[] hashSigned = this.signed_hash(hash, privateKey, certificates, properties.getProperty("identificacion"));
         this.createSignature(hashSigned, documentoPorFirmar, fieldName, privateKey, certificates);
         return this.getDocumentoFirmado().toByteArray();
      } catch (GeneralSecurityException e) {
         LOGGER.log(Level.SEVERE, "Error al firmar: {0}", e.getMessage());
         throw new RuntimeException(e);
      }
   }

   private byte[] signed_hash(byte[] hash, PrivateKey pk, Certificate[] chain, String identificacion) throws GeneralSecurityException {
      PrivateKeySignature signature = new PrivateKeySignature(pk, this.externalSignature.getDigestAlgorithmName(), null);
      BouncyCastleDigest digest = new BouncyCastleDigest();
      PdfPKCS7 sgn = new PdfPKCS7(null, chain, this.externalSignature.getDigestAlgorithmName(), null, digest, false);
      byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, CryptoStandard.CMS, null, null);
      byte[] extSignature = signature.sign(sh);
      sgn.setExternalSignatureValue(extSignature, null, signature.getSignatureAlgorithmName());
      ITSAClient tsaClient = null;
      if (identificacion != null && !identificacion.isEmpty()) {
         tsaClient = new TSAClientBouncyCastle(PropertiesUtils.getConfig().getProperty("tsa_url"), identificacion, identificacion);
      }

      try {
         return sgn.getEncodedPKCS7(hash, CryptoStandard.CMS, tsaClient, null, null);
      } catch (Exception e) {
         String errorMessage = e.getMessage();
         String causeMessage = e.getCause() != null ? e.getCause().getMessage() : "";
         String fullMessage = errorMessage + " " + causeMessage;
         if (fullMessage.contains("HTTP response code: 401")) {
            LOGGER.log(
               Level.SEVERE,
               "Error de autenticación TSA (401): Credenciales incorrectas o usuario no autorizado. URL: {0}",
               PropertiesUtils.getConfig().getProperty("tsa_url")
            );
            throw new GeneralSecurityException(
               "Error de autenticación TSA (sello de tiempo): Las credenciales proporcionadas son incorrectas. Verifique usuario y contraseña.", e
            );
         } else if (fullMessage.contains("HTTP response code: 409")) {
            LOGGER.log(
               Level.SEVERE,
               "Error de conflicto TSA (409): Posiblemente el hash ya fue sellado anteriormente o hay duplicación. URL: {0}",
               PropertiesUtils.getConfig().getProperty("tsa_url")
            );
            throw new GeneralSecurityException(
               "Error de conflicto TSA (sello de tiempo): No se puede obtener el sello de tiempo debido a un conflicto. El hash podría estar duplicado.", e
            );
         } else if (fullMessage.contains("HTTP response code:")) {
            String httpCode = this.extractHttpCode(fullMessage);
            LOGGER.log(Level.SEVERE, "Error HTTP {0} del servidor TSA: {1}", new Object[]{httpCode, fullMessage});
            throw new GeneralSecurityException(
               String.format(
                  "Error del servidor TSA (sello de tiempo) (HTTP %s): No se pudo obtener el sello de tiempo. URL: %s",
                  httpCode,
                  PropertiesUtils.getConfig().getProperty("tsa_url")
               ),
               e
            );
         } else {
            LOGGER.log(Level.SEVERE, "Error al obtener sello de tiempo TSA: {0}", e.getMessage());
            throw new GeneralSecurityException("Error al obtener TSA (sello de tiempo): " + e.getMessage(), e);
         }
      }
   }

   private String extractHttpCode(String message) {
      Pattern pattern = Pattern.compile("HTTP response code: (\\d+)");
      Matcher matcher = pattern.matcher(message);
      return matcher.find() ? matcher.group(1) : "desconocido";
   }
}
