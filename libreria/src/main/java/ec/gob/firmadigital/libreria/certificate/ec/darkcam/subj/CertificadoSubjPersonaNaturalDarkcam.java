package ec.gob.firmadigital.libreria.certificate.ec.darkcam.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjPersonaNaturalDarkcam extends CertificadoSubjImpl implements CertificadoPersonaNatural {
   public CertificadoSubjPersonaNaturalDarkcam(X509Certificate certificado) {
      super(certificado);
   }
}
