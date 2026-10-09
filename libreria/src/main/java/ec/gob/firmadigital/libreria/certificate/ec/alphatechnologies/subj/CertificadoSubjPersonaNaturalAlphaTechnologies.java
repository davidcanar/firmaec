package ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjPersonaNaturalAlphaTechnologies extends CertificadoSubjImpl implements CertificadoPersonaNatural {
   public CertificadoSubjPersonaNaturalAlphaTechnologies(X509Certificate certificado) {
      super(certificado);
   }
}
