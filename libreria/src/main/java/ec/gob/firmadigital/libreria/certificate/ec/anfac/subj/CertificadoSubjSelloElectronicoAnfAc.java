package ec.gob.firmadigital.libreria.certificate.ec.anfac.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelloElectronico;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjSelloElectronicoAnfAc extends CertificadoSubjImpl implements CertificadoSelloElectronico {
   public CertificadoSubjSelloElectronicoAnfAc(X509Certificate certificado) {
      super(certificado);
   }

   @Override
   public String getCommonName() {
      return this.getSubjectField("2.5.4.3");
   }

   @Override
   public String getRazonSocial() {
      return this.getSubjectField("2.5.4.10");
   }
}
