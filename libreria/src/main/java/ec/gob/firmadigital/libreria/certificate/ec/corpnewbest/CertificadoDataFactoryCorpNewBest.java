package ec.gob.firmadigital.libreria.certificate.ec.corpnewbest;

import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.Certificado;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoMiembroEmpresa;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoPersonaNatural;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoRepresentanteLegal;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelladoTiempo;
import ec.gob.firmadigital.libreria.certificate.ec.CertificadoSelloElectronico;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.cert.SubCa1CertCorpNewBest20232033;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.cert.SubCa1CertCorpNewBest2026022620400223;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.cert.SubCa2CertCorpNewBest20232033;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.cert.SubCa2CertCorpNewBest2026022620400223;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.cert.SubCa3CertCorpNewBest20232033;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.cert.SubCa3CertCorpNewBest2026022620400223;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.ext.CertificadoExtImplCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.ext.CertificadoExtMiembroEmpresaCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.ext.CertificadoExtPersonaJuridicaCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.ext.CertificadoExtPersonaNaturalCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.subj.CertificadoSubjMiembroEmpresaCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.subj.CertificadoSubjPersonaNaturalCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.subj.CertificadoSubjRepresentanteLegalCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.subj.CertificadoSubjSelladoTiempoCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.subj.CertificadoSubjSelloElectronicoCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.subj.CertificadoSubjImpl;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;

public class CertificadoDataFactoryCorpNewBest {
   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      try {
         if (Utils.verifySignature(certificado, new SubCa1CertCorpNewBest20232033())) {
            System.out.println("CorpNewBestSubCa1Cert");
            return new SubCa1CertCorpNewBest20232033();
         } else if (Utils.verifySignature(certificado, new SubCa2CertCorpNewBest20232033())) {
            System.out.println("CorpNewBestSubCa2Cert");
            return new SubCa2CertCorpNewBest20232033();
         } else if (Utils.verifySignature(certificado, new SubCa3CertCorpNewBest20232033())) {
            System.out.println("CorpNewBestSubCa3Cert");
            return new SubCa3CertCorpNewBest20232033();
         } else if (Utils.verifySignature(certificado, new SubCa1CertCorpNewBest2026022620400223())) {
            System.out.println("SubCa1CertCorpNewBest2026022620400223Cert");
            return new SubCa1CertCorpNewBest2026022620400223();
         } else if (Utils.verifySignature(certificado, new SubCa2CertCorpNewBest2026022620400223())) {
            System.out.println("SubCa2CertCorpNewBest2026022620400223Cert");
            return new SubCa2CertCorpNewBest2026022620400223();
         } else if (Utils.verifySignature(certificado, new SubCa3CertCorpNewBest2026022620400223())) {
            System.out.println("SubCa3CertCorpNewBest2026022620400223Cert");
            return new SubCa3CertCorpNewBest2026022620400223();
         } else {
            return null;
         }
      } catch (InvalidKeyException ex) {
         throw new EntidadCertificadoraNoValidaException("Tipo Certificado de CORPNEWBEST CIA. LTDA. sin categorizar!");
      }
   }

   public static DatosUsuario getDatosUsuarioCorpNewBest(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      DatosUsuario datosUsuario = null;
      Certificado certificadoCorpNewBest = construir(certificado);
      if (certificadoCorpNewBest != null) {
         datosUsuario = new DatosUsuario();
         if (certificadoCorpNewBest instanceof CertificadoExtImplCorpNewBest) {
            if (certificadoCorpNewBest instanceof CertificadoExtPersonaNaturalCorpNewBest certificadoExtPersonaNaturalCorpNewBest) {
               datosUsuario.setTipoCertificado("Persona Natural (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaNaturalCorpNewBest.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaNaturalCorpNewBest.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaNaturalCorpNewBest.getPrimerApellido() + " " + certificadoExtPersonaNaturalCorpNewBest.getSegundoApellido()
               );
            }

            if (certificadoCorpNewBest instanceof CertificadoExtPersonaJuridicaCorpNewBest certificadoExtPersonaJuridicaCorpNewBest) {
               datosUsuario.setTipoCertificado("Persona Jurídica (EXT)");
               datosUsuario.setCedula(certificadoExtPersonaJuridicaCorpNewBest.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtPersonaJuridicaCorpNewBest.getNombres());
               datosUsuario.setApellido(
                  certificadoExtPersonaJuridicaCorpNewBest.getPrimerApellido() + " " + certificadoExtPersonaJuridicaCorpNewBest.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtPersonaJuridicaCorpNewBest.getRuc());
               datosUsuario.setRazonSocial(certificadoExtPersonaJuridicaCorpNewBest.getRazonSocial());
               datosUsuario.setCargo(certificadoExtPersonaJuridicaCorpNewBest.getCargo());
            }

            if (certificadoCorpNewBest instanceof CertificadoExtMiembroEmpresaCorpNewBest certificadoExtMiembroEmpresaCorpNewBest) {
               datosUsuario.setTipoCertificado("Miembro Empresa (EXT)");
               datosUsuario.setCedula(certificadoExtMiembroEmpresaCorpNewBest.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoExtMiembroEmpresaCorpNewBest.getNombres());
               datosUsuario.setApellido(
                  certificadoExtMiembroEmpresaCorpNewBest.getPrimerApellido() + " " + certificadoExtMiembroEmpresaCorpNewBest.getSegundoApellido()
               );
               datosUsuario.setRuc(certificadoExtMiembroEmpresaCorpNewBest.getRuc());
               datosUsuario.setRazonSocial(certificadoExtMiembroEmpresaCorpNewBest.getRazonSocial());
               datosUsuario.setCargo(certificadoExtMiembroEmpresaCorpNewBest.getCargo());
            }
         }

         if (certificadoCorpNewBest instanceof CertificadoSubjImpl) {
            if (certificadoCorpNewBest instanceof CertificadoPersonaNatural certificadoPersonaNatural) {
               datosUsuario.setTipoCertificado("Persona Natural");
               datosUsuario.setCedula(certificadoPersonaNatural.getCedulaPasaporte());
               datosUsuario.setNombre(certificadoPersonaNatural.getNombres());
               datosUsuario.setApellido(certificadoPersonaNatural.getPrimerApellido());
            }

            if (certificadoCorpNewBest instanceof CertificadoMiembroEmpresa certificadoMiembroEmpresa) {
               datosUsuario.setTipoCertificado("Miembro Empresa");
               datosUsuario.setRuc(certificadoMiembroEmpresa.getRuc());
               datosUsuario.setRazonSocial(certificadoMiembroEmpresa.getRazonSocial());
               datosUsuario.setCargo(certificadoMiembroEmpresa.getCargo());
            }

            if (certificadoCorpNewBest instanceof CertificadoRepresentanteLegal certificadoRepresentanteLegal) {
               datosUsuario.setTipoCertificado("Representante Legal");
               datosUsuario.setRuc(certificadoRepresentanteLegal.getRuc());
               datosUsuario.setRazonSocial(certificadoRepresentanteLegal.getRazonSocial());
               datosUsuario.setCargo(certificadoRepresentanteLegal.getCargo());
            }

            if (certificadoCorpNewBest instanceof CertificadoSelloElectronico certificadoSelloElectronico) {
               datosUsuario.setTipoCertificado("Sello Electrónico");
               datosUsuario.setCommonName(certificadoSelloElectronico.getCommonName());
               datosUsuario.setRuc(certificadoSelloElectronico.getRuc());
               datosUsuario.setRazonSocial(certificadoSelloElectronico.getRazonSocial());
            }

            if (certificadoCorpNewBest instanceof CertificadoSelladoTiempo certificadoSelladoTiempo) {
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
      if (CertUtils.hasExtensionMatchingPattern(certificado, "1.3.6.1.4.1", "34380.3.1")) {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.34380.2.7")) {
            return new CertificadoExtPersonaNaturalCorpNewBest(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.34380.2.8")) {
            return new CertificadoExtPersonaJuridicaCorpNewBest(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.34380.2.10")) {
            return new CertificadoExtMiembroEmpresaCorpNewBest(certificado);
         }
      } else {
         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64849.2.1.1")) {
            return new CertificadoSubjPersonaNaturalCorpNewBest(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64849.2.1.2")) {
            return new CertificadoSubjPersonaNaturalCorpNewBest(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64849.2.2.1")) {
            return new CertificadoSubjMiembroEmpresaCorpNewBest(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64849.2.2.2")) {
            return new CertificadoSubjMiembroEmpresaCorpNewBest(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64849.2.3.1")) {
            return new CertificadoSubjRepresentanteLegalCorpNewBest(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64849.2.3.2")) {
            return new CertificadoSubjRepresentanteLegalCorpNewBest(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64849.102.2.4.1")) {
            return new CertificadoSubjSelloElectronicoCorpNewBest(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64849.102.2.4.2")) {
            return new CertificadoSubjSelloElectronicoCorpNewBest(certificado);
         }

         if (BouncyCastleUtils.certificateHasPolicy(certificado, "1.3.6.1.4.1.64849.102.2.5.1")) {
            return new CertificadoSubjSelladoTiempoCorpNewBest(certificado);
         }
      }

      return null;
   }
}
