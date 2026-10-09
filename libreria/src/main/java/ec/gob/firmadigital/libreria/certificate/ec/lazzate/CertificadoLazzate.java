package ec.gob.firmadigital.libreria.certificate.ec.lazzate;

public class CertificadoLazzate {
   private CertificadoLazzate() {
   }

   public static final class Ext {
      public static final String OID_TIPO_PERSONA_NATURAL = "1.3.6.1.4.1.59382.2.1";
      public static final String OID_TIPO_PERSONA_JURIDICA_EMPRESA = "1.3.6.1.4.1.59382.2.2";
      public static final String OID_CEDULA_PASAPORTE = "1.3.6.1.4.1.59382.3.1";
      public static final String OID_NOMBRES = "1.3.6.1.4.1.59382.3.2";
      public static final String OID_APELLIDO_1 = "1.3.6.1.4.1.59382.3.3";
      public static final String OID_APELLIDO_2 = "1.3.6.1.4.1.59382.3.4";
      public static final String OID_CARGO = "1.3.6.1.4.1.59382.3.5";
      public static final String OID_INSTITUCION = "1.3.6.1.4.1.59382.3.6";
      public static final String OID_DIRECCION = "1.3.6.1.4.1.59382.3.7";
      public static final String OID_TELEFONO = "1.3.6.1.4.1.59382.3.8";
      public static final String OID_CIUDAD = "1.3.6.1.4.1.59382.3.9";
      public static final String OID_RAZON_SOCIAL = "1.3.6.1.4.1.59382.3.10";
      public static final String OID_RUC = "1.3.6.1.4.1.59382.3.11";
      public static final String OID_PAIS = "1.3.6.1.4.1.59382.3.12";

      private Ext() {
      }
   }

   public static final class Subj {
      public static final String OID_TIPO_PERSONA_NATURAL = "1.3.6.1.4.1.59382.2.1.1";
      public static final String OID_TIPO_MIEMBRO_EMPRESA = "1.3.6.1.4.1.59382.2.2.1";
      public static final String OID_TIPO_REPRESENTANTE_LEGAL = "1.3.6.1.4.1.59382.2.3.1";
      public static final String OID_TIPO_SELLO_ELECTRONICO = "1.3.6.1.4.1.59382.102.2.4.1";

      private Subj() {
      }
   }
}
