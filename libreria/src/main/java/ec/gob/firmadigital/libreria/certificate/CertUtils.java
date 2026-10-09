package ec.gob.firmadigital.libreria.certificate;

import ec.gob.firmadigital.libreria.exceptions.CertificadoInvalidoException;
import ec.gob.firmadigital.libreria.exceptions.RubricaException;
import ec.gob.firmadigital.libreria.keystore.Alias;
import ec.gob.firmadigital.libreria.keystore.KeyStoreUtilities;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLTaggedObject;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;

public class CertUtils {
   public static String getExtensionValueSubjectAlternativeNames(X509Certificate certificate, String oid) throws IOException {
      return getSubjectAlternativeName(certificate, oid);
   }

   public static String getSubjectAlternativeName(X509Certificate certificate, String oid) {
      String decoded = null;

      try {
         Collection<List<?>> altNames = certificate.getSubjectAlternativeNames();
         if (altNames == null) {
            return decoded;
         }

         for (List<?> item : altNames) {
            Integer type = (Integer)item.get(0);
            if (type == 0) {
               try {
                  ASN1InputStream decoder = new ASN1InputStream((byte[])item.get(1));
                  Object object = decoder.readObject();
                  ASN1Sequence otherNameSeq = null;
                  if (object != null && object instanceof ASN1Sequence var17) {
                     ASN1ObjectIdentifier objectId = (ASN1ObjectIdentifier)var17.getObjectAt(0);
                     if (objectId.toString().equals(oid)) {
                        ASN1Encodable objectDetail = var17.getObjectAt(1);
                        decoded = objectDetail.toASN1Primitive().toString();
                        decoded = decoded.replace("[0]", "").replace("[CONTEXT 0]", "");
                        break;
                     }
                  }

                  if (object != null && object instanceof DLTaggedObject dlTaggedObject) {
                     Object obj = dlTaggedObject.getBaseObject();
                     if (obj != null && obj instanceof ASN1Sequence var18) {
                        ASN1ObjectIdentifier objectId = (ASN1ObjectIdentifier)var18.getObjectAt(0);
                        if (objectId.toString().equals(oid)) {
                           DLTaggedObject objectDetail = (DLTaggedObject)var18.getObjectAt(1);
                           decoded = objectDetail.getBaseObject().toASN1Primitive().toString();
                           break;
                        }
                     }
                  }

                  if (object != null && object instanceof DERTaggedObject derTaggedObject) {
                     Object obj = derTaggedObject.getBaseObject();
                     if (obj != null && obj instanceof ASN1Sequence var19) {
                        ASN1ObjectIdentifier objectId = (ASN1ObjectIdentifier)var19.getObjectAt(0);
                        if (objectId.toString().equals(oid)) {
                           DERTaggedObject objectDetail = (DERTaggedObject)var19.getObjectAt(1);
                           decoded = objectDetail.getBaseObject().toASN1Primitive().toString();
                           break;
                        }
                     }
                  }
               } catch (UnsupportedEncodingException e) {
                  throw new RuntimeException(e);
               } catch (Exception e) {
                  throw new RuntimeException(e);
               }
            }
         }
      } catch (CertificateParsingException e) {
         System.out.println("Error parsing SubjectAltName in certificate: " + certificate + "\r\nerror:" + e.getLocalizedMessage());
      }

      return decoded;
   }

   public static List<String> getExtensionOIDs(X509Certificate certificate) {
      List<String> oids = new ArrayList<>();

      try {
         Set<String> criticalOIDs = certificate.getCriticalExtensionOIDs();
         Set<String> nonCriticalOIDs = certificate.getNonCriticalExtensionOIDs();
         if (criticalOIDs != null) {
            oids.addAll(criticalOIDs);
         }

         if (nonCriticalOIDs != null) {
            oids.addAll(nonCriticalOIDs);
         }
      } catch (Exception var4) {
      }

      return oids;
   }

   public static boolean hasExtensionMatchingPattern(X509Certificate certificate, String prefix, String suffix) {
      for (String oid : getExtensionOIDs(certificate)) {
         if (oid.startsWith(prefix) && oid.endsWith(suffix)) {
            return true;
         }
      }

      return false;
   }

   public static String getExtensionValue(X509Certificate certificate, String oid) throws IOException {
      String decoded = null;
      byte[] extensionValue = certificate.getExtensionValue(oid);
      if (extensionValue != null
         && toDERObject(extensionValue) instanceof DEROctetString derOctetString
         && toDERObject(derOctetString.getOctets()) instanceof ASN1String s) {
         decoded = s.getString();
      }

      return decoded;
   }

   public static List<String> getCertificatePolicies(X509Certificate certificate) {
      List<String> policyOIDs = new ArrayList<>();

      try {
         byte[] extensionValue = certificate.getExtensionValue("2.5.29.32");
         if (extensionValue == null) {
            return policyOIDs;
         }

         ASN1InputStream asn1Stream = null;

         try {
            if (ASN1Primitive.fromByteArray(extensionValue) instanceof ASN1OctetString octetString
               && ASN1Primitive.fromByteArray(octetString.getOctets()) instanceof ASN1Sequence seq) {
               for (int i = 0; i < seq.size(); i++) {
                  ASN1Sequence policySeq = (ASN1Sequence)seq.getObjectAt(i);
                  if (policySeq.size() > 0) {
                     ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(policySeq.getObjectAt(0));
                     policyOIDs.add(oid.getId());
                  }
               }
            }
         } finally {
            if (asn1Stream != null) {
               asn1Stream.close();
            }
         }
      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
      }

      return policyOIDs;
   }

   public static boolean hasCertificatePoliciesMatchingPattern(X509Certificate certificate, String prefix, String suffix) {
      for (String oid : getCertificatePolicies(certificate)) {
         if (oid.startsWith(prefix) && oid.endsWith(suffix)) {
            return true;
         }
      }

      return false;
   }

   private static ASN1Primitive toDERObject(byte[] data) throws IOException {
      ByteArrayInputStream inStream = new ByteArrayInputStream(data);
      ASN1InputStream asnInputStream = null;

      try {
         asnInputStream = new ASN1InputStream(inStream);
         return asnInputStream.readObject();
      } finally {
         if (asnInputStream != null) {
            try {
               asnInputStream.close();
            } catch (IOException var10) {
            }
         }
      }
   }

   public static List<String> getSubjectAlternativeNames(X509Certificate certificate) {
      List<String> identities = new ArrayList<>();

      try {
         Collection<List<?>> altNames = certificate.getSubjectAlternativeNames();
         if (altNames == null) {
            return Collections.emptyList();
         }

         for (List<?> item : altNames) {
            Integer type = (Integer)item.get(0);
            if (type == 0) {
               try {
                  ASN1InputStream decoder = new ASN1InputStream((byte[])item.get(1));
                  Object object = decoder.readObject();
                  ASN1Sequence otherNameSeq = null;
                  if (object != null && object instanceof ASN1Sequence var17) {
                     ASN1ObjectIdentifier objectId = (ASN1ObjectIdentifier)var17.getObjectAt(0);
                     System.out.println("Parsing otherName for subject alternative names: " + objectId.toString());
                     DERTaggedObject objectDetail = (DERTaggedObject)var17.getObjectAt(1);
                     System.out.println("Parsing otherName for subject alternative names: " + objectDetail.getBaseObject().toASN1Primitive().toString());
                     if (toDERObject(objectDetail.getBaseObject().getEncoded()) instanceof DEROctetString derOctetString
                        && toDERObject(derOctetString.getOctets()) instanceof ASN1String s) {
                        System.out.println(s.getString());
                     }

                     String identity = objectId.toString();
                     identities.add(identity);
                  }
               } catch (UnsupportedEncodingException e) {
                  System.out.println("Error decoding subjectAltName" + e.getLocalizedMessage());
               } catch (Exception e) {
                  System.out.println("Error decoding subjectAltName" + e.getLocalizedMessage());
               }
            }
         }
      } catch (CertificateParsingException e) {
         System.out.println("Error parsing SubjectAltName in certificate: " + certificate + "\r\nerror:" + e.getLocalizedMessage());
      }

      return identities;
   }

   public static String seleccionarAlias(KeyStore keyStore, JRootPane jRootPane) throws CertificadoInvalidoException {
      String aliasString = null;
      List<Alias> signingAliases = KeyStoreUtilities.getSigningAliases(keyStore);
      if (signingAliases.isEmpty()) {
         throw new CertificadoInvalidoException("No se encuentran certificados para firmar\nPuede estar expirado, revocado o no reconocido");
      }

      if (signingAliases.size() == 1) {
         aliasString = signingAliases.get(0).getAlias();
      } else {
         Alias alias = (Alias)JOptionPane.showInputDialog(
            jRootPane == null ? null : jRootPane, "Escoja...", "Certificado para firmar", 3, null, signingAliases.toArray(), signingAliases.get(0)
         );
         if (alias != null) {
            aliasString = alias.getAlias();
         }
      }

      return aliasString;
   }

   public static X509Certificate getCert(KeyStore ks, String alias) throws KeyStoreException, RubricaException {
      return alias != null ? (X509Certificate)ks.getCertificate(alias) : null;
   }

   public static String getSubjectFieldByOID(X500Name subjectName, ASN1ObjectIdentifier oid) {
      RDN[] rdns = subjectName.getRDNs(oid);
      if (rdns != null && rdns.length != 0) {
         AttributeTypeAndValue attribute = rdns[0].getFirst();
         return IETFUtils.valueToString(attribute.getValue());
      } else {
         return null;
      }
   }
}
