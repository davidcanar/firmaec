package ec.gob.firmadigital.libreria.utils;

public class OsUtils {
   public static boolean isWindows() {
      String osName = System.getProperty("os.name");
      return osName.toUpperCase().indexOf("WINDOWS") == 0;
   }

   public static boolean isMac() {
      String osName = System.getProperty("os.name");
      return osName.toUpperCase().contains("MAC");
   }

   public static String getOs() {
      String osName = System.getProperty("os.name");
      return osName.toUpperCase();
   }

   public static String getNameOs(String osName) {
      if (osName.toUpperCase().indexOf("WINDOWS") == 0) {
         osName = "WINDOWS";
      } else if (osName.toUpperCase().contains("MAC")) {
         osName = "MAC";
      } else if (osName.toUpperCase().contains("LINUX")) {
         osName = "LINUX";
      } else if (osName.toUpperCase().contains("ANDROID ")) {
         osName = "ANDROID";
      } else if (osName.toUpperCase().contains("IOS ")) {
         osName = "IOS";
      }

      return osName;
   }

   public static String getJavaVersion() {
      String javaVersion = System.getProperty("java.version");
      return javaVersion.toUpperCase();
   }

   public static boolean is64Bits() {
      return System.getProperty("os.arch").equals("xmd64");
   }
}
