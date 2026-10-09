package ec.gob.firmadigital.libreria.certificate.ec.appfirmas;

import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.appfirmas.cert.SubCaCertAppFirmas20252050;
import ec.gob.firmadigital.libreria.certificate.ec.appfirmas.cert.SubCaCertAppFirmas20262036;
import ec.gob.firmadigital.libreria.certificate.ec.appfirmas.subj.CertificadoSubjPersonaNaturalAppFirmas;
import ec.gob.firmadigital.libreria.certificate.ec.appfirmas.subj.CertificadoSubjRepresentanteLegalAppFirmas;
import ec.gob.firmadigital.libreria.certificate.ec.appfirmas.subj.CertificadoSubjSelloElectronicoAppFirmas;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryAppFirmas {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertAppFirmas20252050())) {
            System.out.println("AppFirmasSubCaCert2025-2050");
            return new SubCaCertAppFirmas20252050();
         } else if (Utils.verifySignature(certificado, new SubCaCertAppFirmas20262036())) {
            System.out.println("AppFirmasSubCaCert2026-2036");
            return new SubCaCertAppFirmas20262036();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado de APPFIRMAS S.A. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioAppFirmas(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoAppFirmas = construir(certificado);
      if (certificadoAppFirmas != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoAppFirmas instanceof CertificadoSubjImpl) {
            if (certificadoAppFirmas instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
            }

            if (certificadoAppFirmas instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoAppFirmas instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
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
      if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62431.2.1.1")) {
         return new CertificadoSubjPersonaNaturalAppFirmas(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62431.2.2.1")) {
         return new CertificadoSubjSelloElectronicoAppFirmas(certificado);
      } else {
         return BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.62431.2.3.1")
            ? new CertificadoSubjRepresentanteLegalAppFirmas(certificado)
            : null;
      }
   }
}
