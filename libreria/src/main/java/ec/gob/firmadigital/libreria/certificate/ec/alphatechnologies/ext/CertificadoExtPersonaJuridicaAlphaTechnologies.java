package ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaJuridicaAlphaTechnologies extends CertificadoExtImplAlphaTechnologies implements CertificadoPersonaJuridica {
   public CertificadoExtPersonaJuridicaAlphaTechnologies(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.56105.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.56105.3.5");
   }
}
