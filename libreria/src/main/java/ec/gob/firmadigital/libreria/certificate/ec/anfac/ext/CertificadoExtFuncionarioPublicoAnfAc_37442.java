package ec.gob.firmadigital.libreria.certificate.ec.anfac.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoFuncionarioPublico;
import java.security.cert.X509Certificate;

public class CertificadoExtFuncionarioPublicoAnfAc_37442 extends CertificadoExtImplAnfAc_37442 implements CertificadoFuncionarioPublico {
   public CertificadoExtFuncionarioPublicoAnfAc_37442(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.18332.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.18332.3.5");
   }

   @Override
   public String getInstitucion() {
      return this.getExtensionField("1.3.6.1.4.1.18332.3.6");
   }
}
