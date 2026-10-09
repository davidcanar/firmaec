package ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies;

import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.cert.SubCaCertAlphaTechnologies20232026;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.cert.SubCaCertAlphaTechnologies20242032;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.ext.CertificadoExtImplAlphaTechnologies;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.ext.CertificadoExtMiembroEmpresaAlphaTechnologies;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.ext.CertificadoExtPersonaJuridicaAlphaTechnologies;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.ext.CertificadoExtPersonaNaturalAlphaTechnologies;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.subj.CertificadoSubjMiembroEmpresaAlphaTechnologies;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.subj.CertificadoSubjPersonaNaturalAlphaTechnologies;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.subj.CertificadoSubjRepresentanteLegalAlphaTechnologies;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryAlphaTechnologies {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertAlphaTechnologies20232026())) {
            System.out.println("AlphaTechnologiesSubCaCert 2023-2026");
            return new SubCaCertAlphaTechnologies20232026();
         } else if (Utils.verifySignature(certificado, new SubCaCertAlphaTechnologies20242032())) {
            System.out.println("AlphaTechnologiesSubCaCert 2024-2032");
            return new SubCaCertAlphaTechnologies20242032();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado de ALPHA TECHNOLOGIES CIA. LTDA. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioAlphaTechnologies(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoAlphaTechnologies = construir(certificado);
      if (certificadoAlphaTechnologies != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoAlphaTechnologies instanceof CertificadoExtImplAlphaTechnologies) {
            datosUsuario.setTipoCertificado("Persona Natural (EXT)");
            if (certificadoAlphaTechnologies instanceof CertificadoExtPersonaNaturalAlphaTechnologies certificadoPersonaNatural) {
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido() + " " + certificadoPersonaNatural.getSegundoApellido());
            }

            if (certificadoAlphaTechnologies instanceof CertificadoExtMiembroEmpresaAlphaTechnologies certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa (EXT)");
               datosUsuario.setCedula(certificadoMiembroEmpresa.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoMiembroEmpresa.getNombres());
               datosUsuario.setApellido(certificadoMiembroEmpresa.getPrimerApellido() + " " + certificadoMiembroEmpresa.getSegundoApellido());
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoAlphaTechnologies instanceof CertificadoExtPersonaJuridicaAlphaTechnologies certificadoPersonaJuridica) {
               datosUsuario.setTipoCertificado("Persona Jurídica (EXT)");
               datosUsuario.setCedula(certificadoPersonaJuridica.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaJuridica.getNombres());
               datosUsuario.setApellido(certificadoPersonaJuridica.getPrimerApellido() + " " + certificadoPersonaJuridica.getSegundoApellido());
               datosUsuario.setRuc(certificadoPersonaJuridica.getRuc());
               datosUsuario.setRazonSocial(certificadoPersonaJuridica.getRazonSocial());
               datosUsuario.setCargo(certificadoPersonaJuridica.getCargo());
            }
         }

         if (certificadoAlphaTechnologies instanceof CertificadoSubjImpl) {
            if (certificadoAlphaTechnologies instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
            }

            if (certificadoAlphaTechnologies instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoAlphaTechnologies instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
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
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.56105.2.1")) {
            return new CertificadoExtPersonaNaturalAlphaTechnologies(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.56105.2.2")) {
            return new CertificadoExtPersonaJuridicaAlphaTechnologies(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.56105.2.3")) {
            return new CertificadoExtMiembroEmpresaAlphaTechnologies(certificado);
         }
      } else {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.56105.2.1.1")) {
            return new CertificadoSubjPersonaNaturalAlphaTechnologies(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.56105.2.2.1")) {
            return new CertificadoSubjMiembroEmpresaAlphaTechnologies(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.56105.2.3.1")) {
            return new CertificadoSubjRepresentanteLegalAlphaTechnologies(certificado);
         }
      }

      return null;
   }
}
