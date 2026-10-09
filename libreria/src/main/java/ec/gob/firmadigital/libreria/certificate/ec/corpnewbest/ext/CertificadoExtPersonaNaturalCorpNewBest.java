package ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalCorpNewBest extends CertificadoExtImplCorpNewBest implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalCorpNewBest(X509Certificate certificado) {
      super(certificado);
   }
}
