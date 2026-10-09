package ec.gob.firmadigital.libreria.certificate.ec.eclipsoft.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonalNaturalEclipsoft extends CertificadoExtImplEcplipsoft implements CertificadoPersonaNatural {
   public CertificadoExtPersonalNaturalEclipsoft(X509Certificate certificado) {
      super(certificado);
   }
}
