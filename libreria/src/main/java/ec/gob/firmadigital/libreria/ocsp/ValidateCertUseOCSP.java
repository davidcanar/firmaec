package ec.gob.firmadigital.libreria.ocsp;

import java.net.URI;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ValidateCertUseOCSP {
   public static void validadorOCSP(X509Certificate ocspCert, X509Certificate rootCACert, String url) {
      try {
         CertPath cp = null;
         List<X509Certificate> certs = new ArrayList<>();
         URI ocspServer = null;
         certs.add(ocspCert);
         ocspServer = new URI(url);
         CertificateFactory cf = CertificateFactory.getInstance("X509");
         cp = cf.generateCertPath(certs);
         TrustAnchor ta = new TrustAnchor(rootCACert, null);
         Set trustedCertsSet = new HashSet();
         trustedCertsSet.add(ta);
         Set certSet = new HashSet();
         certSet.add(ocspCert);
         CertStoreParameters storeParams = new CollectionCertStoreParameters(certSet);
         CertStore store = CertStore.getInstance("Collection", storeParams);
         PKIXParameters params = null;
         params = new PKIXParameters(trustedCertsSet);
         params.addCertStore(store);
         params.setRevocationEnabled(false);
         Security.setProperty("ocsp.enable", "true");
         if (ocspServer != null) {
            Security.setProperty("ocsp.responderURL", url);
            Security.setProperty("ocsp.responderCertSubjectName", ocspCert.getSubjectX500Principal().getName());
         }

         CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
         PKIXCertPathValidatorResult cpv_result = (PKIXCertPathValidatorResult)cpv.validate(cp, params);
         X509Certificate trustedCert = cpv_result.getTrustAnchor().getTrustedCert();
         if (trustedCert == null) {
            System.out.println("Trsuted Cert = NULL");
         } else {
            System.out.println("Trusted CA DN = " + trustedCert.getSubjectDN());
         }
      } catch (CertificateExpiredException | CertPathValidatorException e) {
         e.printStackTrace();
         System.exit(1);
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(-1);
      }

      System.out.println("CERTIFICATE VALIDATION SUCCEEDED");
      System.exit(0);
   }
}
