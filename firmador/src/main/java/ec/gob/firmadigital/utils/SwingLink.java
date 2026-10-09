package ec.gob.firmadigital.utils;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public final class SwingLink extends JLabel {
   private String text;
   private String uriText;

   public SwingLink(String text, String uriText, URI uri) {
      this.setup(text, uriText, uri);
   }

   public SwingLink(String text, String uriText, String uri) {
      this.setup(text, uriText, URI.create(uri));
   }

   public SwingLink(String uriText, URI uri) {
      this.setup("", uriText, uri);
   }

   public SwingLink(String uriText, String uri) {
      this.setup("", uriText, URI.create(uri));
   }

   public void setup(final String text, final String uriText, final URI uri) {
      this.text = text;
      this.uriText = uriText;
      this.setText(uriText);
      this.setToolTipText(uri.toString());
      this.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            SwingLink.open(uri);
         }

         @Override
         public void mouseEntered(MouseEvent e) {
            SwingLink.this.setText(text, uriText, false);
         }

         @Override
         public void mouseExited(MouseEvent e) {
            SwingLink.this.setText(text, uriText, true);
         }
      });
   }

   @Override
   public void setText(String uriText) {
      this.setText(this.text, uriText, true);
   }

   public void setText(String text, String uriText, boolean ul) {
      String link = ul ? "<u>" + uriText + "</u>" : uriText;
      super.setText("<html>" + text + " <span style=\"color: #000099;\">" + link + "</span></html>");
      this.uriText = uriText;
   }

   public String getRawUriText() {
      return this.uriText;
   }

   private static void open(URI uri) {
      if (Desktop.isDesktopSupported()) {
         Desktop desktop = Desktop.getDesktop();

         try {
            desktop.browse(uri);
         } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to launch the link, your computer is likely misconfigured.", "Cannot Launch Link", 2);
         }
      } else {
         JOptionPane.showMessageDialog(null, "Java is not able to launch links on your computer.", "Cannot Launch Link", 2);
      }
   }
}
