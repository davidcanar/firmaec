package ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import java.security.cert.X509Certificate;

public class CertificadoExtPersonaNaturalAlphaTechnologies extends CertificadoExtImplAlphaTechnologies implements CertificadoPersonaNatural {
   public CertificadoExtPersonaNaturalAlphaTechnologies(X509Certificate certificado) {
      super(certificado);
   }
}
