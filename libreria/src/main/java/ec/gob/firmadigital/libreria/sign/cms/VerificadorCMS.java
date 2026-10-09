package ec.gob.firmadigital.libreria.sign.cms;

import ec.gob.firmadigital.libreria.certificate.CertEcUtils;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.exceptions.SignatureVerificationException;
import ec.gob.firmadigital.libreria.utils.BouncyCastleUtils;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;

public class VerificadorCMS {
   public List<DatosUsuario> listaDatosUsuario = new ArrayList<>();
   public List<X509Certificate> certificados;
   public List<Date> fechasFirmados;

   public byte[] verify(byte[] signedBytes) throws SignatureVerificationException, EntidadCertificadoraNoValidaException {
      try {
         CMSSignedData signedData = new CMSSignedData(signedBytes);
         Store<X509CertificateHolder> certStore = signedData.getCertificates();
         SignerInformationStore signerInformationStore = signedData.getSignerInfos();
         Collection<SignerInformation> collection = signerInformationStore.getSigners();
         this.certificados = new ArrayList<>();
         this.fechasFirmados = new ArrayList<>();

         for (SignerInformation signer : collection) {
            Collection<X509CertificateHolder> certCollection = certStore.getMatches(signer.getSID());
            Iterator<X509CertificateHolder> certIt = certCollection.iterator();
            X509CertificateHolder certificateHolder = certIt.next();
            JcaX509CertificateConverter jcaX509CertificateConverter = new JcaX509CertificateConverter();
            X509Certificate x509Certificate = jcaX509CertificateConverter.setProvider("BC").getCertificate(certificateHolder);
            this.certificados.add(x509Certificate);
            AttributeTable attributes = signer.getSignedAttributes();
            if (attributes != null) {
               Attribute messageDigestAttribute = attributes.get(CMSAttributes.signingTime);
               ASN1UTCTime dt = (ASN1UTCTime)messageDigestAttribute.getAttrValues().getObjectAt(0);

               try {
                  this.fechasFirmados.add(dt.getDate());
                  SimpleDateFormat f_DateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                  String dateStr = f_DateTime.format(dt.getDate());
                  System.out.println("Fecha Firma: " + dateStr);
               } catch (ParseException ex) {
                  Logger.getLogger(VerificadorCMS.class.getName()).log(Level.SEVERE, null, ex);
               }
            }

            if (!signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(certificateHolder))) {
               System.out.println("  --> " + certificateHolder.getSerialNumber());
               System.out.println("  --> " + certificateHolder.getNotAfter());
               throw new SignatureVerificationException("La firma no verifico con " + signer.getSID());
            }

            DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(x509Certificate);
            this.listaDatosUsuario.add(datosUsuario);
         }

         CMSProcessable signedContent = signedData.getSignedContent();
         return (byte[])signedContent.getContent();
      } catch (GeneralSecurityException | CMSException e) {
         throw new RuntimeException(e);
      } catch (OperatorCreationException ex) {
         Logger.getLogger(VerificadorCMS.class.getName()).log(Level.SEVERE, null, ex);
         throw new RuntimeException(ex);
      }
   }

   static {
      BouncyCastleUtils.initializeBouncyCastle();
   }
}
