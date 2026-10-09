package ec.gob.firmadigital.libreria.certificate.base;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

public abstract class RubricaCertificate extends X509Certificate {
   private X509Certificate x509Certificate;

   public RubricaCertificate(InputStream inputStream) {
      try {
         CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
         this.x509Certificate = (X509Certificate)certificateFactory.generateCertificate(inputStream);
      } catch (GeneralSecurityException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public RubricaCertificate(StringBuilder stringBuilder) {
      try {
         InputStream is = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
         CertificateFactory cf = CertificateFactory.getInstance("X.509");
         this.x509Certificate = (X509Certificate)cf.generateCertificate(is);
      } catch (UnsupportedEncodingException | GeneralSecurityException e) {
         throw new IllegalArgumentException(e);
      }
   }

   @Override
   public boolean hasUnsupportedCriticalExtension() {
      return this.x509Certificate.hasUnsupportedCriticalExtension();
   }

   @Override
   public Set<String> getCriticalExtensionOIDs() {
      return this.x509Certificate.getCriticalExtensionOIDs();
   }

   @Override
   public Set<String> getNonCriticalExtensionOIDs() {
      return this.x509Certificate.getNonCriticalExtensionOIDs();
   }

   @Override
   public byte[] getExtensionValue(String oid) {
      return this.x509Certificate.getExtensionValue(oid);
   }

   @Override
   public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
      this.x509Certificate.checkValidity();
   }

   @Override
   public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
      this.x509Certificate.checkValidity(date);
   }

   @Override
   public int getVersion() {
      return this.x509Certificate.getVersion();
   }

   @Override
   public BigInteger getSerialNumber() {
      return this.x509Certificate.getSerialNumber();
   }

   @Override
   public Principal getIssuerDN() {
      return this.x509Certificate.getIssuerDN();
   }

   @Override
   public Principal getSubjectDN() {
      return this.x509Certificate.getSubjectDN();
   }

   @Override
   public Date getNotBefore() {
      return this.x509Certificate.getNotBefore();
   }

   @Override
   public Date getNotAfter() {
      return this.x509Certificate.getNotAfter();
   }

   @Override
   public byte[] getTBSCertificate() throws CertificateEncodingException {
      return this.x509Certificate.getTBSCertificate();
   }

   @Override
   public byte[] getSignature() {
      return this.x509Certificate.getSignature();
   }

   @Override
   public String getSigAlgName() {
      return this.x509Certificate.getSigAlgName();
   }

   @Override
   public String getSigAlgOID() {
      return this.x509Certificate.getSigAlgOID();
   }

   @Override
   public byte[] getSigAlgParams() {
      return this.x509Certificate.getSigAlgParams();
   }

   @Override
   public boolean[] getIssuerUniqueID() {
      return this.x509Certificate.getIssuerUniqueID();
   }

   @Override
   public boolean[] getSubjectUniqueID() {
      return this.x509Certificate.getSubjectUniqueID();
   }

   @Override
   public boolean[] getKeyUsage() {
      return this.x509Certificate.getKeyUsage();
   }

   @Override
   public int getBasicConstraints() {
      return this.x509Certificate.getBasicConstraints();
   }

   @Override
   public byte[] getEncoded() throws CertificateEncodingException {
      return this.x509Certificate.getEncoded();
   }

   @Override
   public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      this.x509Certificate.verify(key);
   }

   @Override
   public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      this.x509Certificate.verify(key, sigProvider);
   }

   @Override
   public String toString() {
      return this.x509Certificate.toString();
   }

   @Override
   public PublicKey getPublicKey() {
      return this.x509Certificate.getPublicKey();
   }
}
