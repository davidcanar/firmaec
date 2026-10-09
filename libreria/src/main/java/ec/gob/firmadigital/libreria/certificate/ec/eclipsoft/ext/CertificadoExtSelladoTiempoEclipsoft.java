package ec.gob.firmadigital.libreria.certificate.ec.eclipsoft.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelladoTiempo;
import java.security.cert.X509Certificate;

public class CertificadoExtSelladoTiempoEclipsoft extends CertificadoExtImplEcplipsoft implements CertificadoSelladoTiempo {
   public CertificadoExtSelladoTiempoEclipsoft(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.57153.102.3.10");
   }

   @Override
   public String getCommonName() {
      return "";
   }
}
