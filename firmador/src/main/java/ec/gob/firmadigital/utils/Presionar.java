package ec.gob.firmadigital.utils;

import java.awt.event.KeyEvent;

public class Presionar {
   private static final char COMILLA_SIMPLE = '\'';
   private static final char MINUS = '-';
   private static final char UNDERSCORE = '_';
   private static final char AMPERSAND = '&';
   private static final char ARROBA = '@';
   private static final char SLASH = '/';
   private static final char ASTERISCO = '*';

   public static void presionarAlfanumericoEspacio(KeyEvent evt) {
      char key = evt.getKeyChar();
      if (key != ' '
         && key != '_'
         && key != '\''
         && key != '-'
         && key != '@'
         && key != '/'
         && key != '*'
         && key != '&'
         && (key < '0' || key > '9')
         && (key < 'a' || key > 'z')
         && key != 241
         && key != 225
         && key != 233
         && key != 237
         && key != 243
         && key != 250
         && (key < 'A' || key > 'Z')
         && key != 209
         && key != 193
         && key != 201
         && key != 205
         && key != 211
         && key != 218) {
         evt.consume();
      }
   }

   public static void presionarNumeros(KeyEvent evt) {
      char key = evt.getKeyChar();
      if ((key < '0' || key > '9') && key != 127 && key != '\b' && key != '\n') {
         evt.consume();
      }
   }
}
