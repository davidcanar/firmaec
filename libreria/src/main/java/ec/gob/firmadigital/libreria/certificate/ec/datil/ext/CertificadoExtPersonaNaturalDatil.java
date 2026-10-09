package ec.gob.firmadigital.libreria.certificate.ec.datil.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalDatil extends CertificadoExtImplDatil implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalDatil(X509Certificate certificado) {
      super(certificado);
   }
}
