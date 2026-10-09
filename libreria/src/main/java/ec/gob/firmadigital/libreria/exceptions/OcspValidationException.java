package ec.gob.firmadigital.libreria.exceptions;

import java.util.Date;

public class OcspValidationException extends Exception {
   private int revocationReason;
   private Date revocationTime;

   public OcspValidationException() {
   }

   public OcspValidationException(int revocationReason, Date revocationTime) {
      this.revocationReason = revocationReason;
      this.revocationTime = revocationTime;
   }

   public int getRevocationReason() {
      return this.revocationReason;
   }

   public Date getRevocationTime() {
      return this.revocationTime;
   }
}
