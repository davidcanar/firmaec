package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class SafenetLinuxKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE_32_BITS = "/usr/lib/libeTPkcs11.so";
   private static final String DRIVER_FILE_64_BITS = "/usr/lib64/libeTPkcs11.so";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = is64bit() ? new File("/usr/lib64/libeTPkcs11.so") : new File("/usr/lib/libeTPkcs11.so");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return is64bit()
         ? System.getProperty("user.home") + "/cfg/SafenetLinuxKeyStoreProvider64Bits.cfg"
         : System.getProperty("user.home") + "/cfg/SafenetLinuxKeyStoreProvider32Bits.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=Safenet\n");
      config.append("library=").append(is64bit() ? "/usr/lib64/libeTPkcs11.so" : "/usr/lib/libeTPkcs11.so");
      CONFIG = config.toString();
   }
}
