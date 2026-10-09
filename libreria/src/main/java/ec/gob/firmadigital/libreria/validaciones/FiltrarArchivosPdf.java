package ec.gob.firmadigital.libreria.validaciones;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FiltrarArchivosPdf extends FileFilter {
   @Override
   public boolean accept(File f) {
      if (f.isDirectory()) {
         return true;
      }

      String extension = Extensiones.getExtension(f);
      return extension != null ? extension.equals("pdf") : false;
   }

   @Override
   public String getDescription() {
      return "Archivos PDF (*.pdf)";
   }

   @Override
   public String toString() {
      return ".pdf";
   }
}
