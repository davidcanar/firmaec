package ec.gob.firmadigital.libreria.core;

import java.security.PrivateKey;
import java.security.cert.Certificate;

/** @deprecated */
public class PrivateKeyAndCertificateChain {
   private final String alias;
   private final PrivateKey privateKey;
   private final Certificate[] certificateChain;

   public PrivateKeyAndCertificateChain(String alias, PrivateKey privateKey, Certificate[] certificateChain) {
      this.alias = alias;
      this.privateKey = privateKey;
      this.certificateChain = certificateChain;
   }

   public String getAlias() {
      return this.alias;
   }

   public PrivateKey getPrivateKey() {
      return this.privateKey;
   }

   public Certificate[] getCertificateChain() {
      return this.certificateChain;
   }

   @Override
   public String toString() {
      return this.alias;
   }
}
