package ec.gob.firmadigital.libreria.certificate.ec.argosdata.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjRepresentanteLegalArgosData extends CertificadoSubjImpl implements CertificadoRepresentanteLegal {
   public CertificadoSubjRepresentanteLegalArgosData(X509Certificate certificado) {
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
