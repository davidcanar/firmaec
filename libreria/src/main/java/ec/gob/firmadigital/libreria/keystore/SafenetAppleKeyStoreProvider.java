package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class SafenetAppleKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE = "/usr/local/lib/libeTPkcs11.dylib";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = new File("/usr/local/lib/libeTPkcs11.dylib");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return System.getProperty("user.home") + "/cfg/SafenetAppleKeyStoreProvider.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=Safenet\n");
      config.append("library=/usr/local/lib/libeTPkcs11.dylib");
      CONFIG = config.toString();
   }
}
