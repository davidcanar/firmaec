package ec.gob.firmadigital.cliente;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import ec.gob.firmadigital.libreria.exceptions.XploitException;
import ec.gob.firmadigital.libreria.sign.DigestAlgorithm;
import ec.gob.firmadigital.libreria.sign.PrivateKeySigner;
import ec.gob.firmadigital.libreria.sign.pdf.PadesEnhancedSigner;
import ec.gob.firmadigital.libreria.sign.xades.XAdESSigner;
import ec.gob.firmadigital.libreria.utils.CheckPDF;
import ec.gob.firmadigital.libreria.utils.FileUtils;
import ec.gob.firmadigital.libreria.utils.TiempoUtils;
import ec.gob.firmadigital.libreria.utils.X509CertificateUtils;
import ec.gob.firmadigital.utils.PropertiesUtils;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Properties;
import javax.swing.JOptionPane;

public class FirmaDigital {
   private static final String HASH_ALGORITHM = "SHA512";

   public static byte[] firmar(
      KeyStore keyStore, String alias, File documento, char[] clave, Point point, int page, String razonFirma, String localizacion, String estampado
   ) throws Exception, IOException {
      byte[] docByteArry = FileUtils.fileConvertToByteArray(documento);
      Properties config = PropertiesUtils.getConfig();
      Properties properties = new Properties();
      properties.setProperty("signingLocation", localizacion);
      properties.setProperty("signingReason", razonFirma);
      properties.setProperty("signTime", TiempoUtils.getFechaHoraServidor(null, PropertiesUtils.versionBase64()));
      if (estampado != null) {
         properties.setProperty("page", String.valueOf(page));
         if (estampado.equals("QR")) {
            properties.setProperty("typeSignature", "QR");
         }

         if (estampado.equals("Simple")) {
            properties.setProperty("typeSignature", "information1");
         }

         if (estampado.equals("Avanzada")) {
            properties.setProperty("typeSignature", "information2");
         }

         String osName = System.getProperty("os.name");
         String osVersion = System.getProperty("os.version");
         properties.setProperty(
            "infoQR",
            "VALIDAR CON: https://www.firmadigital.gob.ec\nFirmado digitalmente con FirmaEC " + config.getProperty("version") + " " + osName + " " + osVersion
         );
         properties.setProperty("PositionOnPageLowerLeftX", String.valueOf(point.x));
         properties.setProperty("PositionOnPageLowerLeftY", String.valueOf(point.y));
      }

      PrivateKey key = null;

      try {
         key = (PrivateKey)keyStore.getKey(alias, clave);
      } catch (UnrecoverableKeyException uke) {
         JOptionPane.showMessageDialog(null, PropertiesUtils.getMessages().getProperty("mensaje.error.certificado_problemas") + "\n" + documento, "Error", 0);
      } catch (ProviderException pe) {
         JOptionPane.showMessageDialog(
            null, PropertiesUtils.getMessages().getProperty("mensaje.error.dispositivo_no_encontrado") + "\n" + documento, "Error", 0
         );
      }

      Certificate[] certChain = keyStore.getCertificateChain(alias);
      if (key != null) {
         String extDocumento = FileUtils.getExtension(docByteArry);
         switch (extDocumento.toLowerCase()) {
            case ".pdf":
               PdfReader pdfReader = new PdfReader(documento);
               PdfDocument pdfDocument = new PdfDocument(pdfReader);

               byte[] var34;
               try {
                  String mensajeAnalisisDocumento = CheckPDF.checkPDF(pdfDocument);
                  if (mensajeAnalisisDocumento != null) {
                     throw new XploitException(mensajeAnalisisDocumento);
                  }

                  byte[] signed = null;

                  try (InputStream is = new ByteArrayInputStream(docByteArry)) {
                     PrivateKeySigner signer = new PrivateKeySigner(key, DigestAlgorithm.forName("SHA512"));
                     PadesEnhancedSigner pdfSigner = new PadesEnhancedSigner(signer);
                     properties.setProperty("identificacion", X509CertificateUtils.getCedulaRuc(keyStore, alias));
                     signed = pdfSigner.sign(is, key, certChain, properties);
                  }

                  var34 = signed;
               } catch (Throwable var29) {
                  try {
                     pdfDocument.close();
                  } catch (Throwable var24) {
                     var29.addSuppressed(var24);
                  }

                  throw var29;
               }

               pdfDocument.close();
               return var34;
            case ".xml":
               XAdESSigner xAdESSigner = new XAdESSigner();
               return xAdESSigner.sign(docByteArry, keyStore, clave);
            default:
               JOptionPane.showMessageDialog(null, "Se ha detectado problemas al procesar el documento\n" + documento, "Error", 0);
               return null;
         }
      } else {
         return null;
      }
   }
}
