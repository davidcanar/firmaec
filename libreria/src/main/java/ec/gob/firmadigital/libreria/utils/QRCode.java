package ec.gob.firmadigital.libreria.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class QRCode {
   public static byte[] generateQR(String text, int h, int w) throws Exception {
      Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
      hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.US_ASCII.name());
      hints.put(EncodeHintType.MARGIN, 0);
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
      QRCodeWriter writer = new QRCodeWriter();
      BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, w, h, hints);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(bitMatrix, "png", bos);
      bos.close();
      return bos.toByteArray();
   }

   public static String decoder(File file) throws Exception {
      FileInputStream inputStream = new FileInputStream(file);
      BufferedImage image = ImageIO.read(inputStream);
      LuminanceSource source = new BufferedImageLuminanceSource(image);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      QRCodeReader reader = new QRCodeReader();
      Result result = reader.decode(bitmap);
      return new String(result.getText());
   }
}
