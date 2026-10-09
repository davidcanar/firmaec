package ec.gob.firmadigital.libreria.exceptions;

public class RubricaException extends Exception {
   public RubricaException() {
   }

   public RubricaException(String message) {
      super(message);
   }

   public RubricaException(String msg, Throwable cause) {
      super(msg, cause);
   }

   public RubricaException(Throwable cause) {
      super(cause);
   }
}
