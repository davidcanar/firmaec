package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class Bit4idGenericLinuxKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE = "/usr/lib/bit4id/libbit4xpki.so";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = new File("/usr/lib/bit4id/libbit4xpki.so");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return System.getProperty("user.home") + "/cfg/Bit4idGenericLinuxKeyStoreProvider.cfg";
   }

   static {
      StringBuilder sb = new StringBuilder();
      sb.append("name=Bit4Id\n");
      sb.append("library=").append("/usr/lib/bit4id/libbit4xpki.so").append("\n");
      CONFIG = sb.toString();
   }
}
