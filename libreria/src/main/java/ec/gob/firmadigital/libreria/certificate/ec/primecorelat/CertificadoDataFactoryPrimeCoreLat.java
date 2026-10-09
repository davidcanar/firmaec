package ec.gob.firmadigital.libreria.certificate.ec.primecorelat;

import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelloElectronico;
import ec.gob.firmadigital.libreria.certificate.ec.primecorelat.cert.SubCa1CertPrimeCoreLat20262036;
import ec.gob.firmadigital.libreria.certificate.ec.primecorelat.cert.SubCa2CertPrimeCoreLat20262036;
import ec.gob.firmadigital.libreria.certificate.ec.primecorelat.subj.CertificadoSubjMiembroEmpresaPrimeCoreLat;
import ec.gob.firmadigital.libreria.certificate.ec.primecorelat.subj.CertificadoSubjPersonaJuridicaPrimeCoreLat;
import ec.gob.firmadigital.libreria.certificate.ec.primecorelat.subj.CertificadoSubjPersonaNaturalPrimeCoreLat;
import ec.gob.firmadigital.libreria.certificate.ec.primecorelat.subj.CertificadoSubjSelloElectronicoPrimeCoreLat;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryPrimeCoreLat {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCa1CertPrimeCoreLat20262036())) {
            System.out.println("PrimeCoreLatCA1 2026-2036");
            return new SubCa1CertPrimeCoreLat20262036();
         } else if (Utils.verifySignature(certificado, new SubCa2CertPrimeCoreLat20262036())) {
            System.out.println("PrimeCoreLatCA2 2026-2036");
            return new SubCa2CertPrimeCoreLat20262036();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado de PRIMECORELAT S.A.S. B.I.C. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioPrimeCoreLat(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoPrimeCoreLat = construir(certificado);
      if (certificadoPrimeCoreLat != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoPrimeCoreLat instanceof CertificadoSubjImpl) {
            if (certificadoPrimeCoreLat instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
            }

            if (certificadoPrimeCoreLat instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoPrimeCoreLat instanceof CertificadoPersonaJuridica certificadoPersonaJuridica) {
               datosUsuario.setTipoCertificado("Persona Jurídica");
               datosUsuario.setRuc(certificadoPersonaJuridica.getRuc());
               datosUsuario.setRazonSocial(certificadoPersonaJuridica.getRazonSocial());
               datosUsuario.setCargo(certificadoPersonaJuridica.getCargo());
            }

            if (certificadoPrimeCoreLat instanceof CertificadoSelloElectronico certificadoSelloElectronico) {
               datosUsuario.setTipoCertificado("Sello Electrónico");
               datosUsuario.setRuc(certificadoSelloElectronico.getRuc());
               datosUsuario.setRazonSocial(certificadoSelloElectronico.getRazonSocial());
               datosUsuario.setCommonName(certificadoSelloElectronico.getCommonName());
            }
         }

         datosUsuario.setCertificadoDigitalValido(true);
      }

      return datosUsuario;
   }

   private static Certificado construir(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.63542.2.1.1")) {
         return new CertificadoSubjPersonaNaturalPrimeCoreLat(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.63542.2.2.1")) {
         return new CertificadoSubjMiembroEmpresaPrimeCoreLat(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.63542.2.3.1")) {
         return new CertificadoSubjPersonaJuridicaPrimeCoreLat(certificado);
      } else {
         return BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.63542.102.2.4.1")
            ? new CertificadoSubjSelloElectronicoPrimeCoreLat(certificado)
            : null;
      }
   }
}
