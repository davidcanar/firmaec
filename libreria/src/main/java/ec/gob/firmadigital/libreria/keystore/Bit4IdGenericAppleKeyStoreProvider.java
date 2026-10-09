package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class Bit4IdGenericAppleKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE = "/Applications/PKIManager-bit4id.app/Contents/Resources/etc/libbit4xpki.dylib";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = new File("/Applications/PKIManager-bit4id.app/Contents/Resources/etc/libbit4xpki.dylib");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return System.getProperty("user.home") + "/cfg/Bit4IdGenericAppleKeyStoreProvider.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=Bit4IdGeneric\n");
      config.append("library=/Applications/PKIManager-bit4id.app/Contents/Resources/etc/libbit4xpki.dylib");
      CONFIG = config.toString();
   }
}
