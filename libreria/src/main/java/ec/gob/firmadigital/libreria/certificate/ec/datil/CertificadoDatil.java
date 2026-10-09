package ec.gob.firmadigital.libreria.certificate.ec.datil;

public class CertificadoDatil {
   private CertificadoDatil() {
   }

   public static final class Ext {
      public static final String OID_TIPO_PERSONA_NATURAL = "1.3.6.1.4.1.52643.2.5.1";
      public static final String OID_TIPO_PERSONA_JURIDICA = "1.3.6.1.4.1.52643.2.5.2";
      public static final String OID_CEDULA_PASAPORTE = "1.3.6.1.4.1.52643.3.1";
      public static final String OID_NOMBRES = "1.3.6.1.4.1.52643.3.2";
      public static final String OID_APELLIDO_1 = "1.3.6.1.4.1.52643.3.3";
      public static final String OID_APELLIDO_2 = "1.3.6.1.4.1.52643.3.4";
      public static final String OID_CARGO = "1.3.6.1.4.1.52643.3.5";
      public static final String OID_DIRECCION = "1.3.6.1.4.1.52643.3.7";
      public static final String OID_TELEFONO = "1.3.6.1.4.1.52643.3.8";
      public static final String OID_CIUDAD = "1.3.6.1.4.1.52643.3.9";
      public static final String OID_RAZON_SOCIAL = "1.3.6.1.4.1.52643.3.10";
      public static final String OID_RUC = "1.3.6.1.4.1.52643.3.11";
      public static final String OID_PAIS = "1.3.6.1.4.1.52643.3.12";
      public static final String OID_PROFESION = "1.3.6.1.4.1.52643.3.30";
      public static final String OID_NUMERO_FACTURA = "1.3.6.1.4.1.52643.3.32";

      private Ext() {
      }
   }

   public static final class Subj {
      public static final String OID_TIPO_PERSONA_NATURAL = "1.3.6.1.4.1.52643.2.1.1";
      public static final String OID_TIPO_MIEMBRO_EMPRESA = "1.3.6.1.4.1.52643.2.2.1";
      public static final String OID_TIPO_REPRESENTANTE_LEGAL = "1.3.6.1.4.1.52643.2.3.1";
      public static final String OID_TIPO_SELLO_ELECTRONICO = "1.3.6.1.4.1.52643.102.2.4.1";
      public static final String OID_TIPO_SELLO_TIEMPO = "1.3.6.1.4.1.52643.102.2.5.1";

      private Subj() {
      }
   }
}
