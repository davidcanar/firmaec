package ec.gob.firmadigital.libreria.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Download {
   private static final int TIME_OUT = 5000;
   private static final int BUFFER_SIZE = 8192;
   private static final Logger LOGGER = Logger.getLogger(Download.class.getName());

   public static void downloadCFG() {
      String path = System.getProperty("user.home") + "/cfg";
      File fileCfg = new File(path);
      if (!fileCfg.exists()) {
         try {
            byte[] zip = download(PropertiesUtils.getConfig().getProperty("cfg"), false);

            try (FileOutputStream fileOuputStream = new FileOutputStream(path + ".zip")) {
               fileOuputStream.write(zip);
               fileOuputStream.close();
            }

            File var14 = new File(path);
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(path + ".zip"));

            for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
               File newFile = newFile(var14, zipEntry);
               if (zipEntry.isDirectory()) {
                  if (!newFile.isDirectory() && !newFile.mkdirs()) {
                     LOGGER.log(Level.WARNING, "Failed to create directory {0}", newFile);
                     throw new IOException("Failed to create directory " + newFile);
                  }
               } else {
                  File parent = newFile.getParentFile();
                  if (!parent.isDirectory() && !parent.mkdirs()) {
                     LOGGER.log(Level.WARNING, "Failed to create directory {0}", parent);
                     throw new IOException("Failed to create directory " + parent);
                  }

                  FileOutputStream fos = new FileOutputStream(newFile);

                  int len;
                  while ((len = zis.read(buffer)) > 0) {
                     fos.write(buffer, 0, len);
                  }

                  LOGGER.log(Level.INFO, "Unzipping to {0}", newFile.getAbsolutePath());
                  fos.close();
               }
            }

            zis.closeEntry();
            zis.close();
            LOGGER.info("Directorio cfg integrado con éxito!");
            File fileZip = new File(path + ".zip");
            fileZip.delete();
            LOGGER.info("Fichero zip borrado");
         } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
         }
      }
   }

   public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
      File destFile = new File(destinationDir, zipEntry.getName());
      String destDirPath = destinationDir.getCanonicalPath();
      String destFilePath = destFile.getCanonicalPath();
      if (!destFilePath.startsWith(destDirPath + File.separator)) {
         throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
      } else {
         return destFile;
      }
   }

   private static byte[] download(String strUrl, boolean controlTiempo) throws IOException {
      URL url = new URL(strUrl);
      HttpURLConnection con = (HttpURLConnection)url.openConnection();
      if (controlTiempo) {
         con.setConnectTimeout(5000);
      }

      int responseCode = con.getResponseCode();
      if (responseCode >= 300 && responseCode < 400) {
         con = (HttpURLConnection)new URL(con.getHeaderField("Location")).openConnection();
         if (controlTiempo) {
            con.setConnectTimeout(5000);
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
}
