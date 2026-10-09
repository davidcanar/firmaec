package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class Bit4IdAppleKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE = "/Applications/FourIdentity/4identity.app/Contents/Resources/pkcs11/libbit4xpki.dylib";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = new File("/Applications/FourIdentity/4identity.app/Contents/Resources/pkcs11/libbit4xpki.dylib");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return System.getProperty("user.home") + "/cfg/Bit4IdAppleKeyStoreProvider.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=Bit4Id\n");
      config.append("library=/Applications/FourIdentity/4identity.app/Contents/Resources/pkcs11/libbit4xpki.dylib");
      CONFIG = config.toString();
   }
}
