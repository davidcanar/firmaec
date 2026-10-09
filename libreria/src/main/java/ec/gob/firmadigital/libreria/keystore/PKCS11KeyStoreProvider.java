package ec.gob.firmadigital.libreria.keystore;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;
import java.security.cert.CertificateException;

public abstract class PKCS11KeyStoreProvider implements KeyStoreProvider {
   private static final String SUN_PKCS11_PROVIDER_NAME = "SunPKCS11";

   public abstract String getConfig();

   public abstract boolean existeDriver();

   public abstract String getCfg();

   @Override
   public KeyStore getKeystore() throws KeyStoreException {
      return this.getKeystore(null);
   }

   @Override
   public KeyStore getKeystore(char[] password) throws KeyStoreException {
      try {
         Provider provider = Security.getProvider("SunPKCS11");
         provider = provider.configure(this.getCfg());
         Security.addProvider(provider);
         KeyStore keyStore = KeyStore.getInstance("PKCS11");
         keyStore.load(null, password);
         return keyStore;
      } catch (CertificateException | NoSuchAlgorithmException | IOException | ProviderException e) {
         throw new KeyStoreException(e);
      }
   }

   protected static boolean is64bit() {
      return System.getProperty("sun.arch.data.model").contains("64");
   }
}
