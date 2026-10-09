package ec.gob.firmadigital.libreria.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Util {
   private static final int BUFFER_SIZE = 4096;
   private static final String[] SUPPORTED_URI_SCHEMES = new String[]{"http", "https", "file", "urn"};
   private static final String DEFAULT_ENCODING = "utf-8";
   private static final Logger LOGGER = Logger.getLogger(Util.class.getName());
   private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   public static URI createURI(String file) throws URISyntaxException {
      if (file != null && !file.isEmpty()) {
         String filename = file.trim();
         if (filename.isEmpty()) {
            throw new IllegalArgumentException("La URI no puede ser una cadena vacia");
         }

         filename = filename.replace('\\', '/');
         filename = filename.replace(" ", "%20")
            .replace("<", "%3C")
            .replace(">", "%3E")
            .replace("\"", "%22")
            .replace("{", "%7B")
            .replace("}", "%7D")
            .replace("|", "%7C")
            .replace("^", "%5E")
            .replace("[", "%5B")
            .replace("]", "%5D")
            .replace("`", "%60");
         URI uri = new URI(filename);
         String scheme = uri.getScheme();

         for (String element : SUPPORTED_URI_SCHEMES) {
            if (element.equals(scheme)) {
               return uri;
            }
         }

         if (scheme == null) {
            filename = filename.replace("#", "%23");
            return createURI("file://" + filename);
         } else if (scheme.length() == 1 && Character.isLetter((char)scheme.getBytes()[0])) {
            filename = filename.replace("#", "%23");
            return createURI("file://" + filename);
         } else {
            throw new URISyntaxException(filename, "Tipo de URI no soportado");
         }
      } else {
         throw new IllegalArgumentException("No se puede crear una URI a partir de un nulo");
      }
   }

   public static InputStream loadFile(URI uri) throws IOException {
      if (uri == null) {
         throw new IllegalArgumentException("Se ha pedido el contenido de una URI nula");
      }

      if (uri.getScheme().equals("file")) {
         String path = uri.getSchemeSpecificPart();
         if (path.startsWith("//")) {
            path = path.substring(2);
         }

         return new FileInputStream(new File(path));
      } else {
         InputStream tmpStream = new BufferedInputStream(uri.toURL().openStream());
         byte[] tmpBuffer = getDataFromInputStream(tmpStream);
         return new ByteArrayInputStream(tmpBuffer);
      }
   }

   public static byte[] getDataFromInputStream(InputStream input) throws IOException {
      if (input == null) {
         return new byte[0];
      }

      byte[] buffer = new byte[4096];
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      int nBytes;
      while ((nBytes = input.read(buffer)) != -1) {
         baos.write(buffer, 0, nBytes);
      }

      return baos.toByteArray();
   }

   public static String getCN(X509Certificate c) {
      return c == null ? null : getCN(c.getSubjectX500Principal().toString());
   }

   public static String getCN(String principal) {
      if (principal == null) {
         return null;
      }

      String rdn = getRDNvalueFromLdapName("cn", principal);
      if (rdn == null) {
         rdn = getRDNvalueFromLdapName("ou", principal);
      }

      if (rdn != null) {
         return rdn;
      } else {
         int i = principal.indexOf(61);
         if (i != -1) {
            LOGGER.warning("No se ha podido obtener el Common Name ni la Organizational Unit, se devolvera el fragmento mas significativo");
            return getRDNvalueFromLdapName(principal.substring(0, i), principal);
         } else {
            LOGGER.warning("Principal no valido, se devolvera la entrada");
            return principal;
         }
      }
   }

   public static String getRDNvalueFromLdapName(String rdn, String principal) {
      int offset1 = 0;

      while ((offset1 = principal.toLowerCase(Locale.US).indexOf(rdn.toLowerCase(), offset1)) != -1) {
         if (offset1 > 0 && principal.charAt(offset1 - 1) != ',' && principal.charAt(offset1 - 1) != ' ') {
            offset1++;
         } else {
            offset1 += rdn.length();

            while (offset1 < principal.length() && principal.charAt(offset1) == ' ') {
               offset1++;
            }

            if (offset1 >= principal.length()) {
               return null;
            }

            if (principal.charAt(offset1) == '=') {
               offset1++;

               while (offset1 < principal.length() && principal.charAt(offset1) == ' ') {
                  offset1++;
               }

               if (offset1 >= principal.length()) {
                  return "";
               }

               if (principal.charAt(offset1) == ',') {
                  return "";
               }

               if (principal.charAt(offset1) == '"') {
                  if (++offset1 >= principal.length()) {
                     return "";
                  }

                  int offset2 = principal.indexOf(34, offset1);
                  if (offset2 == offset1) {
                     return "";
                  }

                  if (offset2 != -1) {
                     return principal.substring(offset1, offset2);
                  }

                  return principal.substring(offset1);
               }

               int offset2 = principal.indexOf(44, offset1);
               if (offset2 != -1) {
                  return principal.substring(offset1, offset2).trim();
               }

               return principal.substring(offset1).trim();
            }
         }
      }

      return null;
   }

   public static String hexify(byte[] abyte0, boolean separator) {
      if (abyte0 == null) {
         return "null";
      }

      StringBuffer stringbuffer = new StringBuffer(256);
      int i = 0;

      for (int j = 0; j < abyte0.length; j++) {
         if (separator && i > 0) {
            stringbuffer.append('-');
         }

         stringbuffer.append(HEX_CHARS[abyte0[j] >> 4 & 15]);
         stringbuffer.append(HEX_CHARS[abyte0[j] & 15]);
         if (++i == 16) {
            if (separator && j < abyte0.length - 1) {
               stringbuffer.append('\n');
            }

            i = 0;
         }
      }

      return stringbuffer.toString();
   }

   public static String hexify(byte[] abyte0, String separator) {
      if (abyte0 == null) {
         return "null";
      }

      StringBuffer stringbuffer = new StringBuffer(256);

      for (int j = 0; j < abyte0.length; j++) {
         if (separator != null && j > 0) {
            stringbuffer.append(separator);
         }

         stringbuffer.append(HEX_CHARS[abyte0[j] >> 4 & 15]);
         stringbuffer.append(HEX_CHARS[abyte0[j] & 15]);
      }

      return stringbuffer.toString();
   }

   public static void loadNativeLibrary(String path) throws IOException {
      if (path == null) {
         LOGGER.warning("No se puede cargar una biblioteca nula");
      } else {
         int pos = path.lastIndexOf(46);
         File file = new File(path);
         File tempLibrary = File.createTempFile(
            pos < 1 ? file.getName() : file.getName().substring(0, file.getName().indexOf(46)),
            pos >= 1 && pos != path.length() - 1 ? path.substring(pos) : null
         );
         copyFile(file, tempLibrary);
         if (tempLibrary != null) {
            tempLibrary.deleteOnExit();
         }

         LOGGER.log(Level.INFO, "Cargamos {0}", tempLibrary == null ? path : tempLibrary.getAbsolutePath());
         System.load(tempLibrary != null ? tempLibrary.getAbsolutePath() : path);
      }
   }

   public static void copyFile(File source, File dest) throws IOException {
      if (source != null && dest != null) {
         FileInputStream is = new FileInputStream(source);
         FileOutputStream os = new FileOutputStream(dest);
         FileChannel in = is.getChannel();
         FileChannel out = os.getChannel();
         MappedByteBuffer buf = in.map(MapMode.READ_ONLY, 0L, in.size());
         out.write(buf);
         in.close();
         out.close();
         is.close();
         os.close();
      } else {
         throw new IllegalArgumentException("Ni origen ni destino de la copia pueden ser nulos");
      }
   }

   public static String[] split(String text, String sp) {
      ArrayList<String> parts = new ArrayList<>();
      int i = 0;
      int j = 0;

      while (i != text.length() && (j = text.indexOf(sp, i)) != -1) {
         if (i == j) {
            parts.add("");
         } else {
            parts.add(text.substring(i, j));
         }

         i = j + sp.length();
      }

      if (i == text.length()) {
         parts.add("");
      } else {
         parts.add(text.substring(i));
      }

      return parts.toArray(new String[0]);
   }
}
