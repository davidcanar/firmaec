package ec.gob.firmadigital.libreria.certificate.ec.appfirmas;

public class CertificadoAppFirmas {
   private CertificadoAppFirmas() {
   }

   public static final class Subj {
      public static final String OID_TIPO_PERSONA_NATURAL = "1.3.6.1.4.1.62431.2.1.1";
      public static final String OID_TIPO_MIEMBRO_EMPRESA = "1.3.6.1.4.1.62431.2.2.1";
      public static final String OID_TIPO_REPRESENTANTE_LEGAL = "1.3.6.1.4.1.62431.2.3.1";

      private Subj() {
      }
   }
}
