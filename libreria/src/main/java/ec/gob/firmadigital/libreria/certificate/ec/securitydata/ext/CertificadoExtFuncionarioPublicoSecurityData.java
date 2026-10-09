package ec.gob.firmadigital.libreria.certificate.ec.securitydata.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoFuncionarioPublico;
import java.security.cert.X509Certificate;

public class CertificadoExtFuncionarioPublicoSecurityData extends CertificadoExtImplSecurityData implements CertificadoFuncionarioPublico {
   public CertificadoExtFuncionarioPublicoSecurityData(X509Certificate certificado) {
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

   @Override
   public String getInstitucion() {
      return this.getExtensionField("1.3.6.1.4.1.37746.3.6");
   }
}
