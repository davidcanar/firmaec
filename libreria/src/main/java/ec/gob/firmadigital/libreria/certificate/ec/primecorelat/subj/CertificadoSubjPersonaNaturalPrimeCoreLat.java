package ec.gob.firmadigital.libreria.certificate.ec.primecorelat.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjPersonaNaturalPrimeCoreLat extends CertificadoSubjImpl implements CertificadoPersonaNatural {
   public CertificadoSubjPersonaNaturalPrimeCoreLat(X509Certificate certificado) {
      super(certificado);
   }
}
