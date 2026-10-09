package ec.gob.firmadigital.libreria.keystore;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

public class WindowsKeyStoreProvider implements KeyStoreProvider {
   private static final Logger LOGGER = Logger.getLogger(WindowsKeyStoreProvider.class.getName());

   @Override
   public KeyStore getKeystore() throws KeyStoreException {
      try {
         KeyStore keyStore = KeyStore.getInstance("Windows-MY");
         keyStore.load(null, null);
         if (KeyStoreUtilities.tieneAliasRepetidos(keyStore)) {
            LOGGER.fine("El KeyStore tiene alias repetidos, fixing...");
            KeyStoreUtilities.fixAliases(keyStore);
         }

         return keyStore;
      } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
         throw new KeyStoreException(e);
      }
   }

   @Override
   public KeyStore getKeystore(char[] ignore) throws KeyStoreException {
      return this.getKeystore();
   }
}
