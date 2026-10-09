package ec.gob.firmadigital.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicLabelUI;

public class SmartLongLabel extends JLabel {
   private static JLabel label;

   public static void configureLongLabelUI(final JLabel label, int maxWidth) {
      SmartLongLabel.label = label;
      label.setUI(new BasicLabelUI() {
         @Override
         public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D)g.create();

            try {
               g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
               Insets insets = c.getInsets();
               FontMetrics fm = g2.getFontMetrics();
               int lineHeight = fm.getHeight();
               int maxWidthx = c.getWidth() - insets.left - insets.right - 10;
               int y = insets.top + fm.getAscent();
               String text = label.getText();
               if (text != null && !text.isEmpty()) {
                  String remaining;
                  for (remaining = text; !remaining.isEmpty() && y < c.getHeight() - insets.bottom; y += lineHeight) {
                     int breakPos = this.findBreakPosition(remaining, fm, maxWidthx);
                     String line = remaining.substring(0, breakPos);
                     g2.drawString(line, insets.left + 5, y);
                     remaining = remaining.substring(breakPos).trim();
                  }

                  if (!remaining.isEmpty()) {
                     g2.drawString("...", insets.left + 5, y);
                  }

                  return;
               }
            } finally {
               g2.dispose();
            }
         }

         private int findBreakPosition(String text, FontMetrics fm, int maxWidthx) {
            int lastSpace = -1;

            for (int i = 0; i < text.length(); i++) {
               if (Character.isWhitespace(text.charAt(i))) {
                  lastSpace = i;
               }

               if (fm.stringWidth(text.substring(0, i + 1)) > maxWidthx) {
                  return lastSpace > 0 ? lastSpace : i;
               }
            }

            return text.length();
         }

         @Override
         public Dimension getPreferredSize(JComponent c) {
            String text = label.getText();
            if (text != null && !text.isEmpty()) {
               Graphics g = c.getGraphics();
               if (g == null) {
                  return super.getPreferredSize(c);
               }

               FontMetrics fm = g.getFontMetrics();
               int lineHeight = fm.getHeight();
               int maxWidthx = 900;
               int lines = this.calculateLineCount(text, fm, maxWidthx);
               return new Dimension(maxWidthx, lines * lineHeight);
            } else {
               return super.getPreferredSize(c);
            }
         }

         private int calculateLineCount(String text, FontMetrics fm, int maxWidthx) {
            int lines = 0;

            for (String remaining = text; !remaining.isEmpty(); lines++) {
               int breakPos = this.findBreakPosition(remaining, fm, maxWidthx);
               remaining = remaining.substring(breakPos).trim();
            }

            return Math.max(1, lines);
         }
      });
      label.setVerticalAlignment(1);
      label.setHorizontalAlignment(2);
      label.setToolTipText(label.getText());
      label.setForeground(Color.BLUE.darker());
      label.setCursor(Cursor.getPredefinedCursor(12));
      label.setToolTipText("Haz clic para copiar: " + label.getText());
      label.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            SmartLongLabel.copyToClipboard(label.getText());
            SmartLongLabel.showCopyFeedback();
         }
      });
   }

   private static void copyToClipboard(String text) {
      StringSelection selection = new StringSelection(text);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(selection, null);
   }

   private static void showCopyFeedback() {
      Point location = label.getLocationOnScreen();
      JWindow feedback = new JWindow();
      JLabel label2 = new JLabel("¡Copiado!");
      label2.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
      feedback.getContentPane().add(label2);
      feedback.pack();
      feedback.setLocation(location.x, location.y - 25);
      feedback.setVisible(true);
      new Timer(1500, e -> feedback.dispose()).start();
   }
}
