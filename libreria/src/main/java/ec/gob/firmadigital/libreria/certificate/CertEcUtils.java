package ec.gob.firmadigital.libreria.certificate;

import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.CertificadoDataFactoryAlphaTechnologies;
import ec.gob.firmadigital.libreria.certificate.ec.anfac.CertificadoDataFactoryAnfAc;
import ec.gob.firmadigital.libreria.certificate.ec.appfirmas.CertificadoDataFactoryAppFirmas;
import ec.gob.firmadigital.libreria.certificate.ec.argosdata.CertificadoDataFactoryArgosData;
import ec.gob.firmadigital.libreria.certificate.ec.bce.CertificadoDataFactoryBancoCentral;
import ec.gob.firmadigital.libreria.certificate.ec.cj.CertificadoDataFactoryConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.CertificadoDataFactoryCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.darkcam.CertificadoDataFactoryDarkcam;
import ec.gob.firmadigital.libreria.certificate.ec.datil.CertificadoDataFactoryDatil;
import ec.gob.firmadigital.libreria.certificate.ec.digercic.CertificadoDataFactoryDigercic;
import ec.gob.firmadigital.libreria.certificate.ec.eclipsoft.CertificadoDataFactoryEclipsoft;
import ec.gob.firmadigital.libreria.certificate.ec.firmasegura.CertificadoDataFactoryFirmaSegura;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.CertificadoDataFactoryLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.letmi.CertificadoDataFactoryLetmi;
import ec.gob.firmadigital.libreria.certificate.ec.primecorelat.CertificadoDataFactoryPrimeCoreLat;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.CertificadoDataFactorySecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.CertificadoDataFactoryUanataca;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.security.cert.X509Certificate;

public class CertEcUtils {
   public static final String ALPHATECHNOLOGIES_NAME = "ALPHA TECHNOLOGIES CIA. LTDA.";
   public static final String ANFAC_NAME = "ANFAC AUTORIDAD DE CERTIFICACION ECUADOR C.A.";
   public static final String ANFAC_NAME_OLD = "ANFAC AUTORIDAD DE CERTIFICACION ECUADOR CA";
   public static final String APPFIRMAS_NAME = "APPFIRMAS S.A.";
   public static final String APPFIRMAS_NAME_OLD = "APPFIRMAS S.A. SUB CA";
   public static final String AGOSDATA_NAME = "ARGOSDATA CERTIFICACIÓN DE INFORMACIÓN Y SERVICIOS RELACIONADOS";
   public static final String AGOSDATA_NAME_OLD = "ARGOSDATA";
   public static final String BCE_NAME = "BANCO CENTRAL DEL ECUADOR";
   public static final String CJ_NAME = "CONSEJO DE LA JUDICATURA";
   public static final String CORPNEWBEST_NAME = "CORPNEWBEST CIA. LTDA.";
   public static final String DARKCAM_NAME = "DARKCAM S.A.";
   public static final String DATIL_NAME = "DATILMEDIA S.A.";
   public static final String DIGERCIC_NAME = "DIRECCIÓN GENERAL DE REGISTRO CIVIL, IDENTIFICACIÓN Y CEDULACIÓN";
   public static final String ECLIPSOFT_NAME = "ECLIPSOFT S.A.";
   public static final String FIRMASEGURA_NAME = "FIRMASEGURA S.A.S.";
   public static final String LAZZATE_NAME = "LAZZATE CIA. LTDA.";
   public static final String LETMI_NAME = "LETMI ECUADOR S.A.";
   public static final String PRIMECORELAT_NAME = "PRIMECORELAT S.A.S. B.I.C.";
   public static final String SECURITYDATA_NAME = "SECURITY DATA SEGURIDAD EN DATOS Y FIRMA DIGITAL S.A.";
   public static final String SECURITYDATA_NAME_OLD = "SECURITY DATA";
   public static final String UANATACA_NAME = "UANATACA ECUADOR S.A.";
   public static final String UANATACA_NAME_OLD = "UANATACA S.A.";
   public static final String ECLIPSOFT_COMMON_NAME = "UANATACA CA1 2016";

   public static String getNombreCA(X509Certificate certificado) {
      String organization = Utils.getRDNvalueFromLdapName("O", certificado.getIssuerX500Principal().toString());
      if (organization.toUpperCase().equals("ALPHA TECHNOLOGIES CIA. LTDA.")) {
         return "ALPHA TECHNOLOGIES CIA. LTDA.";
      } else if (organization.toUpperCase().equals("ANFAC AUTORIDAD DE CERTIFICACION ECUADOR C.A.")) {
         return "ANFAC AUTORIDAD DE CERTIFICACION ECUADOR C.A.";
      } else if (organization.toUpperCase().equals("ANFAC AUTORIDAD DE CERTIFICACION ECUADOR CA")) {
         return "ANFAC AUTORIDAD DE CERTIFICACION ECUADOR CA";
      } else if (organization.toUpperCase().equals("APPFIRMAS S.A.")) {
         return "APPFIRMAS S.A.";
      } else if (organization.toUpperCase().equals("APPFIRMAS S.A. SUB CA")) {
         return "APPFIRMAS S.A. SUB CA";
      } else if (organization.toUpperCase().contains("ARGOSDATA CERTIFICACIÓN DE INFORMACIÓN Y SERVICIOS RELACIONADOS")) {
         return "ARGOSDATA CERTIFICACIÓN DE INFORMACIÓN Y SERVICIOS RELACIONADOS";
      } else if (organization.toUpperCase().equals("ARGOSDATA")) {
         return "ARGOSDATA";
      } else if (organization.toUpperCase().equals("BANCO CENTRAL DEL ECUADOR")) {
         return "BANCO CENTRAL DEL ECUADOR";
      } else if (organization.toUpperCase().equals("CONSEJO DE LA JUDICATURA")) {
         return "CONSEJO DE LA JUDICATURA";
      } else if (organization.toUpperCase().equals("CORPNEWBEST CIA. LTDA.")) {
         return "CORPNEWBEST CIA. LTDA.";
      } else if (organization.toUpperCase().equals("DARKCAM S.A.")) {
         return "DARKCAM S.A.";
      } else if (organization.toUpperCase().equals("DATILMEDIA S.A.")) {
         return "DATILMEDIA S.A.";
      } else if (organization.toUpperCase().equals("DIRECCIÓN GENERAL DE REGISTRO CIVIL, IDENTIFICACIÓN Y CEDULACIÓN")) {
         return "DIRECCIÓN GENERAL DE REGISTRO CIVIL, IDENTIFICACIÓN Y CEDULACIÓN";
      } else if (organization.toUpperCase().equals("ECLIPSOFT S.A.")) {
         return "ECLIPSOFT S.A.";
      } else if (organization.toUpperCase().equals("FIRMASEGURA S.A.S.")) {
         return "FIRMASEGURA S.A.S.";
      } else if (organization.toUpperCase().equals("LAZZATE CIA. LTDA.")) {
         return "LAZZATE CIA. LTDA.";
      } else if (organization.toUpperCase().equals("LETMI ECUADOR S.A.")) {
         return "LETMI ECUADOR S.A.";
      } else if (organization.toUpperCase().equals("PRIMECORELAT S.A.S. B.I.C.")) {
         return "PRIMECORELAT S.A.S. B.I.C.";
      } else if (organization.toUpperCase().equals("SECURITY DATA SEGURIDAD EN DATOS Y FIRMA DIGITAL S.A.")) {
         return "SECURITY DATA SEGURIDAD EN DATOS Y FIRMA DIGITAL S.A.";
      } else if (organization.toUpperCase().contains("SECURITY DATA")) {
         return "SECURITY DATA";
      } else if (organization.toUpperCase().equals("UANATACA ECUADOR S.A.")) {
         return "UANATACA ECUADOR S.A.";
      } else if (organization.toUpperCase().contains("UANATACA S.A.")) {
         String commonName = Utils.getRDNvalueFromLdapName("CN", certificado.getIssuerX500Principal().toString());
         return commonName.toUpperCase().equals("UANATACA CA1 2016") ? "ECLIPSOFT S.A." : "UANATACA S.A.";
      } else {
         return "Entidad no reconocida " + certificado.getIssuerX500Principal().getName();
      }
   }

   public static X509Certificate getRootCertificate(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      switch (getNombreCA(certificado)) {
         case "ALPHA TECHNOLOGIES CIA. LTDA.":
            return CertificadoDataFactoryAlphaTechnologies.getRootCertificate(certificado);
         case "ANFAC AUTORIDAD DE CERTIFICACION ECUADOR C.A.":
            return CertificadoDataFactoryAnfAc.getRootCertificate(certificado);
         case "ANFAC AUTORIDAD DE CERTIFICACION ECUADOR CA":
            return CertificadoDataFactoryAnfAc.getRootCertificate(certificado);
         case "APPFIRMAS S.A.":
            return CertificadoDataFactoryAppFirmas.getRootCertificate(certificado);
         case "APPFIRMAS S.A. SUB CA":
            return CertificadoDataFactoryAppFirmas.getRootCertificate(certificado);
         case "ARGOSDATA CERTIFICACIÓN DE INFORMACIÓN Y SERVICIOS RELACIONADOS":
            return CertificadoDataFactoryArgosData.getRootCertificate(certificado);
         case "ARGOSDATA":
            return CertificadoDataFactoryArgosData.getRootCertificate(certificado);
         case "BANCO CENTRAL DEL ECUADOR":
            return CertificadoDataFactoryBancoCentral.getRootCertificate(certificado);
         case "CONSEJO DE LA JUDICATURA":
            return CertificadoDataFactoryConsejoJudicatura.getRootCertificate(certificado);
         case "CORPNEWBEST CIA. LTDA.":
            return CertificadoDataFactoryCorpNewBest.getRootCertificate(certificado);
         case "DARKCAM S.A.":
            return CertificadoDataFactoryDarkcam.getRootCertificate(certificado);
         case "DATILMEDIA S.A.":
            return CertificadoDataFactoryDatil.getRootCertificate(certificado);
         case "DIRECCIÓN GENERAL DE REGISTRO CIVIL, IDENTIFICACIÓN Y CEDULACIÓN":
            return CertificadoDataFactoryDigercic.getRootCertificate(certificado);
         case "ECLIPSOFT S.A.":
            return CertificadoDataFactoryEclipsoft.getRootCertificate(certificado);
         case "FIRMASEGURA S.A.S.":
            return CertificadoDataFactoryFirmaSegura.getRootCertificate(certificado);
         case "LAZZATE CIA. LTDA.":
            return CertificadoDataFactoryLazzate.getRootCertificate(certificado);
         case "LETMI ECUADOR S.A.":
            return CertificadoDataFactoryLetmi.getRootCertificate(certificado);
         case "PRIMECORELAT S.A.S. B.I.C.":
            return CertificadoDataFactoryPrimeCoreLat.getRootCertificate(certificado);
         case "SECURITY DATA SEGURIDAD EN DATOS Y FIRMA DIGITAL S.A.":
            return CertificadoDataFactorySecurityData.getRootCertificate(certificado);
         case "SECURITY DATA":
            return CertificadoDataFactorySecurityData.getRootCertificate(certificado);
         case "UANATACA ECUADOR S.A.":
            return CertificadoDataFactoryUanataca.getRootCertificate(certificado);
         case "UANATACA S.A.":
            return CertificadoDataFactoryUanataca.getRootCertificate(certificado);
         default:
            throw new EntidadCertificadoraNoValidaException("Entidad Certificadora no reconocida");
      }
   }

   public static DatosUsuario getDatosUsuarios(X509Certificate certificado) throws EntidadCertificadoraNoValidaException {
      return switch (getNombreCA(certificado)) {
         case "ALPHA TECHNOLOGIES CIA. LTDA." -> CertificadoDataFactoryAlphaTechnologies.getDatosUsuarioAlphaTechnologies(certificado);
         case "ANFAC AUTORIDAD DE CERTIFICACION ECUADOR C.A." -> CertificadoDataFactoryAnfAc.getDatosUsuarioAnfAc(certificado);
         case "ANFAC AUTORIDAD DE CERTIFICACION ECUADOR CA" -> CertificadoDataFactoryAnfAc.getDatosUsuarioAnfAc(certificado);
         case "APPFIRMAS S.A." -> CertificadoDataFactoryAppFirmas.getDatosUsuarioAppFirmas(certificado);
         case "APPFIRMAS S.A. SUB CA" -> CertificadoDataFactoryAppFirmas.getDatosUsuarioAppFirmas(certificado);
         case "ARGOSDATA CERTIFICACIÓN DE INFORMACIÓN Y SERVICIOS RELACIONADOS" -> CertificadoDataFactoryArgosData.getDatosUsuarioArgosData(certificado);
         case "ARGOSDATA" -> CertificadoDataFactoryArgosData.getDatosUsuarioArgosData(certificado);
         case "BANCO CENTRAL DEL ECUADOR" -> CertificadoDataFactoryBancoCentral.getDatosUsuarioBancoCentral(certificado);
         case "CONSEJO DE LA JUDICATURA" -> CertificadoDataFactoryConsejoJudicatura.getDatosUsuarioConsejoJudicatura(certificado);
         case "CORPNEWBEST CIA. LTDA." -> CertificadoDataFactoryCorpNewBest.getDatosUsuarioCorpNewBest(certificado);
         case "DARKCAM S.A." -> CertificadoDataFactoryDarkcam.getDatosUsuarioDarkcam(certificado);
         case "DATILMEDIA S.A." -> CertificadoDataFactoryDatil.getDatosUsuarioDatil(certificado);
         case "DIRECCIÓN GENERAL DE REGISTRO CIVIL, IDENTIFICACIÓN Y CEDULACIÓN" -> CertificadoDataFactoryDatil.getDatosUsuarioDatil(certificado);
         case "ECLIPSOFT S.A." -> CertificadoDataFactoryEclipsoft.getDatosUsuarioEclipsoft(certificado);
         case "FIRMASEGURA S.A.S." -> CertificadoDataFactoryFirmaSegura.getDatosUsuarioFirmaSegura(certificado);
         case "LAZZATE CIA. LTDA." -> CertificadoDataFactoryLazzate.getDatosUsuarioLazzate(certificado);
         case "LETMI ECUADOR S.A." -> CertificadoDataFactoryLetmi.getDatosUsuarioLetmi(certificado);
         case "PRIMECORELAT S.A.S. B.I.C." -> CertificadoDataFactoryPrimeCoreLat.getDatosUsuarioPrimeCoreLat(certificado);
         case "SECURITY DATA SEGURIDAD EN DATOS Y FIRMA DIGITAL S.A." -> CertificadoDataFactorySecurityData.getDatosUsuarioSecurityData(certificado);
         case "SECURITY DATA" -> CertificadoDataFactorySecurityData.getDatosUsuarioSecurityData(certificado);
         case "UANATACA ECUADOR S.A." -> CertificadoDataFactoryUanataca.getDatosUsuarioUanataca(certificado);
         case "UANATACA S.A." -> CertificadoDataFactoryUanataca.getDatosUsuarioUanataca(certificado);
         default -> throw new EntidadCertificadoraNoValidaException("Entidad Certificadora no reconocida");
      };
   }
}
