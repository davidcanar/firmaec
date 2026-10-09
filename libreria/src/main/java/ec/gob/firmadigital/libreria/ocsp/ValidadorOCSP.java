package ec.gob.firmadigital.libreria.ocsp;

import ec.gob.firmadigital.libreria.exceptions.RubricaException;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Random;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.cert.ocsp.UnknownStatus;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class ValidadorOCSP {
   private static final int TIME_OUT = 5000;

   public static String ValidarOCSP(X509Certificate checkCert, X509Certificate rootCert, String ocspURL) throws IOException, RubricaException {
      OCSPReq request;
      try {
         request = generateOCSPRequest(rootCert, checkCert.getSerialNumber());
      } catch (CertificateEncodingException | OperatorCreationException | OCSPException e) {
         throw new RubricaException(e);
      }

      byte[] array = request.getEncoded();
      URL url = new URL(ocspURL);
      HttpURLConnection con = (HttpURLConnection)url.openConnection();
      con.setRequestProperty("Content-Type", "application/ocsp-request");
      con.setRequestProperty("Accept", "application/ocsp-response");
      con.setConnectTimeout(5000);
      con.setDoOutput(true);
      OutputStream out = con.getOutputStream();
      DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
      dataOut.write(array);
      dataOut.flush();
      dataOut.close();
      if (con.getResponseCode() / 100 != 2) {
         throw new RubricaException("Respuesta HTTP inválida: " + con.getResponseCode());
      }

      InputStream in = (InputStream)con.getContent();
      OCSPResp ocspResponse = new OCSPResp(in);
      if (ocspResponse.getStatus() != 0) {
         throw new RubricaException("Status HTTP inválido: " + ocspResponse.getStatus());
      }

      BasicOCSPResp basicResponse;
      try {
         basicResponse = (BasicOCSPResp)ocspResponse.getResponseObject();
      } catch (OCSPException e) {
         throw new RubricaException("Problema al decodificar respuesta", e);
      }

      if (basicResponse == null) {
         throw new RubricaException("Respuesta OCSP inválida");
      } else {
         SingleResp[] responses = basicResponse.getResponses();
         SingleResp response = responses[0];
         CertificateStatus certStatus = response.getCertStatus();
         if (certStatus instanceof RevokedStatus revokedStatus) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(revokedStatus.getRevocationTime());
         } else {
            UnknownStatus unknownStatus = (UnknownStatus)certStatus;
            System.out.println("unknownStatus: " + unknownStatus);
            return "unknownStatus";
         }
      }
   }

   private static OCSPReq generateOCSPRequest(X509Certificate issuerCert, BigInteger serialNumber) throws OperatorCreationException, CertificateEncodingException, OCSPException, IOException {
      Provider prov = new BouncyCastleProvider();
      Security.addProvider(prov);
      DigestCalculatorProvider digCalcProv = new JcaDigestCalculatorProviderBuilder().setProvider(prov).build();
      CertificateID id = new CertificateID(digCalcProv.get(CertificateID.HASH_SHA1), new JcaX509CertificateHolder(issuerCert), serialNumber);
      OCSPReqBuilder gen = new OCSPReqBuilder();
      gen.addRequest(id);
      ExtensionsGenerator extGen = new ExtensionsGenerator();
      byte[] nonce = new byte[16];
      Random rand = new Random();
      rand.nextBytes(nonce);
      extGen.addExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(nonce));
      gen.setRequestExtensions(extGen.generate());
      return gen.build();
   }
}
