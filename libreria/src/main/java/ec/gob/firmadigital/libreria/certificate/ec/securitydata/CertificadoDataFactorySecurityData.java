package ec.gob.firmadigital.libreria.certificate.ec.securitydata;

import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelladoTiempo;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelloElectronico;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.cert.SubCaCertSecurityData20112026;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.cert.SubCaCertSecurityData20192031;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.cert.SubCaCertSecurityData20202039;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.ext.CertificadoExtFuncionarioPublicoSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.ext.CertificadoExtImplSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.ext.CertificadoExtMiembroEmpresaSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.ext.CertificadoExtPersonaJuridicaSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.ext.CertificadoExtPersonaNaturalSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.ext.CertificadoExtRepresentanteLegalSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.ext.CertificadoExtSelladoTiempoSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.subj.CertificadoSubjMiembroEmpresaSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.subj.CertificadoSubjPersonaNaturalSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.subj.CertificadoSubjRepresentanteLegalSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.subj.CertificadoSubjSelladoTiempoSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.subj.CertificadoSubjSelloElectronicoSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactorySecurityData {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCaCertSecurityData20112026())) {
            System.out.println("SecurityDataSubCaCert");
            return new SubCaCertSecurityData20112026();
         } else if (Utils.verifySignature(certificado, new SubCaCertSecurityData20192031())) {
            System.out.println("SecurityDataSubCaCert 2019-2031");
            return new SubCaCertSecurityData20192031();
         } else if (Utils.verifySignature(certificado, new SubCaCertSecurityData20202039())) {
            System.out.println("SecurityDataSubCaCert 2020-2032");
            return new SubCaCertSecurityData20202039();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Certificado de SECURITY DATA SEGURIDAD EN DATOS Y FIRMA DIGITAL S.A. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioSecurityData(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoSecurityData = construir(certificado);
      if (certificadoSecurityData != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoSecurityData instanceof CertificadoExtImplSecurityData) {
            if (certificadoSecurityData instanceof CertificadoExtPersonaNaturalSecurityData certificadoExtPersonaNaturalSecurityData) {
               datosUsuario.setTipoCertificado("Persona Natural (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaNaturalSecurityData.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaNaturalSecurityData.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaNaturalSecurityData.getPrimerApellido() + " " + certificadoExtPersonaNaturalSecurityData.getSegundoApellido()
               );
            }

            if (certificadoSecurityData instanceof CertificadoExtFuncionarioPublicoSecurityData certificadoExtFuncionarioPublicoSecurityData) {
               datosUsuario.setTipoCertificado("Funcionario Público (EXT)");
               datosUsuario.setCedula(certificadoExtFuncionarioPublicoSecurityData.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtFuncionarioPublicoSecurityData.getNombres());
               datosUsuario.setApellido(
                  certificadoExtFuncionarioPublicoSecurityData.getPrimerApellido() + " " + certificadoExtFuncionarioPublicoSecurityData.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtFuncionarioPublicoSecurityData.getRuc());
               datosUsuario.setInstitucion(certificadoExtFuncionarioPublicoSecurityData.getInstitucion());
               datosUsuario.setRazonSocial(certificadoExtFuncionarioPublicoSecurityData.getRazonSocial());
               datosUsuario.setCargo(certificadoExtFuncionarioPublicoSecurityData.getCargo());
            }

            if (certificadoSecurityData instanceof CertificadoExtMiembroEmpresaSecurityData certificadoExtMiembroEmpresaSecurityData) {
               datosUsuario.setTipoCertificado("Miembro de Empresa (EXT)");
               datosUsuario.setCedula(certificadoExtMiembroEmpresaSecurityData.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtMiembroEmpresaSecurityData.getNombres());
               datosUsuario.setApellido(
                  certificadoExtMiembroEmpresaSecurityData.getPrimerApellido() + " " + certificadoExtMiembroEmpresaSecurityData.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtMiembroEmpresaSecurityData.getRuc());
               datosUsuario.setRazonSocial(certificadoExtMiembroEmpresaSecurityData.getRazonSocial());
               datosUsuario.setCargo(certificadoExtMiembroEmpresaSecurityData.getCargo());
            }

            if (certificadoSecurityData instanceof CertificadoExtRepresentanteLegalSecurityData certificadoExtRepresentanteLegalSecurityData) {
               datosUsuario.setTipoCertificado("Representante Legal (EXT)");
               datosUsuario.setCedula(certificadoExtRepresentanteLegalSecurityData.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtRepresentanteLegalSecurityData.getNombres());
               datosUsuario.setApellido(
                  certificadoExtRepresentanteLegalSecurityData.getPrimerApellido() + " " + certificadoExtRepresentanteLegalSecurityData.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtRepresentanteLegalSecurityData.getRuc());
               datosUsuario.setRazonSocial(certificadoExtRepresentanteLegalSecurityData.getRazonSocial());
               datosUsuario.setCargo(certificadoExtRepresentanteLegalSecurityData.getCargo());
            }

            if (certificadoSecurityData instanceof CertificadoExtPersonaJuridicaSecurityData certificadoExtPersonaJuridicaSecurityData) {
               datosUsuario.setTipoCertificado("Persona Jurídica (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaJuridicaSecurityData.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaJuridicaSecurityData.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaJuridicaSecurityData.getPrimerApellido() + " " + certificadoExtPersonaJuridicaSecurityData.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtPersonaJuridicaSecurityData.getRuc());
               datosUsuario.setRazonSocial(certificadoExtPersonaJuridicaSecurityData.getRazonSocial());
               datosUsuario.setCargo(certificadoExtPersonaJuridicaSecurityData.getCargo());
            }

            if (certificadoSecurityData instanceof CertificadoExtSelladoTiempoSecurityData certificadoExtSelladoTiempoSecurityData) {
               datosUsuario.setTipoCertificado("Sellado de Tiempo (EXT)");
               datosUsuario.setCedula("");
               datosUsuario.setNombre("");
               datosUsuario.setApellido("");
               datosUsuario.setCommonName("");
               datosUsuario.setRuc("");
               datosUsuario.setRazonSocial("");
            }
         }

         if (certificadoSecurityData instanceof CertificadoSubjImpl) {
            if (certificadoSecurityData instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
            }

            if (certificadoSecurityData instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoSecurityData instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
               datosUsuario.setTipoCertificado("Representante Legal");
               datosUsuario.setRuc(certificadoRepresentanteLegal.getRuc());
               datosUsuario.setRazonSocial(certificadoRepresentanteLegal.getRazonSocial());
               datosUsuario.setCargo(certificadoRepresentanteLegal.getCargo());
            }

            if (certificadoSecurityData instanceof CertificadoSelloElectronico certificadoSelloElectronico) {
               datosUsuario.setTipoCertificado("Sellado Electrónico");
               datosUsuario.setCommonName(certificadoSelloElectronico.getCommonName());
               datosUsuario.setRuc(certificadoSelloElectronico.getRuc());
               datosUsuario.setRazonSocial(certificadoSelloElectronico.getRazonSocial());
            }

            if (certificadoSecurityData instanceof CertificadoSelladoTiempo certificadoSelladoTiempo) {
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
      if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.7")) {
         return new CertificadoExtPersonaNaturalSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.8")) {
         return new CertificadoExtPersonaJuridicaSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.9")) {
         return new CertificadoExtRepresentanteLegalSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.10")) {
         return new CertificadoExtMiembroEmpresaSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.11")) {
         return new CertificadoExtFuncionarioPublicoSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.15")) {
         return new CertificadoExtPersonaNaturalSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.3.28")) {
         return new CertificadoExtSelladoTiempoSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.1.1")) {
         return new CertificadoSubjPersonaNaturalSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.1.2")) {
         return new CertificadoSubjPersonaNaturalSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.3.1")) {
         return new CertificadoSubjRepresentanteLegalSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.3.2")) {
         return new CertificadoSubjRepresentanteLegalSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.2.1")) {
         return new CertificadoSubjMiembroEmpresaSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.2.2.2")) {
         return new CertificadoSubjMiembroEmpresaSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.102.2.4.1")) {
         return new CertificadoSubjSelloElectronicoSecurityData(certificado);
      } else if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.102.2.4.2")) {
         return new CertificadoSubjSelloElectronicoSecurityData(certificado);
      } else {
         return BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.37746.102.2.5.1")
            ? new CertificadoSubjSelladoTiempoSecurityData(certificado)
            : null;
      }
   }
}
