package ec.gob.firmadigital.libreria.certificate.ec.cj.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaJuridicaPublicaConsejoJudicatura extends CertificadoExtImplConsejoJudicatura implements CertificadoPersonaJuridica {
   public CertificadoExtPersonaJuridicaPublicaConsejoJudicatura(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.43745.1.3.10");
   }

   @Override
   public String getCargo() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.43745.1.3.5");
   }
}
