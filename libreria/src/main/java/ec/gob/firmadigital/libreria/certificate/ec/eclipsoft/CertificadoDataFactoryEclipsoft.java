package ec.gob.firmadigital.libreria.certificate.ec.eclipsoft;

import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.eclipsoft.ext.CertificadoExtImplEcplipsoft;
import ec.gob.firmadigital.libreria.certificate.ec.eclipsoft.ext.CertificadoExtMiembroEmpresaEclipsoft;
import ec.gob.firmadigital.libreria.certificate.ec.eclipsoft.ext.CertificadoExtPersonaJuridicaPrivadaEclipsoft;
import ec.gob.firmadigital.libreria.certificate.ec.eclipsoft.ext.CertificadoExtPersonalNaturalEclipsoft;
import ec.gob.firmadigital.libreria.certificate.ec.eclipsoft.ext.CertificadoExtRepresentanteLegalEclipsoft;
import ec.gob.firmadigital.libreria.certificate.ec.eclipsoft.ext.CertificadoExtSelladoTiempoEclipsoft;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.cert.SubCaCertUanataca0120162029;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.cert.SubCaCertUanataca0220162029;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.cert.SubCaCertUanataca0320212034;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryEclipsoft {
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
         throw new EntidadCertificadoraNoValidaException("Certificado del ECLIPSOFT S.A. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioEclipsoft(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoEclipsoft = construir(certificado);
      if (certificadoEclipsoft != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoEclipsoft instanceof CertificadoExtImplEcplipsoft) {
            if (certificadoEclipsoft instanceof CertificadoExtPersonalNaturalEclipsoft certificadoExtPersonalNaturalEclipsoft) {
               datosUsuario.setTipoCertificado("Persona Natural (EXT)");
               datosUsuario.setCedula(certificadoExtPersonalNaturalEclipsoft.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonalNaturalEclipsoft.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonalNaturalEclipsoft.getPrimerApellido() + " " + certificadoExtPersonalNaturalEclipsoft.getSegundoApellido()
               );
            }

            if (certificadoEclipsoft instanceof CertificadoExtMiembroEmpresaEclipsoft certificadoExtMiembroEmpresaEclipsoft) {
               datosUsuario.setTipoCertificado("Miembro Empresa (EXT)");
               datosUsuario.setCedula(certificadoExtMiembroEmpresaEclipsoft.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtMiembroEmpresaEclipsoft.getNombres());
               datosUsuario.setApellido(
                  certificadoExtMiembroEmpresaEclipsoft.getPrimerApellido() + " " + certificadoExtMiembroEmpresaEclipsoft.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtMiembroEmpresaEclipsoft.getRuc());
               datosUsuario.setRazonSocial(certificadoExtMiembroEmpresaEclipsoft.getRazonSocial());
               datosUsuario.setCargo(certificadoExtMiembroEmpresaEclipsoft.getCargo());
            }

            if (certificadoEclipsoft instanceof CertificadoExtRepresentanteLegalEclipsoft certificadoExtRepresentanteLegalEclipsoft) {
               datosUsuario.setTipoCertificado("Representante Legal (EXT)");
               datosUsuario.setCedula(certificadoExtRepresentanteLegalEclipsoft.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtRepresentanteLegalEclipsoft.getNombres());
               datosUsuario.setApellido(
                  certificadoExtRepresentanteLegalEclipsoft.getPrimerApellido() + " " + certificadoExtRepresentanteLegalEclipsoft.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtRepresentanteLegalEclipsoft.getRuc());
               datosUsuario.setRazonSocial(certificadoExtRepresentanteLegalEclipsoft.getRazonSocial());
               datosUsuario.setCargo(certificadoExtRepresentanteLegalEclipsoft.getCargo());
            }

            if (certificadoEclipsoft instanceof CertificadoExtPersonaJuridicaPrivadaEclipsoft certificadoExtPersonaJuridicaPrivadaEclipsoft) {
               datosUsuario.setTipoCertificado("Persona Jurídica Privada (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaJuridicaPrivadaEclipsoft.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaJuridicaPrivadaEclipsoft.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaJuridicaPrivadaEclipsoft.getPrimerApellido() + " " + certificadoExtPersonaJuridicaPrivadaEclipsoft.getSegundoApellido()
               );
               datosUsuario.setRuc(datosUsuario.getRuc());
               datosUsuario.setRazonSocial(datosUsuario.getRazonSocial());
               datosUsuario.setCargo(datosUsuario.getCargo());
            }

            if (certificadoEclipsoft instanceof CertificadoExtSelladoTiempoEclipsoft certificadoExtSelladoTiempoEclipsoft) {
               datosUsuario.setTipoCertificado("Sellado de Tiempo (EXT)");
               datosUsuario.setRuc(certificadoExtSelladoTiempoEclipsoft.getRuc());
               datosUsuario.setRazonSocial(certificadoExtSelladoTiempoEclipsoft.getRazonSocial());
               datosUsuario.setCommonName(certificadoExtSelladoTiempoEclipsoft.getCommonName());
            }
         }

         datosUsuario.setCertificadoDigitalValido(true);
      }

      return datosUsuario;
   }

   private static Certificado construir(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      if (CertUtils.hasCertificatePoliciesMatchingPattern(certificado, "1.3.6.1.4.1.57153.1.1", "")) {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.57153.1.1.1.1")) {
            return new CertificadoExtPersonalNaturalEclipsoft(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.57153.1.1.1.2")) {
            return new CertificadoExtPersonalNaturalEclipsoft(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.57153.1.1.2.1")) {
            return new CertificadoExtMiembroEmpresaEclipsoft(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.57153.1.1.2.2")) {
            return new CertificadoExtMiembroEmpresaEclipsoft(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.57153.1.1.3.1")) {
            return new CertificadoExtRepresentanteLegalEclipsoft(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.57153.1.1.3.2")) {
            return new CertificadoExtRepresentanteLegalEclipsoft(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.57153.1.1.4.1")) {
            return new CertificadoExtPersonaJuridicaPrivadaEclipsoft(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.57153.1.1.4.2")) {
            return new CertificadoExtPersonaJuridicaPrivadaEclipsoft(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.57153.1.1.5.2")) {
            return new CertificadoExtSelladoTiempoEclipsoft(certificado);
         }
      }

      return null;
   }
}
