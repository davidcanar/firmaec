package ec.gob.firmadigital.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
   private static final String LOG_FILE_PATTERN = "%h/firmadigital.log";

   public static void initLogging() {
      try {
         Handler handler = new FileHandler("%h/firmadigital.log", 0, 1, true);
         handler.setFormatter(new SimpleFormatter());
         Logger.getLogger("").addHandler(handler);
      } catch (IOException e) {
         System.out.println("Error al inicializar el logging: " + e.getMessage());
      }
   }
}
