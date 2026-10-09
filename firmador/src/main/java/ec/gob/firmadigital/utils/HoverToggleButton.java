package ec.gob.firmadigital.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JToggleButton;

public class HoverToggleButton extends JToggleButton {
   float alpha = 0.9F;
   Color customColor = new Color(98, 92, 163);
   Color customColorOriginal = new Color(181, 186, 219);

   public HoverToggleButton() {
      this.setBorderPainted(false);
      this.setFocusPainted(false);
      this.setBackground(this.customColorOriginal);
      this.addMouseListener(new HoverToggleButton.MouseAdapterCustom());
   }

   public float getAlpha() {
      return this.alpha;
   }

   public void setAlpha(float alpha) {
      this.alpha = alpha;
      this.repaint();
   }

   @Override
   public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      g2.setComposite(AlphaComposite.getInstance(3, this.alpha));
      super.paintComponent(g2);
   }

   public class MouseAdapterCustom extends MouseAdapter {
      @Override
      public void mouseExited(MouseEvent me) {
         HoverToggleButton.this.setForeground(Color.BLACK);
         HoverToggleButton.this.setBackground(HoverToggleButton.this.customColorOriginal);
         new Thread(() -> {
            for (float i = 1.0F; i >= HoverToggleButton.this.alpha; i -= 0.03F) {
               HoverToggleButton.this.setAlpha(i);

               try {
                  Thread.sleep(10L);
               } catch (InterruptedException var3) {
               }
            }
         }).start();
      }

      @Override
      public void mouseEntered(MouseEvent me) {
         HoverToggleButton.this.setForeground(Color.WHITE);
         HoverToggleButton.this.setBackground(HoverToggleButton.this.customColor);
         new Thread(() -> {
            for (float i = HoverToggleButton.this.alpha; i <= 1.0F; i += 0.03F) {
               HoverToggleButton.this.setAlpha(i);

               try {
                  Thread.sleep(10L);
               } catch (InterruptedException var3) {
               }
            }
         }).start();
      }

      @Override
      public void mousePressed(MouseEvent me) {
         HoverToggleButton.this.setForeground(Color.WHITE);
         HoverToggleButton.this.setBackground(HoverToggleButton.this.customColor);
         new Thread(() -> {
            for (float i = 1.0F; i >= HoverToggleButton.this.alpha + 0.1F; i -= 0.1F) {
               HoverToggleButton.this.setAlpha(i);

               try {
                  Thread.sleep(1L);
               } catch (InterruptedException var3) {
               }
            }
         }).start();
      }
   }
}
