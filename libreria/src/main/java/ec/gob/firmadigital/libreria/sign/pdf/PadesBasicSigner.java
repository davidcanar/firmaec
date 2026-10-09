package ec.gob.firmadigital.libreria.sign.pdf;

import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import ec.gob.firmadigital.libreria.sign.RubricaSigner;
import ec.gob.firmadigital.libreria.sign.pdf.itext.SignerAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PadesBasicSigner extends BasePdfSigner {
   private final IExternalSignature externalSignature;
   private static final Logger LOGGER = Logger.getLogger(PadesBasicSigner.class.getName());

   public PadesBasicSigner(RubricaSigner signer) {
      this.externalSignature = new SignerAdapter(signer);
   }

   public byte[] sign(InputStream inputStream, PrivateKey privateKey, Certificate[] certificates, Properties properties) throws IOException {
      try {
         byte[] hash = this.emptySignature(inputStream, certificates, properties, this.externalSignature.getDigestAlgorithmName());
         String fieldName = this.getFieldName();
         ByteArrayOutputStream documentoPorFirmar = this.getDocumentoPorFirmar();
         byte[] hashSigned = this.signed_hash(hash, privateKey, certificates);
         this.createSignature(hashSigned, documentoPorFirmar, fieldName, privateKey, certificates);
         return this.getDocumentoFirmado().toByteArray();
      } catch (GeneralSecurityException e) {
         LOGGER.log(Level.SEVERE, "Error al firmar: {0}", e.getMessage());
         throw new RuntimeException(e);
      }
   }

   private byte[] signed_hash(byte[] hash, PrivateKey privateKey, Certificate[] chain) throws GeneralSecurityException {
      PrivateKeySignature signature = new PrivateKeySignature(privateKey, this.externalSignature.getDigestAlgorithmName(), null);
      BouncyCastleDigest digest = new BouncyCastleDigest();
      PdfPKCS7 sgn = new PdfPKCS7(null, chain, this.externalSignature.getDigestAlgorithmName(), null, digest, false);
      byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, CryptoStandard.CMS, null, null);
      byte[] extSignature = signature.sign(sh);
      sgn.setExternalSignatureValue(extSignature, null, signature.getSignatureAlgorithmName());
      return sgn.getEncodedPKCS7(hash, CryptoStandard.CMS, null, null, null);
   }
}
