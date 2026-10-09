package ec.gob.firmadigital.libreria.certificate.ec.lazzate.ext;

import ec.gob.firmadigital.libreria.certificate.Certificado;
import java.security.cert.X509Certificate;

public class CertificadoExtImplLazzate extends Certificado {
   public CertificadoExtImplLazzate(X509Certificate certificado) {
      super(certificado);
   }

   public String getCedulaPasaporte() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.1");
   }

   public String getNombres() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.2");
   }

   public String getPrimerApellido() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.3");
   }

   public String getSegundoApellido() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.4");
   }

   public String getDireccion() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.7");
   }

   public String getTelefono() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.8");
   }

   public String getCiudad() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.9");
   }

   public String getPais() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.12");
   }

   public String getRuc() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.11");
   }
}
