package ec.gob.firmadigital.libreria.certificate.ec.argosdata.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalArgosData extends CertificadoExtImplArgosData implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalArgosData(X509Certificate certificado) {
      super(certificado);
   }
}
