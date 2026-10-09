package ec.gob.firmadigital.libreria.certificate.ec.uanataca.ext;

import ec.gob.firmadigital.libreria.certificate.Certificado;
import java.security.cert.X509Certificate;

public class CertificadoExtImplUanataca extends Certificado {
   public CertificadoExtImplUanataca(X509Certificate certificado) {
      super(certificado);
   }

   public String getCedulaPasaporte() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.47286.102.3.1");
   }

   public String getNombres() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.47286.102.3.2");
   }

   public String getPrimerApellido() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.47286.102.3.3");
   }

   public String getSegundoApellido() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.47286.102.3.4");
   }

   public String getDireccion() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.47286.102.3.7");
   }

   public String getTelefono() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.47286.102.3.8");
   }

   public String getCiudad() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.47286.102.3.9");
   }

   public String getPais() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.47286.102.3.12");
   }

   public String getRuc() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.47286.102.3.11");
   }
}
