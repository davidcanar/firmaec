package ec.gob.firmadigital.libreria.keystore;

import ec.gob.firmadigital.libreria.utils.Download;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyStoreProviderFactory {
   private static final String WINDOWS = "WINDOWS";
   private static final String LINUX = "LINUX";
   private static final String MACOS = "MACOS";
   private static String tipoKeyStoreProvider;
   private static final Map<String, List<KeyStoreProvider>> lista = new HashMap<>();

   private static void listaWindows() {
      List<KeyStoreProvider> windows = new ArrayList<>();
      if (tipoKeyStoreProvider.equals("TOKEN")) {
         windows.add(new WindowsKeyStoreProvider());
         windows.add(new UKCGenericWindowsDllKeyStoreProvider());
      }

      lista.put("WINDOWS", windows);
   }

   private static void listaLinux() {
      Download.downloadCFG();
      List<KeyStoreProvider> linux = new ArrayList<>();
      if (tipoKeyStoreProvider.equals("TOKEN")) {
         linux.add(new SafenetIKey2032LinuxKeyStoreProvider());
         linux.add(new SafenetLinuxKeyStoreProvider());
         linux.add(new Bit4idLinuxKeyStoreProvider());
         linux.add(new Bit4idGenericLinuxKeyStoreProvider());
         linux.add(new EPass2003LinuxKeyStoreProvider());
         linux.add(new EPass3003LinuxKeyStoreProvider());
         linux.add(new UKCLinuxKeyStoreProvider());
      }

      lista.put("LINUX", linux);
   }

   private static void listaMac() {
      Download.downloadCFG();
      List<KeyStoreProvider> macOS = new ArrayList<>();
      if (tipoKeyStoreProvider.equals("TOKEN")) {
         macOS.add(new SafenetAppleKeyStoreProvider());
         macOS.add(new EPass2003AppleKeyStoreProvider());
         macOS.add(new EPass3003AppleKeyStoreProvider());
         macOS.add(new Bit4IdAppleKeyStoreProvider());
         macOS.add(new Bit4IdGenericAppleKeyStoreProvider());
         macOS.add(new UKCAppleKeyStoreProvider());
         macOS.add(new UKCGenericAppleKeyStoreProvider());
      }

      lista.put("MACOS", macOS);
   }

   public static List<KeyStoreProvider> getKeyStoreProviderList(String os) {
      return lista.get(os);
   }

   public static List<KeyStoreProvider> getKeyStoreProviderList() {
      listaWindows();
      listaLinux();
      listaMac();
      if (isWindows()) {
         return lista.get("WINDOWS");
      } else if (isLinux()) {
         return lista.get("LINUX");
      } else if (isMac()) {
         return lista.get("MACOS");
      } else {
         throw new RuntimeException("Sistema operativo no soportado");
      }
   }

   public static KeyStore getKeyStore(String clave, String tipoKeyStoreProvider) {
      KeyStoreProviderFactory.tipoKeyStoreProvider = tipoKeyStoreProvider;
      List<KeyStoreProvider> ksps = getKeyStoreProviderList();
      return getKeyStore(ksps, clave);
   }

   public static KeyStore getKeyStore(List<KeyStoreProvider> providers, String clave) {
      char[] password = clave != null ? clave.toCharArray() : null;
      KeyStore keyStore = null;
      KeyStoreException exception = null;

      for (KeyStoreProvider keyStoreProvider : providers) {
         try {
            if (keyStoreProvider instanceof PKCS11KeyStoreProvider pkcs11) {
               if (pkcs11.existeDriver()) {
                  keyStore = keyStoreProvider.getKeystore(password);
               }
            } else {
               keyStore = keyStoreProvider.getKeystore(password);
            }
         } catch (KeyStoreException e) {
            exception = e;
         }
      }

      if (keyStore != null) {
         return keyStore;
      } else if (exception != null) {
         return null;
      } else {
         throw new RuntimeException("Error al buscar token");
      }
   }

   public static boolean isLinux() {
      String osName = System.getProperty("os.name");
      return osName.toUpperCase().indexOf("LINUX") == 0;
   }

   public static boolean isWindows() {
      String osName = System.getProperty("os.name");
      return osName.toUpperCase().indexOf("WINDOWS") == 0;
   }

   public static boolean isMac() {
      String osName = System.getProperty("os.name");
      return osName.toUpperCase().indexOf("MAC OS X") == 0;
   }
}
