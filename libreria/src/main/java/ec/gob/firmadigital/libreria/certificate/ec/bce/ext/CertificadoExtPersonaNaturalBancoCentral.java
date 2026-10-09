package ec.gob.firmadigital.libreria.certificate.ec.bce.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalBancoCentral extends CertificadoExtImplBancoCentral implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalBancoCentral(X509Certificate certificado) {
      super(certificado);
   }
}
