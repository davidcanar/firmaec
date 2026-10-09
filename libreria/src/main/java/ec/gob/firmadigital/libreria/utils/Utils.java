package ec.gob.firmadigital.libreria.utils;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import ec.gob.firmadigital.libreria.certificate.CertEcUtils;
import ec.gob.firmadigital.libreria.certificate.to.Certificado;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.certificate.to.Documento;
import ec.gob.firmadigital.libreria.core.Util;
import ec.gob.firmadigital.libreria.exceptions.CRLValidationException;
import ec.gob.firmadigital.libreria.exceptions.CertificadoInvalidoException;
import ec.gob.firmadigital.libreria.exceptions.ConexionException;
import ec.gob.firmadigital.libreria.exceptions.ConexionInvalidaOCSPException;
import ec.gob.firmadigital.libreria.exceptions.ConexionValidarCRLException;
import ec.gob.firmadigital.libreria.exceptions.DocumentoException;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.exceptions.HoraServidorException;
import ec.gob.firmadigital.libreria.exceptions.OcspValidationException;
import ec.gob.firmadigital.libreria.exceptions.RubricaException;
import ec.gob.firmadigital.libreria.exceptions.SignatureVerificationException;
import ec.gob.firmadigital.libreria.sign.SignInfo;
import ec.gob.firmadigital.libreria.sign.Signer;
import ec.gob.firmadigital.libreria.sign.cms.VerificadorCMS;
import ec.gob.firmadigital.libreria.sign.pdf.BasePdfSigner;
import ec.gob.firmadigital.libreria.sign.xades.XAdESSigner;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.w3c.dom.Node;

public class Utils {
   private static final int BUFFER_SIZE = 4096;
   private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
   private static final PdfName PDFNAME_ETSI_RFC3161 = new PdfName("ETSI.RFC3161");
   private static final PdfName PDFNAME_DOCTIMESTAMP = new PdfName("DocTimeStamp");

   public static InputStream loadFile(URI uri) throws IOException {
      if (uri == null) {
         throw new IllegalArgumentException("Se ha pedido el contenido de una URI nula");
      }

      if (uri.getScheme().equals("file")) {
         String path = uri.getSchemeSpecificPart();
         if (path.startsWith("//")) {
            path = path.substring(2);
         }

         return new FileInputStream(new File(path));
      } else {
         InputStream tmpStream = new BufferedInputStream(uri.toURL().openStream());
         byte[] tmpBuffer = getDataFromInputStream(tmpStream);
         return new ByteArrayInputStream(tmpBuffer);
      }
   }

   public static byte[] getBytesFromFile(File file) throws IOException {
      return Files.readAllBytes(file.toPath());
   }

   public static byte[] getDataFromInputStream(InputStream input) throws IOException {
      if (input == null) {
         return new byte[0];
      }

      byte[] buffer = new byte[4096];
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      int nBytes;
      while ((nBytes = input.read(buffer)) != -1) {
         baos.write(buffer, 0, nBytes);
      }

      return baos.toByteArray();
   }

   public static String getUID(X509Certificate c) {
      return c == null ? null : getUID(c.getSubjectX500Principal().toString());
   }

   public static String getUID(String principal) {
      if (principal == null) {
         return null;
      } else {
         String rdn = getRDNvalueFromLdapName("uid", principal);
         if (rdn != null) {
            return rdn;
         } else {
            int i = principal.indexOf(61);
            if (i != -1) {
               LOGGER.warning("No se ha podido obtener el UID, se devolvera el fragmento mas significativo");
               return getRDNvalueFromLdapName(principal.substring(0, i), principal);
            } else {
               LOGGER.warning("Principal no valido, se devolvera la entrada");
               return principal;
            }
         }
      }
   }

   public static String getCN(X509Certificate c) {
      return c == null ? null : getCN(c.getSubjectX500Principal().toString());
   }

   public static String getCN(String principal) {
      if (principal == null) {
         return null;
      }

      String rdn = getRDNvalueFromLdapName("cn", principal);
      if (rdn == null) {
         rdn = getRDNvalueFromLdapName("ou", principal);
      }

      if (rdn != null) {
         return rdn;
      } else {
         int i = principal.indexOf(61);
         if (i != -1) {
            LOGGER.warning("No se ha podido obtener el Common Name ni la Organizational Unit, se devolvera el fragmento mas significativo");
            return getRDNvalueFromLdapName(principal.substring(0, i), principal);
         } else {
            LOGGER.warning("Principal no valido, se devolvera la entrada");
            return principal;
         }
      }
   }

   public static String getRDNvalueFromLdapName(String rdn, String principal) {
      int offset1 = 0;

      while ((offset1 = principal.toLowerCase(Locale.US).indexOf(rdn.toLowerCase(), offset1)) != -1) {
         if (offset1 > 0 && principal.charAt(offset1 - 1) != ',' && principal.charAt(offset1 - 1) != ' ') {
            offset1++;
         } else {
            offset1 += rdn.length();

            while (offset1 < principal.length() && principal.charAt(offset1) == ' ') {
               offset1++;
            }

            if (offset1 >= principal.length()) {
               return null;
            }

            if (principal.charAt(offset1) == '=') {
               offset1++;

               while (offset1 < principal.length() && principal.charAt(offset1) == ' ') {
                  offset1++;
               }

               if (offset1 >= principal.length()) {
                  return "";
               }

               switch (principal.charAt(offset1)) {
                  case '"':
                     if (++offset1 >= principal.length()) {
                        return "";
                     }

                     int offset2 = principal.indexOf(34, offset1);
                     if (offset2 == offset1) {
                        return "";
                     }

                     if (offset2 != -1) {
                        return principal.substring(offset1, offset2);
                     }

                     return principal.substring(offset1);
                  case ',':
                     return "";
                  default:
                     int offset3 = principal.indexOf(44, offset1);
                     if (offset3 != -1) {
                        return principal.substring(offset1, offset3).trim();
                     }

                     return principal.substring(offset1).trim();
               }
            }
         }
      }

      return null;
   }

   public static X509Certificate getCertificate(Node certificateNode) {
      return createCert(certificateNode.getTextContent().trim().replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", ""));
   }

   public static X509Certificate createCert(String b64Cert) {
      if (b64Cert != null && !b64Cert.isEmpty()) {
         try (InputStream isCert = new ByteArrayInputStream(Base64.getDecoder().decode(b64Cert))) {
            X509Certificate cert = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(isCert);

            try {
               isCert.close();
            } catch (IOException e) {
               LOGGER.log(Level.WARNING, "Error cerrando el flujo de lectura del certificado: {0}", e);
            }

            return cert;
         } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "No se pudo decodificar el certificado en Base64, se devolvera null: {0}", e);
            return null;
         }
      } else {
         LOGGER.severe("Se ha proporcionado una cadena nula o vacia, se devolvera null");
         return null;
      }
   }

   public static Date getSignTime(String fechaHora) {
      DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

      try {
         TemporalAccessor accessor = timeFormatter.parse(fechaHora);
         return Date.from(Instant.from(accessor));
      } catch (DateTimeParseException e) {
         LOGGER.log(Level.SEVERE, "La fecha indicada (''{0}'') como momento de firma para PDF no sigue el patron ISO-8601: {1}", new Object[]{fechaHora, e});
         return new Date();
      }
   }

   public static List<Certificado> datosP7mToCertificado(List<X509Certificate> certificados, List<Date> fechasFirmados) throws RubricaException, HoraServidorException, IOException, CertificadoInvalidoException, EntidadCertificadoraNoValidaException, ConexionValidarCRLException, CRLValidationException, OcspValidationException, ConexionException {
      List<Certificado> tempCertificados = new ArrayList<>();

      for (int i = 0; i < certificados.size(); i++) {
         X509Certificate temp = certificados.get(i);
         Date fechaFirmado = fechasFirmados.get(i);
         DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(temp);
         if (datosUsuario == null) {
            datosUsuario = new DatosUsuario();
            datosUsuario.setCedula("No se puede obtener");
            datosUsuario.setNombre(Util.getCN(temp));
         }

         datosUsuario.setFechaFirmaArchivoP7M(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateToCalendar(fechaFirmado).getTime()));
         Certificado certificado = new Certificado(
            temp.getSerialNumber().toString(),
            Util.getCN(temp),
            CertEcUtils.getNombreCA(temp),
            dateToCalendar(temp.getNotBefore()),
            dateToCalendar(temp.getNotAfter()),
            dateToCalendar(fechaFirmado),
            dateToCalendar(UtilsCrlOcsp.validarFechaRevocado(temp, null)),
            esCertificadoValido(temp, fechaFirmado),
            datosUsuario
         );
         certificado.setDocValidTimeStamp(false);
         tempCertificados.add(certificado);
      }

      return tempCertificados;
   }

   public static Documento pdfToDocumento(InputStream pdf) throws IOException, SignatureVerificationException, Exception {
      PdfReader pdfReader = new PdfReader(pdf);
      Signer signer = new BasePdfSigner();
      List<SignInfo> signInfos = signer.getSigners(pdf.readAllBytes());
      return pdfToDocumento(pdfReader, signInfos);
   }

   public static Documento pdfToDocumento(File pdf) throws IOException, SignatureVerificationException, Exception {
      PdfReader pdfReader = new PdfReader(pdf);
      Signer signer = new BasePdfSigner();
      List<SignInfo> signInfos = signer.getSigners(FileUtils.fileConvertToByteArray(pdf));
      return pdfToDocumento(pdfReader, signInfos);
   }

   public static Documento pdfToDocumento(PdfReader pdfReader, List<SignInfo> signInfos) throws IOException, SignatureVerificationException, Exception {
      PdfDocument pdfDocument = new PdfDocument(pdfReader);

      Documento var29;
      label126: {
         Documento documento;
         try {
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            List<Certificado> certificados = new ArrayList<>();
            documento = new Documento(true, false, certificados, null);
            if (signInfos == null || signInfos.isEmpty()) {
               var29 = new Documento(false, false, certificados, "Documento sin firmas");
               break label126;
            }

            for (SignInfo signInfo : signInfos) {
               Certificado certificado = signInfoToCertificado(signInfo);

               try {
                  for (String signatureName : signatureUtil.getSignatureNames()) {
                     PdfPKCS7 pdfPKCS7 = signatureUtil.readSignatureData(signatureName);

                     for (X509Certificate certificate : signInfo.getCerts()) {
                        if (pdfPKCS7.getSigningCertificate().equals(certificate)
                           && certificado.getSignGenerated().getTime().equals(pdfPKCS7.getSignDate().getTime())) {
                           try {
                              if (pdfPKCS7.getTimeStampTokenInfo() != null) {
                                 for (Certificate tsaCertificate : pdfPKCS7.getTimestampCertificates()) {
                                    X509Certificate x509Certificate = (X509Certificate)tsaCertificate;
                                    List<String> extendedKeyUsages = x509Certificate.getExtendedKeyUsage();
                                    if (extendedKeyUsages != null) {
                                       for (String extendedKeyUsage : extendedKeyUsages) {
                                          if (extendedKeyUsage.equals("1.3.6.1.5.5.7.3.8")) {
                                             if (certificado.getDatosUsuario() == null) {
                                                certificado.getDatosUsuario().setApellido("");
                                                certificado.setDatosUsuario(infoCertificado(certificado.getDatosUsuario(), signInfo));
                                             }

                                             certificado.setDocTimeStamp(pdfPKCS7.getTimeStampDate().getTime());
                                             certificado.setDocTimeStampIssuedBy(CertEcUtils.getNombreCA(x509Certificate));
                                             certificado.setCnTimeStamp(getCN(x509Certificate));
                                             if (verifySignature(x509Certificate)) {
                                                certificado.setDocValidTimeStamp(true);
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           } catch (EntidadCertificadoraNoValidaException ex) {
                              LOGGER.log(Level.SEVERE, "Sellado de tiempo: {0}", ex.getMessage());
                           }

                           certificado.setDocReason(pdfPKCS7.getReason());
                           certificado.setDocLocation(pdfPKCS7.getLocation());
                           certificado.setSignVerify(pdfPKCS7.verifySignatureIntegrityAndAuthenticity());
                           if (!documento.getDocValidate()) {
                              boolean cubreTodoElDocumento = signatureUtil.signatureCoversWholeDocument(signatureName);
                              if (!cubreTodoElDocumento) {
                                 LOGGER.log(Level.WARNING, "La firma ''{0}'' no cubre todo el documento (docValidate=false)", signatureName);
                              }

                              documento.setDocValidate(cubreTodoElDocumento);
                           }

                           certificado.setKeyUsages(validacionKeyUsages(pdfPKCS7.getSigningCertificate()));
                           break;
                        }
                     }
                  }
               } catch (SignatureException ex) {
                  LOGGER.log(Level.SEVERE, ex.getMessage());
               }

               certificados.add(certificado);
            }

            if (certificados != null || !certificados.isEmpty()) {
               documento.setSignValidate(validarCertificados(certificados, true));
            }

            documento.setCertificados(certificados);
         } catch (Throwable var28) {
            try {
               pdfDocument.close();
            } catch (Throwable var25) {
               var28.addSuppressed(var25);
            }

            throw var28;
         }

         pdfDocument.close();
         return documento;
      }

      pdfDocument.close();
      return var29;
   }

   public static Certificado isTimeStamping(SignatureUtil signatureUtil, String signatureName, Certificado certificado) throws CertificateParsingException, SignatureException {
      PdfPKCS7 pdfPKCS7 = signatureUtil.readSignatureData(signatureName);
      PdfDictionary pdfDictionary = signatureUtil.getSignatureDictionary(signatureName);
      if (PDFNAME_ETSI_RFC3161.equals(pdfDictionary.get(PdfName.SubFilter)) || PDFNAME_DOCTIMESTAMP.equals(pdfDictionary.get(PdfName.SubFilter))) {
         for (String extendedKeyUsage : pdfPKCS7.getSigningCertificate().getExtendedKeyUsage()) {
            if (extendedKeyUsage.equals("1.3.6.1.5.5.7.3.8")) {
               certificado.setDocValidTimeStamp(true);
            }
         }
      }

      return certificado;
   }

   public static DatosUsuario infoCertificado(DatosUsuario datosUsuario, SignInfo signInfo) {
      X500Principal subjectX500Principal = signInfo.getCerts()[0].getSubjectX500Principal();
      X500Name subjectX500name = new X500Name(subjectX500Principal.getName());
      String cedula = "";
      String nombre = "";

      try {
         cedula = subjectX500name.getRDNs(BCStyle.SERIALNUMBER)[0].getFirst().getValue().toString();
         nombre = subjectX500name.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString();
      } catch (ArrayIndexOutOfBoundsException var7) {
      }

      datosUsuario.setCedula(cedula);
      datosUsuario.setNombre(nombre);
      return datosUsuario;
   }

   public static Certificado signInfoToCertificado(SignInfo signInfo) throws CertificadoInvalidoException, IOException, ConexionException, EntidadCertificadoraNoValidaException {
      signInfo.getCerts();
      Certificado certificado = null;
      DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(signInfo.getCerts()[0]);
      if (datosUsuario == null) {
         DatosUsuario var4 = new DatosUsuario();
         datosUsuario = infoCertificado(var4, signInfo);
         datosUsuario.setCertificadoDigitalValido(false);
      }

      certificado = new Certificado(
         signInfo.getCerts()[0].getSerialNumber().toString(),
         Util.getCN(signInfo.getCerts()[0]),
         CertEcUtils.getNombreCA(signInfo.getCerts()[0]),
         dateToCalendar(signInfo.getCerts()[0].getNotBefore()),
         dateToCalendar(signInfo.getCerts()[0].getNotAfter()),
         dateToCalendar(signInfo.getSigningTime()),
         dateToCalendar(UtilsCrlOcsp.validarFechaRevocado(signInfo.getCerts()[0], null)),
         esCertificadoValido(signInfo.getCerts()[0], signInfo.getSigningTime()),
         datosUsuario
      );
      certificado.setDocValidTimeStamp(false);
      return certificado;
   }

   public static Calendar dateToCalendar(Date date) {
      Calendar calendar = null;
      if (date != null) {
         calendar = Calendar.getInstance();
         calendar.setTime(date);
      }

      return calendar;
   }

   public static boolean esCertificadoValido(X509Certificate cert, Date signingTime) {
      return !signingTime.before(cert.getNotBefore()) && !signingTime.after(cert.getNotAfter());
   }

   public static Documento x509CertificateToDocumento(List<SignInfo> signInfos) throws DocumentoException, CertificadoInvalidoException, IOException, ConexionException, EntidadCertificadoraNoValidaException, CertificateParsingException {
      Documento documento = null;
      List<Certificado> certificados = new ArrayList<>();
      if (signInfos != null && !signInfos.isEmpty()) {
         for (SignInfo signInfo : signInfos) {
            documento = new Documento(true, false, null, null);
            Certificado certificado = signInfoToCertificado(signInfo);
            if (certificado == null) {
               return new Documento(false, false, certificados, "Documento sin firmas");
            }

            certificados.add(certificado);
         }

         documento.setCertificados(certificados);
         documento.setSignValidate(validarCertificados(documento.getCertificados(), false));
         return documento;
      } else {
         return new Documento(false, false, certificados, "Documento sin firmas");
      }
   }

   public static Documento verificarDocumento(File file, String base64) throws IOException, KeyStoreException, OcspValidationException, SignatureException, RubricaException, ConexionInvalidaOCSPException, HoraServidorException, CertificadoInvalidoException, EntidadCertificadoraNoValidaException, ConexionValidarCRLException, SignatureVerificationException, DocumentoException, CRLValidationException, Exception {
      byte[] docByteArray = FileUtils.fileConvertToByteArray(file);
      Documento documento = null;
      String extDocumento = FileUtils.getExtension(docByteArray);
      switch (extDocumento.toLowerCase()) {
         case ".p7s":
            VerificadorCMS verificador = new VerificadorCMS();
            byte[] archivoOriginal = verificador.verify(docByteArray);
            String nombreArchivo = FileUtils.crearNombreVerificado(file, FileUtils.getExtension(archivoOriginal), base64);
            FileUtils.saveByteArrayToDisc(archivoOriginal, nombreArchivo);
            FileUtils.abrirDocumento(nombreArchivo);
            documento = new Documento(true, false, null, null);
            documento.setCertificados(datosP7mToCertificado(verificador.certificados, verificador.fechasFirmados));
            documento.setSignValidate(validarCertificados(documento.getCertificados(), false));
            return documento;
         case ".pdf": {
            PdfReader pdfReader = new PdfReader(file);
            PdfDocument pdfDocumentRevision = new PdfDocument(pdfReader);

            Documento var11;
            label45: {
               Documento certificados;
               try {
                  String mensajeAnalisisDocumento = CheckPDF.checkPDF(pdfDocumentRevision);
                  if (mensajeAnalisisDocumento != null) {
                     List<Certificado> certificadosx = new ArrayList<>();
                     var11 = new Documento(false, false, certificadosx, mensajeAnalisisDocumento);
                     break label45;
                  }

                  certificados = pdfToDocumento(file);
               } catch (Throwable var13) {
                  try {
                     pdfDocumentRevision.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }

                  throw var13;
               }

               pdfDocumentRevision.close();
               return certificados;
            }

            pdfDocumentRevision.close();
            return var11;
         }
         case ".xml":
            XAdESSigner xAdESSigner = new XAdESSigner();
            List<SignInfo> signInfos = xAdESSigner.getSignInfo(docByteArray);
            return x509CertificateToDocumento(signInfos);
         default: {
            List<Certificado> certificados = new ArrayList<>();
            return new Documento(false, false, certificados, "El archivo no se puede validar, es extensión " + extDocumento);
         }
      }
   }

   private static boolean validarCertificados(List<Certificado> certificados, boolean pdf) {
      boolean retorno = true;
      if (certificados != null || !certificados.isEmpty()) {
         for (Certificado certificado : certificados) {
            boolean revocado = validarFirma(certificado.getValidFrom(), certificado.getValidTo(), certificado.getSignGenerated(), certificado.getRevocated());
            boolean certDigitalValido = certificado.getDatosUsuario() != null && certificado.getDatosUsuario().isCertificadoDigitalValido();
            boolean firmaValida;
            if (pdf) {
               firmaValida = revocado
                  && Boolean.TRUE.equals(certificado.getSignVerify())
                  && Boolean.TRUE.equals(certificado.getCertificateValidated())
                  && certDigitalValido;
            } else {
               firmaValida = revocado && Boolean.TRUE.equals(certificado.getCertificateValidated()) && certDigitalValido;
            }

            if (!firmaValida) {
               LOGGER.log(
                  Level.WARNING,
                  "Firma inválida: serial={0}, vigenteYNoRevocado={1}, integridadFirma(signVerify)={2}, certEnVigenciaAlFirmar={3}, certificadoDigitalValido={4}, validFrom={5}, validTo={6}, fechaFirma={7}, fechaRevocado={8}",
                  new Object[]{
                     certificado.getSerial(),
                     revocado,
                     certificado.getSignVerify(),
                     certificado.getCertificateValidated(),
                     certDigitalValido,
                     certificado.getValidFrom() != null ? certificado.getValidFrom().getTime() : null,
                     certificado.getValidTo() != null ? certificado.getValidTo().getTime() : null,
                     certificado.getSignGenerated() != null ? certificado.getSignGenerated().getTime() : null,
                     certificado.getRevocated() != null ? certificado.getRevocated().getTime() : null
                  }
               );
               retorno = false;
               break;
            }
         }
      }

      return retorno;
   }

   public static boolean validarFirma(Calendar fechaDesde, Calendar fechaHasta, Calendar fechaFirmado, Calendar fechaRevocado) {
      boolean retorno = true;
      if (fechaRevocado == null) {
         retorno = true;
      } else if (fechaFirmado.compareTo(fechaDesde) < 0 || fechaFirmado.compareTo(fechaHasta) > 0) {
         retorno = false;
      } else if (fechaRevocado != null && fechaRevocado.compareTo(fechaFirmado) <= 0) {
         retorno = false;
      }

      return retorno;
   }

   public static String validacionKeyUsages(X509Certificate signCert) throws CertificateParsingException {
      String keyUsages = "";
      boolean[] keyUsage = signCert.getKeyUsage();
      if (keyUsage[0]) {
         keyUsages = keyUsages + "Firma Electrónica, ";
      }

      if (keyUsage[1]) {
         keyUsages = keyUsages + "No Repudio, ";
      }

      if (keyUsage[2]) {
         keyUsages = keyUsages + "Cifrado de llave, ";
      }

      if (keyUsage[3]) {
         keyUsages = keyUsages + "Cifrado de datos, ";
      }

      if (keyUsage[4]) {
         keyUsages = keyUsages + "Acuerdo de llaves, ";
      }

      if (keyUsage[5]) {
         keyUsages = keyUsages + "Firma y certificado de llave, ";
      }

      if (keyUsage[6]) {
         keyUsages = keyUsages + "Firma de CRL, ";
      }

      if (keyUsage[7]) {
         keyUsages = keyUsages + "Solo cifrado, ";
      }

      if (keyUsage[8]) {
         keyUsages = keyUsages + "Solo descifrado";
      }

      return keyUsages;
   }

   public static boolean verifySignature(X509Certificate certificate) throws InvalidKeyException, EntidadCertificadoraNoValidaException {
      return verifySignature(certificate, CertEcUtils.getRootCertificate(certificate));
   }

   public static boolean verifySignature(X509Certificate certificate, X509Certificate rootCertificate) throws InvalidKeyException, EntidadCertificadoraNoValidaException {
      if (rootCertificate != null) {
         PublicKey publicKeyForSignature = rootCertificate.getPublicKey();

         try {
            certificate.verify(publicKeyForSignature);
            return true;
         } catch (javax.management.openmbean.InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException var4) {
         }
      }

      return false;
   }

   public static PdfFont loadFont(String fontName) {
      PdfFont pdfFont = null;

      try {
         pdfFont = PdfFontFactory.createFont(fontName);
      } catch (IOException ex) {
         Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
      }

      return pdfFont;
   }

   static {
      BouncyCastleUtils.initializeBouncyCastle();
   }
}
