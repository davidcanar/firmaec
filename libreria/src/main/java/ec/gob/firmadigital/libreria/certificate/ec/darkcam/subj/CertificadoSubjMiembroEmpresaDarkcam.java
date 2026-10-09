package ec.gob.firmadigital.libreria.certificate.ec.darkcam.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjMiembroEmpresaDarkcam extends CertificadoSubjImpl implements CertificadoMiembroEmpresa {
   public CertificadoSubjMiembroEmpresaDarkcam(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getRazonSocial() {
      return this.getSubjectField("2.5.4.10");
   }

   @Override
   public String getCargo() {
      return this.getSubjectField("2.5.4.12");
   }
}
