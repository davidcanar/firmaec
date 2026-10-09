package ec.gob.firmadigital.libreria.certificate.ec.datil.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaJuridicaPrivadaDatil extends CertificadoExtImplDatil implements CertificadoPersonaJuridica {
   public CertificadoExtPersonaJuridicaPrivadaDatil(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.52643.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.52643.3.5");
   }
}
