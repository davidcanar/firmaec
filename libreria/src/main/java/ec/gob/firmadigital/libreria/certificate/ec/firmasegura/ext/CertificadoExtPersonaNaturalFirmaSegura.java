package ec.gob.firmadigital.libreria.certificate.ec.firmasegura.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalFirmaSegura extends CertificadoExtImplFirmaSegura implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalFirmaSegura(X509Certificate certificado) {
      super(certificado);
   }
}
