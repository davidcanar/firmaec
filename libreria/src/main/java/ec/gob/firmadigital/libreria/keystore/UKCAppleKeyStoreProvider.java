package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class UKCAppleKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE = "/Applications/UKC/UKC.app/Contents/Resources/pkcs11/libbit4p11.so";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = new File("/Applications/UKC/UKC.app/Contents/Resources/pkcs11/libbit4p11.so");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return System.getProperty("user.home") + "/cfg/UKCAppleKeyStoreProvider.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=UKC\n");
      config.append("library=/Applications/UKC/UKC.app/Contents/Resources/pkcs11/libbit4p11.so");
      CONFIG = config.toString();
   }
}
