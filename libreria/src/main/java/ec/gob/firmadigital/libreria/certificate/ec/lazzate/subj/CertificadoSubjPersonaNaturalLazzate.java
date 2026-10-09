package ec.gob.firmadigital.libreria.certificate.ec.lazzate.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjPersonaNaturalLazzate extends CertificadoSubjImpl implements CertificadoPersonaNatural {
   public CertificadoSubjPersonaNaturalLazzate(X509Certificate certificado) {
      super(certificado);
   }
}
