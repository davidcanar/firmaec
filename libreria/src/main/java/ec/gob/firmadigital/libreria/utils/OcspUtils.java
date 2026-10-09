package ec.gob.firmadigital.libreria.utils;

import ec.gob.firmadigital.libreria.certificate.CertEcUtils;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.cert.CaCertAlphaTechnologies;
import ec.gob.firmadigital.libreria.certificate.ec.alphatechnologies.cert.CaCertAlphaTechnologies20242034;
import ec.gob.firmadigital.libreria.certificate.ec.anfac.cert.CaCertAnfAc20162036_18332;
import ec.gob.firmadigital.libreria.certificate.ec.anfac.cert.CaCertAnfAc20192039_37442;
import ec.gob.firmadigital.libreria.certificate.ec.anfac.cert.CaCertAnfAc20242044_37442;
import ec.gob.firmadigital.libreria.certificate.ec.appfirmas.cert.CaCertAppFirmas20252050;
import ec.gob.firmadigital.libreria.certificate.ec.argosdata.cert.CaCertArgosData;
import ec.gob.firmadigital.libreria.certificate.ec.bce.cert.CaCertBce;
import ec.gob.firmadigital.libreria.certificate.ec.cj.cert.CaCertConsejoJudicatura;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.cert.CaCertCorpNewBest;
import ec.gob.firmadigital.libreria.certificate.ec.corpnewbest.cert.CaCertCorpNewBest2024011020330619;
import ec.gob.firmadigital.libreria.certificate.ec.darkcam.cert.CaCertDarkcam20262046;
import ec.gob.firmadigital.libreria.certificate.ec.datil.cert.CaCertDatil;
import ec.gob.firmadigital.libreria.certificate.ec.firmasegura.cert.CaCertFirmaSegura;
import ec.gob.firmadigital.libreria.certificate.ec.firmasegura.cert.SubCaCertFirmaSegura20232043;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.cert.Ca1CertLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.cert.Ca2CertLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.cert.CaCertLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.lazzate.cert.CaWeGoCertLazzate;
import ec.gob.firmadigital.libreria.certificate.ec.letmi.cert.CaCertLetmi20252055;
import ec.gob.firmadigital.libreria.certificate.ec.primecorelat.cert.Ca1CertPrimeCoreLat;
import ec.gob.firmadigital.libreria.certificate.ec.primecorelat.cert.Ca2CertPrimeCoreLat;
import ec.gob.firmadigital.libreria.certificate.ec.securitydata.cert.CaCertSecurityData;
import ec.gob.firmadigital.libreria.certificate.ec.uanataca.cert.CaCertUanataca;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.exceptions.RubricaException;
import java.net.SocketTimeoutException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.cert.CertPathValidatorException.Reason;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OcspUtils {
   public static boolean isValidCertificate(X509Certificate certificate) throws RubricaException, EntidadCertificadoraNoValidaException {
      List<X509Certificate> certs = new ArrayList<>();
      certs.add(certificate);
      certs.add(CertEcUtils.getRootCertificate(certificate));

      CertificateFactory cf;
      try {
         cf = CertificateFactory.getInstance("X509");
      } catch (CertificateException e) {
         throw new RuntimeException(e);
      }

      CertPath cp;
      try {
         cp = cf.generateCertPath(certs);
      } catch (CertificateException e) {
         throw new RuntimeException(e);
      }

      X509Certificate rootCACert1 = new CaCertSecurityData();
      X509Certificate rootCACert2 = new CaCertBce();
      X509Certificate rootCACert3 = new CaCertConsejoJudicatura();
      X509Certificate rootCACert4 = new CaCertAnfAc20162036_18332();
      X509Certificate rootCACert5 = new CaCertAnfAc20192039_37442();
      X509Certificate rootCACert6 = new CaCertUanataca();
      X509Certificate rootCACert7 = new CaCertDatil();
      X509Certificate rootCACert8 = new CaCertArgosData();
      X509Certificate rootCACert9 = new CaCertLazzate();
      X509Certificate rootCACert10 = new CaCertAlphaTechnologies();
      X509Certificate rootCACert11 = new CaCertCorpNewBest();
      X509Certificate rootCACert12 = new CaCertCorpNewBest2024011020330619();
      X509Certificate rootCACert13 = new CaCertFirmaSegura();
      X509Certificate rootCACert14 = new Ca1CertLazzate();
      X509Certificate rootCACert15 = new Ca2CertLazzate();
      X509Certificate rootCACert16 = new CaCertLazzate();
      X509Certificate rootCACert17 = new CaWeGoCertLazzate();
      X509Certificate rootCACert18 = new CaCertAlphaTechnologies20242034();
      X509Certificate rootCACert19 = new CaCertCorpNewBest();
      X509Certificate rootCACert20 = new CaCertCorpNewBest2024011020330619();
      X509Certificate rootCACert21 = new CaCertFirmaSegura();
      X509Certificate rootCACert22 = new SubCaCertFirmaSegura20232043();
      X509Certificate rootCACert23 = new CaCertLetmi20252055();
      X509Certificate rootCACert24 = new CaCertAppFirmas20252050();
      X509Certificate rootCACert25 = new CaCertDarkcam20262046();
      X509Certificate rootCACert26 = new Ca1CertPrimeCoreLat();
      X509Certificate rootCACert27 = new Ca2CertPrimeCoreLat();
      X509Certificate rootCACert28 = new CaCertAnfAc20242044_37442();
      TrustAnchor ta1 = new TrustAnchor(rootCACert1, null);
      TrustAnchor ta2 = new TrustAnchor(rootCACert2, null);
      TrustAnchor ta3 = new TrustAnchor(rootCACert3, null);
      TrustAnchor ta4 = new TrustAnchor(rootCACert4, null);
      TrustAnchor ta5 = new TrustAnchor(rootCACert5, null);
      TrustAnchor ta6 = new TrustAnchor(rootCACert6, null);
      TrustAnchor ta7 = new TrustAnchor(rootCACert7, null);
      TrustAnchor ta8 = new TrustAnchor(rootCACert8, null);
      TrustAnchor ta9 = new TrustAnchor(rootCACert9, null);
      TrustAnchor ta10 = new TrustAnchor(rootCACert10, null);
      TrustAnchor ta11 = new TrustAnchor(rootCACert11, null);
      TrustAnchor ta12 = new TrustAnchor(rootCACert12, null);
      TrustAnchor ta13 = new TrustAnchor(rootCACert13, null);
      TrustAnchor ta14 = new TrustAnchor(rootCACert14, null);
      TrustAnchor ta15 = new TrustAnchor(rootCACert15, null);
      TrustAnchor ta16 = new TrustAnchor(rootCACert16, null);
      TrustAnchor ta17 = new TrustAnchor(rootCACert17, null);
      TrustAnchor ta18 = new TrustAnchor(rootCACert18, null);
      TrustAnchor ta19 = new TrustAnchor(rootCACert19, null);
      TrustAnchor ta20 = new TrustAnchor(rootCACert20, null);
      TrustAnchor ta21 = new TrustAnchor(rootCACert21, null);
      TrustAnchor ta22 = new TrustAnchor(rootCACert22, null);
      TrustAnchor ta23 = new TrustAnchor(rootCACert23, null);
      TrustAnchor ta24 = new TrustAnchor(rootCACert24, null);
      TrustAnchor ta25 = new TrustAnchor(rootCACert25, null);
      TrustAnchor ta26 = new TrustAnchor(rootCACert26, null);
      TrustAnchor ta27 = new TrustAnchor(rootCACert27, null);
      TrustAnchor ta28 = new TrustAnchor(rootCACert28, null);
      Set<TrustAnchor> trustedCertsSet = new HashSet<>();
      trustedCertsSet.add(ta1);
      trustedCertsSet.add(ta2);
      trustedCertsSet.add(ta3);
      trustedCertsSet.add(ta4);
      trustedCertsSet.add(ta5);
      trustedCertsSet.add(ta6);
      trustedCertsSet.add(ta7);
      trustedCertsSet.add(ta8);
      trustedCertsSet.add(ta9);
      trustedCertsSet.add(ta10);
      trustedCertsSet.add(ta11);
      trustedCertsSet.add(ta12);
      trustedCertsSet.add(ta13);
      trustedCertsSet.add(ta14);
      trustedCertsSet.add(ta15);
      trustedCertsSet.add(ta16);
      trustedCertsSet.add(ta17);
      trustedCertsSet.add(ta18);
      trustedCertsSet.add(ta19);
      trustedCertsSet.add(ta20);
      trustedCertsSet.add(ta21);
      trustedCertsSet.add(ta22);
      trustedCertsSet.add(ta23);
      trustedCertsSet.add(ta24);
      trustedCertsSet.add(ta25);
      trustedCertsSet.add(ta26);
      trustedCertsSet.add(ta27);
      trustedCertsSet.add(ta28);

      PKIXParameters params;
      try {
         params = new PKIXParameters(trustedCertsSet);
      } catch (InvalidAlgorithmParameterException e) {
         throw new RuntimeException(e);
      }

      params.setRevocationEnabled(false);

      CertPathValidator validator;
      try {
         validator = CertPathValidator.getInstance("PKIX");
      } catch (NoSuchAlgorithmException e) {
         throw new RubricaException(e);
      }

      try {
         CertPathValidatorResult result = validator.validate(cp, params);
         return true;
      } catch (InvalidAlgorithmParameterException e) {
         throw new RubricaException(e);
      } catch (CertPathValidatorException e) {
         Reason reason = e.getReason();
         int index = e.getIndex();
         System.out.println("reason=" + reason + "; index=" + index);
         Throwable t = e.getCause();
         if (t != null) {
            System.out.println("Cause=" + t.getClass());
            if (t instanceof SocketTimeoutException) {
               System.out.println("Timeout al ir al OCSP server!");
               return false;
            }
         }

         return false;
      }
   }

   static {
      BouncyCastleUtils.initializeBouncyCastle();
   }
}
