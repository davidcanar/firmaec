package ec.gob.firmadigital.libreria.sign;

public abstract class BaseSigner implements RubricaSigner {
   protected DigestAlgorithm digestAlgorithm;
   protected EncryptionAlgorithm encryptionAlgorithm;

   protected BaseSigner(DigestAlgorithm digestAlgorithm, EncryptionAlgorithm encryptionAlgorithm) {
      this.digestAlgorithm = digestAlgorithm;
      this.encryptionAlgorithm = encryptionAlgorithm;
   }

   @Override
   public DigestAlgorithm getDigestAlgorithm() {
      return this.digestAlgorithm;
   }

   @Override
   public EncryptionAlgorithm getEncryptionAlgorithm() {
      return this.encryptionAlgorithm;
   }
}
