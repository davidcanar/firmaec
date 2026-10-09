package ec.gob.firmadigital.libreria.exceptions;

public class SignatureVerificationException extends Exception {
   public SignatureVerificationException() {
   }

   public SignatureVerificationException(String message) {
      super(message);
   }

   public SignatureVerificationException(Throwable cause) {
      super(cause);
   }

   public SignatureVerificationException(String message, Throwable cause) {
      super(message, cause);
   }
}
