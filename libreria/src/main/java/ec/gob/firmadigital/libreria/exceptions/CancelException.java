package ec.gob.firmadigital.libreria.exceptions;

public class CancelException extends Exception {
   public CancelException() {
   }

   public CancelException(String message) {
      super(message);
   }

   public CancelException(String msg, Throwable cause) {
      super(msg, cause);
   }

   public CancelException(Throwable cause) {
      super(cause);
   }
}
