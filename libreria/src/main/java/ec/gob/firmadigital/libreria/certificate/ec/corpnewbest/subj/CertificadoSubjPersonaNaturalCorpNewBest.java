package ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjPersonaNaturalCorpNewBest extends CertificadoSubjImpl implements CertificadoPersonaNatural {
   public CertificadoSubjPersonaNaturalCorpNewBest(X509Certificate certificado) {
      super(certificado);
   }
}
