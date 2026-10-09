package ec.gob.firmadigital.libreria.certificate.ec.uanataca.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalUanataca extends CertificadoExtImplUanataca implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalUanataca(X509Certificate certificado) {
      super(certificado);
   }
}
