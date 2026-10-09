package ec.gob.firmadigital.libreria.certificate.ec.anfac.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaJuridicaAnfAc_18332 extends CertificadoExtImplAnfAc_18332 implements CertificadoPersonaJuridica {
   public CertificadoExtPersonaJuridicaAnfAc_18332(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.18332.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.18332.3.5");
   }
}
