package ec.gob.firmadigital.libreria.sign.pdf.appearance;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.signatures.PdfSignatureAppearance;
import ec.gob.firmadigital.libreria.utils.Utils;
import java.io.IOException;

public class Information2Appearance implements CustomAppearance {
   private final String nombreFirmante;
   private final String informacionCertificado;
   private final String reason;
   private final String location;
   private final String signTime;

   public Information2Appearance(String nombreFirmante, String informacionCertificado, String reason, String location, String signTime) {
      this.nombreFirmante = nombreFirmante;
      this.informacionCertificado = informacionCertificado;
      this.reason = reason;
      this.location = location;
      this.signTime = signTime;
   }

   @Override
   public void createCustomAppearance(PdfSignatureAppearance signatureAppearance, int pageNumber, PdfDocument pdfDocument, Rectangle signaturePositionOnPage) throws IOException {
      PdfFont fontHelvetica = Utils.loadFont("fonts/inter.ttf");
      PdfFormXObject layer2 = signatureAppearance.getLayer2();
      PdfCanvas canvas = new PdfCanvas(layer2, pdfDocument);
      Rectangle signatureRect = new Rectangle(0.0F, 0.0F, signaturePositionOnPage.getWidth(), signaturePositionOnPage.getHeight());
      Div textDiv = new Div();
      textDiv.setHeight(signatureRect.getHeight());
      textDiv.setWidth(signatureRect.getWidth());
      textDiv.setVerticalAlignment(VerticalAlignment.MIDDLE);
      textDiv.setHorizontalAlignment(HorizontalAlignment.LEFT);
      Text contenido = new Text(this.nombreFirmante.trim());
      Paragraph paragraph = (Paragraph)((Paragraph)((Paragraph)new Paragraph().add(contenido).setFont(fontHelvetica)).setMargin(0.0F))
         .setMultipliedLeading(0.8F)
         .setFontSize(4.75F);
      textDiv.add(paragraph);
      contenido = new Text("Validar únicamente en FirmaEC");
      paragraph = (Paragraph)((Paragraph)((Paragraph)new Paragraph().add(contenido).setFont(fontHelvetica)).setMargin(0.0F))
         .setMultipliedLeading(0.9F)
         .setFontSize(2.75F);
      textDiv.add(paragraph);
      contenido = new Text("Fecha: " + this.signTime);
      paragraph = (Paragraph)((Paragraph)((Paragraph)new Paragraph().add(contenido).setFont(fontHelvetica)).setMargin(0.0F))
         .setMultipliedLeading(0.9F)
         .setFontSize(2.75F);
      textDiv.add(paragraph);
      contenido = new Text("Razón: " + this.reason);
      paragraph = (Paragraph)((Paragraph)((Paragraph)new Paragraph().add(contenido).setFont(fontHelvetica)).setMargin(0.0F))
         .setMultipliedLeading(0.9F)
         .setFontSize(2.75F);
      textDiv.add(paragraph);
      contenido = new Text("Localización: " + this.location);
      paragraph = (Paragraph)((Paragraph)((Paragraph)new Paragraph().add(contenido).setFont(fontHelvetica)).setMargin(0.0F))
         .setMultipliedLeading(0.9F)
         .setFontSize(2.75F);
      textDiv.add(paragraph);
      contenido = new Text("Nombre de reconocimiento " + this.informacionCertificado.trim());
      paragraph = (Paragraph)((Paragraph)((Paragraph)new Paragraph().add(contenido).setFont(fontHelvetica)).setMargin(0.0F))
         .setMultipliedLeading(0.8F)
         .setFontSize(2.5F);
      textDiv.add(paragraph);
      Canvas textLayoutCanvas = new Canvas(canvas, signatureRect);

      try {
         textLayoutCanvas.add(textDiv);
      } catch (Throwable var16) {
         try {
            textLayoutCanvas.close();
         } catch (Throwable var15) {
            var16.addSuppressed(var15);
         }

         throw var16;
      }

      textLayoutCanvas.close();
   }
}
