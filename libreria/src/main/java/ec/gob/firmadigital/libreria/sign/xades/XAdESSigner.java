package ec.gob.firmadigital.libreria.sign.xades;

import ec.gob.firmadigital.libreria.exceptions.CertificadoInvalidoException;
import ec.gob.firmadigital.libreria.sign.SignInfo;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.signature.AdvancedSignature;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.reference.CanonicalizationTransform;
import eu.europa.esig.dss.xades.reference.DSSReference;
import eu.europa.esig.dss.xades.reference.DSSTransform;
import eu.europa.esig.dss.xades.reference.EnvelopedSignatureTransform;
import eu.europa.esig.dss.xades.signature.XAdESService;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XAdESSigner {
   public byte[] sign(byte[] data, KeyStore keyStore, char[] claveFirma) throws Exception {
      DSSDocument facturaDocument = new InMemoryDocument(data);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      keyStore.store(baos, claveFirma);
      byte[] keyStoreBytes = baos.toByteArray();
      Pkcs12SignatureToken signingToken = new Pkcs12SignatureToken(keyStoreBytes, new PasswordProtection(claveFirma));
      DSSPrivateKeyEntry privateKey = (DSSPrivateKeyEntry)signingToken.getKeys().get(0);
      XAdESSignatureParameters parameters = new XAdESSignatureParameters();
      parameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
      parameters.setSignaturePackaging(SignaturePackaging.ENVELOPED);
      parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
      parameters.setSigningCertificate(privateKey.getCertificate());
      List<DSSReference> references = new ArrayList<>();
      DSSReference comprobanteReference = new DSSReference();
      comprobanteReference.setUri("#comprobante");
      comprobanteReference.setContents(facturaDocument);
      comprobanteReference.setDigestMethodAlgorithm(parameters.getDigestAlgorithm());
      List<DSSTransform> transforms = new ArrayList<>();
      transforms.add(new EnvelopedSignatureTransform());
      transforms.add(new CanonicalizationTransform("http://www.w3.org/2001/10/xml-exc-c14n#"));
      comprobanteReference.setTransforms(transforms);
      references.add(comprobanteReference);
      parameters.setReferences(references);
      CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
      XAdESService service = new XAdESService(commonCertificateVerifier);
      service.setTspSource(null);
      ToBeSigned dataToSign = service.getDataToSign(facturaDocument, parameters);
      SignatureValue signatureValue = signingToken.sign(dataToSign, parameters.getDigestAlgorithm(), privateKey);
      DSSDocument signedDocument = service.signDocument(facturaDocument, parameters, signatureValue);

      try (InputStream inputStream = signedDocument.openStream()) {
         return DSSUtils.toByteArray(inputStream);
      }
   }

   public List<SignInfo> getSignInfo(byte[] data) throws CertificadoInvalidoException {
      DSSDocument signedXml = new InMemoryDocument(data);
      SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(signedXml);
      validator.setCertificateVerifier(new CommonCertificateVerifier());
      List<AdvancedSignature> signatures = validator.getSignatures();
      if (signatures.isEmpty()) {
         throw new IllegalArgumentException("El documento no contiene firmas digitales");
      }

      X509Certificate[] x509Certificates = null;
      Date signingTime = null;

      for (AdvancedSignature signature : signatures) {
         signingTime = signature.getSigningTime();
         CertificateToken signingCert = signature.getSigningCertificateToken();
         if (signingCert == null) {
            throw new IllegalStateException("No se pudo obtener el certificado firmante");
         }

         List<CertificateToken> allCertificates = new ArrayList<>();
         allCertificates.add(signingCert);
         if (signature.getCertificateSource() != null) {
            allCertificates.addAll(signature.getCertificateSource().getCertificates());
         }

         x509Certificates = new X509Certificate[allCertificates.size()];

         for (int i = 0; i < allCertificates.size(); i++) {
            x509Certificates[i] = allCertificates.get(i).getCertificate();
         }
      }

      List<SignInfo> signInfos = new ArrayList<>();
      SignInfo signInfo = new SignInfo(x509Certificates, signingTime);
      signInfos.add(signInfo);
      return signInfos;
   }
}
