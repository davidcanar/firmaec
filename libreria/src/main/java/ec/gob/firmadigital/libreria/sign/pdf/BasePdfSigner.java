package ec.gob.firmadigital.libreria.sign.pdf;

import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.SignatureUtil;
import ec.gob.firmadigital.libreria.certificate.CertEcUtils;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.exceptions.InvalidFormatException;
import ec.gob.firmadigital.libreria.sign.SignInfo;
import ec.gob.firmadigital.libreria.sign.Signer;
import ec.gob.firmadigital.libreria.sign.pdf.appearance.Information1Appearance;
import ec.gob.firmadigital.libreria.sign.pdf.appearance.Information2Appearance;
import ec.gob.firmadigital.libreria.sign.pdf.appearance.QrAppereance;
import ec.gob.firmadigital.libreria.utils.PropertiesUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasePdfSigner implements Signer {
   private static final Logger LOGGER = Logger.getLogger(BasePdfSigner.class.getName());
   public static final String SIGNING_REASON = "signingReason";
   public static final String SIGNING_LOCATION = "signingLocation";
   public static final String SIGN_TIME = "signTime";
   public static final String SIGNATURE_PAGE = "signingPage";
   public static final String LAST_PAGE = "page";
   public static final String FONT_SIZE = "font";
   public static final String TYPE_SIG = "typeSignature";
   public static final String INFO_QR = "infoQR";
   private String fieldName;
   private ByteArrayOutputStream baos;
   private ByteArrayOutputStream baosSign;

   public String getFieldName() {
      return this.fieldName;
   }

   public ByteArrayOutputStream getDocumentoPorFirmar() {
      return this.baos;
   }

   public ByteArrayOutputStream getDocumentoFirmado() {
      return this.baosSign;
   }

   public byte[] emptySignature(InputStream is, Certificate[] certChain, Properties properties, String algorithm) throws IOException, BadPasswordException {
      PdfReader pdfReader = new PdfReader(is) {
         public boolean hasRebuiltXref() {
            return false;
         }
      };

      byte[] var41;
      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
         this.baos = os;
         StampingProperties stampingProperties = new StampingProperties();
         stampingProperties.useAppendMode();
         PdfSigner pdfSigner = new PdfSigner(pdfReader, this.baos, stampingProperties);
         X509Certificate x509Certificate = (X509Certificate)certChain[0];
         Properties extraParams = properties != null ? properties : new Properties();
         String reason = extraParams.getProperty("signingReason");
         String location = extraParams.getProperty("signingLocation");
         String signTime = extraParams.getProperty("signTime");
         float fontSize = 3.0F;

         try {
            if (extraParams.getProperty("font") == null) {
               fontSize = 3.0F;
            } else {
               fontSize = Float.parseFloat(extraParams.getProperty("font").trim());
            }
         } catch (NumberFormatException e) {
            LOGGER.log(
               Level.WARNING,
               "Se ha indicado un tamaño de letra invalida (''{0}''), se usara el tamaño por defecto: {1} {2}",
               new Object[]{extraParams.getProperty("font"), fontSize, e}
            );
         }

         String typeSig = extraParams.getProperty("typeSignature");
         if (typeSig != null && typeSig.equals("QR") && extraParams.getProperty("font") == null) {
            fontSize = 4.5F;
         }

         String infoQR = "";
         if (extraParams.getProperty("infoQR") == null) {
            infoQR = "";
         } else {
            infoQR = extraParams.getProperty("infoQR", "").trim();
         }

         int page = 0;

         try {
            if (extraParams.getProperty("page") == null) {
               page = 0;
            } else {
               page = Integer.parseInt(extraParams.getProperty("page").trim());
            }
         } catch (NumberFormatException e) {
            LOGGER.log(
               Level.WARNING,
               "Se ha indicado un numero de pagina invalido (''{0}''), se usara la ultima pagina: {1}",
               new Object[]{extraParams.getProperty("page"), e}
            );
         }

         if (page == 0 || page < 0 || page > pdfSigner.getDocument().getNumberOfPages()) {
            page = pdfSigner.getDocument().getNumberOfPages();
         }

         Rectangle signaturePositionOnPage = RectanguloUtil.getPositionOnPage(extraParams);
         if (typeSig != null) {
            PdfSignatureAppearance signatureAppearance = pdfSigner.getSignatureAppearance();
            signatureAppearance.setPageRect(signaturePositionOnPage).setPageNumber(page);
            if (signaturePositionOnPage != null) {
               DatosUsuario datosUsuario = null;

               try {
                  datosUsuario = CertEcUtils.getDatosUsuarios(x509Certificate);
               } catch (EntidadCertificadoraNoValidaException ex) {
                  Logger.getLogger(BasePdfSigner.class.getName()).log(Level.SEVERE, null, ex);
               }

               String nombreFirmante = "";
               if (datosUsuario.getCedula() != null) {
                  nombreFirmante = datosUsuario.getNombre() + " " + datosUsuario.getApellido();
               } else {
                  nombreFirmante = nombreFirmante + datosUsuario.getRazonSocial();
               }

               nombreFirmante = nombreFirmante.toUpperCase();
               String informacionCertificado = x509Certificate.getSubjectDN().getName();
               PdfDocument pdfDocument = pdfSigner.getDocument();

               (switch (typeSig) {
                  case "QR" -> new QrAppereance(nombreFirmante, reason, location, signTime, infoQR);
                  case "information1" -> new Information1Appearance(nombreFirmante, reason, location, signTime);
                  case "information2" -> new Information2Appearance(nombreFirmante, informacionCertificado, reason, location, signTime);
                  default -> throw new RuntimeException("typeSign unknown");
               }).createCustomAppearance(signatureAppearance, page, pdfDocument, signaturePositionOnPage);
            }

            if (reason != null) {
               signatureAppearance.setReason(reason);
            }

            if (location != null) {
               signatureAppearance.setLocation(location);
            }

            signatureAppearance.setSignatureCreator(PropertiesUtils.getConfig().getProperty("version") + " (FirmaEC)");
            signatureAppearance.setCertificate(certChain[0]);
         }

         if (signTime != null) {
            Date date = Utils.getSignTime(signTime);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            pdfSigner.setSignDate(calendar);
         }

         byte[] hash = null;

         try {
            PreSignatureContainer external = new PreSignatureContainer(PdfName.Adobe_PPKLite, PdfName.Adbe_pkcs7_detached, algorithm);
            pdfSigner.signExternalContainer(external, 16384);
            hash = external.getHash();
         } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
         }

         this.fieldName = pdfSigner.getFieldName();
         var41 = hash;
      } catch (Throwable var34) {
         try {
            pdfReader.close();
         } catch (Throwable var27) {
            var34.addSuppressed(var27);
         }

         throw var34;
      }

      pdfReader.close();
      return var41;
   }

   public void createSignature(byte[] hashSigned, ByteArrayOutputStream baos, String fieldName, PrivateKey pk, Certificate[] chain) throws IOException, GeneralSecurityException {
      try (InputStream is = new ByteArrayInputStream(baos.toByteArray())) {
         PdfReader reader = new PdfReader(is);

         try {
            this.baosSign = new ByteArrayOutputStream();
            PdfSigner signer = new PdfSigner(reader, this.baosSign, new StampingProperties());
            IExternalSignatureContainer external = new BasePdfSigner.MyExternalSignatureContainer(hashSigned, pk, chain);
            PdfSigner.signDeferred(signer.getDocument(), fieldName, this.baosSign, external);
         } catch (Throwable var12) {
            try {
               reader.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }

            throw var12;
         }

         reader.close();
      }
   }

   @Override
   public List<SignInfo> getSigners(byte[] sign) throws InvalidFormatException, IOException {
      PdfReader pdfReader;
      try (InputStream is = new ByteArrayInputStream(sign)) {
         pdfReader = new PdfReader(is);
      } catch (Exception e) {
         LOGGER.log(Level.SEVERE, "No se ha podido leer el PDF: {0}", e);
         throw new InvalidFormatException("No se ha podido leer el PDF", e);
      }

      SignatureUtil signatureUtil;
      try {
         PdfDocument pdfDocument = new PdfDocument(pdfReader);
         signatureUtil = new SignatureUtil(pdfDocument);
      } catch (Exception e) {
         LOGGER.log(Level.SEVERE, "No se ha podido obtener la informacion de los firmantes del PDF, se devolvera un arbol vacio: {0}", e);
         throw new InvalidFormatException("No se ha podido obtener la informacion de los firmantes del PDF", e);
      }

      List<String> names = signatureUtil.getSignatureNames();
      List<SignInfo> signInfos = new ArrayList<>();

      for (String signatureName : names) {
         PdfPKCS7 pdfPKCS7;
         try {
            pdfPKCS7 = signatureUtil.readSignatureData(signatureName);
         } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(
               Level.SEVERE,
               "El PDF contiene una firma corrupta o con un formato desconocido ({0}), se continua con las siguientes si las hubiese: {1}",
               new Object[]{signatureName, e}
            );
            continue;
         }

         Certificate[] signCertificateChain = pdfPKCS7.getSignCertificateChain();
         X509Certificate[] certChain = new X509Certificate[signCertificateChain.length];

         for (int i = 0; i < certChain.length; i++) {
            certChain[i] = (X509Certificate)signCertificateChain[i];
         }

         SignInfo signInfo = new SignInfo(certChain, pdfPKCS7.getSignDate().getTime());
         signInfos.add(signInfo);
      }

      return signInfos;
   }

   class MyExternalSignatureContainer implements IExternalSignatureContainer {
      protected byte[] hashSigned;
      protected PrivateKey pk;
      protected Certificate[] chain;

      public MyExternalSignatureContainer(byte[] hashSigned, PrivateKey pk, Certificate[] chain) {
         this.hashSigned = hashSigned;
         this.pk = pk;
         this.chain = chain;
      }

      public byte[] sign(InputStream is) throws GeneralSecurityException {
         return this.hashSigned;
      }

      public void modifySigningDictionary(PdfDictionary signDic) {
      }
   }
}
