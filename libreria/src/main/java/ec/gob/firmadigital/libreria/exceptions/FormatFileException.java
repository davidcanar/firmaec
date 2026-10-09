package ec.gob.firmadigital.libreria.exceptions;

public class FormatFileException extends RubricaException {
   public FormatFileException(String msg) {
      super(msg);
   }

   public FormatFileException(String msg, Throwable e) {
      super(msg, e);
   }
}
