package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class EPass2003AppleKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE = "/usr/local/lib/SecurityDataCsp11_2003auto.dylib";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = new File("/usr/local/lib/SecurityDataCsp11_2003auto.dylib");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return System.getProperty("user.home") + "/cfg/EPass2003AppleKeyStoreProvider.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=ePass2003\n");
      config.append("library=/usr/local/lib/SecurityDataCsp11_2003auto.dylib");
      CONFIG = config.toString();
   }
}
