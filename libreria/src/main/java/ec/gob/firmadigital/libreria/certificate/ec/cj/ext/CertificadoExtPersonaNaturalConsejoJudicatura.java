package ec.gob.firmadigital.libreria.certificate.ec.cj.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalConsejoJudicatura extends CertificadoExtImplConsejoJudicatura implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalConsejoJudicatura(X509Certificate certificado) {
      super(certificado);
   }
}
