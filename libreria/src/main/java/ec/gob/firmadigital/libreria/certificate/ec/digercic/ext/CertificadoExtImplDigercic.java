package ec.gob.firmadigital.libreria.certificate.ec.digercic.ext;

import ec.gob.firmadigital.libreria.certificate.Certificado;
import java.security.cert.X509Certificate;

public class CertificadoExtImplDigercic extends Certificado {
   public CertificadoExtImplDigercic(X509Certificate certificado) {
      super(certificado);
   }

   public String getCedulaPasaporte() {
      return this.getExtensionField("1.3.6.1.4.1.55519.1.1.1.5.2.1");
   }

   public String getNombres() {
      return this.getExtensionField("");
   }

   public String getPrimerApellido() {
      return this.getExtensionField("1.3.6.1.4.1.55519.1.1.1.5.2.3");
   }

   public String getSegundoApellido() {
      return this.getExtensionField("");
   }

   public String getDireccion() {
      return this.getExtensionField("");
   }

   public String getTelefono() {
      return this.getExtensionField("");
   }

   public String getCiudad() {
      return this.getExtensionField("");
   }

   public String getPais() {
      return this.getExtensionField("");
   }

   public String getRuc() {
      return this.getExtensionField("1.3.6.1.4.1.55519.1.1.1.5.2.4");
   }
}
