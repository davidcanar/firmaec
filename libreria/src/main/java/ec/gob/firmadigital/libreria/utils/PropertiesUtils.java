package ec.gob.firmadigital.libreria.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesUtils {
   private static final Logger LOGGER = Logger.getLogger(PropertiesUtils.class.getName());
   private static final String MESSAGES = "messages.rubrica.properties";
   private static final String CONFIG = "config.rubrica.properties";
   private static volatile Properties messages;
   private static volatile Properties config;

   public static Properties getMessages() {
      Properties result = messages;
      if (result == null) {
         synchronized (PropertiesUtils.class) {
            result = messages;
            if (result == null) {
               result = cargar("messages.rubrica.properties");
               messages = result;
            }
         }
      }

      return result;
   }

   public static Properties getConfig() {
      Properties result = config;
      if (result == null) {
         synchronized (PropertiesUtils.class) {
            result = config;
            if (result == null) {
               result = cargar("config.rubrica.properties");
               config = result;
            }
         }
      }

      return result;
   }

   private static Properties cargar(String recurso) {
      Properties properties = new Properties();

      try (InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream(recurso)) {
         if (is != null) {
            properties.load(is);
         } else {
            LOGGER.log(Level.SEVERE, "No se encontro el recurso {0} en el classpath", recurso);
         }
      } catch (IOException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      }

      return properties;
   }

   public static String versionBase64() {
      String jsonVersion = Json.generarJsonVersion(OsUtils.getOs(), "LIBRERIA", getConfig().getProperty("version"));
      return Base64.getEncoder().encodeToString(jsonVersion.getBytes());
   }
}
