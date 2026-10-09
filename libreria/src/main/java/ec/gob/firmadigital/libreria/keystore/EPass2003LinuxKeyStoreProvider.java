package ec.gob.firmadigital.libreria.keystore;

import java.io.File;

public class EPass2003LinuxKeyStoreProvider extends PKCS11KeyStoreProvider {
   private static final String CONFIG;
   private static final String DRIVER_FILE_32_BITS = "/opt/ePass2003_SecurityData_20151225/i386/redist/SecurityDataCsp11_2003auto.so";
   private static final String DRIVER_FILE_64_BITS = "/opt/ePass2003_SecurityData_20151225/x86_64/redist/SecurityDataCsp11_2003auto.so";

   @Override
   public String getConfig() {
      return CONFIG;
   }

   @Override
   public boolean existeDriver() {
      File driver = is64bit()
         ? new File("/opt/ePass2003_SecurityData_20151225/x86_64/redist/SecurityDataCsp11_2003auto.so")
         : new File("/opt/ePass2003_SecurityData_20151225/i386/redist/SecurityDataCsp11_2003auto.so");
      return driver.exists();
   }

   @Override
   public String getCfg() {
      return is64bit()
         ? System.getProperty("user.home") + "/cfg/EPass2003LinuxKeyStoreProvider64Bits.cfg"
         : System.getProperty("user.home") + "/cfg/EPass2003LinuxKeyStoreProvider32Bits.cfg";
   }

   static {
      StringBuilder config = new StringBuilder();
      config.append("name=ePass2003\n");
      config.append("library=")
         .append(
            is64bit()
               ? "/opt/ePass2003_SecurityData_20151225/x86_64/redist/SecurityDataCsp11_2003auto.so"
               : "/opt/ePass2003_SecurityData_20151225/i386/redist/SecurityDataCsp11_2003auto.so"
         );
      CONFIG = config.toString();
   }
}
