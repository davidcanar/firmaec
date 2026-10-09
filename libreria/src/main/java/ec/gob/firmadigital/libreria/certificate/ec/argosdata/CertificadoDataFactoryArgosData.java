package ec.gob.firmadigital.libreria.certificate.ec.argosdata;

import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.argosdata.cert.SubCaCertArgosData20242032;
import ec.gob.firmadigital.libreria.certificate.ec.argosdata.cert.SubCaCertArgosData20262036;
import ec.gob.firmadigital.libreria.certificate.ec.argosdata.ext.CertificadoExtImplArgosData;
import ec.gob.firmadigital.libreria.certificate.ec.argosdata.ext.CertificadoExtPersonaNaturalArgosData;
import ec.gob.firmadigital.libreria.certificate.ec.argosdata.ext.CertificadoExtRepresentanteLegalArgosData;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryArgosData {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertArgosData20242032())) {
            System.out.println("SubCA ArgosData 2024-2032");
            return new SubCaCertArgosData20242032();
         } else if (Utils.verifySignature(certificado, new SubCaCertArgosData20262036())) {
            System.out.println("SubCA ArgosData 2026-2036");
            return new SubCaCertArgosData20262036();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException(
            "Certificado de ARGOSDATA CERTIFICACIÓN DE INFORMACIÓN Y SERVICIOS RELACIONADOS S.A.S. sin categorizar!"
         );
      }
   }

   public static DatosUsuario getDatosUsuarioArgosData(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoArgosData = construir(certificado);
      if (certificadoArgosData != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoArgosData instanceof CertificadoExtImplArgosData) {
            if (certificadoArgosData instanceof CertificadoExtPersonaNaturalArgosData certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural (EXT)");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido() + " " + certificadoPersonaNatural.getSegundoApellido());
            }

            if (certificadoArgosData instanceof CertificadoExtRepresentanteLegalArgosData certificadoRepresentanteLegal) {
               datosUsuario.setTipoCertificado("Representante Legal (EXT)");
               datosUsuario.setCedula(certificadoRepresentanteLegal.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoRepresentanteLegal.getNombres());
               datosUsuario.setApellido(certificadoRepresentanteLegal.getPrimerApellido() + " " + certificadoRepresentanteLegal.getSegundoApellido());
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
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.59198.2.1.1")) {
            return new CertificadoExtPersonaNaturalArgosData(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.59198.2.2.1")) {
            return new CertificadoExtRepresentanteLegalArgosData(certificado);
         }
      }

      return null;
   }
}
