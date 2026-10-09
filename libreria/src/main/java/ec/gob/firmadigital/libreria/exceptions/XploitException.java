package ec.gob.firmadigital.libreria.exceptions;

public class XploitException extends Exception {
   public XploitException() {
   }

   public XploitException(String message) {
      super(message);
   }

   public XploitException(String msg, Throwable cause) {
      super(msg, cause);
   }

   public XploitException(Throwable cause) {
      super(cause);
   }
}
