package ec.gob.firmadigital.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DialogUtils {
   private static final int DEFAULT_MAX_WIDTH = 900;

   public static void showDialog(Component parent, String message, String content, String title, int jOptionPaneMessage) {
      showDialog(parent, message, content, title, 900, jOptionPaneMessage);
   }

   public static void showDialog(Component parent, String message, String content, String title, int maxWidth, int jOptionPaneMessage) {
      JPanel panel = createPanel(message, content, maxWidth);
      showDialog(parent, panel, title, jOptionPaneMessage);
   }

   private static JPanel createPanel(String message, String content, int maxWidth) {
      JPanel panel = new JPanel(new BorderLayout(10, 10));
      JLabel lblMensaje = new JLabel(message + ":");
      panel.add(lblMensaje, "North");
      JLabel lblContenido = new JLabel();
      SmartLongLabel.configureLongLabelUI(lblContenido, maxWidth);
      lblContenido.setText(content);
      panel.add(lblContenido, "Center");
      return panel;
   }

   private static void showDialog(Component parent, JPanel panel, String title, int jOptionPaneMessage) {
      JOptionPane.showMessageDialog(parent, panel, title, jOptionPaneMessage);
   }

   public static void showDialogWithCopy(Component parent, String message, String title, String content, int jOptionPaneMessage) {
      JPanel panel = createPanel(message, content, 900);
      JButton btnCopy = new JButton("boton.copiar");
      btnCopy.addActionListener(e -> copyToClipboard(content));
      JPanel buttonPanel = new JPanel(new FlowLayout(2));
      buttonPanel.add(btnCopy);
      panel.add(buttonPanel, "South");
      showDialog(parent, panel, title, jOptionPaneMessage);
   }

   private static void copyToClipboard(String text) {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(new StringSelection(text), null);
   }
}
