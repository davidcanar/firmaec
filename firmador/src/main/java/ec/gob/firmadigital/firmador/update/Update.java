package ec.gob.firmadigital.firmador.update;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Update {
   private static final int TIME_OUT = 10000;
   private static final String JAR_BASE_URL = "https://mirror.cedia.org.ec/firmaec/";
   private static final String FIRMADOR_JAR_NAME = "firmador-jar-with-dependencies.jar";
   private static final String CLIENTE_JAR_NAME = "cliente-jar-with-dependencies.jar";
   private static String ambiente;
   private static String firmador_jar_url;
   private static String firmador_jar_sha256_url;
   private static String cliente_jar_url;
   private static String cliente_jar_sha256_url;
   private static final int BUFFER_SIZE = 8192;
   private static final Logger LOGGER = Logger.getLogger(Update.class.getName());
   private static final char[] HEXCODE = "0123456789ABCDEF".toCharArray();

   public static void setAmbiente(String ambiente) {
      Update.ambiente = ambiente;
      generarURL();
   }

   private static void generarURL() {
      firmador_jar_url = "https://mirror.cedia.org.ec/firmaec//" + (ambiente.equals("--updatedes") ? "des_" : "") + "firmador-jar-with-dependencies.jar";
      firmador_jar_sha256_url = "https://mirror.cedia.org.ec/firmaec//"
         + (ambiente.equals("--updatedes") ? "des_" : "")
         + "firmador-jar-with-dependencies.jar.sha256";
      cliente_jar_url = "https://mirror.cedia.org.ec/firmaec//" + (ambiente.equals("--updatedes") ? "des_" : "") + "cliente-jar-with-dependencies.jar";
      cliente_jar_sha256_url = "https://mirror.cedia.org.ec/firmaec//"
         + (ambiente.equals("--updatedes") ? "des_" : "")
         + "cliente-jar-with-dependencies.jar.sha256";
   }

   public File actualizarFirmador() throws IllegalArgumentException {
      String path = this.rutaJar();
      LOGGER.log(Level.INFO, "path={0}", path);
      File file = new File(path);
      LOGGER.log(Level.INFO, "file={0}; canWrite={1};file.getName()={2}", new Object[]{file.getAbsolutePath(), file.canWrite(), file.getName()});
      if (file.canWrite()) {
         return file;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public File actualizarCliente() throws IllegalArgumentException {
      String path = this.rutaJar();
      LOGGER.log(Level.INFO, "path={0}", path);
      File file = new File(path);
      String firmaDigitalJar = file.getParent() + File.separator + "cliente-jar-with-dependencies.jar";
      File firmaDigital = new File(firmaDigitalJar);
      LOGGER.log(
         Level.INFO, "file={0}; canWrite={1};file.getName()={2}", new Object[]{firmaDigital.getAbsolutePath(), firmaDigital.canWrite(), firmaDigital.getName()}
      );
      if (firmaDigital.canWrite()) {
         return firmaDigital;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String update() throws IOException {
      String hashBajado = new String(this.download(firmador_jar_sha256_url, true));
      String hash = hashBajado.split("\\s")[0];
      File file = new File(this.rutaJar());
      byte[] actualJar = Files.readAllBytes(file.toPath());
      String actualHash = this.generateHash(actualJar);
      String mensaje = null;
      if (!actualHash.equals(hash)) {
         mensaje = "Se encuentra disponible una actualización.<br>Mayor información en el manual de usuario disponible en ";
      }

      return mensaje;
   }

   public void updateFirmador(File file) throws IOException {
      String hashBajado = new String(this.download(firmador_jar_sha256_url, false));
      LOGGER.log(Level.INFO, "hashBajado={0}", hashBajado);
      String hash = hashBajado.split("\\s")[0];
      byte[] actualJar = Files.readAllBytes(file.toPath());
      String actualHash = this.generateHash(actualJar);
      LOGGER.log(Level.INFO, "actualHash={0}", actualHash);
      if (actualHash.equals(hash)) {
         LOGGER.info("Ya tiene el ultimo archivo!");
      } else {
         LOGGER.info("No tiene la ultima version, descargando...");
         byte[] jar = this.download(firmador_jar_url, false);
         if (!this.verifyHash(hash, jar)) {
            LOGGER.severe("ERROR de verificacion de hash");
         } else {
            LOGGER.info("Hash comprobado OK");
            if (!file.getName().equals("firmador-jar-with-dependencies.jar")) {
               LOGGER.severe("El nombre del archivo no es firmador-jar-with-dependencies.jar");
            } else if (!file.canWrite()) {
               LOGGER.severe("No se puede actualizar el archivo");
            } else {
               try (FileOutputStream fileOuputStream = new FileOutputStream(file)) {
                  fileOuputStream.write(jar);
                  LOGGER.info("Actualizado con exito!!!");
               }
            }
         }
      }
   }

   public void updateCliente(File file) throws IOException {
      String hashBajado = new String(this.download(cliente_jar_sha256_url, false));
      LOGGER.log(Level.INFO, "hashBajado={0}", hashBajado);
      String hash = hashBajado.split("\\s")[0];
      byte[] actualJar = Files.readAllBytes(file.toPath());
      String actualHash = this.generateHash(actualJar);
      LOGGER.log(Level.INFO, "actualHash={0}", actualHash);
      if (actualHash.equals(hash)) {
         LOGGER.info("Ya tiene el ultimo archivo!");
      } else {
         LOGGER.info("No tiene la ultima version, descargando...");
         byte[] jar = this.download(cliente_jar_url, false);
         if (!this.verifyHash(hash, jar)) {
            LOGGER.severe("ERROR de verificacion de hash");
         } else {
            LOGGER.info("Hash comprobado OK");
            if (!file.getName().equals("cliente-jar-with-dependencies.jar")) {
               LOGGER.severe("El nombre del archivo no es cliente-jar-with-dependencies.jar");
            } else if (!file.canWrite()) {
               LOGGER.severe("No se puede actualizar el archivo");
            } else {
               try (FileOutputStream fileOuputStream = new FileOutputStream(file)) {
                  fileOuputStream.write(jar);
                  LOGGER.info("Actualizado con exito!!!");
               }
            }
         }
      }
   }

   private byte[] download(String strUrl, boolean controlTiempo) throws IOException {
      URL url = new URL(strUrl);
      HttpURLConnection con = (HttpURLConnection)url.openConnection();
      if (controlTiempo) {
         con.setConnectTimeout(10000);
      }

      int responseCode = con.getResponseCode();
      if (responseCode >= 300 && responseCode < 400) {
         con = (HttpURLConnection)new URL(con.getHeaderField("Location")).openConnection();
         if (controlTiempo) {
            con.setConnectTimeout(10000);
         }

         responseCode = con.getResponseCode();
      }

      if (responseCode >= 400) {
         throw new RuntimeException("Failed : HTTP error code : " + responseCode);
      }

      byte[] buffer = new byte[8192];
      long size = con.getContentLength();
      LOGGER.log(Level.INFO, "size={0}", size);

      try (
         InputStream in = con.getInputStream();
         ByteArrayOutputStream out = new ByteArrayOutputStream();
      ) {
         int count;
         while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
         }

         return out.toByteArray();
      }
   }

   private boolean verifyHash(String hash, byte[] jar) {
      return hash.equals(this.generateHash(jar));
   }

   private String generateHash(byte[] jar) {
      try {
         LOGGER.info("Hashing...");
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         md.update(jar);
         byte[] digest = md.digest();
         return printHexBinary(digest).toLowerCase();
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e);
      }
   }

   private String rutaJar() {
      try {
         return Update.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
      } catch (URISyntaxException e) {
         throw new RuntimeException(e);
      }
   }

   private static String printHexBinary(byte[] data) {
      StringBuilder r = new StringBuilder(data.length * 2);

      for (byte b : data) {
         r.append(HEXCODE[b >> 4 & 15]);
         r.append(HEXCODE[b & 15]);
      }

      return r.toString();
   }
}
