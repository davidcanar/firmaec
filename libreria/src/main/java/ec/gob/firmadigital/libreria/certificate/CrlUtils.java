package ec.gob.firmadigital.libreria.certificate;

import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.exceptions.RubricaException;
import ec.gob.firmadigital.libreria.utils.CertificateUtils;
import ec.gob.firmadigital.libreria.utils.HttpClient;
import ec.gob.firmadigital.libreria.utils.OcspUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;

public class CrlUtils {
   private static final Logger LOGGER = Logger.getLogger(CrlUtils.class.getName());
   private String revocationDate;

   public String getRevocationDate() {
      return this.revocationDate;
   }

   public ValidationResult verifyCertificateCRLs(X509Certificate cert, PublicKey vaPublicKey, List<String> overridingDistributionPoints) throws RubricaException, EntidadCertificadoraNoValidaException {
      if (cert == null) {
         return ValidationResult.CORRUPT;
      }

      List<String> crlDistPoints;
      try {
         for (String ocsp : CertificateUtils.getAuthorityInformationAccess(cert)) {
            System.out.println("OCSP=" + ocsp);
         }

         System.out.println("Valid? " + OcspUtils.isValidCertificate(cert));
         crlDistPoints = overridingDistributionPoints != null && !overridingDistributionPoints.isEmpty()
            ? overridingDistributionPoints
            : CertificateUtils.getCrlDistributionPoints(cert);
      } catch (IOException e) {
         LOGGER.log(Level.SEVERE, "Error obteniendo los puntos de distribucion de CRL: {0}", e);
         return ValidationResult.SERVER_ERROR;
      }

      LOGGER.log(Level.FINE, "El certificado con serie ''{0}'' tiene asociadas las siguientes CRL: {1}", new Object[]{cert.getSerialNumber(), crlDistPoints});

      CertificateFactory cf;
      try {
         cf = CertificateFactory.getInstance("X.509");
      } catch (CertificateException e) {
         LOGGER.log(Level.SEVERE, "Error instanciando la factoria de certificados: {0}", e);
         return ValidationResult.SERVER_ERROR;
      }

      boolean checked = false;
      boolean cannotDownload = false;

      for (String crlDP : crlDistPoints) {
         if (!crlDP.toLowerCase().contains("ocsp")) {
            byte[] crlBytes;
            try {
               crlBytes = downloadCRL(crlDP);
            } catch (Exception e) {
               LOGGER.log(
                  Level.SEVERE, "No se ha podido descargar la CRL ({0}), se continuara con el siguiente punto de distribucion: {1}", new Object[]{crlDP, e}
               );
               cannotDownload = true;
               continue;
            }

            X509CRL crl;
            try {
               crl = (X509CRL)cf.generateCRL(new ByteArrayInputStream(crlBytes));
               X509CRLEntry entry = crl.getRevokedCertificate(cert);
               if (entry != null) {
                  this.revocationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entry.getRevocationDate());
               }
            } catch (Exception e) {
               LOGGER.log(Level.WARNING, "Error analizando la lista de revocacion: {0}", e);
               return ValidationResult.SERVER_ERROR;
            }

            if (vaPublicKey != null) {
               try {
                  crl.verify(vaPublicKey);
               } catch (Exception e) {
                  LOGGER.log(Level.SEVERE, "No se ha podido comprobar la firma de la CRL: {0}", e);
                  return ValidationResult.SERVER_ERROR;
               }
            }

            if (crl.isRevoked(cert)) {
               return ValidationResult.REVOKED;
            }

            checked = true;
         }
      }

      if (checked) {
         return ValidationResult.VALID;
      } else {
         return cannotDownload ? ValidationResult.CANNOT_DOWNLOAD_CRL : ValidationResult.UNKNOWN;
      }
   }

   private static byte[] downloadCRL(String crlURL) throws IOException, NamingException, CRLException {
      if (crlURL.startsWith("http://") || crlURL.startsWith("https://")) {
         return downloadCRLFromWeb(crlURL);
      } else if (crlURL.startsWith("ldap://")) {
         return downloadCRLFromLDAP(crlURL);
      } else {
         throw new CRLException("No se soporta el protocolo del punto de distribucion de CRL: " + crlURL);
      }
   }

   private static byte[] downloadCRLFromLDAP(String ldapURL) throws NamingException {
      Hashtable<String, String> env = new Hashtable<>();
      env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
      env.put("java.naming.provider.url", ldapURL);
      Attribute aval = new InitialDirContext(env).getAttributes("").get("certificateRevocationList;binary");
      byte[] val = (byte[])aval.get();
      if (val != null && val.length != 0) {
         return val;
      } else {
         throw new NamingException("No se ha podido descargar la CRL desde " + ldapURL);
      }
   }

   private static byte[] downloadCRLFromWeb(String url) throws IOException, CRLException {
      HttpClient httpClient = new HttpClient();
      return httpClient.download(url);
   }
}
