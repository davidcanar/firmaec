package ec.gob.firmadigital.libreria.certificate.ec.argosdata.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjPersonaNaturalArgosData extends CertificadoSubjImpl implements CertificadoPersonaNatural {
   public CertificadoSubjPersonaNaturalArgosData(X509Certificate certificado) {
      super(certificado);
   }
}
