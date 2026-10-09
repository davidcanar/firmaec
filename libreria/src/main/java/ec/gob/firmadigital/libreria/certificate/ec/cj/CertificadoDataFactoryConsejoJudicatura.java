package ec.gob.firmadigital.libreria.certificate.ec.cj;

import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.cj.cert.SubCaCertConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.ec.cj.ext.CertificadoExtDepartamentoEmpresaConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.ec.cj.ext.CertificadoExtEmpresaConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.ec.cj.ext.CertificadoExtImplConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.ec.cj.ext.CertificadoExtMiembroEmpresaConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.ec.cj.ext.CertificadoExtPersonaJuridicaPrivadaConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.ec.cj.ext.CertificadoExtPersonaJuridicaPublicaConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.ec.cj.ext.CertificadoExtPersonaNaturalConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.ec.cj.ext.CertificadoExtSelladoTiempoConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryConsejoJudicatura {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertConsejoJudicatura())) {
            System.out.println("SubCA Consejo de la Judicatura");
            return new SubCaCertConsejoJudicatura();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado del CONSEJO DE LA JUDICATURA sin categorizar!w");
      }
   }

   public static DatosUsuario getDatosUsuarioConsejoJudicatura(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoConsejoJudicatura = construir(certificado);
      if (certificadoConsejoJudicatura != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoConsejoJudicatura instanceof CertificadoExtImplConsejoJudicatura) {
            if (certificadoConsejoJudicatura instanceof CertificadoExtPersonaNaturalConsejoJudicatura certificadoExtPersonaNaturalConsejoJudicatura) {
               datosUsuario.setTipoCertificado("Persona Natural (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaNaturalConsejoJudicatura.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaNaturalConsejoJudicatura.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaNaturalConsejoJudicatura.getPrimerApellido() + " " + certificadoExtPersonaNaturalConsejoJudicatura.getSegundoApellido()
               );
            }

            if (certificadoConsejoJudicatura instanceof CertificadoExtDepartamentoEmpresaConsejoJudicatura certificadoExtDepartamentoEmpresaConsejoJudicatura) {
               datosUsuario.setTipoCertificado("Departamento Empresa (EXT)");
               datosUsuario.setCedula(certificadoExtDepartamentoEmpresaConsejoJudicatura.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtDepartamentoEmpresaConsejoJudicatura.getNombres());
               datosUsuario.setApellido(
                  certificadoExtDepartamentoEmpresaConsejoJudicatura.getPrimerApellido()
                     + " "
                     + certificadoExtDepartamentoEmpresaConsejoJudicatura.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtDepartamentoEmpresaConsejoJudicatura.getRuc());
               datosUsuario.setRazonSocial(certificadoExtDepartamentoEmpresaConsejoJudicatura.getRazonSocial());
               datosUsuario.setCargo(certificadoExtDepartamentoEmpresaConsejoJudicatura.getCargo());
            }

            if (certificadoConsejoJudicatura instanceof CertificadoExtEmpresaConsejoJudicatura certificadoExtEmpresaConsejoJudicatura) {
               datosUsuario.setTipoCertificado("Empresa (EXT)");
               datosUsuario.setCedula(certificadoExtEmpresaConsejoJudicatura.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtEmpresaConsejoJudicatura.getNombres());
               datosUsuario.setApellido(
                  certificadoExtEmpresaConsejoJudicatura.getPrimerApellido() + " " + certificadoExtEmpresaConsejoJudicatura.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtEmpresaConsejoJudicatura.getRuc());
               datosUsuario.setRazonSocial(certificadoExtEmpresaConsejoJudicatura.getRazonSocial());
               datosUsuario.setCargo(certificadoExtEmpresaConsejoJudicatura.getCargo());
            }

            if (certificadoConsejoJudicatura instanceof CertificadoExtMiembroEmpresaConsejoJudicatura certificadoExtMiembroEmpresaConsejoJudicatura) {
               datosUsuario.setTipoCertificado("Miembro de Empresa (EXT)");
               datosUsuario.setCedula(certificadoExtMiembroEmpresaConsejoJudicatura.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtMiembroEmpresaConsejoJudicatura.getNombres());
               datosUsuario.setApellido(
                  certificadoExtMiembroEmpresaConsejoJudicatura.getPrimerApellido() + " " + certificadoExtMiembroEmpresaConsejoJudicatura.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtMiembroEmpresaConsejoJudicatura.getRuc());
               datosUsuario.setRazonSocial(certificadoExtMiembroEmpresaConsejoJudicatura.getRazonSocial());
               datosUsuario.setCargo(certificadoExtMiembroEmpresaConsejoJudicatura.getCargo());
            }

            if (certificadoConsejoJudicatura instanceof CertificadoExtPersonaJuridicaPrivadaConsejoJudicatura certificadoExtPersonaJuridicaPrivadaConsejoJudicatura
               )
             {
               datosUsuario.setTipoCertificado("Persona Jurídica (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaJuridicaPrivadaConsejoJudicatura.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaJuridicaPrivadaConsejoJudicatura.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaJuridicaPrivadaConsejoJudicatura.getPrimerApellido()
                     + " "
                     + certificadoExtPersonaJuridicaPrivadaConsejoJudicatura.getSegundoApellido()
               );
               datosUsuario.setRuc(datosUsuario.getRuc());
               datosUsuario.setRazonSocial(datosUsuario.getRazonSocial());
               datosUsuario.setCargo(datosUsuario.getCargo());
            }

            if (certificadoConsejoJudicatura instanceof CertificadoExtPersonaJuridicaPublicaConsejoJudicatura certificadoExtPersonaJuridicaPublicaConsejoJudicatura
               )
             {
               datosUsuario.setTipoCertificado("Persona Jurídica Pública (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaJuridicaPublicaConsejoJudicatura.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaJuridicaPublicaConsejoJudicatura.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaJuridicaPublicaConsejoJudicatura.getPrimerApellido()
                     + " "
                     + certificadoExtPersonaJuridicaPublicaConsejoJudicatura.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtPersonaJuridicaPublicaConsejoJudicatura.getRuc());
               datosUsuario.setRazonSocial(certificadoExtPersonaJuridicaPublicaConsejoJudicatura.getRazonSocial());
               datosUsuario.setCargo(certificadoExtPersonaJuridicaPublicaConsejoJudicatura.getCargo());
            }

            if (certificadoConsejoJudicatura instanceof CertificadoExtSelladoTiempoConsejoJudicatura certificadoExtSelladoTiempoConsejoJudicatura) {
               datosUsuario.setTipoCertificado("Sellado de Tiempo (EXT)");
               datosUsuario.setCommonName(certificadoExtSelladoTiempoConsejoJudicatura.getCommonName());
               datosUsuario.setRuc(certificadoExtSelladoTiempoConsejoJudicatura.getRuc());
               datosUsuario.setRazonSocial(certificadoExtSelladoTiempoConsejoJudicatura.getRazonSocial());
            }
         }

         datosUsuario.setCertificadoDigitalValido(true);
      }

      return datosUsuario;
   }

   private static Certificado construir(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      if (CertUtils.hasCertificatePoliciesMatchingPattern(certificado, "1.3.6.1.4.1.43745.1.2", "")) {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.1.1.1")) {
            return new CertificadoExtPersonaNaturalConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.1.1.2")) {
            return new CertificadoExtPersonaNaturalConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.1.1.3")) {
            return new CertificadoExtPersonaNaturalConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.1.2.1")) {
            return new CertificadoExtPersonaNaturalConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.2.1.1")) {
            return new CertificadoExtPersonaJuridicaPrivadaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.2.1.2")) {
            return new CertificadoExtPersonaJuridicaPrivadaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.2.2.1")) {
            return new CertificadoExtPersonaJuridicaPrivadaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.3.1.1")) {
            return new CertificadoExtPersonaJuridicaPublicaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.3.1.2")) {
            return new CertificadoExtPersonaJuridicaPublicaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.3.1.3")) {
            return new CertificadoExtPersonaJuridicaPublicaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.3.2.1")) {
            return new CertificadoExtPersonaJuridicaPublicaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.4.1.1")) {
            return new CertificadoExtMiembroEmpresaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.4.1.2")) {
            return new CertificadoExtMiembroEmpresaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.4.1.3")) {
            return new CertificadoExtMiembroEmpresaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.1.4.2.1")) {
            return new CertificadoExtMiembroEmpresaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.2.1.1.2")) {
            return new CertificadoExtEmpresaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.2.1.2.1")) {
            return new CertificadoExtEmpresaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.3.1.1.2")) {
            return new CertificadoExtDepartamentoEmpresaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.3.1.2.1")) {
            return new CertificadoExtDepartamentoEmpresaConsejoJudicatura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.43745.1.2.4.1.1.3")) {
            return new CertificadoExtSelladoTiempoConsejoJudicatura(certificado);
         }
      }

      return null;
   }
}
