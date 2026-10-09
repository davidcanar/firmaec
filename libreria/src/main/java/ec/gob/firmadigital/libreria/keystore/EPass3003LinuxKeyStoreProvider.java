package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class EPass3003LinuxKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE_32_BITS = "/opt/SecurityData_Linux/redist/i386/libshuttle_p11v220.so.1.0.0";
   private static final String DRIVER_FILE_64_BITS = "/opt/SecurityData_Linux/redist/x86_64/libshuttle_p11v220.so.1.0.0";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = is64bit()
         ? new File("/opt/SecurityData_Linux/redist/x86_64/libshuttle_p11v220.so.1.0.0")
         : new File("/opt/SecurityData_Linux/redist/i386/libshuttle_p11v220.so.1.0.0");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return is64bit()
         ? System.getProperty("user.home") + "/cfg/EPass3003LinuxKeyStoreProvider64Bits.cfg"
         : System.getProperty("user.home") + "/cfg/EPass3003LinuxKeyStoreProvider32Bits.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=ePass3003\n");
      config.append("library=")
         .append(
            is64bit() ? "/opt/SecurityData_Linux/redist/x86_64/libshuttle_p11v220.so.1.0.0" : "/opt/SecurityData_Linux/redist/i386/libshuttle_p11v220.so.1.0.0"
         );
      CONFIG = config.toString();
   }
}
