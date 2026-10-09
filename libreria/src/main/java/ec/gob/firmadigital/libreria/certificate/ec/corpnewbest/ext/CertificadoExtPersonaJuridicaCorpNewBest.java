package ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaJuridicaCorpNewBest extends CertificadoExtImplCorpNewBest implements CertificadoPersonaJuridica {
   public CertificadoExtPersonaJuridicaCorpNewBest(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.34380.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.34380.3.5");
   }
}
