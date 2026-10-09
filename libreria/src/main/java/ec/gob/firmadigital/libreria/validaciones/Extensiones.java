package ec.gob.firmadigital.libreria.validaciones;

import java.io.File;

public class Extensiones {
   public static final String ODS = "ods";
   public static final String ODT = "odt";
   public static final String PDF = "pdf";
   public static final String TXT = "txt";
   public static final String XLS = "xls";
   public static final String XML = "xml";

   public static String getExtension(File f) {
      String ext = null;
      String s = f.getName();
      int i = s.lastIndexOf(46);
      if (i > 0 && i < s.length() - 1) {
         ext = s.substring(i + 1).toLowerCase();
      }

      return ext;
   }
}
