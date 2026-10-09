package ec.gob.firmadigital.libreria.sign.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.TSAClientBouncyCastle;
import com.itextpdf.signatures.LtvVerification.CertificateInclusion;
import com.itextpdf.signatures.LtvVerification.CertificateOption;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import ec.gob.firmadigital.libreria.sign.RubricaSigner;
import ec.gob.firmadigital.libreria.sign.pdf.itext.SignerAdapter;
import ec.gob.firmadigital.libreria.utils.PropertiesTsa;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PadesLtvSigner extends BasePdfSigner {
   private final IExternalSignature externalSignature;
   private static final Logger LOGGER = Logger.getLogger(PadesLtvSigner.class.getName());

   public PadesLtvSigner(RubricaSigner signer) {
      this.externalSignature = new SignerAdapter(signer);
   }

   public byte[] sign(InputStream inputStream, PrivateKey privateKey, Certificate[] certificates, Properties properties) throws IOException {
      try {
         byte[] hash = this.emptySignature(inputStream, certificates, properties, this.externalSignature.getDigestAlgorithmName());
         ByteArrayOutputStream documentoPorFirmar = this.getDocumentoPorFirmar();
         byte[] hashSigned = this.signed_hash(hash, privateKey, certificates);
         this.createSignature(hashSigned, documentoPorFirmar, this.getFieldName(), privateKey, certificates);
         return this.signLtv(this.getDocumentoFirmado().toByteArray());
      } catch (GeneralSecurityException e) {
         LOGGER.log(Level.SEVERE, "Error al firmar: {0}", e.getMessage());
         throw new RuntimeException(e);
      }
   }

   private byte[] signed_hash(byte[] hash, PrivateKey pk, Certificate[] chain) throws GeneralSecurityException {
      PrivateKeySignature signature = new PrivateKeySignature(pk, this.externalSignature.getDigestAlgorithmName(), null);
      BouncyCastleDigest digest = new BouncyCastleDigest();
      PdfPKCS7 sgn = new PdfPKCS7(null, chain, this.externalSignature.getDigestAlgorithmName(), null, digest, false);
      byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, CryptoStandard.CMS, null, null);
      byte[] extSignature = signature.sign(sh);
      sgn.setExternalSignatureValue(extSignature, null, signature.getSignatureAlgorithmName());
      PropertiesTsa propertiesTsa = new PropertiesTsa();
      ITSAClient tsaClient = new TSAClientBouncyCastle(propertiesTsa.getTsaUrl(), propertiesTsa.getTsaUsername(), propertiesTsa.getTsaPassword());
      return sgn.getEncodedPKCS7(hash, CryptoStandard.CMS, tsaClient, null, null);
   }

   private byte[] signLtv(byte[] signed) {
      ByteArrayInputStream bis = new ByteArrayInputStream(signed);
      ICrlClient crlClient = new CrlClientOnline();
      OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
      IOcspClient ocspClient = new OcspClientBouncyCastle(ocspVerifier);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PropertiesTsa propertiesTsa = new PropertiesTsa();
      ITSAClient tsaClient = new TSAClientBouncyCastle(propertiesTsa.getTsaUrl(), propertiesTsa.getTsaUsername(), propertiesTsa.getTsaPassword());
      this.ltvEnable(bis, baos, this.getFieldName(), ocspClient, crlClient, tsaClient);
      return baos.toByteArray();
   }

   private void ltvEnable(
      ByteArrayInputStream signedPdfInput, ByteArrayOutputStream baos, String name, IOcspClient ocspClient, ICrlClient crlClient, ITSAClient tsc
   ) {
      try {
         PdfReader pdfReader = new PdfReader(signedPdfInput);

         try {
            PdfDocument document = new PdfDocument(pdfReader, new PdfWriter(baos), new StampingProperties().useAppendMode());
            LtvVerification ltvVerification = new LtvVerification(document, "BC");
            crlClient = null;
            ltvVerification.addVerification(
               name, ocspClient, crlClient, CertificateOption.WHOLE_CHAIN, com.itextpdf.signatures.LtvVerification.Level.OCSP, CertificateInclusion.YES
            );
            ltvVerification.merge();
            document.close();
         } catch (Throwable var11) {
            try {
               pdfReader.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }

            throw var11;
         }

         pdfReader.close();
      } catch (IOException | GeneralSecurityException e) {
         LOGGER.severe("Error while making signature ltv enabled");
         throw new RuntimeException(e);
      }
   }
}
