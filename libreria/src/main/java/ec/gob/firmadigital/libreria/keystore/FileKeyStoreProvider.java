package ec.gob.firmadigital.libreria.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

public class FileKeyStoreProvider implements KeyStoreProvider {
   private static final Logger LOGGER = Logger.getLogger(FileKeyStoreProvider.class.getName());
   private File keyStoreFile;
   private InputStream keyStoreFileIS;

   public FileKeyStoreProvider(File keyStoreFile) {
      this.keyStoreFile = keyStoreFile;
   }

   public FileKeyStoreProvider(InputStream keyStoreFileIS) {
      this.keyStoreFileIS = keyStoreFileIS;
   }

   public FileKeyStoreProvider(String keyStoreFile) {
      this.keyStoreFile = new File(keyStoreFile);
   }

   @Override
   public KeyStore getKeystore() throws KeyStoreException {
      return this.getKeystore(null);
   }

   @Override
   public KeyStore getKeystore(char[] password) throws KeyStoreException {
      InputStream input = null;

      try {
         if (this.keyStoreFile != null) {
            input = new FileInputStream(this.keyStoreFile);
         }

         if (this.keyStoreFileIS != null) {
            input = this.keyStoreFileIS;
         }

         KeyStore keyStore = KeyStore.getInstance("JKS");
         keyStore.load(input, password);
         return keyStore;
      } catch (FileNotFoundException e) {
         throw new KeyStoreException(e);
      } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
         throw new KeyStoreException(e);
      } finally {
         if (input != null) {
            try {
               input.close();
            } catch (IOException e) {
               LOGGER.warning(e.getMessage());
            }
         }
      }
   }
}
