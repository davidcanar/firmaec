package ec.gob.firmadigital.libreria.sign.pdf;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignatureContainer;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

public class PreSignatureContainer implements IExternalSignatureContainer {
   private PdfDictionary sigDic = new PdfDictionary();
   private byte[] hash;
   String hashAlgorithm;

   public PreSignatureContainer(PdfName filter, PdfName subFilter, String hashAlgorithm) {
      this.sigDic.put(PdfName.Filter, filter);
      this.sigDic.put(PdfName.SubFilter, subFilter);
      this.hashAlgorithm = hashAlgorithm;
   }

   public byte[] sign(InputStream data) throws GeneralSecurityException {
      BouncyCastleDigest digest = new BouncyCastleDigest();

      try {
         this.hash = DigestAlgorithms.digest(data, digest.getMessageDigest(this.hashAlgorithm));
      } catch (IOException e) {
         throw new GeneralSecurityException("PreSignatureContainer signing exception", e);
      }

      return new byte[0];
   }

   public void modifySigningDictionary(PdfDictionary signDic) {
      signDic.putAll(this.sigDic);
   }

   public byte[] getHash() {
      return this.hash;
   }

   public void setHash(byte[] hash) {
      this.hash = hash;
   }
}
