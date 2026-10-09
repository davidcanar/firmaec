package ec.gob.firmadigital.libreria.certificate.ec.digercic;

import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.digercic.cert.SubCaCertDigercic20212031;
import ec.gob.firmadigital.libreria.certificate.ec.digercic.ext.CertificadoExtImplDigercic;
import ec.gob.firmadigital.libreria.certificate.ec.digercic.ext.CertificadoExtPersonaNaturalDigercic;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryDigercic {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertDigercic20212031())) {
            System.out.println("SubCA Digercic");
            return new SubCaCertDigercic20212031();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado de DIRECCIÓN GENERAL DE REGISTRO CIVIL, IDENTIFICACIÓN sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioDatil(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoDigercic = construir(certificado);
      if (certificadoDigercic != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoDigercic instanceof CertificadoExtImplDigercic
            && certificadoDigercic instanceof CertificadoExtPersonaNaturalDigercic certificadoExtPersonaNaturalDigercic) {
            datosUsuario.setTipoCertificado("Persona Natural (EXT)");
            datosUsuario.setCedula(certificadoExtPersonaNaturalDigercic.getCedulaPasaporte());
            datosUsuario.setNombre(Utils.getCN(certificado));
            datosUsuario.setApellido("");
         }

         datosUsuario.setCertificadoDigitalValido(true);
      }

      return datosUsuario;
   }

   private static Certificado construir(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      if (CertUtils.hasExtensionMatchingPattern(certificado, "1.3.6.1.4.1", "3.1")) {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.55519.1.1.1.5.3.2")) {
            return new CertificadoExtPersonaNaturalDigercic(certificado);
         } else {
            throw new EntidadCertificadoraNoValidaException("Certificado de DIRECCIÓN GENERAL DE REGISTRO CIVIL, IDENTIFICACIÓN sin categorizar!");
         }
      } else {
         return null;
      }
   }
}
