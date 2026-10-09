package ec.gob.firmadigital.libreria.certificate.ec.letmi;

import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelloElectronico;
import ec.gob.firmadigital.libreria.certificate.ec.letmi.cert.SubCaCertLetmi20252035;
import ec.gob.firmadigital.libreria.certificate.ec.letmi.subj.CertificadoSubjMiembroEmpresaLetmi;
import ec.gob.firmadigital.libreria.certificate.ec.letmi.subj.CertificadoSubjPersonaNaturalLetmi;
import ec.gob.firmadigital.libreria.certificate.ec.letmi.subj.CertificadoSubjRepresentanteLegalLetmi;
import ec.gob.firmadigital.libreria.certificate.ec.letmi.subj.CertificadoSubjSelloElectronicoLetmi;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryLetmi {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertLetmi20252035())) {
            System.out.println("LetmiSubCaCert2025-2035");
            return new SubCaCertLetmi20252035();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado de LETMI ECUADOR S.A. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioLetmi(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoLetmi = construir(certificado);
      if (certificadoLetmi != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoLetmi instanceof CertificadoSubjImpl) {
            if (certificadoLetmi instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
            }

            if (certificadoLetmi instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoLetmi instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
               datosUsuario.setTipoCertificado("Representante Legal");
               datosUsuario.setRuc(certificadoRepresentanteLegal.getRuc());
               datosUsuario.setRazonSocial(certificadoRepresentanteLegal.getRazonSocial());
               datosUsuario.setCargo(certificadoRepresentanteLegal.getCargo());
            }

            if (certificadoLetmi instanceof CertificadoSelloElectronico certificadoSelloElectronico) {
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
      if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62566.2.1.2")
         || BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62566.2.1.1")) {
         return new CertificadoSubjPersonaNaturalLetmi(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62566.2.2.2")
         || BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62566.2.2.1")) {
         return new CertificadoSubjMiembroEmpresaLetmi(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62566.2.3.2")
         || BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62566.2.3.1")) {
         return new CertificadoSubjRepresentanteLegalLetmi(certificado);
      } else {
         return !BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62566.102.2.4.2")
               && !BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62566.102.2.4.1")
            ? null
            : new CertificadoSubjSelloElectronicoLetmi(certificado);
      }
   }
}
