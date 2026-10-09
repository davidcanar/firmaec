package ec.gob.firmadigital.libreria.certificate.ec.datil;

import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelladoTiempo;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelloElectronico;
import ec.gob.firmadigital.libreria.certificate.ec.datil.cert.SubCaCertDatil20212031;
import ec.gob.firmadigital.libreria.certificate.ec.datil.cert.SubCaCertDatil20252035;
import ec.gob.firmadigital.libreria.certificate.ec.datil.cert.SubCaCertDatilCortaDuracion20262036;
import ec.gob.firmadigital.libreria.certificate.ec.datil.ext.CertificadoExtImplDatil;
import ec.gob.firmadigital.libreria.certificate.ec.datil.ext.CertificadoExtPersonaJuridicaPrivadaDatil;
import ec.gob.firmadigital.libreria.certificate.ec.datil.ext.CertificadoExtPersonaNaturalDatil;
import ec.gob.firmadigital.libreria.certificate.ec.datil.subj.CertificadoSubjMiembroEmpresaDatil;
import ec.gob.firmadigital.libreria.certificate.ec.datil.subj.CertificadoSubjPersonaNaturalDatil;
import ec.gob.firmadigital.libreria.certificate.ec.datil.subj.CertificadoSubjRepresentanteLegalDatil;
import ec.gob.firmadigital.libreria.certificate.ec.datil.subj.CertificadoSubjSelladoTiempoDatil;
import ec.gob.firmadigital.libreria.certificate.ec.datil.subj.CertificadoSubjSelloElectronicoDatil;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryDatil {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertDatil20212031())) {
            System.out.println("SubCA DatilMedia 2021-2031");
            return new SubCaCertDatil20212031();
         } else if (Utils.verifySignature(certificado, new SubCaCertDatil20252035())) {
            System.out.println("SubCA DatilMedia 2025-2035");
            return new SubCaCertDatil20252035();
         } else if (Utils.verifySignature(certificado, new SubCaCertDatilCortaDuracion20262036())) {
            System.out.println("SubCA DatilMedia Corta Duracion 2026-2036");
            return new SubCaCertDatilCortaDuracion20262036();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado de DATILMEDIA S.A. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioDatil(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoDatil = construir(certificado);
      if (certificadoDatil != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoDatil instanceof CertificadoExtImplDatil) {
            if (certificadoDatil instanceof CertificadoExtPersonaNaturalDatil certificadoExtPersonaNaturalDatil) {
               datosUsuario.setTipoCertificado("Persona Natural (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaNaturalDatil.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaNaturalDatil.getNombres());
               datosUsuario.setApellido(certificadoExtPersonaNaturalDatil.getPrimerApellido() + " " + certificadoExtPersonaNaturalDatil.getSegundoApellido());
            }

            if (certificadoDatil instanceof CertificadoExtPersonaJuridicaPrivadaDatil certificadoExtPersonaJuridicaPrivadaDatil) {
               datosUsuario.setTipoCertificado("Persona Jurídica (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaJuridicaPrivadaDatil.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaJuridicaPrivadaDatil.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaJuridicaPrivadaDatil.getPrimerApellido() + " " + certificadoExtPersonaJuridicaPrivadaDatil.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtPersonaJuridicaPrivadaDatil.getRuc());
               datosUsuario.setRazonSocial(certificadoExtPersonaJuridicaPrivadaDatil.getRazonSocial());
               datosUsuario.setCargo(certificadoExtPersonaJuridicaPrivadaDatil.getCargo());
            }
         }

         if (certificadoDatil instanceof CertificadoSubjImpl) {
            if (certificadoDatil instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
            }

            if (certificadoDatil instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoDatil instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
               datosUsuario.setTipoCertificado("Representante Legal");
               datosUsuario.setRuc(certificadoRepresentanteLegal.getRuc());
               datosUsuario.setRazonSocial(certificadoRepresentanteLegal.getRazonSocial());
               datosUsuario.setCargo(certificadoRepresentanteLegal.getCargo());
            }

            if (certificadoDatil instanceof CertificadoSelloElectronico certificadoSelloElectronico) {
               datosUsuario.setTipoCertificado("Sellado Electrónico");
               datosUsuario.setCommonName(certificadoSelloElectronico.getCommonName());
               datosUsuario.setRuc(certificadoSelloElectronico.getRuc());
               datosUsuario.setRazonSocial(certificadoSelloElectronico.getRazonSocial());
            }

            if (certificadoDatil instanceof CertificadoSelladoTiempo certificadoSelladoTiempo) {
               datosUsuario.setTipoCertificado("Sellado de Tiempo");
               datosUsuario.setCommonName(certificadoSelladoTiempo.getCommonName());
               datosUsuario.setRuc(certificadoSelladoTiempo.getRuc());
               datosUsuario.setRazonSocial(certificadoSelladoTiempo.getRazonSocial());
            }
         }

         datosUsuario.setCertificadoDigitalValido(true);
      }

      return datosUsuario;
   }

   private static Certificado construir(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      if (CertUtils.hasExtensionMatchingPattern(certificado, "1.3.6.1.4.1", "3.1")) {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.52643.2.5.1")) {
            return new CertificadoExtPersonaNaturalDatil(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.52643.2.5.2")) {
            return new CertificadoExtPersonaJuridicaPrivadaDatil(certificado);
         }
      } else {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.52643.2.1.1")) {
            return new CertificadoSubjPersonaNaturalDatil(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.52643.2.3.1")) {
            return new CertificadoSubjRepresentanteLegalDatil(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.52643.2.2.1")) {
            return new CertificadoSubjMiembroEmpresaDatil(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.52643.102.2.4.1")) {
            return new CertificadoSubjSelloElectronicoDatil(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.52643.102.2.5.1")) {
            return new CertificadoSubjSelladoTiempoDatil(certificado);
         }
      }

      return null;
   }
}
