package ec.gob.firmadigital.libreria.certificate.ec.cj.ext;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import java.security.cert.X509Certificate;

public class CertificadoExtMiembroEmpresaConsejoJudicatura extends CertificadoExtImplConsejoJudicatura implements CertificadoMiembroEmpresa {
   public CertificadoExtMiembroEmpresaConsejoJudicatura(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.43745.1.3.10");
   }

   @Override
   public String getCargo() {
      return this.getSubjectAlternativeName("1.3.6.1.4.1.43745.1.3.5");
   }
}
