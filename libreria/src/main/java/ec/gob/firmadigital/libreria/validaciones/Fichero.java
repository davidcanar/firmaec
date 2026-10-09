package ec.gob.firmadigital.libreria.validaciones;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Fichero {
   public static String rutaPdf() throws Exception {
      String ruta = "";
      JFileChooser elegirRuta = new JFileChooser(new File(System.getProperty("user.home")));
      elegirRuta.setAcceptAllFileFilterUsed(false);
      elegirRuta.setFileFilter(new FiltrarArchivosPdf());
      int opcionElegida = elegirRuta.showSaveDialog(null);
      if (opcionElegida == 0) {
         File file = new File(elegirRuta.getSelectedFile() + elegirRuta.getFileFilter().toString());
         if (file.exists() && file.isFile()) {
            if (0 == JOptionPane.showConfirmDialog(null, "El archivo será sobrescrito. Desea continuar?", "Confirmacion", 0)) {
               file.delete();
               ruta = file.toString();
            }
         } else if (!file.isDirectory()) {
            ruta = file.toString();
         }
      }

      return ruta;
   }

   public static String ruta(File archivo, String extencion) throws Exception {
      String ruta = "";
      JFileChooser elegirRuta = new JFileChooser(new File(System.getProperty("user.home")));
      elegirRuta.setSelectedFile(archivo);
      elegirRuta.setAcceptAllFileFilterUsed(false);
      if (extencion.equals(".pdf")) {
         elegirRuta.setFileFilter(new FiltrarArchivosPdf());
      }

      int opcionElegida = elegirRuta.showSaveDialog(null);
      if (opcionElegida == 0) {
         String archivoEscogido = elegirRuta.getSelectedFile() + elegirRuta.getFileFilter().toString();
         if (archivoEscogido.indexOf(extencion, archivoEscogido.indexOf(extencion) + 1) > -1) {
            archivoEscogido = archivoEscogido.substring(0, archivoEscogido.indexOf(extencion, archivoEscogido.indexOf(extencion) + 1));
         }

         File file = new File(archivoEscogido);
         if (file.exists() && file.isFile()) {
            if (0 == JOptionPane.showConfirmDialog(null, "El archivo será sobrescrito. Desea continuar?", "Confirmacion", 0)) {
               file.delete();
               ruta = file.toString();
            }
         } else if (!file.isDirectory()) {
            ruta = file.toString();
         }
      }

      return ruta;
   }

   public static String rutaAbrir() throws Exception {
      String ruta = "";
      JFileChooser elegirRuta = new JFileChooser(new File(System.getProperty("user.home")));
      elegirRuta.setAcceptAllFileFilterUsed(false);
      int opcionElegida = elegirRuta.showOpenDialog(null);
      if (opcionElegida == 0) {
         File file = new File(elegirRuta.getSelectedFile().toString());
         if (file.exists() && file.isFile()) {
            ruta = file.toString();
         } else if (!file.isDirectory()) {
            ruta = file.toString();
         }
      }

      return ruta;
   }

   public static void guardarFichero(String rutaFichero, List<String> lista) throws IOException {
      FileWriter fichero = null;
      PrintWriter pw = null;

      try {
         fichero = new FileWriter(rutaFichero);
         pw = new PrintWriter(fichero);

         for (int i = 0; i < lista.size(); i++) {
            pw.print(lista.get(i));
         }

         pw.flush();
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         fichero.close();
         pw.close();
      }
   }

   public static List<String> leerFichero(String rutaFichero) throws IOException {
      List<String> lista = new ArrayList<>();
      File fichero = null;
      FileReader fr = null;
      BufferedReader br = null;

      try {
         fichero = new File(rutaFichero);
         fr = new FileReader(fichero);
         br = new BufferedReader(fr);

         String linea;
         while ((linea = br.readLine()) != null) {
            lista.add(linea);
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            if (null != fr) {
               fr.close();
            }
         } catch (Exception e2) {
            e2.printStackTrace();
         }

         return lista;
      }
   }

   public static List<String> separarLinea(String linea) {
      List<String> columnas = new ArrayList<>();

      try {
         StringTokenizer st = new StringTokenizer(linea, "¬");

         while (st.hasMoreTokens()) {
            String token = st.nextToken();
            columnas.add(token);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return columnas;
   }

   public static void abrirFichero(String rutaFichero) {
      File archivo = new File(rutaFichero);
      if (Desktop.isDesktopSupported()) {
         Desktop desktop = Desktop.getDesktop();

         try {
            if (archivo.exists()) {
               desktop.open(archivo);
            } else {
               JOptionPane.showMessageDialog(null, "No se encontrar el archivo: " + archivo.getAbsolutePath(), "Aviso", 2);
            }
         } catch (IOException var4) {
         }
      } else {
         JOptionPane.showMessageDialog(null, "No se puede ejecutar el comando de apertura en este sistema operativo", "Aviso", 2);
      }
   }

   public static void ficheros(String pathInicial, String mascara, LinkedList<File> listaFicheros, boolean busquedaRecursiva) {
      File directorioInicial = new File(pathInicial);
      if (directorioInicial.isDirectory()) {
         File[] ficheros = directorioInicial.listFiles();

         for (int i = 0; i < ficheros.length; i++) {
            if (ficheros[i].isDirectory() && busquedaRecursiva) {
               ficheros(ficheros[i].getAbsolutePath(), mascara, listaFicheros, busquedaRecursiva);
            } else if (Pattern.matches(mascara, ficheros[i].getName())) {
               listaFicheros.add(ficheros[i]);
            }
         }
      }
   }

   private static String regex(String mascara) {
      mascara = mascara.replace(".", "\\.");
      mascara = mascara.replace("*", ".*");
      return mascara.replace("?", ".");
   }

   public static void eliminarFicheros(String ruta, String ficheroBuscar) {
      LinkedList<File> ficheros = new LinkedList<>();
      ficheros(ruta, regex(ficheroBuscar), ficheros, true);

      for (int i = 0; i < ficheros.size(); i++) {
         System.out.println(ficheros.get(i).getAbsolutePath().toString());
         File file = new File(ficheros.get(i).getAbsolutePath().toString());
         file.delete();
      }
   }

   public static String rutaDirectorio() throws Exception {
      String rutaFichero = "";
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileSelectionMode(2);
      fileChooser.setDialogType(1);
      int seleccion = fileChooser.showOpenDialog(fileChooser);
      if (seleccion == 0) {
         File fichero = fileChooser.getSelectedFile();
         rutaFichero = fichero.getAbsolutePath();
      }

      return rutaFichero;
   }

   public void verificarCrearDirectorio() {
      String DIRECTORIO_TEMPORAL = "tmp";
      String directorioComprobantesAutorizados = "tmp/comprobantesAutorizados";
      String directorioComprobantesNoAutorizados = "tmp/comprobantesNoAutorizados";
      String directorioComprobantesGenerados = "tmp/comprobantesGenerados";
      String directorioComprobantesFirmados = "tmp/comprobantesFirmados";
      File directorio = new File(DIRECTORIO_TEMPORAL);
      if (!directorio.exists()) {
         directorio.mkdir();
         directorio = new File(directorioComprobantesAutorizados);
         if (!directorio.exists()) {
            directorio.mkdir();
         }

         directorio = new File(directorioComprobantesNoAutorizados);
         if (!directorio.exists()) {
            directorio.mkdir();
         }

         directorio = new File(directorioComprobantesGenerados);
         if (!directorio.exists()) {
            directorio.mkdir();
         }

         directorio = new File(directorioComprobantesFirmados);
         if (!directorio.exists()) {
            directorio.mkdir();
         }
      }
   }
}
