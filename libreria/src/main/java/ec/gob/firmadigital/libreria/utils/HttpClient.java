package ec.gob.firmadigital.libreria.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpClient {
   private static final Logger LOGGER = Logger.getLogger(HttpClient.class.getName());
   private static final String SSL_CONTEXT = "SSL";
   private static final String HTTPS = "https";
   private static final HostnameVerifier DEFAULT_HOSTNAME_VERIFIER = HttpsURLConnection.getDefaultHostnameVerifier();
   private static final SSLSocketFactory DEFAULT_SSL_SOCKET_FACTORY = HttpsURLConnection.getDefaultSSLSocketFactory();
   private static final boolean DISABLE_SSL_CHECKS = true;
   private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
   private static final int DEFAULT_READ_TIMEOUT = 15000;
   private static final TrustManager[] DUMMY_TRUST_MANAGER = new TrustManager[]{new X509TrustManager() {
      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType) {
      }

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType) {
      }

      @Override
      public X509Certificate[] getAcceptedIssuers() {
         return null;
      }
   }};
   private static final HostnameVerifier ALL_HOSTNAME_VERIFIER = new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
         return true;
      }
   };

   public byte[] download(String urlString) throws IOException {
      return this.download(urlString, 5000, 15000);
   }

   public byte[] download(String urlString, int connectTimeOut, int readTimeOut) throws IOException {
      if (urlString == null) {
         throw new IllegalArgumentException("La URL a leer no puede ser nula");
      }

      URL url = new URL(urlString);
      if (url.getProtocol().equals("https")) {
         try {
            disableSslChecks();
         } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se ha podido ajustar la confianza SSL, es posible que no se pueda completar la conexion: {0}", e);
         }
      }

      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setConnectTimeout(connectTimeOut);
      conn.setReadTimeout(readTimeOut);
      conn.connect();
      int resCode = conn.getResponseCode();
      String statusCode = Integer.toString(resCode);
      LOGGER.log(Level.FINE, "Recibido: {0}: {1}", new Object[]{resCode, conn.getResponseMessage()});
      if (!statusCode.startsWith("4") && !statusCode.startsWith("5")) {
         try (InputStream is = conn.getInputStream()) {
            byte[] data = Utils.getDataFromInputStream(is);
            if (url.getProtocol().equals("https")) {
               enableSslChecks();
            }

            return data;
         }
      } else {
         if (url.getProtocol().equals("https")) {
            enableSslChecks();
         }

         throw new HttpError(resCode, conn.getResponseMessage(), urlString);
      }
   }

   public static void disableSslChecks() throws NoSuchAlgorithmException, KeyManagementException {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, DUMMY_TRUST_MANAGER, new SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HttpsURLConnection.setDefaultHostnameVerifier(ALL_HOSTNAME_VERIFIER);
      System.setProperty("jsse.enableSNIExtension", "false");
   }

   public static void enableSslChecks() {
      HttpsURLConnection.setDefaultSSLSocketFactory(DEFAULT_SSL_SOCKET_FACTORY);
      HttpsURLConnection.setDefaultHostnameVerifier(DEFAULT_HOSTNAME_VERIFIER);
   }
}
