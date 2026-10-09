package ec.gob.firmadigital.libreria.utils;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckPDF {
   private static final Logger LOGGER = Logger.getLogger(CheckPDF.class.getName());

   private static void getJavaScriptFromPdfArray(PdfArray pdfArray, StringBuilder stringBuilder) {
      if (pdfArray != null) {
         for (PdfObject pdfObject : pdfArray) {
            if (pdfObject != null) {
               if (pdfObject.isDictionary()) {
                  getJavaScriptFromPdfDictionary((PdfDictionary)pdfObject, stringBuilder);
               } else if (pdfObject.isArray()) {
                  getJavaScriptFromPdfArray((PdfArray)pdfObject, stringBuilder);
               }
            }
         }
      }
   }

   private static void getJavaScriptFromPdfDictionary(PdfDictionary pdfDictionary, StringBuilder stringBuilder) {
      if (pdfDictionary != null) {
         PdfObject pdfObject = pdfDictionary.get(PdfName.JS);
         if (pdfObject != null) {
            if (pdfObject.isString()) {
               stringBuilder.append(((PdfString)pdfObject).getValue());
            } else if (pdfObject.isDictionary()) {
               getJavaScriptFromPdfDictionary((PdfDictionary)pdfObject, stringBuilder);
            } else if (pdfObject.isArray()) {
               getJavaScriptFromPdfArray((PdfArray)pdfObject, stringBuilder);
            }
         }
      }
   }

   private static String getPdfObject(PdfObject pdfObject) {
      StringBuilder stringBuilder = new StringBuilder();
      if (pdfObject != null) {
         if (pdfObject.isDictionary()) {
            getJavaScriptFromPdfDictionary((PdfDictionary)pdfObject, stringBuilder);
         } else if (pdfObject.isArray()) {
            getJavaScriptFromPdfArray((PdfArray)pdfObject, stringBuilder);
         } else if (pdfObject.isString()) {
            stringBuilder.append(((PdfString)pdfObject).getValue());
         } else if (pdfObject.isName()) {
            stringBuilder.append(((PdfName)pdfObject).getValue());
         } else {
            stringBuilder.append(pdfObject);
         }
      }

      return stringBuilder.toString().trim();
   }

   public static String checkPDF(PdfDocument pdfDocument) {
      String checkPDF = "";
      PdfDictionary pdfDictionaryCatalog = (PdfDictionary)pdfDocument.getCatalog().getPdfObject();
      PdfDictionary namesDictionary = pdfDictionaryCatalog.getAsDictionary(PdfName.OpenAction);
      if (namesDictionary != null && !namesDictionary.isEmpty()) {
         String openAction = "";
         PdfObject pdfObject = namesDictionary.get(PdfName.JS);
         if (pdfObject != null) {
            openAction = openAction + "\n" + PdfName.JS.getValue() + ": " + getPdfObject(pdfObject);
         }

         pdfObject = namesDictionary.get(PdfName.URI);
         if (pdfObject != null) {
            openAction = openAction + "\n" + PdfName.URI.getValue() + ": " + getPdfObject(pdfObject);
         }

         pdfObject = namesDictionary.get(PdfName.S);
         if (pdfObject != null) {
            openAction = openAction + "\n" + PdfName.S.getValue() + ": " + getPdfObject(pdfObject);
         }

         pdfObject = namesDictionary.get(PdfName.EmbeddedFile);
         if (pdfObject != null) {
            openAction = openAction + "\n" + PdfName.EmbeddedFile.getValue() + ": " + getPdfObject(pdfObject);
         }

         pdfObject = namesDictionary.get(PdfName.EmbeddedFiles);
         if (pdfObject != null) {
            openAction = openAction + "\n" + PdfName.EmbeddedFiles.getValue() + ": " + getPdfObject(pdfObject);
         }

         if (!openAction.isEmpty()) {
            checkPDF = "OpenAction:" + openAction;
         }
      }

      String javaScriptAnnotation = "";

      for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
         PdfPage page = pdfDocument.getPage(i);
         List<PdfAnnotation> annotList = page.getAnnotations();
         if (annotList != null) {
            for (PdfAnnotation pdfAnnotation : annotList) {
               if (pdfAnnotation.getPdfObject() != null) {
                  PdfDictionary annotationAction = ((PdfDictionary)pdfAnnotation.getPdfObject()).getAsDictionary(PdfName.A);
                  if (annotationAction != null && PdfName.JavaScript.equals(annotationAction.get(PdfName.S))) {
                     PdfString javascript = annotationAction.getAsString(PdfName.JS);
                     if (javascript != null) {
                        checkPDF = checkPDF + "\n" + javascript;
                     }
                  }
               }
            }
         }
      }

      if (!javaScriptAnnotation.isEmpty()) {
         checkPDF = checkPDF + "Annotation Action Level - JavaScript" + javaScriptAnnotation;
      }

      if (!checkPDF.isEmpty()) {
         LOGGER.log(Level.WARNING, checkPDF.trim());
         checkPDF = "El documento es potencialmente sospechoso para procesarlo";
      }

      return checkPDF.isEmpty() ? null : checkPDF;
   }
}
