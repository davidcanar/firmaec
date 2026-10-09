package ec.gob.firmadigital.libreria.certificate.ec.darkcam;

import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelloElectronico;
import ec.gob.firmadigital.libreria.certificate.ec.darkcam.cert.SubCaCertDarkcam20262036;
import ec.gob.firmadigital.libreria.certificate.ec.darkcam.cert.SubCaShortCertDarkcam20262036;
import ec.gob.firmadigital.libreria.certificate.ec.darkcam.subj.CertificadoSubjMiembroEmpresaDarkcam;
import ec.gob.firmadigital.libreria.certificate.ec.darkcam.subj.CertificadoSubjPersonaNaturalDarkcam;
import ec.gob.firmadigital.libreria.certificate.ec.darkcam.subj.CertificadoSubjRepresentanteLegalDarkcam;
import ec.gob.firmadigital.libreria.certificate.ec.darkcam.subj.CertificadoSubjSelloElectronicoDarkcam;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryDarkcam {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaShortCertDarkcam20262036())) {
            System.out.println("DarkcamSubCaShortCert2026-2036");
            return new SubCaShortCertDarkcam20262036();
         } else if (Utils.verifySignature(certificado, new SubCaCertDarkcam20262036())) {
            System.out.println("DarkcamSubCaCert2026-2036");
            return new SubCaCertDarkcam20262036();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado de DARKCAM S.A. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioDarkcam(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoDarkcam = construir(certificado);
      if (certificadoDarkcam != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoDarkcam instanceof CertificadoSubjImpl) {
            if (certificadoDarkcam instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
            }

            if (certificadoDarkcam instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoDarkcam instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
               datosUsuario.setTipoCertificado("Representante Legal");
               datosUsuario.setRuc(certificadoRepresentanteLegal.getRuc());
               datosUsuario.setRazonSocial(certificadoRepresentanteLegal.getRazonSocial());
               datosUsuario.setCargo(certificadoRepresentanteLegal.getCargo());
            }

            if (certificadoDarkcam instanceof CertificadoSelloElectronico certificadoSelloElectronico) {
               datosUsuario.setTipoCertificado("Sellado Electrónico");
               datosUsuario.setCommonName(certificadoSelloElectronico.getCommonName());
               datosUsuario.setRuc(certificadoSelloElectronico.getRuc());
               datosUsuario.setRazonSocial(certificadoSelloElectronico.getRazonSocial());
            }
         }

         datosUsuario.setCertificadoDigitalValido(true);
      }

      return datosUsuario;
   }

   private static Certificado construir(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64482.2.1.1")) {
         return new CertificadoSubjPersonaNaturalDarkcam(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64482.2.2.1")) {
         return new CertificadoSubjMiembroEmpresaDarkcam(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64482.2.3.1")) {
         return new CertificadoSubjRepresentanteLegalDarkcam(certificado);
      } else {
         return BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64482.2.2.2") ? new CertificadoSubjSelloElectronicoDarkcam(certificado) : null;
      }
   }
}
