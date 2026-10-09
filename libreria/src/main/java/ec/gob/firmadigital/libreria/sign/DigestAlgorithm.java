package ec.gob.firmadigital.libreria.sign;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public enum DigestAlgorithm {
   SHA256("SHA256", "SHA-256"),
   SHA512("SHA512", "SHA-512");

   private final String name;
   private final String javaName;

   DigestAlgorithm(String name, String javaName) {
      this.name = name;
      this.javaName = javaName;
   }

   public String getName() {
      return this.name;
   }

   public String getJavaName() {
      return this.javaName;
   }

   public static DigestAlgorithm forName(String name) {
      DigestAlgorithm algorithm = DigestAlgorithm.Registry.ALGORITHMS.get(name);
      if (algorithm == null) {
         throw new IllegalArgumentException("Unsupported algorithm: " + name);
      } else {
         return algorithm;
      }
   }

   public static boolean isSupportedAlgorithm(String name) {
      return DigestAlgorithm.Registry.ALGORITHMS.get(name) != null;
   }

   public static DigestAlgorithm forJavaName(String javaName) {
      DigestAlgorithm algorithm = DigestAlgorithm.Registry.JAVA_ALGORITHMS.get(javaName);
      if (algorithm == null) {
         throw new IllegalArgumentException("Unsupported algorithm: " + javaName);
      } else {
         return algorithm;
      }
   }

   public MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
      return MessageDigest.getInstance(this.javaName);
   }

   private static class Registry {
      private static final Map<String, DigestAlgorithm> ALGORITHMS = registerAlgorithms();
      private static final Map<String, DigestAlgorithm> JAVA_ALGORITHMS = registerJavaAlgorithms();

      private static Map<String, DigestAlgorithm> registerAlgorithms() {
         Map<String, DigestAlgorithm> map = new HashMap<>();

         for (DigestAlgorithm digestAlgorithm : DigestAlgorithm.values()) {
            map.put(digestAlgorithm.name, digestAlgorithm);
         }

         return map;
      }

      private static Map<String, DigestAlgorithm> registerJavaAlgorithms() {
         Map<String, DigestAlgorithm> map = new HashMap<>();

         for (DigestAlgorithm digestAlgorithm : DigestAlgorithm.values()) {
            map.put(digestAlgorithm.javaName, digestAlgorithm);
         }

         return map;
      }
   }
}
