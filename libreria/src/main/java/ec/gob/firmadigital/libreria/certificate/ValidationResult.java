package ec.gob.firmadigital.libreria.certificate;

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

public enum ValidationResult {
   VALID(0),
   CORRUPT(1),
   CA_NOT_SUPPORTED(2),
   NOT_YET_VALID(3),
   EXPIRED(4),
   REVOKED(5),
   UNKNOWN(6),
   SERVER_ERROR(7),
   UNAUTHORIZED(8),
   MALFORMED_REQUEST(9),
   SIG_REQUIRED(10),
   CANNOT_DOWNLOAD_CRL(11);

   private static final int CODE_VALID = 0;
   private static final int CODE_CORRUPT = 1;
   private static final int CODE_CA_NOT_SUPPORTED = 2;
   private static final int CODE_NOT_YET_VALID = 3;
   private static final int CODE_EXPIRED = 4;
   private static final int CODE_REVOKED = 5;
   private static final int CODE_UNKNOWN = 6;
   private static final int CODE_SERVER_ERROR = 7;
   private static final int CODE_UNAUTHORIZED = 8;
   private static final int CODE_MALFORMED_REQUEST = 9;
   private static final int CODE_SIG_REQUIRED = 10;
   private static final int CODE_CANNOT_DOWNLOAD_CRL = 11;
   private final int resultCode;

   ValidationResult(int code) {
      if (code >= 0 && code <= 11) {
         this.resultCode = code;
      } else {
         throw new IllegalArgumentException("El codigo de resultado debe estar comprendido entre 0 y 11: " + code);
      }
   }

   public String toJsonString() {
      return "{\n" + "  \"result\": \"" + (this.isValid() ? "OK" : "KO") + "\",\n" + "  \"reason\": \"" + this.toString() + "\"\n" + "}";
   }

   public boolean isValid() {
      return this.resultCode == 0;
   }

   public void check() throws CertificateException {
      switch (this.resultCode) {
         case 0:
            return;
         case 1:
            throw new CertificateEncodingException();
         case 2:
            throw new CertificateException("El certificado no es de una CA soportada");
         case 3:
            throw new CertificateNotYetValidException();
         case 4:
            throw new CertificateExpiredException();
         case 5:
            throw new CertificateException("Certificado revocado");
         case 6:
            throw new CertificateException("Validez del certificado desconocida");
         case 7:
            throw new CertificateException("Error interno o del servidor al validar el certificado");
         case 8:
            throw new CertificateException("No autorizado");
         case 9:
            throw new CertificateException("Peticion OCSP mal formada");
         case 10:
            throw new CertificateException("La peticion OCSP no esta firmada");
         case 11:
            throw new CertificateException("No se ha podido descargar la lista de certificados revocados");
         default:
            throw new IllegalStateException("El codigo de resultado debe estar comprendido entre 0 y 11: " + this.resultCode);
      }
   }
}
