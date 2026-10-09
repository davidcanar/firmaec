package ec.gob.firmadigital.libreria.sign.pdf.itext;

import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.ISignatureMechanismParams;
import ec.gob.firmadigital.libreria.sign.RubricaSigner;
import java.security.GeneralSecurityException;

public class SignerAdapter implements IExternalSignature {
   private final RubricaSigner signer;

   public SignerAdapter(RubricaSigner signer) {
      this.signer = signer;
   }

   public byte[] sign(byte[] message) throws GeneralSecurityException {
      return this.signer.sign(message);
   }

   public String getDigestAlgorithmName() {
      return this.signer.getDigestAlgorithm().getJavaName();
   }

   public String getSignatureAlgorithmName() {
      return this.signer.getEncryptionAlgorithm().getName();
   }

   public ISignatureMechanismParams getSignatureMechanismParameters() {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
