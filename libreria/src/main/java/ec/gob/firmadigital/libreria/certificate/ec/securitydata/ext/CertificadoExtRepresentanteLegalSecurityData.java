package ec.gob.firmadigital.libreria.certificate.ec.securitydata.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import java.security.cert.X509Certificate;

public class CertificadoExtRepresentanteLegalSecurityData extends CertificadoExtImplSecurityData implements CertificadoRepresentanteLegal {
   public CertificadoExtRepresentanteLegalSecurityData(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.37746.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.37746.3.5");
   }
}
