package ec.gob.firmadigital.libreria.sign;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;

public class PrivateKeySigner extends BaseSigner {
   private final PrivateKey privateKey;

   public PrivateKeySigner(PrivateKey privateKey, DigestAlgorithm digestAlgorithm) {
      super(digestAlgorithm, EncryptionAlgorithm.forName(privateKey.getAlgorithm()));
      this.privateKey = privateKey;
   }

   @Override
   public byte[] sign(byte[] message) throws GeneralSecurityException {
      try {
         Signature sig = this.getSignature();
         sig.initSign(this.privateKey);
         sig.update(message);
         return sig.sign();
      } catch (NoSuchAlgorithmException e) {
         throw new GeneralSecurityException(e);
      }
   }

   private Signature getSignature() throws NoSuchAlgorithmException {
      String algorithm = this.getDigestAlgorithm().getName() + "with" + this.getEncryptionAlgorithm().getName();
      return Signature.getInstance(algorithm);
   }
}
