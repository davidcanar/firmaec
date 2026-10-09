package ec.gob.firmadigital.libreria.certificate.ec.argosdata.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import java.security.cert.X509Certificate;

public class CertificadoExtRepresentanteLegalArgosData extends CertificadoExtImplArgosData implements CertificadoRepresentanteLegal {
   public CertificadoExtRepresentanteLegalArgosData(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.59198.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.59198.3.5");
   }
}
