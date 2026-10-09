package ec.gob.firmadigital.libreria.exceptions;

public class InvalidFormatException extends RubricaException {
   public InvalidFormatException(String msg) {
      super(msg);
   }

   public InvalidFormatException(String msg, Exception e) {
      super(msg, e);
   }
}
