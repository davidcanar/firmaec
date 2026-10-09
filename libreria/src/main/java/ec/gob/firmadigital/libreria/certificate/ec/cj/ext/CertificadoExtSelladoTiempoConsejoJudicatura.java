package ec.gob.firmadigital.libreria.certificate.ec.cj.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelladoTiempo;
import java.security.cert.X509Certificate;

public class CertificadoExtSelladoTiempoConsejoJudicatura extends CertificadoExtImplConsejoJudicatura implements CertificadoSelladoTiempo {
   public CertificadoExtSelladoTiempoConsejoJudicatura(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.43745.1.3.10");
   }

   @Override
   public String getCommonName() {
      return "";
   }
}
