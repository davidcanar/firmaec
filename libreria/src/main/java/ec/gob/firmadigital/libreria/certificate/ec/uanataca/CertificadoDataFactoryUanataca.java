package ec.gob.firmadigital.libreria.certificate.ec.uanataca;

import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelladoTiempo;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.cert.SubCaCertUanataca0120162029;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.cert.SubCaCertUanataca0220162029;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.cert.SubCaCertUanataca0320212034;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.ext.CertificadoExtImplUanataca;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.ext.CertificadoExtMiembroEmpresaUanataca;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.ext.CertificadoExtPersonaJuridicaPrivadaUanataca;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.ext.CertificadoExtPersonaNaturalUanataca;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.ext.CertificadoExtRepresentanteLegalUanataca;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.ext.CertificadoExtSelladoTiempoUanataca;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.subj.CertificadoSubjMiembroEmpresaUanataca;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.subj.CertificadoSubjPersonaJuridicaUanataca;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.subj.CertificadoSubjPersonaNaturalUanataca;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.subj.CertificadoSubjRepresentanteLegalUanataca;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.subj.CertificadoSubjSelladoTiempoUanataca;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryUanataca {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertUanataca0120162029())) {
            System.out.println("Uanataca 2016-2029");
            return new SubCaCertUanataca0120162029();
         } else if (Utils.verifySignature(certificado, new SubCaCertUanataca0220162029())) {
            System.out.println("Uanataca 2016-2029");
            return new SubCaCertUanataca0220162029();
         } else if (Utils.verifySignature(certificado, new SubCaCertUanataca0320212034())) {
            System.out.println("Uanataca 2021-2034");
            return new SubCaCertUanataca0320212034();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado de UANATACA ECUADOR S.A. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioUanataca(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoUanataca = construir(certificado);
      if (certificadoUanataca != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoUanataca instanceof CertificadoExtImplUanataca) {
            if (certificadoUanataca instanceof CertificadoExtPersonaNaturalUanataca certificadoExtPersonaNaturalUanataca) {
               datosUsuario.setTipoCertificado("Persona Natural (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaNaturalUanataca.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaNaturalUanataca.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaNaturalUanataca.getPrimerApellido() + " " + certificadoExtPersonaNaturalUanataca.getSegundoApellido()
               );
            }

            if (certificadoUanataca instanceof CertificadoExtMiembroEmpresaUanataca certificadoExtMiembroEmpresaUanataca) {
               datosUsuario.setTipoCertificado("Miembro Empresa (EXT)");
               datosUsuario.setCedula(certificadoExtMiembroEmpresaUanataca.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtMiembroEmpresaUanataca.getNombres());
               datosUsuario.setApellido(
                  certificadoExtMiembroEmpresaUanataca.getPrimerApellido() + " " + certificadoExtMiembroEmpresaUanataca.getSegundoApellido()
               );
               datosUsuario.setCargo(certificadoExtMiembroEmpresaUanataca.getCargo());
            }

            if (certificadoUanataca instanceof CertificadoExtPersonaJuridicaPrivadaUanataca certificadoExtPersonaJuridicaPrivadaUanataca) {
               datosUsuario.setTipoCertificado("Persona Jurídica Privada (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaJuridicaPrivadaUanataca.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaJuridicaPrivadaUanataca.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaJuridicaPrivadaUanataca.getPrimerApellido() + " " + certificadoExtPersonaJuridicaPrivadaUanataca.getSegundoApellido()
               );
               datosUsuario.setCargo(datosUsuario.getCargo());
            }

            if (certificadoUanataca instanceof CertificadoExtRepresentanteLegalUanataca certificadoExtRepresentanteLegalUanataca) {
               datosUsuario.setTipoCertificado("Representante Legal (EXT)");
               datosUsuario.setCedula(certificadoExtRepresentanteLegalUanataca.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtRepresentanteLegalUanataca.getNombres());
               datosUsuario.setApellido(
                  certificadoExtRepresentanteLegalUanataca.getPrimerApellido() + " " + certificadoExtRepresentanteLegalUanataca.getSegundoApellido()
               );
               datosUsuario.setCargo(certificadoExtRepresentanteLegalUanataca.getCargo());
            }

            if (certificadoUanataca instanceof CertificadoExtSelladoTiempoUanataca certificadoExtSelladoTiempoUanataca) {
               datosUsuario.setTipoCertificado("Sellado de Tiempo (EXT)");
               datosUsuario.setCommonName(certificadoExtSelladoTiempoUanataca.getCommonName());
               datosUsuario.setRuc(certificadoExtSelladoTiempoUanataca.getRuc());
               datosUsuario.setRazonSocial(certificadoExtSelladoTiempoUanataca.getRazonSocial());
            }
         }

         if (certificadoUanataca instanceof CertificadoSubjImpl) {
            if (certificadoUanataca instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
            }

            if (certificadoUanataca instanceof CertificadoPersonaJuridica certificadoPersonaJuridica) {
               datosUsuario.setTipoCertificado("Persona Jurídica");
               datosUsuario.setRuc(certificadoPersonaJuridica.getRuc());
               datosUsuario.setRazonSocial(certificadoPersonaJuridica.getRazonSocial());
               datosUsuario.setCargo(certificadoPersonaJuridica.getCargo());
            }

            if (certificadoUanataca instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoUanataca instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
               datosUsuario.setTipoCertificado("Representante Legal");
               datosUsuario.setRuc(certificadoRepresentanteLegal.getRuc());
               datosUsuario.setRazonSocial(certificadoRepresentanteLegal.getRazonSocial());
               datosUsuario.setCargo(certificadoRepresentanteLegal.getCargo());
            }

            if (certificadoUanataca instanceof CertificadoSelladoTiempo certificadoSelladoTiempo) {
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
      if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.47286.102.2.1.1")) {
         return new CertificadoExtPersonaNaturalUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.47286.102.2.1.2")) {
         return new CertificadoExtPersonaNaturalUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.47286.102.2.4.1")) {
         return new CertificadoExtPersonaJuridicaPrivadaUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.47286.102.2.4.2")) {
         return new CertificadoExtPersonaJuridicaPrivadaUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.47286.102.2.2.1")) {
         return new CertificadoExtMiembroEmpresaUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.47286.102.2.2.2")) {
         return new CertificadoExtMiembroEmpresaUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.47286.102.2.3.1")) {
         return new CertificadoExtRepresentanteLegalUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.47286.102.2.3.2")) {
         return new CertificadoExtRepresentanteLegalUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.47286.102.2.5.1")) {
         return new CertificadoExtSelladoTiempoUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.65311.2.1.1")) {
         return new CertificadoSubjPersonaNaturalUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.65311.2.1.2")) {
         return new CertificadoSubjPersonaNaturalUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.65311.102.2.4.1")) {
         return new CertificadoSubjPersonaJuridicaUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.65311.102.2.4.2")) {
         return new CertificadoSubjPersonaJuridicaUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.65311.2.2.1")) {
         return new CertificadoSubjMiembroEmpresaUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.65311.2.2.2")) {
         return new CertificadoSubjMiembroEmpresaUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.65311.2.3.1")) {
         return new CertificadoSubjRepresentanteLegalUanataca(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.65311.2.3.2")) {
         return new CertificadoSubjRepresentanteLegalUanataca(certificado);
      } else {
         return BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.65311.102.2.5.1")
            ? new CertificadoSubjSelladoTiempoUanataca(certificado)
            : null;
      }
   }
}
