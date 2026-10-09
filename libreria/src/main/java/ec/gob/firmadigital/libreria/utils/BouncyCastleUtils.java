package ec.gob.firmadigital.libreria.utils;

import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BouncyCastleUtils {
   private static final Logger LOGGER = Logger.getLogger(BouncyCastleUtils.class.getName());

   public static void initializeBouncyCastle() {
      if (Security.getProvider("BC") == null) {
         Security.addProvider(new BouncyCastleProvider());
      }
   }

   public static boolean certificateHasPolicy(X509Certificate cert, String policyOid) {
      try {
         JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder(cert);
         CertificatePolicies policies = CertificatePolicies.fromExtensions(certHolder.getExtensions());
         if (policies != null) {
            for (PolicyInformation policyInfo : policies.getPolicyInformation()) {
               if (policyInfo.getPolicyIdentifier().getId().equals(policyOid)) {
                  return true;
               }
            }
         }
      } catch (CertificateEncodingException ex) {
         LOGGER.severe(() -> "Error reading certificate policies: " + ex);
      }

      return false;
   }
}
