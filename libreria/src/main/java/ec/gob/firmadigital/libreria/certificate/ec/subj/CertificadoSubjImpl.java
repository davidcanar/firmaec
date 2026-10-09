package ec.gob.firmadigital.libreria.certificate.ec.subj;

import ec.gob.firmadigital.libreria.certificate.Certificado;
import java.security.cert.X509Certificate;

public class CertificadoSubjImpl extends Certificado {
   public CertificadoSubjImpl(X509Certificate certificado) {
      super(certificado);
      super.cargarSubjectName();
   }

   public String getCedulaPasaporte() {
      return this.getSubjectField("2.5.4.5");
   }

   public String getNombres() {
      return this.getSubjectField("2.5.4.42");
   }

   public String getPrimerApellido() {
      return this.getSubjectField("2.5.4.4");
   }

   public String getSegundoApellido() {
      return "";
   }

   public String getDireccion() {
      return "";
   }

   public String getTelefono() {
      return "";
   }

   public String getCiudad() {
      return this.getSubjectField("2.5.4.7");
   }

   public String getPais() {
      return this.getSubjectField("2.5.4.6");
   }

   public String getRuc() {
      return this.getSubjectField("2.5.4.97");
   }
}
