package ec.gob.firmadigital.libreria.certificate.ec.firmasegura.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import java.security.cert.X509Certificate;

public class CertificadoExtRepresentanteLegalFirmaSegura extends CertificadoExtImplFirmaSegura implements CertificadoRepresentanteLegal {
   public CertificadoExtRepresentanteLegalFirmaSegura(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getExtensionField("1.3.6.1.4.1.61305.3.10");
   }

   @Override
   public String getCargo() {
      return this.getExtensionField("1.3.6.1.4.1.61305.3.5");
   }
}
