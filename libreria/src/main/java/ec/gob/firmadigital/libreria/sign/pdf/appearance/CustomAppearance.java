package ec.gob.firmadigital.libreria.sign.pdf.appearance;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.signatures.PdfSignatureAppearance;
import java.io.IOException;

public interface CustomAppearance {
   void createCustomAppearance(PdfSignatureAppearance var1, int var2, PdfDocument var3, Rectangle var4) throws IOException;
}
