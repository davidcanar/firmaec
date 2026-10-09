package ec.gob.firmadigital.libreria.certificate.ec.securitydata.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalSecurityData extends CertificadoExtImplSecurityData implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalSecurityData(X509Certificate certificado) {
      super(certificado);
   }
}
