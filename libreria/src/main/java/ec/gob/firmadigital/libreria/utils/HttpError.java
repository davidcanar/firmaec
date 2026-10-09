package ec.gob.firmadigital.libreria.utils;

import java.io.IOException;

class HttpError extends IOException {
   private int responseCode;
   private String responseDescription;

   HttpError(int resCode) {
      super("Error en conexion HTTP con codigo de respuesta " + resCode);
      this.responseCode = resCode;
      this.responseDescription = null;
   }

   public HttpError(int resCode, String resDescription, String url) {
      super("Error en conexion HTTP con codigo de respuesta " + resCode + " y descripcion '" + resDescription + "' para la direccion: " + url);
      this.responseCode = resCode;
      this.responseDescription = resDescription;
   }

   public int getResponseCode() {
      return this.responseCode;
   }

   public String getResponseDescription() {
      return this.responseDescription;
   }
}
