package ec.gob.firmadigital.libreria.sign;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class MessageDigestSigner extends BaseSigner {
   public MessageDigestSigner(DigestAlgorithm digestAlgorithm, EncryptionAlgorithm encryptionAlgorithm) {
      super(digestAlgorithm, encryptionAlgorithm);
   }

   @Override
   public byte[] sign(byte[] data) throws GeneralSecurityException {
      byte[] digest = this.digest(data);
      return this.signDigest(digest);
   }

   public abstract byte[] signDigest(byte[] var1) throws GeneralSecurityException;

   private byte[] digest(byte[] data) throws NoSuchAlgorithmException {
      MessageDigest messageDigest = this.digestAlgorithm.getMessageDigest();
      messageDigest.update(data);
      return messageDigest.digest();
   }
}
