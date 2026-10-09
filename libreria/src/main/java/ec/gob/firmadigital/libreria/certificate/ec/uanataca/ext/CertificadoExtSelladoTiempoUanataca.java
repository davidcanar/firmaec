package ec.gob.firmadigital.libreria.certificate.ec.uanataca.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelladoTiempo;
import java.security.cert.X509Certificate;

public class CertificadoExtSelladoTiempoUanataca extends CertificadoExtImplUanataca implements CertificadoSelladoTiempo {
   public CertificadoExtSelladoTiempoUanataca(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.47286.102.3.10");
   }

   @Override
   public String getCommonName() {
      return "";
   }
}
