package ec.gob.firmadigital.firmador;

import ec.gob.firmadigital.libreria.utils.FileUtils;
import ec.gob.firmadigital.utils.PropertiesUtils;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class JPanelAcercaDe extends JPanel {
   private static final Logger LOGGER = Logger.getLogger(JPanelAcercaDe.class.getName());
   private final String IMAGE_ICONO_FIRMAEC = PropertiesUtils.getConfig().getProperty("image.logo_firmaec");
   private final String FONT_WORKSANS_SEMIBOLD = PropertiesUtils.getConfig().getProperty("font.worksans_semibold");
   private final String FONT_WORKSANS_MEDIUM = PropertiesUtils.getConfig().getProperty("font.worksans_medium");
   private final String FONT_WORKSANS_REGULAR = PropertiesUtils.getConfig().getProperty("font.worksans_regular");
   private Font customFontWorkSansSemiBold;
   private Font customFontWorkSansMedium;
   private Font customFontWorkSansRegular;
   private static final String URL = "https://www.mit.gob.ec";
   private static final String ACUERDO_USO = "https://www.firmadigital.gob.ec/acuerdo-de-uso";
   private String log = System.getProperty("user.home") + "/firmadigital.log";
   private JLabel jlbAcuerdoUso;
   private JLabel jlbEtiqueta1;
   private JLabel jlbEtiqueta2;
   private JLabel jlbLog;
   private JLabel jlbUrl;
   private JLabel jlbVersion;

   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
         JFrame frame = new JFrame("Testing");
         frame.setDefaultCloseOperation(3);
         frame.add(new JPanelAcercaDe());
         frame.pack();
         frame.setLocationRelativeTo(null);
         frame.setVisible(true);
      });
   }

   public JPanelAcercaDe() {
      this.initComponents();
      Properties config = PropertiesUtils.getConfig();

      try {
         this.customFontWorkSansSemiBold = Font.createFont(0, this.getClass().getClassLoader().getResourceAsStream(this.FONT_WORKSANS_SEMIBOLD));
         this.customFontWorkSansMedium = Font.createFont(0, this.getClass().getClassLoader().getResourceAsStream(this.FONT_WORKSANS_MEDIUM));
         this.customFontWorkSansRegular = Font.createFont(0, this.getClass().getClassLoader().getResourceAsStream(this.FONT_WORKSANS_REGULAR));
      } catch (FontFormatException | IOException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      }

      ImageIcon imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_ICONO_FIRMAEC)).getImage().getScaledInstance(130, 30, 4)
      );
      this.jlbEtiqueta1.setIcon(imageIcon);
      this.jlbEtiqueta1.setVerticalTextPosition(3);
      this.jlbEtiqueta1.setFont(this.customFontWorkSansSemiBold.deriveFont(15.0F));
      this.jlbEtiqueta1
         .setText(
            "<html><div style='text-align: center; font-size: 1em; font-weight: bold; color:rgb(98, 92, 163);'>FirmaEC - Firma Electrónica Ecuador</div><div style='text-align: center; font-size: 0.75em;'>Versión: "
               + config.getProperty("version")
               + " - "
               + config.getProperty("fecha")
               + "</div></html>"
         );
      this.jlbEtiqueta2.setFont(this.customFontWorkSansSemiBold.deriveFont(13.0F));
      this.jlbEtiqueta2
         .setText(
            "<html><div style='text-align: center; font-size: 1em; font-weight: bold;'><br%>Sistema desarrollado por:</div><div style='text-align: center; font-size: 1em;'>Subsecretaría de Gobierno Electrónico y Registro Civil<br>Ministerio de Infraestructura y Tecnología</div></html>"
         );
      this.jlbUrl.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jlbUrl.setText("<html><div style='text-align: center; font-size: 1em;'><u>https://www.mit.gob.ec</u></div></html>");
      this.jlbVersion.setVisible(false);
      this.jlbLog.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jlbLog
         .setText("<html><div style='text-align: center; font-size: 1em; font-weight: bold;'><br>Registro del sistema</div><u>" + this.log + "</u></html>");
      this.jlbAcuerdoUso.setFont(this.customFontWorkSansMedium.deriveFont(13.0F));
      this.jlbAcuerdoUso
         .setText(
            "<html><div style='text-align: center; font-size: 1em; font-weight: bold;'><br>Acuerdo de Uso</div><u>https://www.firmadigital.gob.ec/acuerdo-de-uso</u></html>"
         );
      this.jlbUrl.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            try {
               Desktop.getDesktop().browse(new URI("https://www.mit.gob.ec"));
            } catch (URISyntaxException | IOException ex) {
               JPanelAcercaDe.LOGGER.log(Level.SEVERE, null, ex);
            }
         }
      });
      this.jlbLog.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            try {
               FileUtils.abrirDocumento(JPanelAcercaDe.this.log);
            } catch (IOException | IllegalArgumentException ex) {
               JOptionPane.showMessageDialog(null, "No se generó ningún registro", "Error", 0);
            }
         }
      });
      this.jlbAcuerdoUso.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            try {
               Desktop.getDesktop().browse(new URI("https://www.firmadigital.gob.ec/acuerdo-de-uso"));
            } catch (URISyntaxException | IOException ex) {
               JPanelAcercaDe.LOGGER.log(Level.SEVERE, null, ex);
            }
         }
      });
   }

   private void initComponents() {
      this.jlbEtiqueta1 = new JLabel();
      this.jlbEtiqueta2 = new JLabel();
      this.jlbUrl = new JLabel();
      this.jlbVersion = new JLabel();
      this.jlbLog = new JLabel();
      this.jlbAcuerdoUso = new JLabel();
      this.jlbEtiqueta1.setHorizontalAlignment(0);
      this.jlbEtiqueta1.setCursor(new Cursor(0));
      this.jlbEtiqueta1.setHorizontalTextPosition(0);
      this.jlbEtiqueta2.setHorizontalAlignment(0);
      this.jlbEtiqueta2.setCursor(new Cursor(0));
      this.jlbEtiqueta2.setHorizontalTextPosition(0);
      this.jlbUrl.setHorizontalAlignment(0);
      this.jlbUrl.setCursor(new Cursor(0));
      this.jlbUrl.setHorizontalTextPosition(0);
      this.jlbVersion.setHorizontalAlignment(0);
      this.jlbVersion.setCursor(new Cursor(0));
      this.jlbVersion.setHorizontalTextPosition(0);
      this.jlbLog.setHorizontalAlignment(0);
      this.jlbLog.setCursor(new Cursor(0));
      this.jlbLog.setHorizontalTextPosition(0);
      this.jlbAcuerdoUso.setHorizontalAlignment(0);
      this.jlbAcuerdoUso.setCursor(new Cursor(0));
      this.jlbAcuerdoUso.setHorizontalTextPosition(0);
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addGap(23, 23, 23)
                  .addGroup(
                     layout.createParallelGroup(Alignment.CENTER)
                        .addComponent(this.jlbEtiqueta1, -1, 22, 32767)
                        .addComponent(this.jlbEtiqueta2, -1, -1, 32767)
                        .addComponent(this.jlbUrl, -1, -1, 32767)
                        .addComponent(this.jlbVersion, -1, -1, 32767)
                        .addComponent(this.jlbLog, -1, -1, 32767)
                        .addComponent(this.jlbAcuerdoUso, -1, -1, 32767)
                  )
                  .addGap(26, 26, 26)
            )
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addGap(12, 12, 12)
                  .addComponent(this.jlbEtiqueta1, -1, 21, 32767)
                  .addGap(6, 6, 6)
                  .addComponent(this.jlbEtiqueta2, -1, 21, 32767)
                  .addGap(6, 6, 6)
                  .addComponent(this.jlbUrl, -1, 21, 32767)
                  .addGap(6, 6, 6)
                  .addComponent(this.jlbVersion, -1, 22, 32767)
                  .addGap(5, 5, 5)
                  .addComponent(this.jlbLog, -1, 22, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jlbAcuerdoUso, -1, 22, 32767)
                  .addGap(13, 13, 13)
            )
      );
   }
}
