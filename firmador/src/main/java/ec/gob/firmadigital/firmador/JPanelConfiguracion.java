package ec.gob.firmadigital.firmador;

import ec.gob.firmadigital.libreria.utils.FileUtils;
import ec.gob.firmadigital.libreria.utils.OsUtils;
import ec.gob.firmadigital.utils.HoverButton;
import ec.gob.firmadigital.utils.HoverToggleButton;
import ec.gob.firmadigital.utils.PropertiesUtils;
import ec.gob.firmadigital.utils.TransferHandlerComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JPanelConfiguracion extends JPanel {
   private static final Logger LOGGER = Logger.getLogger(JPanelConfiguracion.class.getName());
   private final FileNameExtensionFilter FILTRO_CERTIFICADOS = new FileNameExtensionFilter("Certificado Digital", "pfx", "p12");
   private String pathCertificado;
   private String uiScale;
   private final String IMAGE_RESTABLECER = PropertiesUtils.getConfig().getProperty("image.restablecer");
   private final String IMAGE_ESCALA = PropertiesUtils.getConfig().getProperty("image.escala");
   private final String IMAGE_BUSCAR_CERTIFICADO = PropertiesUtils.getConfig().getProperty("image.buscar_certificado");
   private final String FONT_WORKSANS_MEDIUM = PropertiesUtils.getConfig().getProperty("font.worksans_medium");
   private final String FONT_WORKSANS_REGULAR = PropertiesUtils.getConfig().getProperty("font.worksans_regular");
   private Font customFontWorkSansMedium;
   private Font customFontWorkSansRegular;
   private final ButtonGroup tipoResolucionBtnGRP = new ButtonGroup();
   private JLabel label;
   private ImageIcon imageIcon;
   private String multilineText;
   private ButtonGroup buttonGroupScale;
   private JSeparator jSeparator;
   private JSeparator jSeparator1;
   private JLabel jblCertificado;
   private JButton jbtnCertificado;
   private JButton jbtnRestablecer;
   private JLabel jlblDetalleEscalar;
   private JLabel jlblDetalleEscalar1;
   private JToggleButton jtb100;
   private JToggleButton jtb200;
   private JToggleButton jtb300;
   private JToggleButton jtb400;
   private JTextField jtxRutaCertificado;

   public void setPathCertificado(String pathCertificado) {
      this.pathCertificado = pathCertificado;
   }

   public static void main(String[] args) {
      try {
         System.setProperty("sun.java2d.uiScale", PropertiesUtils.getFirmaDigitalConfig().getProperty("uiScale"));
      } catch (NullPointerException npe) {
         System.setProperty("sun.java2d.uiScale", "1.0");
      }

      try {
         for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      }

      JPanelConfiguracion jPanelConfiguracion = new JPanelConfiguracion();
      JButton btnSalir = new JButton();
      btnSalir.setText("Salir");
      btnSalir.setMnemonic(83);
      Object[] options = new Object[]{btnSalir};
      btnSalir.addActionListener(evt1 -> {
         Component component1 = (Component)evt1.getSource();
         JDialog dialog = (JDialog)SwingUtilities.getRoot(component1);
         dialog.dispose();
      });
      JOptionPane.showOptionDialog(null, jPanelConfiguracion, "Panel de Configuración", 0, -1, null, options, options[0]);
   }

   public JPanelConfiguracion() {
      this.initComponents();
      this.design();
      this.dragAndDrop();
      this.setJTexFieldChanged(this.jtxRutaCertificado);
      this.uiScale = PropertiesUtils.getFirmaDigitalConfig().getProperty("uiScale");
      if (this.uiScale != null) {
         if (this.uiScale.equals("1.0")) {
            this.jtb100.setSelected(true);
         }

         if (this.uiScale.equals("2.0")) {
            this.jtb200.setSelected(true);
         }

         if (this.uiScale.equals("3.0")) {
            this.jtb300.setSelected(true);
         }

         if (this.uiScale.equals("4.0")) {
            this.jtb400.setSelected(true);
         }
      }

      this.pathCertificado = PropertiesUtils.getFirmaDigitalConfig().getProperty("user.home");
      if (this.pathCertificado != null) {
         this.jtxRutaCertificado.setText(this.pathCertificado);
      }

      this.mnemonic();
      if (OsUtils.isMac()) {
         this.jlblDetalleEscalar.setVisible(false);
         this.jlblDetalleEscalar1.setVisible(false);
         this.jtb100.setVisible(false);
         this.jtb200.setVisible(false);
         this.jtb300.setVisible(false);
         this.jtb400.setVisible(false);
      }
   }

   private void design() {
      try {
         this.customFontWorkSansMedium = Font.createFont(0, this.getClass().getClassLoader().getResourceAsStream(this.FONT_WORKSANS_MEDIUM));
         this.customFontWorkSansRegular = Font.createFont(0, this.getClass().getClassLoader().getResourceAsStream(this.FONT_WORKSANS_REGULAR));
      } catch (FontFormatException | IOException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      }

      this.jbtnRestablecer.setText("Restablecer");
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_RESTABLECER)).getImage().getScaledInstance(15, 15, 1)
      );
      this.jbtnRestablecer.setIcon(this.imageIcon);
      this.multilineText = "<html><body><b>Guardar Certificado</b><br>También lo puedes arrastrar aquí</body></html>";
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_BUSCAR_CERTIFICADO)).getImage().getScaledInstance(55, 55, 1)
      );
      this.label = new JLabel(this.multilineText);
      this.label.setIcon(this.imageIcon);
      this.label.setIconTextGap(10);
      this.label.setPreferredSize(this.newDimension(this.jbtnCertificado, 0, 0));
      this.label.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.label.setHorizontalAlignment(0);
      this.jbtnCertificado.setText("");
      this.jbtnCertificado.setName("jbtnCertificado");
      this.jbtnCertificado.add(this.label);
      this.jlblDetalleEscalar.setFont(this.customFontWorkSansMedium.deriveFont(15.0F));
      this.jlblDetalleEscalar.setHorizontalAlignment(0);
      this.jlblDetalleEscalar1.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jlblDetalleEscalar1.setHorizontalAlignment(0);
      this.multilineText = "<html><body>100%</body></html>";
      this.imageIcon = new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_ESCALA)).getImage().getScaledInstance(55, 55, 1));
      this.label = new JLabel(this.multilineText);
      this.label.setIcon(this.imageIcon);
      this.label.setIconTextGap(10);
      this.label.setPreferredSize(this.newDimension(this.jtb100, 0, 0));
      this.label.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jtb100.setText("");
      this.jtb100.add(this.label);
      this.multilineText = "<html><body>200%</body></html>";
      this.imageIcon = new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_ESCALA)).getImage().getScaledInstance(55, 55, 1));
      this.label = new JLabel(this.multilineText);
      this.label.setIcon(this.imageIcon);
      this.label.setIconTextGap(10);
      this.label.setPreferredSize(this.newDimension(this.jtb200, 0, 0));
      this.label.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jtb200.setText("");
      this.jtb200.add(this.label);
      this.multilineText = "<html><body>300%</body></html>";
      this.imageIcon = new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_ESCALA)).getImage().getScaledInstance(55, 55, 1));
      this.label = new JLabel(this.multilineText);
      this.label.setIcon(this.imageIcon);
      this.label.setIconTextGap(10);
      this.label.setPreferredSize(this.newDimension(this.jtb300, 0, 0));
      this.label.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jtb300.setText("");
      this.jtb300.add(this.label);
      this.multilineText = "<html><body>400%</body></html>";
      this.imageIcon = new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_ESCALA)).getImage().getScaledInstance(55, 55, 1));
      this.label = new JLabel(this.multilineText);
      this.label.setIcon(this.imageIcon);
      this.label.setIconTextGap(10);
      this.label.setPreferredSize(this.newDimension(this.jtb400, 0, 0));
      this.label.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jtb400.setText("");
      this.jtb400.add(this.label);
      this.tipoResolucionBtnGRP.add(this.jtb100);
      this.tipoResolucionBtnGRP.add(this.jtb200);
      this.tipoResolucionBtnGRP.add(this.jtb300);
      this.tipoResolucionBtnGRP.add(this.jtb400);
   }

   private Dimension newDimension(Component component, int widthAditional, int height) {
      return new Dimension((int)component.getPreferredSize().getWidth() + widthAditional, (int)component.getPreferredSize().getHeight() + height);
   }

   public void jbtnCertificado() {
      if (this.pathCertificado != null && !this.pathCertificado.isEmpty()) {
         File file = new File(this.pathCertificado);
         this.label = (JLabel)this.jbtnCertificado.getComponent(0);
         this.multilineText = "<html><body><b>Nombre del archivo</b><br>"
            + (file.getName().length() <= 20 ? file.getName() : file.getName().substring(0, 19) + "...")
            + "</body></html>";
         this.label.setText(this.multilineText);
         this.jbtnCertificado.setComponentZOrder(this.label, 0);
         this.jtxRutaCertificado.setText(this.pathCertificado);
      }
   }

   private void mnemonic() {
      this.jbtnCertificado.setMnemonic(69);
      this.jbtnRestablecer.setMnemonic(82);
   }

   private void dragAndDrop() {
      List<String> extensionesCertificados = new ArrayList<>(Arrays.asList(this.FILTRO_CERTIFICADOS.getExtensions()));
      this.jbtnCertificado.setTransferHandler(new TransferHandlerComponent(this, extensionesCertificados));
   }

   private void setJTexFieldChanged(JTextField jTextField) {
      DocumentListener documentListener = new DocumentListener() {
         @Override
         public void changedUpdate(DocumentEvent documentEvent) {
            JPanelConfiguracion.this.update();
         }

         @Override
         public void insertUpdate(DocumentEvent documentEvent) {
            JPanelConfiguracion.this.update();
         }

         @Override
         public void removeUpdate(DocumentEvent documentEvent) {
            JPanelConfiguracion.this.update();
         }
      };
      jTextField.getDocument().addDocumentListener(documentListener);
   }

   private void update() {
      this.pathCertificado = this.jtxRutaCertificado.getText();
      this.uiScale = "";
      if (this.jtb100.isSelected()) {
         this.uiScale = "1.0";
      }

      if (this.jtb200.isSelected()) {
         this.uiScale = "2.0";
      }

      if (this.jtb300.isSelected()) {
         this.uiScale = "3.0";
      }

      if (this.jtb400.isSelected()) {
         this.uiScale = "4.0";
      }

      PropertiesUtils.updateFirmaDigitalConfig(this.pathCertificado, this.uiScale);
   }

   private void initComponents() {
      this.buttonGroupScale = new ButtonGroup();
      this.jbtnRestablecer = new JButton();
      this.jblCertificado = new JLabel();
      this.jtxRutaCertificado = new JTextField();
      this.jbtnCertificado = new HoverButton();
      this.jtb100 = new HoverToggleButton();
      this.jtb200 = new HoverToggleButton();
      this.jtb300 = new HoverToggleButton();
      this.jtb400 = new HoverToggleButton();
      this.jlblDetalleEscalar1 = new JLabel();
      this.jSeparator = new JSeparator();
      this.jlblDetalleEscalar = new JLabel();
      this.jSeparator1 = new JSeparator();
      this.jbtnRestablecer.setText("Restablecer");
      this.jbtnRestablecer.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            JPanelConfiguracion.this.jbtnRestablecerActionPerformed(evt);
         }
      });
      this.jblCertificado.setText("<html><center>Ubicación de<br>Certificado</html>");
      this.jtxRutaCertificado.setEditable(false);
      this.jbtnCertificado.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            JPanelConfiguracion.this.jbtnCertificadoActionPerformed(evt);
         }
      });
      this.jtb100.setText("100%");
      this.jtb100.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            JPanelConfiguracion.this.jtb100ActionPerformed(evt);
         }
      });
      this.jtb200.setText("200%");
      this.jtb200.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            JPanelConfiguracion.this.jtb200ActionPerformed(evt);
         }
      });
      this.jtb300.setText("300%");
      this.jtb300.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            JPanelConfiguracion.this.jtb300ActionPerformed(evt);
         }
      });
      this.jtb400.setText("400%");
      this.jtb400.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            JPanelConfiguracion.this.jtb400ActionPerformed(evt);
         }
      });
      this.jlblDetalleEscalar1.setText("Al aplicar los cambios en la resolución, la aplicación se reiniciará");
      this.jlblDetalleEscalar.setText("Escalar tamaño y texto");
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addComponent(this.jtb100, -1, -1, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jtb200, -1, -1, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jtb300, -1, -1, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jtb400, -1, -1, 32767)
                  .addGap(277, 277, 277)
            )
            .addComponent(this.jlblDetalleEscalar1, -1, -1, 32767)
            .addComponent(this.jlblDetalleEscalar, -1, -1, 32767)
            .addComponent(this.jSeparator)
            .addGroup(layout.createSequentialGroup().addGap(99, 99, 99).addComponent(this.jbtnCertificado, -1, -1, 32767).addGap(89, 89, 89))
            .addComponent(this.jSeparator1)
            .addGroup(
               layout.createSequentialGroup()
                  .addGroup(
                     layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                           layout.createSequentialGroup()
                              .addComponent(this.jblCertificado, -2, -1, -2)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jtxRutaCertificado, -2, 475, -2)
                        )
                        .addComponent(this.jbtnRestablecer)
                  )
                  .addContainerGap(-1, 32767)
            )
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               Alignment.TRAILING,
               layout.createSequentialGroup()
                  .addComponent(this.jbtnRestablecer)
                  .addGap(8, 8, 8)
                  .addComponent(this.jbtnCertificado, -2, 75, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                     layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(this.jtxRutaCertificado, -2, 36, -2)
                        .addComponent(this.jblCertificado, -2, -1, -2)
                  )
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jSeparator, -2, 7, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jlblDetalleEscalar)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jlblDetalleEscalar1)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                     layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(this.jtb100)
                        .addComponent(this.jtb200)
                        .addComponent(this.jtb300)
                        .addComponent(this.jtb400)
                  )
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jSeparator1, -2, 7, -2)
            )
      );
   }

   private void jbtnRestablecerActionPerformed(ActionEvent evt) {
      this.jtxRutaCertificado.setText("");
      this.jtb100.setSelected(true);
      this.jtb200.setSelected(false);
      this.jtb300.setSelected(false);
      this.jtb400.setSelected(false);
      this.update();
      String path = System.getProperty("user.home") + "/cfg";
      File directoryCfg = new File(path);
      if (directoryCfg.exists()) {
         String[] entriesCfg = directoryCfg.list();

         for (String string : entriesCfg) {
            File currentFile = new File(directoryCfg.getPath(), string);
            currentFile.delete();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Unzipping to {0}", currentFile.getAbsolutePath());
         }

         directoryCfg.delete();
         Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Directorio {0} borrado", path);
      }

      System.exit(0);
   }

   private void jbtnCertificadoActionPerformed(ActionEvent evt) {
      this.jtxRutaCertificado.setText(FileUtils.rutaFichero(this.FILTRO_CERTIFICADOS, this.getRootPane()));
   }

   private void jtb100ActionPerformed(ActionEvent evt) {
      this.update();
      System.exit(0);
   }

   private void jtb200ActionPerformed(ActionEvent evt) {
      this.update();
      System.exit(0);
   }

   private void jtb300ActionPerformed(ActionEvent evt) {
      this.update();
      System.exit(0);
   }

   private void jtb400ActionPerformed(ActionEvent evt) {
      this.update();
      System.exit(0);
   }
}
