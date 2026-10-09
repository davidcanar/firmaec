package ec.gob.firmadigital.libreria.certificate.ec.lazzate.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalLazzate extends CertificadoExtImplLazzate implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalLazzate(X509Certificate certificado) {
      super(certificado);
   }
}
