package ec.gob.firmadigital.libreria.certificate.ec.uanataca.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjPersonaJuridicaUanataca extends CertificadoSubjImpl implements CertificadoPersonaJuridica {
   public CertificadoSubjPersonaJuridicaUanataca(X509Certificate certificado) {
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
