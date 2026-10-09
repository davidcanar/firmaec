package ec.gob.firmadigital.libreria.certificate.ec.digercic.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalDigercic extends CertificadoExtImplDigercic implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalDigercic(X509Certificate certificado) {
      super(certificado);
   }
}
