package ec.gob.firmadigital.libreria.exceptions;

public class RubricaRuntimeException extends RuntimeException {
   public RubricaRuntimeException() {
   }

   public RubricaRuntimeException(String message) {
      super(message);
   }

   public RubricaRuntimeException(Throwable cause) {
      super(cause);
   }

   public RubricaRuntimeException(String message, Throwable cause) {
      super(message, cause);
   }
}
