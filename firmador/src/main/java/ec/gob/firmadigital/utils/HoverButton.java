package ec.gob.firmadigital.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class HoverButton extends JButton {
   float alpha = 0.9F;
   Color customColor = new Color(98, 92, 163);
   Color customColorOriginal = new Color(181, 186, 219);

   public HoverButton() {
      this.setBorderPainted(false);
      this.setFocusPainted(false);
      this.setBackground(this.customColorOriginal);
      this.addMouseListener(new HoverButton.MouseAdapterCustom());
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
         if (HoverButton.this.getComponentCount() > 0 && HoverButton.this.isEnabled()) {
            HoverButton.this.getComponent(0).setForeground(Color.BLACK);
         }

         HoverButton.this.setBackground(HoverButton.this.customColorOriginal);
         new Thread(() -> {
            for (float i = 1.0F; i >= HoverButton.this.alpha; i -= 0.03F) {
               HoverButton.this.setAlpha(i);

               try {
                  Thread.sleep(10L);
               } catch (InterruptedException var3) {
               }
            }
         }).start();
      }

      @Override
      public void mouseEntered(MouseEvent me) {
         if (HoverButton.this.getComponentCount() > 0 && HoverButton.this.isEnabled()) {
            HoverButton.this.getComponent(0).setForeground(Color.WHITE);
         }

         HoverButton.this.setBackground(HoverButton.this.customColor);
         new Thread(() -> {
            for (float i = HoverButton.this.alpha; i <= 1.0F; i += 0.03F) {
               HoverButton.this.setAlpha(i);

               try {
                  Thread.sleep(10L);
               } catch (InterruptedException var3) {
               }
            }
         }).start();
      }

      @Override
      public void mousePressed(MouseEvent me) {
         if (HoverButton.this.getComponentCount() > 0 && HoverButton.this.isEnabled()) {
            HoverButton.this.getComponent(0).setForeground(Color.WHITE);
         }

         HoverButton.this.setBackground(HoverButton.this.customColor);
         new Thread(() -> {
            for (float i = 1.0F; i >= HoverButton.this.alpha + 0.1F; i -= 0.1F) {
               HoverButton.this.setAlpha(i);

               try {
                  Thread.sleep(1L);
               } catch (InterruptedException var3) {
               }
            }
         }).start();
      }
   }
}
