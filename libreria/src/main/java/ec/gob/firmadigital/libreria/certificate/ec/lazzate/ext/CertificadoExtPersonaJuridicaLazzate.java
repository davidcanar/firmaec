package ec.gob.firmadigital.libreria.certificate.ec.lazzate.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaJuridicaLazzate extends CertificadoExtImplLazzate implements CertificadoPersonaJuridica {
   public CertificadoExtPersonaJuridicaLazzate(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.59382.3.5");
   }
}
