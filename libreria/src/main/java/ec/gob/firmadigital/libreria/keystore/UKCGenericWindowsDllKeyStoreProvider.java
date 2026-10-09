package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class UKCGenericWindowsDllKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE = "C:\\windows\\system32\\bit4p11.dll";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = new File("C:\\windows\\system32\\bit4p11.dll");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return System.getProperty("user.home") + "/cfg/UKCGenericWindowsDllKeyStoreProvider.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=UKCGeneric\n");
      config.append("library=C:\\windows\\system32\\bit4p11.dll");
      CONFIG = config.toString();
   }
}
