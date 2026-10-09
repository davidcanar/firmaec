package ec.gob.firmadigital.libreria.sign;

public class SignValidity {
   private final SignValidity.SIGN_DETAIL_TYPE validity;
   private final SignValidity.VALIDITY_ERROR error;

   public SignValidity(SignValidity.SIGN_DETAIL_TYPE type, SignValidity.VALIDITY_ERROR error) {
      this.validity = type;
      this.error = error;
   }

   public SignValidity.SIGN_DETAIL_TYPE getValidity() {
      return this.validity;
   }

   public SignValidity.VALIDITY_ERROR getError() {
      return this.error;
   }

   public enum SIGN_DETAIL_TYPE {
      OK,
      FAIL;
   }

   public enum VALIDITY_ERROR {
      NO_DATA,
      CORRUPTED_SIGN,
      NO_MATCH_DATA,
      NO_SIGN,
      CERTIFICATE_PROBLEM,
      CERTIFICATE_EXPIRED,
      CERTIFICATE_NOT_VALID_YET,
      ALGORITHM_NOT_SUPPORTED,
      CA_NOT_SUPPORTED,
      CRL_PROBLEM,
      PDF_UNKOWN_VALIDITY,
      OOXML_UNKOWN_VALIDITY,
      ODF_UNKOWN_VALIDITY,
      UNKOWN_ERROR,
      UNKOWN_SIGNATURE_FORMAT;
   }
}
