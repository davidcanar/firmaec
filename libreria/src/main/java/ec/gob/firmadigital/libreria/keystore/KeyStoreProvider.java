package ec.gob.firmadigital.libreria.keystore;

import java.security.KeyStore;
import java.security.KeyStoreException;

public interface KeyStoreProvider {
   KeyStore getKeystore() throws KeyStoreException;

   KeyStore getKeystore(char[] var1) throws KeyStoreException;
}
