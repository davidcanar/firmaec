package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class SafenetIKey2032LinuxKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE = "/usr/local/SafeNet/lib/libsfntpkcs11.so";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = new File("/usr/local/SafeNet/lib/libsfntpkcs11.so");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return System.getProperty("user.home") + "/cfg/SafenetIKey2032LinuxKeyStoreProvider.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=SafenetIKey2032\n");
      config.append("library=/usr/local/SafeNet/lib/libsfntpkcs11.so");
      CONFIG = config.toString();
   }
}
