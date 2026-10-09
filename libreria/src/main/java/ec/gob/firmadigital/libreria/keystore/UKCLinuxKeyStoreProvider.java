package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class UKCLinuxKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE = System.getProperty("user.home") + "/.bit4id/UKC/pkcs11/linux64/libbit4p11.so";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = new File(DRIVER_FILE);
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return System.getProperty("user.home") + "/cfg/UKCLinuxKeyStoreProvider.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=UKCGeneric\n");
      config.append("library=" + DRIVER_FILE);
      CONFIG = config.toString();
   }
}
