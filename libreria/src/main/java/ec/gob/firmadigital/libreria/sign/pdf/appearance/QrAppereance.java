package ec.gob.firmadigital.libreria.sign.pdf.appearance;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.signatures.PdfSignatureAppearance;
import ec.gob.firmadigital.libreria.utils.QRCode;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QrAppereance implements CustomAppearance {
   private final String nombreFirmante;
   private final String reason;
   private final String location;
   private final String signTime;
   private final String infoQR;
   private static final Logger LOGGER = Logger.getLogger(QrAppereance.class.getName());

   public QrAppereance(String nombreFirmante, String reason, String location, String signTime, String infoQR) {
      this.nombreFirmante = nombreFirmante;
      this.reason = reason;
      this.location = location;
      this.signTime = signTime;
      this.infoQR = infoQR;
   }

   @Override
   public void createCustomAppearance(PdfSignatureAppearance signatureAppearance, int pageNumber, PdfDocument pdfDocument, Rectangle signaturePositionOnPage) throws IOException {
      signatureAppearance.setPageRect(signaturePositionOnPage);
      signatureAppearance.setPageNumber(pageNumber);
      PdfFormXObject layer2 = signatureAppearance.getLayer2();
      PdfCanvas canvas = new PdfCanvas(layer2, pdfDocument);
      PdfFont fontCourier = Utils.loadFont("fonts/courier.ttf");
      PdfFont fontCourierBold = Utils.loadFont("fonts/courier-bold.ttf");
      byte[] byteQR = null;
      String text = "FIRMADO POR: " + this.nombreFirmante.trim() + "\n";
      text = text + "RAZON: " + this.reason + "\n";
      text = text + "LOCALIZACION: " + this.location + "\n";
      text = text + "FECHA: " + this.signTime + "\n";
      text = text + this.infoQR;

      try {
         byteQR = QRCode.generateQR(text, (int)signaturePositionOnPage.getHeight(), (int)signaturePositionOnPage.getHeight());
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "Error al generar QR: {0}", e);
      }

      Rectangle dataRect = new Rectangle(0.0F, 0.0F, signaturePositionOnPage.getWidth(), signaturePositionOnPage.getHeight());
      Rectangle signatureRect = new Rectangle(
         signaturePositionOnPage.getWidth() / 3.0F, 0.0F, signaturePositionOnPage.getWidth(), signaturePositionOnPage.getHeight()
      );
      Div imageDiv = new Div();
      imageDiv.setHeight(dataRect.getHeight());
      imageDiv.setWidth(dataRect.getWidth());
      imageDiv.setVerticalAlignment(VerticalAlignment.MIDDLE);
      imageDiv.setHorizontalAlignment(HorizontalAlignment.CENTER);
      Image image = new Image(ImageDataFactory.create(byteQR));
      image.setAutoScale(true);
      imageDiv.add(image);
      Canvas imageLayoutCanvas = new Canvas(canvas, dataRect);

      try {
         imageLayoutCanvas.add(imageDiv);
      } catch (Throwable var25) {
         try {
            imageLayoutCanvas.close();
         } catch (Throwable var23) {
            var25.addSuppressed(var23);
         }

         throw var25;
      }

      imageLayoutCanvas.close();
      Div textDiv = new Div();
      textDiv.setHeight(signatureRect.getHeight());
      textDiv.setWidth(signatureRect.getWidth() - signaturePositionOnPage.getWidth() / 3.0F);
      textDiv.setVerticalAlignment(VerticalAlignment.MIDDLE);
      textDiv.setHorizontalAlignment(HorizontalAlignment.LEFT);
      textDiv.setMargin(0.0F);
      Text texto = new Text("Validar únicamente en FirmaEC.\nFirmado electrónicamente por:\n");
      Paragraph paragraph = (Paragraph)((Paragraph)((Paragraph)new Paragraph().add(texto).setFont(fontCourier)).setMargin(0.0F))
         .setMultipliedLeading(1.0F)
         .setFontSize(3.25F);
      textDiv.add(paragraph);
      Text contenido = new Text(this.truncarLinea(this.nombreFirmante, "...").trim());
      paragraph = (Paragraph)((Paragraph)((Paragraph)new Paragraph().add(contenido).setFont(fontCourierBold)).setMargin(0.0F))
         .setMultipliedLeading(0.6F)
         .setFontSize(6.25F);
      textDiv.add(paragraph);
      Canvas textLayoutCanvas = new Canvas(canvas, signatureRect);

      try {
         textLayoutCanvas.add(textDiv);
      } catch (Throwable var24) {
         try {
            textLayoutCanvas.close();
         } catch (Throwable var22) {
            var24.addSuppressed(var22);
         }

         throw var24;
      }

      textLayoutCanvas.close();
   }

   private String truncarLinea(String linea, String sufijo) {
      int maxCaracteres = 57;
      return linea.length() > maxCaracteres ? linea.substring(0, maxCaracteres - sufijo.length()) + sufijo : linea;
   }
}
