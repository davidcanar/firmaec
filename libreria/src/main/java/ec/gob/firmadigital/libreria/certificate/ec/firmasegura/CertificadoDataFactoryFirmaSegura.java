package ec.gob.firmadigital.libreria.certificate.ec.firmasegura;

import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.firmasegura.cert.SubCaCertFirmaSegura20232043;
import ec.gob.firmadigital.libreria.certificate.ec.firmasegura.ext.CertificadoExtImplFirmaSegura;
import ec.gob.firmadigital.libreria.certificate.ec.firmasegura.ext.CertificadoExtPersonaNaturalFirmaSegura;
import ec.gob.firmadigital.libreria.certificate.ec.firmasegura.ext.CertificadoExtRepresentanteLegalFirmaSegura;
import ec.gob.firmadigital.libreria.certificate.ec.firmasegura.subj.CertificadoSubjMiembroEmpresaFirmaSegura;
import ec.gob.firmadigital.libreria.certificate.ec.firmasegura.subj.CertificadoSubjPersonaNaturalFirmaSegura;
import ec.gob.firmadigital.libreria.certificate.ec.firmasegura.subj.CertificadoSubjRepresentanteLegalFirmaSegura;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryFirmaSegura {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertFirmaSegura20232043())) {
            System.out.println("FirmaSeguraSubCaCert2023-2043");
            return new SubCaCertFirmaSegura20232043();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Tipo Certificado de FIRMASEGURA S.A.S. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioFirmaSegura(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoFirmaSegura = construir(certificado);
      if (certificadoFirmaSegura != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoFirmaSegura instanceof CertificadoExtImplFirmaSegura) {
            if (certificadoFirmaSegura instanceof CertificadoExtPersonaNaturalFirmaSegura certificadoExtPersonaNaturalFirmaSegura) {
               datosUsuario.setTipoCertificado("Persona Natural (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaNaturalFirmaSegura.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaNaturalFirmaSegura.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaNaturalFirmaSegura.getPrimerApellido() + " " + certificadoExtPersonaNaturalFirmaSegura.getSegundoApellido()
               );
            }

            if (certificadoFirmaSegura instanceof CertificadoExtRepresentanteLegalFirmaSegura certificadoExtRepresentanteLegalFirmaSegura) {
               datosUsuario.setTipoCertificado("Representante Legal (EXT)");
               datosUsuario.setCedula(certificadoExtRepresentanteLegalFirmaSegura.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtRepresentanteLegalFirmaSegura.getNombres());
               datosUsuario.setApellido(
                  certificadoExtRepresentanteLegalFirmaSegura.getPrimerApellido() + " " + certificadoExtRepresentanteLegalFirmaSegura.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtRepresentanteLegalFirmaSegura.getRuc());
               datosUsuario.setRazonSocial(certificadoExtRepresentanteLegalFirmaSegura.getRazonSocial());
               datosUsuario.setCargo(certificadoExtRepresentanteLegalFirmaSegura.getCargo());
            }
         }

         if (certificadoFirmaSegura instanceof CertificadoSubjImpl) {
            if (certificadoFirmaSegura instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
               datosUsuario.setRuc(certificadoPersonaNatural.getRuc());
            }

            if (certificadoFirmaSegura instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoFirmaSegura instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
               datosUsuario.setTipoCertificado("Representante Legal");
               datosUsuario.setRuc(certificadoRepresentanteLegal.getRuc());
               datosUsuario.setRazonSocial(certificadoRepresentanteLegal.getRazonSocial());
               datosUsuario.setCargo(certificadoRepresentanteLegal.getCargo());
            }
         }

         datosUsuario.setCertificadoDigitalValido(true);
      }

      return datosUsuario;
   }

   private static Certificado construir(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      if (CertUtils.hasExtensionMatchingPattern(certificado, "1.3.6.1.4.1", "3.1")) {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.61305.2.2.1")) {
            return new CertificadoExtPersonaNaturalFirmaSegura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.61305.2.2.2")) {
            return new CertificadoExtRepresentanteLegalFirmaSegura(certificado);
         }
      } else {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.61305.2.1.1")) {
            return new CertificadoSubjPersonaNaturalFirmaSegura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.61305.2.1.2")) {
            return new CertificadoSubjPersonaNaturalFirmaSegura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.61305.2.3.1")) {
            return new CertificadoSubjRepresentanteLegalFirmaSegura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.61305.2.3.2")) {
            return new CertificadoSubjRepresentanteLegalFirmaSegura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.61305.2.2.1")) {
            return new CertificadoSubjMiembroEmpresaFirmaSegura(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.61305.2.2.2")) {
            return new CertificadoSubjMiembroEmpresaFirmaSegura(certificado);
         }
      }

      return null;
   }
}
