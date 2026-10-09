package ec.gob.firmadigital.libreria.sign;

import java.security.Key;

public enum EncryptionAlgorithm {
   RSA("RSA");

   private String name;

   EncryptionAlgorithm(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public static EncryptionAlgorithm forKey(Key key) {
      return forName(key.getAlgorithm());
   }

   public static EncryptionAlgorithm forName(String name) {
      for (EncryptionAlgorithm encryptionAlgo : values()) {
         if (encryptionAlgo.getName().equals(name) || encryptionAlgo.name().equals(name)) {
            return encryptionAlgo;
         }
      }

      throw new IllegalArgumentException("Unsupported algorithm: " + name);
   }
}
