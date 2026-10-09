package ec.gob.firmadigital.libreria.certificate.ec.lazzate;

import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelloElectronico;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.cert.SubCa1CertLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.cert.SubCa2CertLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.cert.SubCaCertLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.cert.SubCaWeGoCertLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.ext.CertificadoExtImplLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.ext.CertificadoExtPersonaJuridicaLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.ext.CertificadoExtPersonaNaturalLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.subj.CertificadoSubjMiembroEmpresaLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.subj.CertificadoSubjPersonaNaturalLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.subj.CertificadoSubjRepresentanteLegalLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.subj.CertificadoSubjSelloElectronicoLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryLazzate {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertLazzate())) {
            System.out.println("LazzateCA 2022-2037");
            return new SubCaCertLazzate();
         } else if (Utils.verifySignature(certificado, new SubCa1CertLazzate())) {
            System.out.println("LazzateCA1 2023-2053");
            return new SubCa1CertLazzate();
         } else if (Utils.verifySignature(certificado, new SubCa2CertLazzate())) {
            System.out.println("LazzateCA2 2023-2053");
            return new SubCa2CertLazzate();
         } else if (Utils.verifySignature(certificado, new SubCaWeGoCertLazzate())) {
            System.out.println("LazzateCAWeGo 2023-2053");
            return new SubCaWeGoCertLazzate();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado de LAZZATE CIA. LTDA. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioLazzate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoLazzate = construir(certificado);
      if (certificadoLazzate != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoLazzate instanceof CertificadoExtImplLazzate) {
            if (certificadoLazzate instanceof CertificadoExtPersonaNaturalLazzate certificadoExtPersonaNaturalLazzate) {
               datosUsuario.setTipoCertificado("Persona Natural (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaNaturalLazzate.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaNaturalLazzate.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaNaturalLazzate.getPrimerApellido() + " " + certificadoExtPersonaNaturalLazzate.getSegundoApellido()
               );
            }

            if (certificadoLazzate instanceof CertificadoExtPersonaJuridicaLazzate certificadoExtPersonaJuridicaLazzate) {
               datosUsuario.setTipoCertificado("Persona Jurídica (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaJuridicaLazzate.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaJuridicaLazzate.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaJuridicaLazzate.getPrimerApellido() + " " + certificadoExtPersonaJuridicaLazzate.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtPersonaJuridicaLazzate.getRuc());
               datosUsuario.setRazonSocial(certificadoExtPersonaJuridicaLazzate.getRazonSocial());
               datosUsuario.setCargo(certificadoExtPersonaJuridicaLazzate.getCargo());
            }
         }

         if (certificadoLazzate instanceof CertificadoSubjImpl) {
            if (certificadoLazzate instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
            }

            if (certificadoLazzate instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoLazzate instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
               datosUsuario.setTipoCertificado("Representante Legal");
               datosUsuario.setRuc(certificadoRepresentanteLegal.getRuc());
               datosUsuario.setRazonSocial(certificadoRepresentanteLegal.getRazonSocial());
               datosUsuario.setCargo(certificadoRepresentanteLegal.getCargo());
            }

            if (certificadoLazzate instanceof CertificadoSelloElectronico certificadoSelloElectronico) {
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
      if (CertUtils.hasExtensionMatchingPattern(certificado, "1.3.6.1.4.1", "3.1")) {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.59382.2.1")) {
            return new CertificadoExtPersonaNaturalLazzate(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.59382.2.2")) {
            return new CertificadoExtPersonaJuridicaLazzate(certificado);
         }
      } else {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.59382.2.1.1")) {
            return new CertificadoSubjPersonaNaturalLazzate(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.59382.2.2.1")) {
            return new CertificadoSubjMiembroEmpresaLazzate(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.59382.2.3.1")) {
            return new CertificadoSubjRepresentanteLegalLazzate(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.59382.102.2.4.1")) {
            return new CertificadoSubjSelloElectronicoLazzate(certificado);
         }
      }

      return null;
   }
}
