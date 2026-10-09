package ec.gob.firmadigital.libreria.certificate.ec.primecorelat.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjPersonaJuridicaPrimeCoreLat extends CertificadoSubjImpl implements CertificadoPersonaJuridica {
   public CertificadoSubjPersonaJuridicaPrimeCoreLat(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getSubjectField("2.5.4.10");
   }

   @Override
   public String getCargo() {
      return this.getSubjectField("2.5.4.12");
   }
}
