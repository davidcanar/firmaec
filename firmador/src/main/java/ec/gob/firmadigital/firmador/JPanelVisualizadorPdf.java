package ec.gob.firmadigital.firmador;

import ec.gob.firmadigital.utils.Presionar;
import ec.gob.firmadigital.utils.PropertiesUtils;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public final class JPanelVisualizadorPdf extends JPanel {
   private static final Logger LOGGER = Logger.getLogger(JPanelVisualizadorPdf.class.getName());
   private final File documento;
   private final int limiteRazon;
   private final int limiteLocalizacion;
   private final int signatureWidth;
   private final int signatureHeight;
   private int tmpSignatureWidth;
   private int tmpSignatureHeight;
   private PDDocument pdfDocument;
   private PDFRenderer pdfRenderer;
   private BufferedImage bufferedImage = null;
   private ImageIcon iconImage;
   private final Point point = new Point();
   private SpinnerModel spNumPagesModel;
   private final String IMAGE_ICONO_FIRMAEC = PropertiesUtils.getConfig().getProperty("image.logo_firmaec");
   private final Image image = new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_ICONO_FIRMAEC)).getImage();
   private int width;
   private int height;
   private JLabel label;
   private int tmpPage = 0;
   private int tmpZoom = 0;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPanel;
   private JLabel jblEstampado;
   private JLabel jblNroPagina;
   private JLabel jblNroPaginaTotal;
   private JLabel jblNroZoom;
   private JLabel jblZoom;
   private JComboBox<String> jcbEstampado;
   private JLabel jlblLocalizacion;
   private JLabel jlblMensaje;
   private JLabel jlblRazon;
   private JLabel jlblRuta;
   private JSpinner jspNumPages;
   private JSpinner jspZoom;
   private JTextArea jtaRuta;
   private JTextField jtxLocalizacion;
   private JTextField jtxRazonFirma;
   private JPanel preview;

   public static void main(String[] args) {
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

      File documento = new File("/home/Misael Fernández/Test/test.pdf");
      JPanelVisualizadorPdf jPanelFirmaPdf = new JPanelVisualizadorPdf(documento, 70, 70, 153, 50);
      JButton btnEstampar = new JButton();
      btnEstampar.setText("Estampar");
      btnEstampar.setMnemonic(69);
      btnEstampar.addActionListener(evt -> {
         Component component = (Component)evt.getSource();
         JDialog dialog = (JDialog)SwingUtilities.getRoot(component);
         dialog.dispose();
      });
      Object[] options = new Object[]{btnEstampar};
      JOptionPane.showOptionDialog(null, jPanelFirmaPdf, "Visualizador PDF para estampado de firma en formato A4", 0, -1, null, options, options[0]);
   }

   public JPanelVisualizadorPdf(File documento, int limiteRazon, int limiteLocalizacion, int signatureWidth, int signatureHeight) {
      this.documento = documento;
      this.limiteRazon = limiteRazon;
      this.limiteLocalizacion = limiteLocalizacion;
      this.signatureWidth = signatureWidth;
      this.signatureHeight = signatureHeight;
      this.initComponents();
      String[] arregloItems = new String[]{"QR", "Avanzada", "Simple"};
      this.jcbEstampado.setModel(new DefaultComboBoxModel<>(arregloItems));
      this.jtaRuta.setText(this.documento.getPath());
      this.getDocumento();
      SpinnerModel spZoom = new SpinnerNumberModel(90, 45, 100, 5);
      this.jspZoom.setModel(spZoom);
      this.jspNumPages.setValue(this.pdfDocument.getNumberOfPages());
      this.loadPreview(this.getPagina(), this.getZoom());
      this.eventos();
   }

   private void eventos() {
      this.jScrollPanel
         .getViewport()
         .addChangeListener(
            e -> {
               if (this.tmpPage > 0 && this.tmpPage == this.getPagina()) {
                  this.width = (this.preview.getWidth() - this.label.getWidth()) / 2;
                  this.height = (this.preview.getHeight() - this.label.getHeight()) / 2;
                  this.preview
                     .getGraphics()
                     .drawImage(
                        this.image,
                        this.point.x * this.getZoom() / this.tmpZoom + this.width,
                        this.point.y * this.getZoom() / this.tmpZoom + this.height,
                        this.tmpSignatureWidth,
                        this.tmpSignatureHeight,
                        null
                     );
               }
            }
         );
      this.jspNumPages.addChangeListener(e -> {
         JSpinner jSpinner = (JSpinner)e.getSource();
         this.loadPreview((Integer)jSpinner.getValue(), (Integer)this.jspZoom.getValue());
         this.jspNumPages.requestFocus();
      });
      this.jspZoom.addChangeListener(e -> {
         JSpinner jSpinner = (JSpinner)e.getSource();
         this.loadPreview((Integer)this.jspNumPages.getValue(), (Integer)jSpinner.getValue());
         this.jspZoom.requestFocus();
      });
      this.jtxRazonFirma.addKeyListener(new KeyAdapter() {
         @Override
         public void keyTyped(KeyEvent evt) {
            if (JPanelVisualizadorPdf.this.jtxRazonFirma.getText().length() < JPanelVisualizadorPdf.this.limiteRazon) {
               Presionar.presionarAlfanumericoEspacio(evt);
            } else {
               evt.consume();
            }

            if (evt.getKeyChar() == '\n') {
               evt.consume();
            }
         }
      });
      this.jtxLocalizacion.addKeyListener(new KeyAdapter() {
         @Override
         public void keyTyped(KeyEvent evt) {
            if (JPanelVisualizadorPdf.this.jtxLocalizacion.getText().length() < JPanelVisualizadorPdf.this.limiteLocalizacion) {
               Presionar.presionarAlfanumericoEspacio(evt);
            } else {
               evt.consume();
            }

            if (evt.getKeyChar() == '\n') {
               evt.consume();
            }
         }
      });
   }

   private void eventoLabel() {
      this.label
         .addMouseListener(
            new MouseAdapter() {
               Point hotSpot = new Point(0, 0);
               Toolkit toolkit = Toolkit.getDefaultToolkit();
               Cursor cursor = toolkit
                  .createCustomCursor(
                     JPanelVisualizadorPdf.this.image
                        .getScaledInstance(JPanelVisualizadorPdf.this.tmpSignatureWidth, JPanelVisualizadorPdf.this.tmpSignatureHeight, 1),
                     hotSpot,
                     ""
                  );

               @Override
               public void mouseEntered(MouseEvent e) {
                  if (0 == JPanelVisualizadorPdf.this.getCursor().getType()) {
                     JPanelVisualizadorPdf.this.setCursor(this.cursor);
                  }
               }

               @Override
               public void mouseExited(MouseEvent e) {
                  JPanelVisualizadorPdf.this.setCursor(Cursor.getPredefinedCursor(0));
               }

               @Override
               public void mouseClicked(MouseEvent e) {
                  switch (e.getButton()) {
                     case 1:
                        JPanelVisualizadorPdf.this.setCursor(this.cursor);
                        JPanelVisualizadorPdf.this.preview.repaint();
                        JPanelVisualizadorPdf.this.width = (JPanelVisualizadorPdf.this.preview.getWidth() - JPanelVisualizadorPdf.this.label.getWidth()) / 2;
                        JPanelVisualizadorPdf.this.height = (JPanelVisualizadorPdf.this.preview.getHeight() - JPanelVisualizadorPdf.this.label.getHeight()) / 2;
                        if (e.getPoint().x < JPanelVisualizadorPdf.this.iconImage.getIconWidth() - JPanelVisualizadorPdf.this.tmpSignatureWidth
                           && e.getPoint().y < JPanelVisualizadorPdf.this.iconImage.getIconHeight() - JPanelVisualizadorPdf.this.tmpSignatureHeight) {
                           JButton jbtnAceptar = new JButton();
                           jbtnAceptar.setText("Aceptar");
                           jbtnAceptar.setMnemonic(65);
                           JButton jbtnCancelar = new JButton();
                           jbtnCancelar.setText("Cancelar");
                           jbtnCancelar.setMnemonic(67);
                           Object[] options = new Object[]{jbtnAceptar, jbtnCancelar};
                           jbtnAceptar.addActionListener(evt -> {
                              JPanelVisualizadorPdf.this.point.setLocation(e.getPoint().x, e.getPoint().y);
                              Component component1 = (Component)evt.getSource();
                              JDialog dialog = (JDialog)SwingUtilities.getRoot(component1);
                              dialog.dispose();
                           });
                           jbtnCancelar.addActionListener(evt -> {
                              JPanelVisualizadorPdf.this.point.setLocation(0, 0);
                              Component component1 = (Component)evt.getSource();
                              JDialog dialog = (JDialog)SwingUtilities.getRoot(component1);
                              dialog.dispose();
                           });
                           JOptionPane.showOptionDialog(
                              JPanelVisualizadorPdf.this.getRootPane(),
                              "¿Insertar firma digital aquí?",
                              "Datos de firma digital",
                              2,
                              1,
                              null,
                              options,
                              options[0]
                           );
                        } else {
                           JOptionPane.showMessageDialog(
                              JPanelVisualizadorPdf.this.getRootPane(),
                              "Fuera de margen,\nSe estampa la firma en la posición superior izquierda de la hoja actual.",
                              "Error",
                              0
                           );
                           JPanelVisualizadorPdf.this.point.setLocation(0, 0);
                        }

                        JPanelVisualizadorPdf.this.tmpPage = JPanelVisualizadorPdf.this.getPagina();
                        JPanelVisualizadorPdf.this.tmpZoom = JPanelVisualizadorPdf.this.getZoom();
                        JPanelVisualizadorPdf.this.preview
                           .getGraphics()
                           .drawImage(
                              JPanelVisualizadorPdf.this.image,
                              JPanelVisualizadorPdf.this.point.x + JPanelVisualizadorPdf.this.width,
                              JPanelVisualizadorPdf.this.point.y + JPanelVisualizadorPdf.this.height,
                              JPanelVisualizadorPdf.this.tmpSignatureWidth,
                              JPanelVisualizadorPdf.this.tmpSignatureHeight,
                              null
                           );
                        System.out.println("zoom-> " + JPanelVisualizadorPdf.this.getZoom());
                        System.out.println("X-> " + JPanelVisualizadorPdf.this.point.getX() + ", Y->" + JPanelVisualizadorPdf.this.point.getY());
                        System.out.println("lblW-> " + JPanelVisualizadorPdf.this.label.getWidth() + ", lblH->" + JPanelVisualizadorPdf.this.label.getHeight());
                  }
               }
            }
         );
   }

   public File getDocumento() {
      try {
         this.pdfDocument = Loader.loadPDF(this.documento);
      } catch (IllegalArgumentException iae) {
         JOptionPane.showMessageDialog(this.getRootPane(), "Problema en membrete del documento (cabecera y/o pie de pagina)\n" + this.documento, "Error", 0);
         Logger.getLogger(JPanelVisualizadorPdf.class.getName()).log(Level.SEVERE, null, iae);
      } catch (IOException ex) {
         JOptionPane.showMessageDialog(this.getRootPane(), "Se ha detectado problemas al procesar el documento\n" + this.documento, "Error", 0);
         Logger.getLogger(JPanelVisualizadorPdf.class.getName()).log(Level.SEVERE, null, ex);
      }

      return this.documento;
   }

   public String getRazonFirma() {
      return this.jtxRazonFirma.getText();
   }

   public String getLocalizacion() {
      return this.jtxLocalizacion.getText();
   }

   public String getEstampado() {
      return this.jcbEstampado.getSelectedItem().toString();
   }

   public int getPagina() {
      return (Integer)this.jspNumPages.getValue();
   }

   public int getZoom() {
      return (Integer)this.jspZoom.getValue();
   }

   public Point getPoint() throws IOException {
      PDDocument pdf = Loader.loadPDF(this.documento);
      PDPage page = pdf.getPage(this.getPagina() == 0 ? this.getPagina() : this.getPagina() - 1);
      PDRectangle pageSize = page.getMediaBox();
      int porcentajeX = this.point.x * 72 / this.getZoom();
      int porcentajeY = this.point.y * 72 / this.getZoom();
      System.out.println("porcentajeX-> " + porcentajeX + ", porcentajeY->" + porcentajeY);
      return new Point(porcentajeX, (int)(pageSize.getHeight() - porcentajeY));
   }

   private BufferedImage loadPagePreview(int page, int zoom) {
      BufferedImage bim = null;

      try {
         bim = this.pdfRenderer.renderImageWithDPI(page == 0 ? page : page - 1, zoom, ImageType.RGB);
      } catch (IOException e) {
         e.printStackTrace();
      }

      return bim;
   }

   private boolean a4(PDRectangle pageSize) {
      int aproximado = 2;
      boolean a4Vertical = false;
      boolean a4Horizontal = false;
      if (pageSize.getWidth() >= (int)PDRectangle.A4.getWidth() - aproximado
         && pageSize.getWidth() <= (int)PDRectangle.A4.getWidth() + aproximado
         && pageSize.getHeight() >= (int)PDRectangle.A4.getHeight() - aproximado
         && pageSize.getHeight() <= (int)PDRectangle.A4.getHeight() + aproximado) {
         a4Vertical = true;
      }

      if (pageSize.getWidth() >= (int)PDRectangle.A4.getHeight() - aproximado
         && pageSize.getWidth() <= (int)PDRectangle.A4.getHeight() + aproximado
         && pageSize.getHeight() >= (int)PDRectangle.A4.getWidth() - aproximado
         && pageSize.getHeight() <= (int)PDRectangle.A4.getWidth() + aproximado) {
         a4Horizontal = true;
      }

      return a4Vertical || a4Horizontal;
   }

   private void loadPreview(int page, int zoom) {
      this.tmpSignatureWidth = this.signatureWidth * this.getZoom() / 100;
      this.tmpSignatureHeight = this.signatureHeight * this.getZoom() / 100;
      this.pdfRenderer = new PDFRenderer(this.pdfDocument);
      this.bufferedImage = this.loadPagePreview(page, zoom);
      PDRectangle pageSize = this.pdfDocument.getPage(this.getPagina() == 0 ? this.getPagina() : this.getPagina() - 1).getMediaBox();
      if (!this.a4(pageSize)) {
         JOptionPane.showMessageDialog(
            this.getRootPane(),
            "La hoja del documento no cumple con el formato A4 Horizontal (595 x 842 pixeles) o Vertical (842 x 595 pixeles)\n\nEl tamaño actual es: "
               + (int)pageSize.getHeight()
               + " x "
               + (int)pageSize.getWidth()
               + " pixeles",
            "Advertencia",
            2
         );
      }

      this.spNumPagesModel = new SpinnerNumberModel(page, 1, this.pdfDocument.getNumberOfPages(), 1);
      this.jblNroPaginaTotal.setText("/ " + this.pdfDocument.getNumberOfPages());
      this.jspNumPages.setModel(this.spNumPagesModel);
      assert this.bufferedImage != null;
      this.iconImage = new ImageIcon(this.bufferedImage);
      this.label = new JLabel(this.iconImage, 0);
      this.label.setSize(new Dimension(this.bufferedImage.getWidth(), this.bufferedImage.getHeight()));
      this.add(new JScrollPane(this.label));
      if (this.preview.getComponentCount() > 0) {
         this.preview.remove(this.preview.getComponent(0));
      }

      this.preview.setSize(new Dimension(this.bufferedImage.getWidth(), this.bufferedImage.getHeight()));
      this.preview.add(this.label, "First");
      this.preview.setLayout(new FlowLayout());
      this.preview.setOpaque(true);
      this.preview.setBorder(BorderFactory.createLoweredBevelBorder());
      this.preview.repaint();
      this.eventoLabel();
   }

   private void initComponents() {
      this.jlblRazon = new JLabel();
      this.jtxRazonFirma = new JTextField();
      this.jblNroPagina = new JLabel();
      this.jspNumPages = new JSpinner();
      this.jblNroPaginaTotal = new JLabel();
      this.jblZoom = new JLabel();
      this.jspZoom = new JSpinner();
      this.jblNroZoom = new JLabel();
      this.jlblMensaje = new JLabel();
      this.jScrollPanel = new JScrollPane();
      this.preview = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.jtaRuta = new JTextArea();
      this.jlblRuta = new JLabel();
      this.jcbEstampado = new JComboBox<>();
      this.jblEstampado = new JLabel();
      this.jtxLocalizacion = new JTextField();
      this.jlblLocalizacion = new JLabel();
      this.jlblRazon.setText("Razón de firma");
      this.jblNroPagina.setText("N° Página");
      this.jblNroPaginaTotal.setText("/");
      this.jblZoom.setText("Zoom");
      this.jblNroZoom.setText("/ 100%");
      this.jlblMensaje.setText("Seleccione con el puntero el lugar donde estampará la firma");
      GroupLayout previewLayout = new GroupLayout(this.preview);
      this.preview.setLayout(previewLayout);
      previewLayout.setHorizontalGroup(previewLayout.createParallelGroup(Alignment.LEADING).addGap(0, 773, 32767));
      previewLayout.setVerticalGroup(previewLayout.createParallelGroup(Alignment.LEADING).addGap(0, 429, 32767));
      this.jScrollPanel.setViewportView(this.preview);
      this.jtaRuta.setEditable(false);
      this.jtaRuta.setColumns(20);
      this.jScrollPane1.setViewportView(this.jtaRuta);
      this.jlblRuta.setText("<html>Ruta del<br>documento</html>");
      this.jblEstampado.setText("Estampado");
      this.jlblLocalizacion.setText("Localización");
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addComponent(this.jScrollPanel, -1, 791, 32767)
            .addGroup(
               layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                           layout.createSequentialGroup()
                              .addComponent(this.jlblMensaje)
                              .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                              .addComponent(this.jblNroPagina, -2, 64, -2)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jspNumPages, -2, 60, -2)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jblNroPaginaTotal, -2, 40, -2)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jblZoom)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jspZoom, -2, 60, -2)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jblNroZoom)
                        )
                        .addGroup(
                           layout.createSequentialGroup()
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING)
                                    .addComponent(this.jlblRazon, -2, 100, -2)
                                    .addComponent(this.jlblRuta, -2, 100, -2)
                                    .addComponent(this.jlblLocalizacion, -2, 100, -2)
                              )
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addGroup(
                                 layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(
                                       layout.createSequentialGroup()
                                          .addComponent(this.jtxLocalizacion)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(this.jblEstampado)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(this.jcbEstampado, -2, 113, -2)
                                    )
                                    .addComponent(this.jtxRazonFirma)
                                    .addComponent(this.jScrollPane1)
                              )
                        )
                  )
            )
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jtxRazonFirma, -2, -1, -2).addComponent(this.jlblRazon))
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                     layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(this.jcbEstampado, -2, -1, -2)
                        .addComponent(this.jblEstampado)
                        .addComponent(this.jtxLocalizacion, -2, -1, -2)
                        .addComponent(this.jlblLocalizacion)
                  )
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                     layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                           layout.createParallelGroup(Alignment.BASELINE)
                              .addComponent(this.jspZoom, -2, 28, -2)
                              .addComponent(this.jblZoom)
                              .addComponent(this.jblNroZoom)
                              .addComponent(this.jspNumPages, -2, 28, -2)
                              .addComponent(this.jblNroPagina)
                              .addComponent(this.jblNroPaginaTotal)
                        )
                        .addComponent(this.jlblMensaje)
                  )
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jScrollPanel, -1, 324, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane1, -1, -1, -2).addComponent(this.jlblRuta, -2, 27, -2))
                  .addContainerGap()
            )
      );
   }
}
