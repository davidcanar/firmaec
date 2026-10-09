package ec.gob.firmadigital.libreria.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ec.gob.firmadigital.libreria.certificate.CertEcUtils;
import ec.gob.firmadigital.libreria.certificate.CrlUtils;
import ec.gob.firmadigital.libreria.certificate.ValidationResult;
import ec.gob.firmadigital.libreria.exceptions.CRLValidationException;
import ec.gob.firmadigital.libreria.exceptions.CertificadoInvalidoException;
import ec.gob.firmadigital.libreria.exceptions.ConexionException;
import ec.gob.firmadigital.libreria.exceptions.ConexionValidarCRLException;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.exceptions.RubricaException;
import ec.gob.firmadigital.libreria.ocsp.ValidadorOCSP;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtilsCrlOcsp {
   private static final int TIME_OUT = 5000;
   private static final Logger LOGGER = Logger.getLogger(UtilsCrlOcsp.class.getName());

   public static String validarCertificado(X509Certificate cert, String apiUrl) throws EntidadCertificadoraNoValidaException, IOException, RubricaException, ConexionValidarCRLException, CRLValidationException {
      String fechaRevocado = null;

      try {
         fechaRevocado = validarCertificadoProxy(cert);
         if (fechaRevocado != null) {
            if (fechaRevocado.contains("errorRed")) {
               throw new ConexionValidarCRLException("Error de red al validar contra Proxy CRL");
            } else {
               return fechaRevocado;
            }
         } else {
            LOGGER.log(Level.INFO, "Certificado NO revocado según Proxy CRL");
            return null;
         }
      } catch (IOException ex) {
         LOGGER.log(Level.WARNING, "Fallo la validación por Proxy CRL: {0}", ex.getMessage());

         try {
            LOGGER.log(Level.INFO, "Intentando validación CRL directa en tiempo real");
            return validarCRLDirecta(cert);
         } catch (ConexionValidarCRLException exx) {
            LOGGER.log(Level.SEVERE, "No se pudo validar contra CRL directo: {0}", exx.getMessage());
            throw new ConexionValidarCRLException("No se puede validar el certificado contra CRL: " + exx.getMessage());
         }
      }
   }

   public static Date fechaString_Date(String fecha) {
      DateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date date = null;
      if (fecha != null) {
         try {
            date = formato.parse(fecha);
         } catch (ParseException ex) {
            LOGGER.log(Level.SEVERE, fecha, ex);
         }
      }

      return date;
   }

   public static Date validarFechaRevocado(X509Certificate cert, String apiUrl) throws CertificadoInvalidoException, IOException, ConexionException {
      Date fechaRevocado = null;

      try {
         String revocado = validarCertificado(cert, apiUrl);
         if (revocado != null && revocado.contains("errorRed")) {
            LOGGER.log(Level.WARNING, "Error de red al validar revocacion del certificado, se omite la validacion");
            return null;
         }

         fechaRevocado = fechaString_Date(revocado);
      } catch (RubricaException | ConexionValidarCRLException | CRLValidationException | EntidadCertificadoraNoValidaException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         LOGGER.log(Level.WARNING, "Sin acceso a la red: no se pudo validar la revocacion del certificado, se omite la validacion: {0}", ex.getMessage());
      } catch (RuntimeException ex) {
         LOGGER.log(Level.WARNING, "No se pudo validar la revocacion del certificado, se omite la validacion: {0}", ex.toString());
      }

      return fechaRevocado;
   }

   public static Date validarOCSPDate(X509Certificate cert) throws IOException, RubricaException, EntidadCertificadoraNoValidaException {
      List<String> ocspUrls = CertificateUtils.getAuthorityInformationAccess(cert);
      ocspUrls.forEach(ocsp -> System.out.println("OCSP=" + ocsp));
      X509Certificate certRoot = CertEcUtils.getRootCertificate(cert);
      Date fechaRevocado = null;

      try {
         fechaRevocado = fechaString_Date(ValidadorOCSP.ValidarOCSP(cert, certRoot, ocspUrls.get(0)));
      } catch (RubricaException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      }

      return fechaRevocado;
   }

   public static String validarOCSP(X509Certificate cert) throws IOException, RubricaException, EntidadCertificadoraNoValidaException {
      List<String> ocspUrls = CertificateUtils.getAuthorityInformationAccess(cert);
      ocspUrls.forEach(ocsp -> System.out.println("OCSP=" + ocsp));
      X509Certificate certRoot = CertEcUtils.getRootCertificate(cert);
      return ValidadorOCSP.ValidarOCSP(cert, certRoot, ocspUrls.get(0));
   }

   public static String validarCRL(X509Certificate cert) throws IOException, EntidadCertificadoraNoValidaException, RubricaException, ConexionValidarCRLException, CRLValidationException {
      X509Certificate root = CertEcUtils.getRootCertificate(cert);
      CrlUtils crlUtils = new CrlUtils();
      String urlCrl = obtenerUrlCRL(CertificateUtils.getCrlDistributionPoints(cert));
      ValidationResult result = crlUtils.verifyCertificateCRLs(cert, root.getPublicKey(), Arrays.asList(urlCrl));
      if (result == ValidationResult.CANNOT_DOWNLOAD_CRL) {
         throw new ConexionValidarCRLException("No se puede validar contra la lista de revocación:" + urlCrl);
      } else if (!result.isValid()) {
         throw new CRLValidationException("Certificado Inválido");
      } else {
         return crlUtils.getRevocationDate();
      }
   }

   private static String validarCRLDirecta(X509Certificate cert) throws IOException, EntidadCertificadoraNoValidaException, RubricaException, ConexionValidarCRLException, CRLValidationException {
      try {
         String fechaRevocado = validarCRL(cert);
         if (fechaRevocado != null) {
            LOGGER.log(Level.INFO, "Certificado revocado según CRL directo. Fecha: {0}", fechaRevocado);
            return fechaRevocado;
         } else {
            LOGGER.log(Level.INFO, "Certificado NO revocado según CRL directo");
            return null;
         }
      } catch (ConexionValidarCRLException ex) {
         LOGGER.log(Level.SEVERE, "Error de conexión al CRL directo: {0}", ex.getMessage());
         throw ex;
      } catch (CRLValidationException ex) {
         LOGGER.log(Level.SEVERE, "Certificado inválido según CRL directo: {0}", ex.getMessage());
         throw ex;
      }
   }

   private static String obtenerUrlCRL(List<String> urls) {
      for (String url : urls) {
         if (url.toLowerCase().contains("crl")) {
            return url;
         }
      }

      return null;
   }

   private static String validarCertificadoProxy(X509Certificate cert) throws IOException {
      BigInteger serial = cert.getSerialNumber();
      String crlUrl = null;

      try {
         List<String> crlUrls = CertificateUtils.getCrlDistributionPoints(cert);
         if (crlUrls != null && !crlUrls.isEmpty()) {
            crlUrl = obtenerUrlCRL(crlUrls);
            if (crlUrl == null) {
               crlUrl = crlUrls.get(0);
            }
         }
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "No se pudo extraer CRL Distribution Points: {0}", e.getMessage());
      }

      String ocspUrl = null;

      try {
         List<String> ocspUrls = CertificateUtils.getAuthorityInformationAccess(cert);
         if (ocspUrls != null && !ocspUrls.isEmpty()) {
            for (String url : ocspUrls) {
               if (url.toLowerCase().contains("ocsp")) {
                  ocspUrl = url;
                  break;
               }
            }
         }
      } catch (Exception e) {
         LOGGER.log(Level.FINE, "No se pudo extraer OCSP URL: {0}", e.getMessage());
      }

      if (crlUrl == null && ocspUrl == null) {
         LOGGER.log(Level.WARNING, "Certificado sin CRL Distribution Point ni OCSP URL");
         throw new IOException("No se encontraron puntos de distribucion CRL ni OCSP en el certificado");
      }

      String certBase64 = null;

      try {
         certBase64 = Base64.getEncoder().encodeToString(cert.getEncoded());
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "No se pudo codificar el certificado en Base64: {0}", e.getMessage());
      }

      return llamarProxyCrl(crlUrl, ocspUrl, serial.toString(), certBase64);
   }

   private static String llamarProxyCrl(String crlUrl, String ocspUrl, String serial, String certBase64) throws IOException {
      String proxyUrl = PropertiesUtils.getConfig().getProperty("certificado_revocado_proxy_url");
      if (proxyUrl != null && !proxyUrl.isEmpty()) {
         StringBuilder body = new StringBuilder();
         body.append("serial=").append(URLEncoder.encode(serial, StandardCharsets.UTF_8.name()));
         if (crlUrl != null) {
            body.append("&crlUrl=").append(URLEncoder.encode(crlUrl, StandardCharsets.UTF_8.name()));
         }

         if (ocspUrl != null) {
            body.append("&ocspUrl=").append(URLEncoder.encode(ocspUrl, StandardCharsets.UTF_8.name()));
         }

         if (certBase64 != null) {
            body.append("&certBase64=").append(URLEncoder.encode(certBase64, StandardCharsets.UTF_8.name()));
         }

         URL url = new URL(proxyUrl);
         HttpURLConnection conn = (HttpURLConnection)url.openConnection();
         conn.setRequestMethod("POST");
         conn.setConnectTimeout(5000);
         conn.setReadTimeout(15000);
         conn.setDoOutput(true);
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

         try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
         }

         int responseCode = conn.getResponseCode();
         if (responseCode >= 300 && responseCode < 400) {
            String location = conn.getHeaderField("Location");
            conn = (HttpURLConnection)new URL(location).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
               os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            responseCode = conn.getResponseCode();
         }

         if (responseCode >= 400) {
            LOGGER.log(Level.SEVERE, "Proxy CRL respondio con codigo {0}", responseCode);
            throw new IOException("Error en proxy CRL. Response Code: " + responseCode);
         }

         String responseStr;
         try (
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
         ) {
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
               sb.append(line);
            }

            responseStr = sb.toString();
         }

         try {
            JsonObject json = JsonParser.parseString(responseStr).getAsJsonObject();
            if (json.has("error") && !json.get("error").isJsonNull()) {
               String error = json.get("error").getAsString();
               LOGGER.log(Level.WARNING, "Proxy CRL retorno error: {0}", error);
               throw new IOException("Error del proxy CRL: " + error);
            } else {
               boolean revocado = json.has("revocado") && json.get("revocado").getAsBoolean();
               return revocado && json.has("fechaRevocacion") && !json.get("fechaRevocacion").isJsonNull() ? json.get("fechaRevocacion").getAsString() : null;
            }
         } catch (Exception e) {
            if (e instanceof IOException) {
               throw (IOException)e;
            }

            LOGGER.log(Level.SEVERE, "Error parseando respuesta del proxy CRL: {0}", responseStr);
            throw new IOException("Error parseando respuesta del proxy CRL", e);
         }
      } else {
         throw new IOException("No se ha configurado la propiedad certificado_revocado_proxy_url");
      }
   }
}
