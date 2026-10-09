package ec.gob.firmadigital.libreria.certificate.ec.firmasegura.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjPersonaNaturalFirmaSegura extends CertificadoSubjImpl implements CertificadoPersonaNatural {
   public CertificadoSubjPersonaNaturalFirmaSegura(X509Certificate certificado) {
      super(certificado);
   }
}
