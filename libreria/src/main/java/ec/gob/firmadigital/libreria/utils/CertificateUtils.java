package ec.gob.firmadigital.libreria.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

public class CertificateUtils {
   public static X509Certificate certificateFromByteArray(byte[] bytes) {
      try {
         return (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bytes));
      } catch (CertificateException e) {
         return null;
      }
   }

   public static List<String> getCrlDistributionPoints(X509Certificate cert) throws IOException {
      byte[] crldpExt = cert.getExtensionValue(Extension.cRLDistributionPoints.getId());
      ASN1Primitive object = X509ExtensionUtil.fromExtensionValue(crldpExt);
      CRLDistPoint distPoint = CRLDistPoint.getInstance(object);
      List<String> crlUrls = new ArrayList<>();

      for (DistributionPoint dp : distPoint.getDistributionPoints()) {
         DistributionPointName dpn = dp.getDistributionPoint();
         if (dpn != null && dpn.getType() == 0) {
            GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();

            for (GeneralName genName : genNames) {
               if (genName.getTagNo() == 6) {
                  crlUrls.add(DERIA5String.getInstance(genName.getName()).getString());
               }
            }
         }
      }

      return crlUrls;
   }

   public static List<String> getAuthorityInformationAccess(X509Certificate cert) throws IOException {
      byte[] authInfoExt = cert.getExtensionValue(Extension.authorityInfoAccess.getId());
      ASN1Sequence object = (ASN1Sequence)X509ExtensionUtil.fromExtensionValue(authInfoExt);
      AuthorityInformationAccess authInfo = AuthorityInformationAccess.getInstance(object);
      List<String> ocspUrls = new ArrayList<>();

      for (AccessDescription accessDescription : authInfo.getAccessDescriptions()) {
         GeneralName genName = accessDescription.getAccessLocation();
         if (genName.getTagNo() == 6) {
            DERIA5String str = (DERIA5String)genName.getName();
            String accessLocation = str.getString();
            ocspUrls.add(accessLocation);
         }
      }

      return ocspUrls;
   }

   public static String getCN(X509Certificate c) {
      return c == null ? null : getCN(c.getSubjectX500Principal().toString());
   }

   public static String getCN(String principal) {
      if (principal == null) {
         return null;
      }

      String rdn = getRDNvalueFromLdapName("cn", principal);
      if (rdn == null) {
         rdn = getRDNvalueFromLdapName("ou", principal);
      }

      if (rdn != null) {
         return rdn;
      } else {
         int i = principal.indexOf(61);
         if (i != -1) {
            System.out.println("No se ha podido obtener el Common Name ni la Organizational Unit, se devolvera el fragmento mas significativo");
            return getRDNvalueFromLdapName(principal.substring(0, i), principal);
         } else {
            System.out.println("Principal no valido, se devolvera la entrada");
            return principal;
         }
      }
   }

   public static String getRDNvalueFromLdapName(String rdn, String principal) {
      int offset1 = 0;

      while ((offset1 = principal.toLowerCase(Locale.US).indexOf(rdn.toLowerCase(), offset1)) != -1) {
         if (offset1 > 0 && principal.charAt(offset1 - 1) != ',' && principal.charAt(offset1 - 1) != ' ') {
            offset1++;
         } else {
            offset1 += rdn.length();

            while (offset1 < principal.length() && principal.charAt(offset1) == ' ') {
               offset1++;
            }

            if (offset1 >= principal.length()) {
               return null;
            }

            if (principal.charAt(offset1) == '=') {
               offset1++;

               while (offset1 < principal.length() && principal.charAt(offset1) == ' ') {
                  offset1++;
               }

               if (offset1 >= principal.length()) {
                  return "";
               }

               if (principal.charAt(offset1) == ',') {
                  return "";
               }

               if (principal.charAt(offset1) == '"') {
                  if (++offset1 >= principal.length()) {
                     return "";
                  }

                  int offset2 = principal.indexOf(34, offset1);
                  if (offset2 == offset1) {
                     return "";
                  }

                  if (offset2 != -1) {
                     return principal.substring(offset1, offset2);
                  }

                  return principal.substring(offset1);
               }

               int offset2 = principal.indexOf(44, offset1);
               if (offset2 != -1) {
                  return principal.substring(offset1, offset2).trim();
               }

               return principal.substring(offset1).trim();
            }
         }
      }

      return null;
   }
}
