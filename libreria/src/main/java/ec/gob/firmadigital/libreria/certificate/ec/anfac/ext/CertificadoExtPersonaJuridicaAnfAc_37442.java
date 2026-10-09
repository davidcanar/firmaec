package ec.gob.firmadigital.libreria.certificate.ec.anfac.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaJuridicaAnfAc_37442 extends CertificadoExtImplAnfAc_37442 implements CertificadoPersonaJuridica {
   public CertificadoExtPersonaJuridicaAnfAc_37442(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.37442.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.37442.3.5");
   }
}
