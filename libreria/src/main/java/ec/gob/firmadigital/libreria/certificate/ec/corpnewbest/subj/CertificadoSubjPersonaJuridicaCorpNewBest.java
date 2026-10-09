package ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjPersonaJuridicaCorpNewBest extends CertificadoSubjImpl implements CertificadoRepresentanteLegal {
   public CertificadoSubjPersonaJuridicaCorpNewBest(X509Certificate certificado) {
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
