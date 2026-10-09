package ec.gob.firmadigital.libreria.certificate;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

public abstract class Certificado {
   private final X509Certificate certificado;
   private X500Name subjectName;

   public Certificado(X509Certificate certificado) {
      this.certificado = certificado;
   }

   protected void cargarSubjectName() {
      try {
         this.subjectName = new JcaX509CertificateHolder(this.certificado).getSubject();
      } catch (CertificateEncodingException e) {
         throw new RuntimeException(e);
      }
   }

   protected String getExtensionField(String oid) {
      try {
         String valor = CertUtils.getExtensionValue(this.certificado, oid);
         return valor != null ? valor : "";
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   protected String getSubjectAlternativeName(String oid) {
      try {
         String valor = CertUtils.getExtensionValueSubjectAlternativeNames(this.certificado, oid);
         return valor != null ? valor : "";
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   protected String getSubjectField(String oid) {
      ASN1ObjectIdentifier Asn1OID = new ASN1ObjectIdentifier(oid);
      String fieldValue = CertUtils.getSubjectFieldByOID(this.subjectName, Asn1OID);
      return fieldValue != null ? fieldValue : "";
   }
}
