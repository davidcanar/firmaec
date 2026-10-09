package ec.gob.firmadigital.libreria.certificate.ec.bce.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoFuncionarioPublico;
import java.security.cert.X509Certificate;

public class CertificadoExtFuncionarioPublicoBancoCentral extends CertificadoExtImplBancoCentral implements CertificadoFuncionarioPublico {
   public CertificadoExtFuncionarioPublicoBancoCentral(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getInstitucion() {
      return this.getExtensionField("1.3.6.1.4.1.37947.3.6");
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.37947.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.37947.3.5");
   }
}
