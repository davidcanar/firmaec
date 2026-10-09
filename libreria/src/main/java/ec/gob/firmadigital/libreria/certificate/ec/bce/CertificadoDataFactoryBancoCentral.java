package ec.gob.firmadigital.libreria.certificate.ec.bce;

import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoFuncionarioPublico;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaJuridica;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelladoTiempo;
import ec.gob.firmadigital.libreria.certificate.ec.bce.cert.SubCaCertBce20112021;
import ec.gob.firmadigital.libreria.certificate.ec.bce.cert.SubCaCertBce20192029;
import ec.gob.firmadigital.libreria.certificate.ec.bce.ext.CertificadoExtFuncionarioPublicoBancoCentral;
import ec.gob.firmadigital.libreria.certificate.ec.bce.ext.CertificadoExtImplBancoCentral;
import ec.gob.firmadigital.libreria.certificate.ec.bce.ext.CertificadoExtPersonaJuridicaBancoCentral;
import ec.gob.firmadigital.libreria.certificate.ec.bce.ext.CertificadoExtPersonaNaturalBancoCentral;
import ec.gob.firmadigital.libreria.certificate.ec.bce.ext.CertificadoExtSelladoTiempoBancoCentral;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryBancoCentral {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertBce20112021())) {
            System.out.println("BceSubCaCert 2011-2021");
            return new SubCaCertBce20112021();
         } else if (Utils.verifySignature(certificado, new SubCaCertBce20192029())) {
            System.out.println("BceSubCaCert 2019-2029");
            return new SubCaCertBce20192029();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado del BANCO CENTRAL DEL ECUADOR sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioBancoCentral(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoBancoCentral = construir(certificado);
      if (certificadoBancoCentral != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoBancoCentral instanceof CertificadoExtImplBancoCentral) {
            if (certificadoBancoCentral instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural (EXT)");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido() + " " + certificadoPersonaNatural.getSegundoApellido());
            }

            if (certificadoBancoCentral instanceof CertificadoFuncionarioPublico certificadoFuncionarioPublico) {
               datosUsuario.setTipoCertificado("Funcionario Publico (EXT)");
               datosUsuario.setCedula(certificadoFuncionarioPublico.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoFuncionarioPublico.getNombres());
               datosUsuario.setApellido(certificadoFuncionarioPublico.getPrimerApellido() + " " + certificadoFuncionarioPublico.getSegundoApellido());
               datosUsuario.setRuc(certificadoFuncionarioPublico.getRuc());
               datosUsuario.setInstitucion(certificadoFuncionarioPublico.getInstitucion());
               datosUsuario.setCargo(certificadoFuncionarioPublico.getCargo());
            }

            if (certificadoBancoCentral instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa (EXT)");
               datosUsuario.setCedula(certificadoMiembroEmpresa.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoMiembroEmpresa.getNombres());
               datosUsuario.setApellido(certificadoMiembroEmpresa.getPrimerApellido() + " " + certificadoMiembroEmpresa.getSegundoApellido());
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoBancoCentral instanceof CertificadoPersonaJuridica certificadoPersonaJuridica) {
               datosUsuario.setTipoCertificado("Persona Jurídica (EXT)");
               datosUsuario.setCedula(certificadoPersonaJuridica.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaJuridica.getNombres());
               datosUsuario.setApellido(certificadoPersonaJuridica.getPrimerApellido() + " " + certificadoPersonaJuridica.getSegundoApellido());
               datosUsuario.setRuc(certificadoPersonaJuridica.getRuc());
               datosUsuario.setRazonSocial(certificadoPersonaJuridica.getRazonSocial());
               datosUsuario.setCargo(certificadoPersonaJuridica.getCargo());
            }

            if (certificadoBancoCentral instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
               datosUsuario.setTipoCertificado("Representante Legal (EXT)");
               datosUsuario.setCedula(certificadoRepresentanteLegal.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoRepresentanteLegal.getNombres());
               datosUsuario.setApellido(certificadoRepresentanteLegal.getPrimerApellido() + " " + certificadoRepresentanteLegal.getSegundoApellido());
               datosUsuario.setRuc(certificadoRepresentanteLegal.getRuc());
               datosUsuario.setRazonSocial(certificadoRepresentanteLegal.getRazonSocial());
               datosUsuario.setCargo(certificadoRepresentanteLegal.getCargo());
            }

            if (certificadoBancoCentral instanceof CertificadoSelladoTiempo certificadoSelladoTiempo) {
               datosUsuario.setTipoCertificado("Sellado de Tiempo (EXT)");
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
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37947.2.1.1")) {
            return new CertificadoExtPersonaNaturalBancoCentral(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37947.2.2.1")) {
            return new CertificadoExtPersonaJuridicaBancoCentral(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37947.2.3.1")) {
            return new CertificadoExtFuncionarioPublicoBancoCentral(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37947.4.1")) {
            return new CertificadoExtSelladoTiempoBancoCentral(certificado);
         }
      }

      return null;
   }
}
