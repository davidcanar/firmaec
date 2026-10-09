package ec.gob.firmadigital.libreria.sign;

import java.security.GeneralSecurityException;

public interface RubricaSigner {
   DigestAlgorithm getDigestAlgorithm();

   EncryptionAlgorithm getEncryptionAlgorithm();

   byte[] sign(byte[] var1) throws GeneralSecurityException;
}
