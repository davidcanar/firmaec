package ec.gob.firmadigital.libreria.exceptions;

import java.net.URL;

public class OcspTimeoutException extends Exception {
   private String url;

   public OcspTimeoutException(Exception e, URL url) {
      super(e);
      this.url = url.toString();
   }

   public String getUrl() {
      return this.url;
   }
}
