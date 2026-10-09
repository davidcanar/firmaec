package ec.gob.firmadigital.libreria.certificate.ec.primecorelat.subj;

import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelloElectronico;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import java.security.cert.X509Certificate;

public class CertificadoSubjSelloElectronicoPrimeCoreLat extends CertificadoSubjImpl implements CertificadoSelloElectronico {
   public CertificadoSubjSelloElectronicoPrimeCoreLat(X509Certificate certificado) {
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
