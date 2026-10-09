package ec.gob.firmadigital.firmador;

import com.itextpdf.kernel.exceptions.BadPasswordException;
import ec.gob.firmadigital.cliente.FirmaDigital;
import ec.gob.firmadigital.exceptions.DocumentoNoExistenteException;
import ec.gob.firmadigital.exceptions.DocumentoNoPermitidoException;
import ec.gob.firmadigital.exceptions.RazonFirma;
import ec.gob.firmadigital.exceptions.TokenNoConectadoException;
import ec.gob.firmadigital.exceptions.TokenNoEncontradoException;
import ec.gob.firmadigital.firmador.update.Update;
import ec.gob.firmadigital.libreria.certificate.CertEcUtils;
import ec.gob.firmadigital.libreria.certificate.CertUtils;
import ec.gob.firmadigital.libreria.certificate.to.DatosUsuario;
import ec.gob.firmadigital.libreria.certificate.to.Documento;
import ec.gob.firmadigital.libreria.exceptions.CancelException;
import ec.gob.firmadigital.libreria.exceptions.CertificadoInvalidoException;
import ec.gob.firmadigital.libreria.exceptions.ConexionException;
import ec.gob.firmadigital.libreria.exceptions.DocumentoException;
import ec.gob.firmadigital.libreria.exceptions.EntidadCertificadoraNoValidaException;
import ec.gob.firmadigital.libreria.exceptions.HoraServidorException;
import ec.gob.firmadigital.libreria.exceptions.RubricaException;
import ec.gob.firmadigital.libreria.exceptions.XploitException;
import ec.gob.firmadigital.libreria.keystore.FileKeyStoreProvider;
import ec.gob.firmadigital.libreria.keystore.KeyStoreProvider;
import ec.gob.firmadigital.libreria.keystore.KeyStoreProviderFactory;
import ec.gob.firmadigital.libreria.utils.FileUtils;
import ec.gob.firmadigital.libreria.utils.Json;
import ec.gob.firmadigital.libreria.utils.OsUtils;
import ec.gob.firmadigital.libreria.utils.Utils;
import ec.gob.firmadigital.libreria.utils.X509CertificateUtils;
import ec.gob.firmadigital.utils.CellRendererButton;
import ec.gob.firmadigital.utils.CellRendererWordWrap;
import ec.gob.firmadigital.utils.DialogUtils;
import ec.gob.firmadigital.utils.HoverButton;
import ec.gob.firmadigital.utils.HoverToggleButton;
import ec.gob.firmadigital.utils.Log;
import ec.gob.firmadigital.utils.PropertiesUtils;
import ec.gob.firmadigital.utils.SwingLink;
import ec.gob.firmadigital.utils.TransferHandlerComponent;
import jakarta.ws.rs.ProcessingException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.NoSuchFileException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicMenuBarUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Main extends JFrame {
   private static final String VERSION = PropertiesUtils.getConfig().getProperty("version");
   private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
   private static final Color CUSTOM_COLOR_ORIGINAL = new Color(181, 186, 219);
   private static final Color CUSTOM_COLOR = new Color(98, 92, 163);
   private final FileNameExtensionFilter FILTRO_DOCUMENTOS = new FileNameExtensionFilter("Documentos de Oficina", "pdf", "p7m", "odt", "ods", "odp", "xml");
   private final FileNameExtensionFilter FILTRO_CERTIFICADOS = new FileNameExtensionFilter("Certificado Digital", "pfx", "p12");
   private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(PropertiesUtils.getConfig().getProperty("setting.simple_date_format"));
   private final String IMAGE_ICONO_FIRMAEC = PropertiesUtils.getConfig().getProperty("image.icono_firmaec");
   private final String IMAGE_RESTABLECER = PropertiesUtils.getConfig().getProperty("image.restablecer");
   private final String IMAGE_PANEL_FIRMAR = PropertiesUtils.getConfig().getProperty("image.panel_firmar");
   private final String IMAGE_PANEL_VERIFICAR = PropertiesUtils.getConfig().getProperty("image.panel_verificar");
   private final String IMAGE_PANEL_VALIDAR = PropertiesUtils.getConfig().getProperty("image.panel_validar");
   private final String IMAGE_ABRIR = PropertiesUtils.getConfig().getProperty("image.abrir");
   private final String IMAGE_QUITAR = PropertiesUtils.getConfig().getProperty("image.quitar");
   private final String IMAGE_DETALLE = PropertiesUtils.getConfig().getProperty("image.detalle");
   private final String IMAGE_BUSCAR_DOCUMENTO = PropertiesUtils.getConfig().getProperty("image.buscar_documento");
   private final String IMAGE_BUSCAR_CERTIFICADO = PropertiesUtils.getConfig().getProperty("image.buscar_certificado");
   private final String IMAGE_BUSCAR_CERTIFICADO_ALTERNATIVE = PropertiesUtils.getConfig().getProperty("image.buscar_certificado_alternative");
   private final String IMAGE_FIRMAR_DOCUMENTO = PropertiesUtils.getConfig().getProperty("image.firmar_documento");
   private final String IMAGE_VERIFICAR_DOCUMENTO = PropertiesUtils.getConfig().getProperty("image.verificar_documento");
   private final String IMAGE_VALIDAR_CERTIFICADO = PropertiesUtils.getConfig().getProperty("image.validar_certificado");
   private final String IMAGE_CONFIGURACION = PropertiesUtils.getConfig().getProperty("image.configuracion");
   private final String IMAGE_AYUDA = PropertiesUtils.getConfig().getProperty("image.ayuda");
   private final String IMAGE_CORRECTO = PropertiesUtils.getConfig().getProperty("image.correcto");
   private final String IMAGE_INCORRECTO = PropertiesUtils.getConfig().getProperty("image.incorrecto");
   private final String FONT_WORKSANS_SEMIBOLD = PropertiesUtils.getConfig().getProperty("font.worksans_semibold");
   private final String FONT_WORKSANS_MEDIUM = PropertiesUtils.getConfig().getProperty("font.worksans_medium");
   private final String FONT_WORKSANS_REGULAR = PropertiesUtils.getConfig().getProperty("font.worksans_regular");
   private final JButton btnSi = new JButton();
   private final JButton btnNo = new JButton();
   private final JButton btnAceptar = new JButton();
   private final ButtonGroup tipoFirmarBtnGRP = new ButtonGroup();
   private final ButtonGroup tipoValidarBtnGRP = new ButtonGroup();
   private Font customFontWorkSansSemiBold;
   private Font customFontWorkSansMedium;
   private Font customFontWorkSansRegular;
   private static Properties messages;
   private static Properties config;
   private static Properties firmaDigitalConfig;
   private int pagina = 50;
   private final int limiteRazon = 50;
   private final int limiteLocalizacion = 50;
   private String pathCertificadoFirmar;
   private String pathCertificadoValidar;
   private File documento;
   private String alias;
   private String razonFirma;
   private String localizacion;
   private Point point;
   private List<String> extensionesCertificados;
   private List<String> extensionesDocumentos;
   private X509CertificateUtils x509CertificateUtils = null;
   private JLabel label;
   private ImageIcon imageIcon;
   private String multilineText;
   private DefaultTableModel tableModelFirmarDocumentos;
   private DefaultTableModel tableModelVerificarDocumentos;
   String[] columnNames;
   JTableHeader jTableHeader;
   private String estampado;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JScrollPane jScrollPaneFirmarDocumentos;
   private JScrollPane jScrollPaneVerificarDocumentos;
   private JSeparator jSeparatorFirmarDocumento1;
   private JSeparator jSeparatorFirmarDocumento2;
   private JSeparator jSeparatorValidarCertificado;
   private JSeparator jSeparatorValidarCertificado1;
   private JSeparator jSeparatorVerificarDocumento;
   private JButton jbtnCertificadoFirmar;
   private JButton jbtnCertificadoValidar;
   private JButton jbtnDocumentoFirmar;
   private JButton jbtnDocumentoVerificar;
   private JButton jbtnFirmar;
   private JButton jbtnRestablecerFirmar;
   private JButton jbtnRestablecerValidar;
   private JButton jbtnRestablecerVerificar;
   private JButton jbtnValidar;
   private JButton jbtnVerificar;
   private JCheckBox jchkBoxFirmaInvisible;
   private JLabel jlblCertificadoEnFimador;
   private JLabel jlblCertificadoEnFimador1;
   private JLabel jlblClaveFirmar;
   private JLabel jlblClaveValidar;
   private JLabel jlblDocumentosFirmar;
   private JLabel jlblDocumentosVerificar;
   private JLabel jlblResultadoValidacion;
   private JMenu jmAyuda;
   private JMenu jmConfiguracion;
   private JMenuBar jmbMenuPrincipal;
   private JMenuItem jmiAcerca;
   private JMenuItem jmiActualizar;
   private JMenuItem jmiAyuda;
   private JMenuItem jmiPanelConfiguracion;
   private JPasswordField jpfFirmarClave;
   private JPasswordField jpfValidarClave;
   private JPanel jplFirmarDocumento;
   private JPanel jplValidarCertificado;
   private JPanel jplVerificarDocumento;
   private JToggleButton jtbFirmarArchivo;
   private JToggleButton jtbFirmarToken;
   private JToggleButton jtbValidarArchivo;
   private JToggleButton jtbValidarToken;
   private JTable jtblFirmarDocumentos;
   private JTable jtblVerificarDocumentos;
   private JTextPane jtpValidarCertificado;
   private JTabbedPane mainPanel;

   public void setPathCertificadoFirmar(String pathCertificadoFirmar) {
      this.pathCertificadoFirmar = pathCertificadoFirmar;
   }

   public void setPathCertificadoValidar(String pathCertificadoValidar) {
      this.pathCertificadoValidar = pathCertificadoValidar;
   }

   public Main() {
      this.initComponents();
      this.design();
      this.inicializarTablas();
      this.mnemonic();
      this.tabulacion();
      this.eventos();
      this.dragAndDrop();
      this.restaurarPreferenciasFirmar();
      this.setTitle("FirmaEC " + VERSION);
   }

   private void design() {
      this.setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_ICONO_FIRMAEC)).getImage());
      UIManager.put("nimbusFocus", Color.WHITE);
      UIManager.put("nimbusBase", CUSTOM_COLOR);
      UIManager.put("nimbusBlueGrey", Color.WHITE);
      UIManager.put("control", Color.WHITE);
      UIManager.put("TabbedPane.tabInsets", new Insets(5, 10, 5, 10));
      UIManager.put("TabbedPane.tabAreaInsets", new Insets(0, 0, 0, 0));
      UIManager.put("TabbedPane.selectedLabelShift", 0);
      UIManager.put("TabbedPane.labelShift", 0);
      this.mainPanel.setOpaque(true);
      this.mainPanel.setTabLayoutPolicy(1);
      this.mainPanel
         .setUI(
            new BasicTabbedPaneUI() {
               private final Insets borderInsets = new Insets(0, 0, 0, 0);

               @Override
               protected void paintFocusIndicator(
                  Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected
               ) {
               }

               @Override
               protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
               }

               @Override
               protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                  g.setColor(isSelected ? Color.WHITE : Main.CUSTOM_COLOR_ORIGINAL);
                  g.fillRect(x, y, w, h);
               }

               @Override
               protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
                  g.setColor(Color.WHITE);
                  g.fillRect(x, y, w, h);
               }

               @Override
               protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
               }

               @Override
               protected Insets getContentBorderInsets(int tabPlacement) {
                  return this.borderInsets;
               }
            }
         );
      this.jmbMenuPrincipal.setOpaque(true);
      this.jmbMenuPrincipal.setUI(new BasicMenuBarUI());
      this.jmbMenuPrincipal.setBackground(CUSTOM_COLOR_ORIGINAL);
      this.jbtnFirmar.setBackground(CUSTOM_COLOR);
      this.jbtnVerificar.setBackground(CUSTOM_COLOR);
      this.jbtnValidar.setBackground(CUSTOM_COLOR);
      this.jbtnFirmar.setForeground(Color.WHITE);
      this.jpfFirmarClave.setSelectedTextColor(CUSTOM_COLOR);
      this.jpfValidarClave.setSelectedTextColor(CUSTOM_COLOR);
      this.jpfFirmarClave.setSelectionColor(CUSTOM_COLOR_ORIGINAL);
      this.jpfValidarClave.setSelectionColor(CUSTOM_COLOR_ORIGINAL);
      if (this.jbtnVerificar.isEnabled()) {
         this.jbtnVerificar.setForeground(Color.WHITE);
      } else {
         this.jbtnVerificar.setForeground(Color.BLACK);
      }

      if (this.jbtnValidar.isEnabled()) {
         this.jbtnValidar.setForeground(Color.WHITE);
      } else {
         this.jbtnValidar.setForeground(Color.BLACK);
      }

      this.jpfFirmarClave.setUI(new BasicPasswordFieldUI() {
         @Override
         public boolean contains(JComponent c, int x, int y) {
            if (c.isEnabled()) {
               c.setBorder(new LineBorder(Main.CUSTOM_COLOR, 1));
            } else {
               c.setBorder(new LineBorder(Main.CUSTOM_COLOR_ORIGINAL, 1));
            }

            return super.contains(c, x, y);
         }
      });
      this.jpfValidarClave.setUI(new BasicPasswordFieldUI() {
         @Override
         public boolean contains(JComponent c, int x, int y) {
            if (c.isEnabled()) {
               c.setBorder(new LineBorder(Main.CUSTOM_COLOR, 1));
            } else {
               c.setBorder(new LineBorder(Main.CUSTOM_COLOR_ORIGINAL, 1));
            }

            return super.contains(c, x, y);
         }
      });
      this.jSeparatorFirmarDocumento1.setBackground(Color.RED);
      this.jSeparatorFirmarDocumento1.setForeground(Color.RED);

      try {
         this.customFontWorkSansSemiBold = Font.createFont(0, this.getClass().getClassLoader().getResourceAsStream(this.FONT_WORKSANS_SEMIBOLD));
         this.customFontWorkSansMedium = Font.createFont(0, this.getClass().getClassLoader().getResourceAsStream(this.FONT_WORKSANS_MEDIUM));
         this.customFontWorkSansRegular = Font.createFont(0, this.getClass().getClassLoader().getResourceAsStream(this.FONT_WORKSANS_REGULAR));
      } catch (FontFormatException | IOException ex) {
         LOGGER.log(Level.SEVERE, null, ex.getMessage());
      }

      int width = 15;
      int height = 15;
      this.jmConfiguracion.setFont(this.customFontWorkSansMedium.deriveFont(14.0F));
      this.jmAyuda.setFont(this.customFontWorkSansMedium.deriveFont(14.0F));
      this.jmiPanelConfiguracion.setFont(this.customFontWorkSansMedium.deriveFont(13.0F));
      this.jmiActualizar.setFont(this.customFontWorkSansMedium.deriveFont(13.0F));
      this.jmiAyuda.setFont(this.customFontWorkSansMedium.deriveFont(13.0F));
      this.jmiAcerca.setFont(this.customFontWorkSansMedium.deriveFont(13.0F));
      this.jtbFirmarArchivo.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jtbFirmarToken.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jtbValidarArchivo.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jtbValidarToken.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      JLabel jLabelFirmarDocumento = new JLabel("Firmar Documento (1)");
      jLabelFirmarDocumento.setIcon(
         new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_PANEL_FIRMAR)).getImage().getScaledInstance(width, height, 1))
      );
      jLabelFirmarDocumento.setFont(this.customFontWorkSansSemiBold.deriveFont(15.0F));
      jLabelFirmarDocumento.setPreferredSize(new Dimension((int)jLabelFirmarDocumento.getPreferredSize().getWidth(), 30));
      this.mainPanel.setTabComponentAt(0, jLabelFirmarDocumento);
      JLabel jLabelVerificarDocumento = new JLabel("Verificar Documento (2)");
      jLabelVerificarDocumento.setIcon(
         new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_PANEL_VERIFICAR)).getImage().getScaledInstance(width, height, 1))
      );
      jLabelVerificarDocumento.setFont(this.customFontWorkSansSemiBold.deriveFont(15.0F));
      jLabelVerificarDocumento.setPreferredSize(new Dimension((int)jLabelVerificarDocumento.getPreferredSize().getWidth(), 30));
      this.mainPanel.setTabComponentAt(1, jLabelVerificarDocumento);
      JLabel jLabelValidarCertificado = new JLabel("Validar Certificado (3)");
      jLabelValidarCertificado.setIcon(
         new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_PANEL_VALIDAR)).getImage().getScaledInstance(width, height, 1))
      );
      jLabelValidarCertificado.setFont(this.customFontWorkSansSemiBold.deriveFont(15.0F));
      jLabelValidarCertificado.setPreferredSize(new Dimension((int)jLabelValidarCertificado.getPreferredSize().getWidth(), 30));
      this.mainPanel.setTabComponentAt(2, jLabelValidarCertificado);
      this.jlblClaveFirmar.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jlblDocumentosFirmar.setFont(this.customFontWorkSansSemiBold.deriveFont(13.0F));
      this.jlblDocumentosFirmar.setHorizontalAlignment(0);
      this.jlblDocumentosFirmar.setVerticalAlignment(0);
      this.jtblFirmarDocumentos.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jchkBoxFirmaInvisible.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jbtnFirmar.setFont(this.customFontWorkSansSemiBold.deriveFont(13.0F));
      this.jbtnRestablecerFirmar.setFont(this.customFontWorkSansSemiBold.deriveFont(13.0F));
      this.jlblDocumentosVerificar.setFont(this.customFontWorkSansSemiBold.deriveFont(13.0F));
      this.jlblDocumentosVerificar.setHorizontalAlignment(0);
      this.jlblDocumentosVerificar.setVerticalAlignment(0);
      this.jtblVerificarDocumentos.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jbtnVerificar.setFont(this.customFontWorkSansSemiBold.deriveFont(13.0F));
      this.jbtnRestablecerVerificar.setFont(this.customFontWorkSansSemiBold.deriveFont(13.0F));
      this.jlblClaveValidar.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jlblResultadoValidacion.setHorizontalAlignment(0);
      this.jlblResultadoValidacion.setFont(this.customFontWorkSansSemiBold.deriveFont(13.0F));
      this.jtpValidarCertificado.setEditable(false);
      this.jtpValidarCertificado.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.jbtnValidar.setFont(this.customFontWorkSansSemiBold.deriveFont(13.0F));
      this.jbtnRestablecerValidar.setFont(this.customFontWorkSansSemiBold.deriveFont(13.0F));
      this.jmConfiguracion
         .setIcon(
            new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_CONFIGURACION)).getImage().getScaledInstance(15, 15, 1)) {
               @Override
               public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                  super.paintIcon(c, g, x + 15, y);
               }
            }
         );
      this.jmAyuda
         .setIcon(new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_AYUDA)).getImage().getScaledInstance(15, 15, 1)) {
            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
               super.paintIcon(c, g, x + 15, y);
            }
         });
      this.multilineText = "<html><body><b>Buscar Certificado</b><br>También lo puedes arrastrar aquí</body></html>";
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_BUSCAR_CERTIFICADO)).getImage().getScaledInstance(55, 55, 1)
      );
      this.label = new JLabel(this.multilineText);
      this.label.setIcon(this.imageIcon);
      this.label.setIconTextGap(10);
      this.label.setPreferredSize(this.newDimension(this.jbtnCertificadoFirmar, 0, 0));
      this.label.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.label.setHorizontalAlignment(0);
      this.jbtnCertificadoFirmar.setText("");
      this.jbtnCertificadoFirmar.setName("jbtnCertificadoFirmar");
      this.jbtnCertificadoFirmar.add(this.label);
      this.multilineText = "<html><body><b>Buscar Documento(s)</b><br>También lo puedes arrastrar aquí</body></html>";
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_BUSCAR_DOCUMENTO)).getImage().getScaledInstance(55, 55, 1)
      );
      this.label = new JLabel(this.multilineText);
      this.label.setIcon(this.imageIcon);
      this.label.setIconTextGap(10);
      this.label.setPreferredSize(this.newDimension(this.jbtnDocumentoFirmar, 0, 0));
      this.label.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.label.setHorizontalAlignment(0);
      this.jbtnDocumentoFirmar.setText("");
      this.jbtnDocumentoFirmar.setName("jbtnDocumentoFirmar");
      this.jbtnDocumentoFirmar.add(this.label);
      this.jbtnFirmar.setText("Firmar");
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_FIRMAR_DOCUMENTO)).getImage().getScaledInstance(15, 15, 1)
      );
      this.jbtnFirmar.setIcon(this.imageIcon);
      this.jbtnRestablecerFirmar.setText("Restablecer");
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_RESTABLECER)).getImage().getScaledInstance(15, 15, 1)
      );
      this.jbtnRestablecerFirmar.setIcon(this.imageIcon);
      this.multilineText = "<html><body><b>Buscar Documento(s)</b><br>También lo puedes arrastrar aquí</body></html>";
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_BUSCAR_DOCUMENTO)).getImage().getScaledInstance(55, 55, 1)
      );
      this.label = new JLabel(this.multilineText);
      this.label.setIcon(this.imageIcon);
      this.label.setIconTextGap(10);
      this.label.setPreferredSize(this.newDimension(this.jbtnDocumentoVerificar, 0, 0));
      this.label.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.label.setHorizontalAlignment(0);
      this.jbtnDocumentoVerificar.setText("");
      this.jbtnDocumentoVerificar.setName("jbtnDocumentoVerificar");
      this.jbtnDocumentoVerificar.add(this.label);
      this.jbtnVerificar.setText("Verificar");
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_VERIFICAR_DOCUMENTO)).getImage().getScaledInstance(15, 15, 1)
      );
      this.jbtnVerificar.setIcon(this.imageIcon);
      this.jbtnRestablecerVerificar.setText("Restablecer");
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_RESTABLECER)).getImage().getScaledInstance(15, 15, 1)
      );
      this.jbtnRestablecerVerificar.setIcon(this.imageIcon);
      this.multilineText = "<html><body><b>Buscar Certificado</b><br>También lo puedes arrastrar aquí</body></html>";
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_BUSCAR_CERTIFICADO_ALTERNATIVE)).getImage().getScaledInstance(55, 55, 1)
      );
      this.label = new JLabel(this.multilineText);
      this.label.setIcon(this.imageIcon);
      this.label.setIconTextGap(10);
      this.label.setPreferredSize(this.newDimension(this.jbtnCertificadoValidar, 0, 0));
      this.label.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      this.label.setHorizontalAlignment(0);
      this.jbtnCertificadoValidar.setText("");
      this.jbtnCertificadoValidar.setName("jbtnCertificadoValidar");
      this.jbtnCertificadoValidar.add(this.label);
      this.jbtnValidar.setText("Validar");
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_VALIDAR_CERTIFICADO)).getImage().getScaledInstance(15, 15, 1)
      );
      this.jbtnValidar.setIcon(this.imageIcon);
      this.jbtnRestablecerValidar.setText("Restablecer");
      this.imageIcon = new ImageIcon(
         new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_RESTABLECER)).getImage().getScaledInstance(15, 15, 1)
      );
      this.jbtnRestablecerValidar.setIcon(this.imageIcon);
      this.jmConfiguracion.setPreferredSize(this.newDimension(this.jmConfiguracion, 40, 20));
      this.jmConfiguracion.setIconTextGap(30);
      this.jmAyuda.setPreferredSize(this.newDimension(this.jmAyuda, 40, 20));
      this.jmAyuda.setIconTextGap(30);
      this.jmiPanelConfiguracion.setPreferredSize(this.newDimension(this.jmiPanelConfiguracion, 0, 10));
      this.jmiPanelConfiguracion
         .setText("<html><table width='100%'><tr><td halign='center' align='center'>" + this.jmiPanelConfiguracion.getText() + "</td></tr></table></html>");
      this.jmiActualizar.setPreferredSize(this.newDimension(this.jmiActualizar, 0, 10));
      this.jmiActualizar
         .setText("<html><table width='100%'><tr><td halign='center' align='center'>" + this.jmiActualizar.getText() + "</td></tr></table></html>");
      this.jmiAyuda.setPreferredSize(this.newDimension(this.jmiAyuda, 0, 10));
      this.jmiAyuda.setText("<html><table width='100%'><tr><td halign='center' align='center'>" + this.jmiAyuda.getText() + "</td></tr></table></html>");
      this.jmiAcerca.setPreferredSize(this.newDimension(this.jmiAcerca, 0, 10));
      this.jmiAcerca.setText("<html><table width='100%'><tr><td halign='center' align='center'>" + this.jmiAcerca.getText() + "</td></tr></table></html>");
      this.jmbMenuPrincipal.setBorder(null);
      this.jmbMenuPrincipal.setBorderPainted(false);
      this.jmConfiguracion.setBorder(null);
      this.jmConfiguracion.setBorderPainted(false);
      this.jmAyuda.setBorder(null);
      this.jmAyuda.setBorderPainted(false);
      this.jmiPanelConfiguracion.setBorder(null);
      this.jmiPanelConfiguracion.setBorderPainted(false);
      this.jmiActualizar.setBorder(null);
      this.jmiActualizar.setBorderPainted(false);
      this.jmiAyuda.setBorder(null);
      this.jmiAyuda.setBorderPainted(false);
      this.jmiAcerca.setBorder(null);
      this.jmiAcerca.setBorderPainted(false);
      this.tipoFirmarBtnGRP.add(this.jtbFirmarArchivo);
      this.tipoFirmarBtnGRP.add(this.jtbFirmarToken);
      this.tipoValidarBtnGRP.add(this.jtbValidarArchivo);
      this.tipoValidarBtnGRP.add(this.jtbValidarToken);
      this.resetFirmarDocumento();
      this.resetVerificarDocumento();
   }

   private Dimension newDimension(Component component, int widthAditional, int height) {
      return new Dimension((int)component.getPreferredSize().getWidth() + widthAditional, (int)component.getPreferredSize().getHeight() + height);
   }

   private void mnemonic() {
      this.mainPanel.setMnemonicAt(0, 49);
      this.mainPanel.setMnemonicAt(1, 50);
      this.mainPanel.setMnemonicAt(2, 51);
      this.jbtnCertificadoFirmar.setMnemonic(67);
      this.jbtnDocumentoFirmar.setMnemonic(68);
      this.jbtnFirmar.setMnemonic(70);
      this.jbtnRestablecerFirmar.setMnemonic(82);
      this.jbtnVerificar.setMnemonic(86);
      this.jbtnDocumentoVerificar.setMnemonic(68);
      this.jbtnRestablecerVerificar.setMnemonic(82);
      this.jbtnValidar.setMnemonic(86);
      this.jbtnCertificadoValidar.setMnemonic(67);
      this.jbtnRestablecerValidar.setMnemonic(82);
   }

   private void tabulacion() {
      this.jbtnCertificadoFirmar.setNextFocusableComponent(this.jpfFirmarClave);
      this.jpfFirmarClave.setNextFocusableComponent(this.jchkBoxFirmaInvisible);
      this.jchkBoxFirmaInvisible.setNextFocusableComponent(this.jbtnFirmar);
      this.jbtnFirmar.setNextFocusableComponent(this.jbtnRestablecerFirmar);
      this.jbtnDocumentoVerificar.setNextFocusableComponent(this.jbtnVerificar);
      this.jbtnCertificadoValidar.setNextFocusableComponent(this.jpfValidarClave);
      this.jpfValidarClave.setNextFocusableComponent(this.jbtnValidar);
      this.jbtnValidar.setNextFocusableComponent(this.jbtnRestablecerValidar);
   }

   public void resetCertificadoFirmar() {
      this.pathCertificadoFirmar = "";
      this.label = (JLabel)this.jbtnCertificadoFirmar.getComponent(0);
      this.multilineText = "<html><body><b>Buscar Certificado</b><br>También lo puedes arrastrar aquí</body></html>";
      this.label.setText(this.multilineText);
      this.jbtnCertificadoFirmar.setComponentZOrder(this.label, 0);
   }

   public void jbtnCertificadoFirmar() {
      if (this.pathCertificadoFirmar != null && !this.pathCertificadoFirmar.isEmpty()) {
         File file = new File(this.pathCertificadoFirmar);
         this.label = (JLabel)this.jbtnCertificadoFirmar.getComponent(0);
         this.multilineText = "<html><body><b>Nombre del archivo</b><br>"
            + (file.getName().length() <= 20 ? file.getName() : file.getName().substring(0, 19) + "...")
            + "</body></html>";
         this.label.setText(this.multilineText);
         this.jbtnCertificadoFirmar.setComponentZOrder(this.label, 0);
         String tipoFirmar = this.jtbFirmarArchivo.isSelected() ? "archivo" : (this.jtbFirmarToken.isSelected() ? "token" : null);
         PropertiesUtils.updateFirmaDigitalConfig(
            this.pathCertificadoFirmar,
            PropertiesUtils.getFirmaDigitalConfig().getProperty("uiScale", "1.0"),
            tipoFirmar
         );
      } else {
         this.resetCertificadoFirmar();
      }
   }

   public void resetCertificadoValidar() {
      this.pathCertificadoValidar = "";
      this.label = (JLabel)this.jbtnCertificadoValidar.getComponent(0);
      this.multilineText = "<html><body><b>Buscar Certificado</b><br>También lo puedes arrastrar aquí</body></html>";
      this.label.setText(this.multilineText);
      this.jbtnCertificadoValidar.setComponentZOrder(this.label, 0);
   }

   public void jbtnCertificadoValidar() {
      if (this.pathCertificadoValidar != null && !this.pathCertificadoValidar.isEmpty()) {
         File file = new File(this.pathCertificadoValidar);
         this.label = (JLabel)this.jbtnCertificadoValidar.getComponent(0);
         this.multilineText = "<html><body><b>Nombre del archivo</b><br>"
            + (file.getName().length() <= 20 ? file.getName() : file.getName().substring(0, 19) + "...")
            + "</body></html>";
         this.label.setText(this.multilineText);
         this.jbtnCertificadoValidar.setComponentZOrder(this.label, 0);
      } else {
         this.resetCertificadoValidar();
      }
   }

   private void resetDocumentoFirmar() {
      this.label = (JLabel)this.jbtnDocumentoFirmar.getComponent(0);
      this.multilineText = "<html><body><b>Buscar Documento(s)</b><br>También lo puedes arrastrar aquí</body></html>";
      this.label.setText(this.multilineText);
      this.jbtnDocumentoFirmar.setComponentZOrder(this.label, 0);
   }

   private void resetDocumentoVerificar() {
      this.label = (JLabel)this.jbtnDocumentoVerificar.getComponent(0);
      this.multilineText = "<html><body><b>Buscar Documento(s)</b><br>También lo puedes arrastrar aquí</body></html>";
      this.label.setText(this.multilineText);
      this.jbtnDocumentoVerificar.setComponentZOrder(this.label, 0);
   }

   private JButton jbtnAbrirDocumento(JComponent jComponent) {
      JButton jbtnAbrirDocumento = new JButton();
      jbtnAbrirDocumento.setText("");
      this.imageIcon = new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_ABRIR)).getImage().getScaledInstance(15, 15, 1));
      jbtnAbrirDocumento.setIcon(this.imageIcon);
      jbtnAbrirDocumento.addActionListener(
         e -> {
            if (jComponent.getName().equals("jtblFirmarDocumentos")) {
               String ruta = (String)this.tableModelFirmarDocumentos
                  .getValueAt(this.jtblFirmarDocumentos.convertRowIndexToModel(this.jtblFirmarDocumentos.getSelectedRow()), 0);
               if (this.jtblFirmarDocumentos.getSelectedRows().length > 0 && ruta != null && !ruta.isEmpty()) {
                  try {
                     FileUtils.abrirDocumento(ruta);
                  } catch (IOException ex) {
                     LOGGER.log(Level.SEVERE, null, ex.getMessage());
                  }
               }
            }

            if (jComponent.getName().equals("jtblVerificarDocumentos")) {
               String ruta = (String)this.tableModelVerificarDocumentos
                  .getValueAt(this.jtblVerificarDocumentos.convertRowIndexToModel(this.jtblVerificarDocumentos.getSelectedRow()), 0);
               if (this.jtblVerificarDocumentos.getSelectedRows().length > 0 && ruta != null && !ruta.isEmpty()) {
                  try {
                     FileUtils.abrirDocumento(ruta);
                  } catch (IOException ex) {
                     LOGGER.log(Level.SEVERE, null, ex.getMessage());
                  }
               }
            }
         }
      );
      return jbtnAbrirDocumento;
   }

   private JButton jbtnEliminar(JComponent jComponent) {
      JButton jbtnEliminar = new JButton();
      jbtnEliminar.setText("");
      this.imageIcon = new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_QUITAR)).getImage().getScaledInstance(15, 15, 1));
      jbtnEliminar.setIcon(this.imageIcon);
      jbtnEliminar.addActionListener(e -> {
         if (jComponent.getName().equals("jtblFirmarDocumentos") && this.jtblFirmarDocumentos.getSelectedRow() >= 0) {
            this.tableModelFirmarDocumentos.removeRow(this.jtblFirmarDocumentos.convertRowIndexToModel(this.jtblFirmarDocumentos.getSelectedRow()));
         }

         if (jComponent.getName().equals("jtblVerificarDocumentos") && this.jtblVerificarDocumentos.getSelectedRow() >= 0) {
            this.tableModelVerificarDocumentos.removeRow(this.jtblVerificarDocumentos.convertRowIndexToModel(this.jtblVerificarDocumentos.getSelectedRow()));
         }
      });
      return jbtnEliminar;
   }

   private JButton jbtnEstado(boolean estado) {
      JButton jbtnEstado = new JButton();
      jbtnEstado.setText("");
      if (estado) {
         this.imageIcon = new ImageIcon(
            new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_CORRECTO)).getImage().getScaledInstance(15, 15, 1)
         );
      } else {
         this.imageIcon = new ImageIcon(
            new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_INCORRECTO)).getImage().getScaledInstance(15, 15, 1)
         );
      }

      jbtnEstado.setIcon(this.imageIcon);
      return jbtnEstado;
   }

   private JButton jbtnDetalle(Documento documento, String rutaDocumento) {
      JButton jbtnDetalle = new JButton();
      jbtnDetalle.setText("");
      this.imageIcon = new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_DETALLE)).getImage().getScaledInstance(15, 15, 1));
      jbtnDetalle.setIcon(this.imageIcon);
      jbtnDetalle.addActionListener(e -> {
         if (!documento.getCertificados().isEmpty()) {
            JPanelFirmante jPanelFirmante = new JPanelFirmante(documento, rutaDocumento);
            JButton btnSalir = new JButton();
            btnSalir.setText("Salir");
            btnSalir.setMnemonic(83);
            Object[] options = new Object[]{btnSalir};
            btnSalir.addActionListener(evt1 -> {
               Component component = (Component)evt1.getSource();
               JDialog dialog = (JDialog)SwingUtilities.getRoot(component);
               dialog.dispose();
            });
            JOptionPane.showOptionDialog(this.getRootPane(), jPanelFirmante, "Firmantes", 0, -1, null, options, options[0]);
         } else {
            JOptionPane.showMessageDialog(this.getRootPane(), documento.getError(), "Error", 0);
         }
      });
      return jbtnDetalle;
   }

   private void eventos() {
      this.jtblFirmarDocumentos
         .addMouseListener(
            new MouseAdapter() {
               @Override
               public void mouseClicked(MouseEvent evt) {
                  if (evt.getClickCount() == 1) {
                     int column = Main.this.jtblFirmarDocumentos.columnAtPoint(evt.getPoint());
                     int row = Main.this.jtblFirmarDocumentos.rowAtPoint(evt.getPoint());
                     if (row < Main.this.jtblFirmarDocumentos.getRowCount()
                        && row >= 0
                        && column < Main.this.jtblFirmarDocumentos.getColumnCount()
                        && column >= 0
                        && Main.this.jtblFirmarDocumentos.getValueAt(row, column) instanceof JButton jButton) {
                        jButton.doClick();
                     }
                  }
               }
            }
         );
      this.jtblFirmarDocumentos
         .getModel()
         .addTableModelListener(
            evt -> this.jlblDocumentosFirmar.setText("<html><b>" + this.tableModelFirmarDocumentos.getRowCount() + " DOCUMENTO(S) SELECCIONADO(S)</b></html>")
         );
      this.jtblVerificarDocumentos
         .addMouseListener(
            new MouseAdapter() {
               @Override
               public void mouseClicked(MouseEvent evt) {
                  if (evt.getClickCount() == 1) {
                     int column = Main.this.jtblVerificarDocumentos.columnAtPoint(evt.getPoint());
                     int row = Main.this.jtblVerificarDocumentos.rowAtPoint(evt.getPoint());
                     if (row < Main.this.jtblVerificarDocumentos.getRowCount()
                        && row >= 0
                        && column < Main.this.jtblVerificarDocumentos.getColumnCount()
                        && column >= 0
                        && Main.this.jtblVerificarDocumentos.getValueAt(row, column) instanceof JButton jButton) {
                        jButton.doClick();
                     }
                  }
               }
            }
         );
      this.jtblVerificarDocumentos
         .getModel()
         .addTableModelListener(
            evt -> this.jlblDocumentosVerificar
               .setText("<html><b>" + this.tableModelVerificarDocumentos.getRowCount() + " DOCUMENTO(S) SELECCIONADO(S)</b></html>")
         );
   }

   private void inicializarTablas() {
      this.columnNames = new String[]{"<html><b>Documento</b></html>", "<html><b>Abrir</b></html>", "<html><b>Quitar</b></html>"};
      this.jtblFirmarDocumentos.setModel(new DefaultTableModel(new Object[0][], this.columnNames) {
         @Override
         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
         }
      });
      this.jTableHeader = this.jtblFirmarDocumentos.getTableHeader();
      ((DefaultTableCellRenderer)this.jtblFirmarDocumentos.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
      this.jTableHeader.setReorderingAllowed(false);
      this.jtblFirmarDocumentos.setUI(new BasicTableUI());
      this.tableModelFirmarDocumentos = (DefaultTableModel)this.jtblFirmarDocumentos.getModel();
      this.jtblFirmarDocumentos.getColumnModel().getColumn(1).setMaxWidth(50);
      this.jtblFirmarDocumentos.getColumnModel().getColumn(2).setMaxWidth(50);
      this.jtblFirmarDocumentos.getColumnModel().getColumn(0).setCellRenderer(new CellRendererWordWrap());
      this.jtblFirmarDocumentos.getColumnModel().getColumn(1).setCellRenderer(new CellRendererButton());
      this.jtblFirmarDocumentos.getColumnModel().getColumn(2).setCellRenderer(new CellRendererButton());
      this.columnNames = new String[]{
         "<html><b>Documento</b></html>",
         "<html><b>Abrir</b></html>",
         "<html><b>Quitar</b></html>",
         "<html><b>Estado</b></html>",
         "<html><b>Detalle</b></html>"
      };
      this.jtblVerificarDocumentos.setModel(new DefaultTableModel(new Object[0][], this.columnNames) {
         @Override
         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
         }
      });
      this.jTableHeader = this.jtblVerificarDocumentos.getTableHeader();
      ((DefaultTableCellRenderer)this.jtblVerificarDocumentos.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
      this.jTableHeader.setReorderingAllowed(false);
      this.jtblVerificarDocumentos.setUI(new BasicTableUI());
      this.tableModelVerificarDocumentos = (DefaultTableModel)this.jtblVerificarDocumentos.getModel();
      this.jtblVerificarDocumentos.getColumnModel().getColumn(1).setMaxWidth(50);
      this.jtblVerificarDocumentos.getColumnModel().getColumn(2).setMaxWidth(50);
      this.jtblVerificarDocumentos.getColumnModel().getColumn(3).setMaxWidth(60);
      this.jtblVerificarDocumentos.getColumnModel().getColumn(4).setMaxWidth(60);
      this.jtblVerificarDocumentos.getColumnModel().getColumn(0).setCellRenderer(new CellRendererWordWrap());
      this.jtblVerificarDocumentos.getColumnModel().getColumn(1).setCellRenderer(new CellRendererButton());
      this.jtblVerificarDocumentos.getColumnModel().getColumn(2).setCellRenderer(new CellRendererButton());
      this.jtblVerificarDocumentos.getColumnModel().getColumn(3).setCellRenderer(new CellRendererButton());
      this.jtblVerificarDocumentos.getColumnModel().getColumn(4).setCellRenderer(new CellRendererButton());
   }

   public void agregarFirmarDocumento(List<String> pathDocuments) {
      this.tableModelFirmarDocumentos = (DefaultTableModel)this.jtblFirmarDocumentos.getModel();

      for (int i = 0; i < this.tableModelFirmarDocumentos.getRowCount(); i++) {
         boolean repetido = false;
         String rutaDocumento = (String)this.tableModelFirmarDocumentos.getValueAt(i, 0);

         for (String pathDocument : pathDocuments) {
            if (pathDocument.equals(rutaDocumento)) {
               repetido = true;
               break;
            }
         }

         if (!repetido) {
            pathDocuments.add(rutaDocumento);
         }
      }

      this.tableModelFirmarDocumentos.setRowCount(0);
      pathDocuments.stream()
         .map(pathDocumentx -> new Object[]{pathDocumentx, this.jbtnAbrirDocumento(this.jtblFirmarDocumentos), this.jbtnEliminar(this.jtblFirmarDocumentos)})
         .forEachOrdered(data -> this.tableModelFirmarDocumentos.addRow(data));
      this.jtblFirmarDocumentos.setModel(this.tableModelFirmarDocumentos);
      this.tableModelFirmarDocumentos.fireTableDataChanged();
   }

   public void agregarVerificarListaDocumentos(List<Documento> documents) {
      this.tableModelVerificarDocumentos = (DefaultTableModel)this.jtblVerificarDocumentos.getModel();

      for (int i = 0; i < this.tableModelVerificarDocumentos.getRowCount(); i++) {
         boolean estadoPDF = false;
         String rutaDocumento = (String)this.tableModelVerificarDocumentos.getValueAt(this.jtblVerificarDocumentos.convertRowIndexToModel(i), 0);
         switch (rutaDocumento.substring(rutaDocumento.lastIndexOf(".") + 1).toLowerCase()) {
            case "pdf":
               estadoPDF = true;
         }

         boolean estado = false;
         if (documents.get(i).getDocValidate() && documents.get(i).getSignValidate()) {
            estado = true;
         }

         if (!documents.get(i).getDocValidate() && !estadoPDF && documents.get(i).getSignValidate()) {
            estado = true;
            documents.get(i).setDocValidate(true);
         }

         this.tableModelVerificarDocumentos.setValueAt(this.jbtnEstado(estado), i, 3);
         this.tableModelVerificarDocumentos.setValueAt(this.jbtnDetalle(documents.get(i), rutaDocumento), i, 4);
      }

      this.tableModelVerificarDocumentos.fireTableDataChanged();
   }

   public void agregarVerificarDocumento(List<String> pathDocuments) {
      this.tableModelVerificarDocumentos = (DefaultTableModel)this.jtblVerificarDocumentos.getModel();

      for (int i = 0; i < this.tableModelVerificarDocumentos.getRowCount(); i++) {
         boolean repetido = false;
         String rutaDocumento = (String)this.tableModelVerificarDocumentos.getValueAt(i, 0);

         for (String pathDocument : pathDocuments) {
            if (pathDocument.equals(rutaDocumento)) {
               repetido = true;
               break;
            }
         }

         if (!repetido) {
            pathDocuments.add(rutaDocumento);
         }
      }

      this.tableModelVerificarDocumentos.setRowCount(0);
      pathDocuments.stream()
         .map(
            pathDocumentx -> new Object[]{
               pathDocumentx, this.jbtnAbrirDocumento(this.jtblVerificarDocumentos), this.jbtnEliminar(this.jtblVerificarDocumentos)
            }
         )
         .forEachOrdered(data -> this.tableModelVerificarDocumentos.addRow(data));
      this.jtblVerificarDocumentos.setModel(this.tableModelVerificarDocumentos);
      this.tableModelVerificarDocumentos.fireTableDataChanged();
   }

   private void agregarValidacionCertificado(X509Certificate x509Certificate) throws CertificadoInvalidoException, InvalidKeyException, EntidadCertificadoraNoValidaException {
      if (x509Certificate != null) {
         DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(x509Certificate);
         if (datosUsuario == null && (this.jpfValidarClave.getPassword() == null || this.jpfValidarClave.getPassword().length == 0)) {
            throw new CertificadoInvalidoException(messages.getProperty("mensaje.error.extraer_datos_certificados"));
         }

         if (datosUsuario == null) {
            throw new CertificadoInvalidoException("No se pudo extraer los datos del certificados.");
         }

         try {
            this.x509CertificateUtils = null;
            this.x509CertificateUtils = new X509CertificateUtils();
            this.x509CertificateUtils.validarX509Certificate(x509Certificate, null, PropertiesUtils.versionBase64());
            this.jtpValidarCertificado.setContentType("text/html");
            this.multilineText = "<html><body><b>Certificado Emitido por: </b>"
               + (this.x509CertificateUtils.isDesconocido() ? "desconocido" : CertEcUtils.getNombreCA(x509Certificate))
               + " - "
               + datosUsuario.getTipoCertificado()
               + "<br>"
               + (!datosUsuario.getCedula().isBlank() ? "<b>Cédula: </b>" + datosUsuario.getCedula() + "<br>" : "")
               + (!datosUsuario.getNombre().isBlank() ? "<b>Nombres: </b>" + datosUsuario.getNombre() + "<br>" : "")
               + (!datosUsuario.getApellido().isBlank() ? "<b>Apellidos: </b>" + datosUsuario.getApellido() + "<br>" : "")
               + (!datosUsuario.getRuc().isBlank() ? "<b>Ruc: </b>" + datosUsuario.getRuc() + "<br>" : "")
               + (!datosUsuario.getRazonSocial().isBlank() ? "<b>Razón Social: </b>" + datosUsuario.getRazonSocial() + "<br>" : "")
               + (!datosUsuario.getInstitucion().isBlank() ? "<b>Institución: </b>" + datosUsuario.getInstitucion() + "<br>" : "")
               + (!datosUsuario.getCargo().isBlank() ? "<b>Cargo: </b>" + datosUsuario.getCargo() + "<br>" : "")
               + "<b>Fecha de Emisión: </b>"
               + this.SIMPLE_DATE_FORMAT.format(x509Certificate.getNotBefore().getTime())
               + " - "
               + this.SIMPLE_DATE_FORMAT.getTimeZone().getDisplayName(Locale.getDefault())
               + "<br><b>Fecha de Expiración: </b>"
               + this.SIMPLE_DATE_FORMAT.format(x509Certificate.getNotAfter().getTime())
               + " - "
               + this.SIMPLE_DATE_FORMAT.getTimeZone().getDisplayName(Locale.getDefault())
               + "<br><b>Expirado: </b>"
               + (this.x509CertificateUtils.isExpirado() ? messages.getProperty("mensaje.si") : messages.getProperty("mensaje.no"))
               + "<br><b>Revocado: </b>"
               + (
                  this.x509CertificateUtils.getRevocado() != null
                     ? this.x509CertificateUtils.getRevocado() + " - " + this.SIMPLE_DATE_FORMAT.getTimeZone().getDisplayName(Locale.getDefault())
                     : messages.getProperty("mensaje.no")
               )
               + "<br></body></html>";
            this.jtpValidarCertificado.setText(this.multilineText);
         } catch (RubricaException | KeyStoreException | IOException | HoraServidorException | ConexionException ex) {
            LOGGER.log(Level.SEVERE, null, ex.getMessage());
         }
      }
   }

   private void resetJtpValidarCertificado() {
      this.jtpValidarCertificado.setText("");
   }

   private void dragAndDrop() {
      this.extensionesCertificados = new ArrayList<>(Arrays.asList(this.FILTRO_CERTIFICADOS.getExtensions()));
      this.extensionesDocumentos = new ArrayList<>(Arrays.asList(this.FILTRO_DOCUMENTOS.getExtensions()));
      this.jbtnCertificadoFirmar.setTransferHandler(new TransferHandlerComponent(this, this.extensionesCertificados));
      this.jbtnDocumentoFirmar.setTransferHandler(new TransferHandlerComponent(this, this.extensionesDocumentos));
      this.jScrollPaneFirmarDocumentos.setTransferHandler(new TransferHandlerComponent(this, this.extensionesDocumentos));
      this.jbtnDocumentoVerificar.setTransferHandler(new TransferHandlerComponent(this, this.extensionesDocumentos));
      this.jScrollPaneVerificarDocumentos.setTransferHandler(new TransferHandlerComponent(this, this.extensionesDocumentos));
      this.jbtnCertificadoValidar.setTransferHandler(new TransferHandlerComponent(this, this.extensionesCertificados));
   }

   private void nullDragAndDrop() {
      this.jbtnCertificadoFirmar.setTransferHandler(null);
      this.jbtnDocumentoFirmar.setTransferHandler(null);
      this.jScrollPaneFirmarDocumentos.setTransferHandler(null);
      this.jbtnDocumentoVerificar.setTransferHandler(null);
      this.jScrollPaneVerificarDocumentos.setTransferHandler(null);
      this.jbtnCertificadoValidar.setTransferHandler(null);
   }

   public static Component findPrevFocus() {
      Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      Container root = c.getFocusCycleRootAncestor();
      FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
      Component prevFocus = policy.getComponentBefore(root, c);
      if (prevFocus == null) {
         prevFocus = policy.getDefaultComponent(root);
      }

      return prevFocus;
   }

   private void resetJtblDocumentosFirmar() {
      DefaultTableModel tableModelCert = (DefaultTableModel)this.jtblFirmarDocumentos.getModel();
      tableModelCert.setRowCount(0);
      this.jtblFirmarDocumentos.setModel(tableModelCert);
      this.jtblFirmarDocumentos.setName("jtblFirmarDocumentos");
      this.jScrollPaneFirmarDocumentos.setName("jScrollPaneFirmarDocumentos");
      tableModelCert.fireTableDataChanged();
   }

   private void resetJtblDocumentosVerificar() {
      DefaultTableModel tableModelCert = (DefaultTableModel)this.jtblVerificarDocumentos.getModel();
      tableModelCert.setRowCount(0);
      this.jtblVerificarDocumentos.setModel(tableModelCert);
      this.jtblVerificarDocumentos.setName("jtblVerificarDocumentos");
      this.jScrollPaneVerificarDocumentos.setName("jScrollPaneVerificarDocumentos");
      tableModelCert.fireTableDataChanged();
   }

   private void resetFirmarDocumento() {
      this.documento = null;
      this.pathCertificadoFirmar = "";
      this.tipoFirmarBtnGRP.clearSelection();
      this.resetCertificadoFirmar();
      this.resetDocumentoFirmar();
      this.resetJtblDocumentosFirmar();
      this.jbtnFirmar.setEnabled(false);
      this.jbtnDocumentoFirmar.setEnabled(false);
      this.jpfFirmarClave.setText("");
      this.jpfFirmarClave.setEnabled(false);
      this.jchkBoxFirmaInvisible.setSelected(false);
      this.jbtnCertificadoFirmar.setEnabled(false);
   }

   private void resetVerificarDocumento() {
      this.documento = null;
      this.resetDocumentoVerificar();
      this.resetJtblDocumentosVerificar();
   }

   private void restaurarPreferenciasFirmar() {
      Properties firmaDigitalConfig = PropertiesUtils.getFirmaDigitalConfig();
      String tipoFirmar = firmaDigitalConfig.getProperty("tipoFirmar");
      String pathCertificado = firmaDigitalConfig.getProperty("user.home");
      if ("token".equals(tipoFirmar)) {
         this.jtbFirmarToken.setSelected(true);
         this.selFirmarConToken();
      } else if ("archivo".equals(tipoFirmar)) {
         this.jtbFirmarArchivo.setSelected(true);
         this.selFirmarConArchivo();
         if (pathCertificado != null && !pathCertificado.isEmpty() && new File(pathCertificado).exists()) {
            this.pathCertificadoFirmar = pathCertificado;
            this.jbtnCertificadoFirmar();
         }
      }
   }

   private void selFirmarConArchivo() {
      this.jbtnFirmar.setEnabled(true);
      this.jbtnDocumentoFirmar.setEnabled(true);
      this.jbtnCertificadoFirmar.setEnabled(true);
      this.jpfFirmarClave.setEnabled(true);
   }

   private void selFirmarConToken() {
      this.resetCertificadoFirmar();
      this.jbtnFirmar.setEnabled(true);
      this.jbtnDocumentoFirmar.setEnabled(true);
      this.jpfFirmarClave.setEnabled(false);
      this.jpfFirmarClave.setText("");
      this.jbtnCertificadoFirmar.setEnabled(false);
      if (!OsUtils.isWindows()) {
         this.jpfFirmarClave.setEnabled(true);
      } else {
         this.jpfFirmarClave.setEnabled(false);
      }
   }

   private void selValidarConArchivo() {
      this.jbtnCertificadoValidar.setEnabled(true);
      this.jpfValidarClave.setEnabled(true);
      this.jpfValidarClave.setText("");
   }

   private void selValidarConToken() {
      this.resetCertificadoValidar();
      this.jbtnCertificadoValidar.setEnabled(false);
      this.jpfValidarClave.setText("");
      if (!OsUtils.isWindows()) {
         this.jpfValidarClave.setEnabled(true);
      } else {
         this.jpfValidarClave.setEnabled(false);
      }
   }

   private void validacionPdf() throws DocumentoException, RazonFirma {
      if (this.point != null && this.point.getX() == 0.0 && this.point.getY() == 0.0) {
         throw new DocumentoException(messages.getProperty("mensaje.error.documento_sin_firma"));
      }

      if (this.razonFirma != null && this.razonFirma.length() > 50) {
         throw new RazonFirma(
            MessageFormat.format(messages.getProperty("mensaje.error.razonfirma_limite"), Integer.valueOf(messages.getProperty("limite.razon_firma")))
         );
      }

      if (this.localizacion != null && this.localizacion.length() > 50) {
         throw new RazonFirma(
            MessageFormat.format(messages.getProperty("mensaje.error.localizacionfirma_limite"), Integer.valueOf(messages.getProperty("limite.razon_firma")))
         );
      }

      if (this.point == null) {
         throw new DocumentoException(messages.getProperty("mensaje.error.documento_sin_firma"));
      }
   }

   private void validacionPreFirmar() throws DocumentoNoExistenteException, TokenNoConectadoException, DocumentoNoPermitidoException, CertificadoInvalidoException, DocumentoException, RazonFirma {
      if (!this.documento.exists()) {
         throw new DocumentoNoExistenteException(MessageFormat.format(messages.getProperty("mensaje.error.documento_inexistente"), this.documento.getPath()));
      }

      if (this.jtbFirmarToken.isSelected() && !OsUtils.isWindows() && this.jpfFirmarClave.getPassword().length == 0) {
         throw new CertificadoInvalidoException(messages.getProperty("mensaje.error.certificado_clave_vacia"));
      }

      if (this.documento.length() == 0L) {
         throw new DocumentoException(messages.getProperty("mensaje.error.documento_vacio"));
      }

      if ("p7m".equals(FileUtils.getFileExtension(this.documento).toLowerCase())) {
         throw new DocumentoException(messages.getProperty("mensaje.error.documento_p7m"));
      }

      this.tipoDeDocumentPermitido(this.documento);
   }

   private void tipoDeDocumentPermitido(File documento) throws DocumentoNoPermitidoException {
      String extDocumento = FileUtils.getFileExtension(documento).toLowerCase();
      if (!documento.getName().contains(".")) {
         throw new DocumentoNoPermitidoException(messages.getProperty("mensaje.error.extension_vacia"));
      }

      if (!this.extensionesDocumentos.stream().anyMatch(extension -> extension.equals(extDocumento))) {
         throw new DocumentoNoPermitidoException(MessageFormat.format(messages.getProperty("mensaje.error.extension_no_permitida"), extDocumento));
      }
   }

   private KeyStore getKeyStore() throws TokenNoEncontradoException {
      KeyStore ks = null;

      try {
         if (this.jtbFirmarToken.isSelected()) {
            String tipoKeyStoreProvider = null;
            if (this.jtbFirmarToken.isSelected()) {
               tipoKeyStoreProvider = "TOKEN";
            }

            ks = KeyStoreProviderFactory.getKeyStore(new String(this.jpfFirmarClave.getPassword()), tipoKeyStoreProvider);
            if (ks == null) {
               this.setCursor(Cursor.getDefaultCursor());
               throw new TokenNoEncontradoException(
                  messages.getProperty("mensaje.error.token_contrasena_invalida") + " o " + messages.getProperty("mensaje.error.dispositivo_no_encontrado")
               );
            }
         }

         if (this.jtbFirmarArchivo.isSelected()) {
            File llave = new File(this.pathCertificadoFirmar);
            if (llave.exists()) {
               KeyStoreProvider ksp = new FileKeyStoreProvider(this.pathCertificadoFirmar);
               ks = ksp.getKeystore(this.jpfFirmarClave.getPassword());
            } else {
               this.setCursor(Cursor.getDefaultCursor());
               DialogUtils.showDialog(this, "No se encontró el certificado digital :", llave.getPath(), "Error", 0);
            }
         }
      } catch (KeyStoreException e) {
         this.setCursor(Cursor.getDefaultCursor());
         if (e.getMessage().equals("java.io.IOException: keystore password was incorrect")) {
            JOptionPane.showMessageDialog(this, messages.getProperty("mensaje.error.clave_incorrecta"), "Error", 0);
         } else {
            LOGGER.log(Level.SEVERE, null, e.getMessage());
            JOptionPane.showMessageDialog(this, messages.getProperty("mensaje.error.certificado_formato_invalido"), "Error", 0);
         }
      }

      return ks;
   }

   private void enableBotones(boolean estado) {
      this.jbtnCertificadoFirmar.setEnabled(estado);
      this.jbtnFirmar.setEnabled(estado);
      this.jbtnDocumentoFirmar.setEnabled(estado);
      this.jbtnRestablecerFirmar.setEnabled(estado);
      this.jbtnVerificar.setEnabled(estado);
      this.jbtnDocumentoVerificar.setEnabled(estado);
      this.jbtnRestablecerVerificar.setEnabled(estado);
      this.jbtnValidar.setEnabled(estado);
      this.jbtnRestablecerValidar.setEnabled(estado);
   }

   private void enableControles(boolean estado) {
      this.jpfFirmarClave.setEnabled(estado);
      this.jpfValidarClave.setEnabled(estado);
      this.enableBotones(estado);
      if (estado) {
         this.setCursor(Cursor.getDefaultCursor());
         this.dragAndDrop();
      } else {
         this.setCursor(Cursor.getPredefinedCursor(3));
         this.nullDragAndDrop();
      }
   }

   private void firmarDocumento(List<String> rutaDocumentos) {
      List<String> documentosFirmados = new ArrayList<>();

      try {
         this.validacionPreFirmar();
         KeyStore ks = this.getKeyStore();
         if (ks != null) {
            this.alias = null;
            this.alias = CertUtils.seleccionarAlias(ks, this.getRootPane());
            X509Certificate x509Certificate = CertUtils.getCert(ks, this.alias);
            this.x509CertificateUtils = null;
            this.x509CertificateUtils = new X509CertificateUtils();
            if (this.x509CertificateUtils.validarX509Certificate(x509Certificate, null, PropertiesUtils.versionBase64())) {
               if (x509Certificate != null && this.alias != null) {
                  if (FileUtils.getFileExtension(this.documento).toLowerCase().equals("pdf")) {
                     if (!this.jchkBoxFirmaInvisible.isSelected()) {
                        this.estampado = "Firma Visible";
                        this.previewPdf();
                        this.validacionPdf();
                     } else {
                        this.estampado = null;
                     }
                  }

                  this.razonFirma = this.razonFirma == null ? "" : this.razonFirma;
                  this.localizacion = this.localizacion == null ? "" : this.localizacion;
                  char[] password = this.jpfFirmarClave.getPassword();
                  new Thread(
                        () -> {
                           this.enableControles(false);
                           ProgressMonitor pm = new ProgressMonitor(this, "Firmando", "Task starting", 1, rutaDocumentos.size());
                           int i = 0;

                           for (String rutaDocumento : rutaDocumentos) {
                              try {
                                 i++;
                                 File fileDocument = new File(rutaDocumento);
                                 pm.setNote("<html><b>" + i + " de " + rutaDocumentos.size() + " documento(s)</b><br>" + fileDocument.toString() + "</html>");
                                 pm.setProgress(i);
                                 if (pm.isCanceled()) {
                                    throw new CancelException("Se canceló la operación");
                                 }

                                 byte[] docSigned = null;
                                 docSigned = FirmaDigital.firmar(
                                    ks, this.alias, fileDocument, password, this.point, this.pagina, this.razonFirma, this.localizacion, this.estampado
                                 );
                                 if (docSigned != null) {
                                    String nombreDocFirmado = FileUtils.crearNombreFirmado(
                                       fileDocument, FileUtils.getExtension(FileUtils.fileConvertToByteArray(fileDocument))
                                    );
                                    FileUtils.saveByteArrayToDisc(docSigned, nombreDocFirmado);
                                    documentosFirmados.add(nombreDocFirmado);
                                 }
                              } catch (CancelException exx) {
                                 LOGGER.log(Level.SEVERE, null, exx.getMessage());
                                 JOptionPane.showMessageDialog(this, exx.getMessage(), "Error", 0);
                                 this.resetFirmarDocumento();
                                 this.resetJtblDocumentosFirmar();
                                 break;
                              } catch (XploitException e) {
                                 this.enableControles(true);
                                 this.resetFirmarDocumento();
                                 this.resetJtblDocumentosFirmar();
                                 String mensaje = e.getMessage();
                                 JOptionPane.showMessageDialog(this, mensaje + "\nRuta del documento: " + rutaDocumento, "Error", 0);
                                 LOGGER.log(Level.SEVERE, null, e.getMessage());
                              } catch (NullPointerException e) {
                                 this.enableControles(true);
                                 this.resetFirmarDocumento();
                                 this.resetJtblDocumentosFirmar();
                                 JOptionPane.showMessageDialog(
                                    this, "Problemas con el certificado digital, contáctese con la entidad de certificación", "Error", 0
                                 );
                                 LOGGER.log(Level.SEVERE, null, e.getMessage());
                              } catch (Exception e) {
                                 this.enableControles(true);
                                 this.resetFirmarDocumento();
                                 this.resetJtblDocumentosFirmar();
                                 String mensaje = e.getMessage();
                                 System.out.println("Exception Normal " + mensaje);
                                 e.printStackTrace();
                                 if (mensaje != null) {
                                    if (mensaje.contains("org.xml.sax.SAXParseException")) {
                                       mensaje = messages.getProperty("mensaje.error.documento_corrupto");
                                    }

                                    if (mensaje.contains("IllegalStateException") && mensaje.contains("Content_Types")) {
                                       mensaje = messages.getProperty("mensaje.error.documento_corrupto");
                                    }

                                    if (mensaje.contains("Las firmas XAdES Enveloped solo pueden realizarse sobre datos XML")) {
                                       mensaje = messages.getProperty("mensaje.error.documento_xml");
                                    }

                                    if (e.getClass() == ProcessingException.class
                                       || e.getClass() == UnknownHostException.class
                                       || e.getClass() == NoRouteToHostException.class
                                       || e.getClass() == SocketTimeoutException.class
                                       || e.getClass() == SocketException.class
                                       || e.getClass() == ConnectException.class) {
                                       mensaje = messages.getProperty("mensaje.error.problema_red");
                                    }

                                    if (e.getClass() == BadPasswordException.class) {
                                       mensaje = messages.getProperty("mensaje.error.documento_contrasena");
                                    }
                                 } else {
                                    mensaje = messages.getProperty("mensaje.error.documento_problemas");
                                 }

                                 JOptionPane.showMessageDialog(this, mensaje + "\nRuta del documento: " + rutaDocumento, "Error", 0);
                                 LOGGER.log(Level.SEVERE, null, e.getMessage());
                                 System.err.println("Error no se pudo firmar ");
                              }
                           }

                           if (!documentosFirmados.isEmpty()) {
                              JPanelDocumento jPanelVariosDocumentos = new JPanelDocumento(documentosFirmados);
                              JButton btnSalir = new JButton();
                              btnSalir.setText("Salir");
                              btnSalir.setMnemonic(83);
                              Object[] options = new Object[]{btnSalir};
                              btnSalir.addActionListener(evt1 -> {
                                 Component component1 = (Component)evt1.getSource();
                                 JDialog dialog = (JDialog)SwingUtilities.getRoot(component1);
                                 dialog.dispose();
                              });
                              JOptionPane.showOptionDialog(this.getRootPane(), jPanelVariosDocumentos, "Documentos firmados", 0, -1, null, options, options[0]);
                           }

                           this.enableControles(true);
                           this.resetFirmarDocumento();
                           this.resetJtblDocumentosFirmar();
                        }
                     )
                     .start();
                  DatosUsuario datosUsuario = CertEcUtils.getDatosUsuarios(x509Certificate);
                  System.out.println("datosUsuario: " + datosUsuario.toString());
                  this.resetFirmarDocumento();
               }
            } else {
               JOptionPane.showMessageDialog(null, PropertiesUtils.getMessages().getProperty("mensaje.error.certificado_invalido"), "Advertencia", 2);
            }
         }
      } catch (KeyStoreException e) {
         this.enableControles(true);
         if (e.getMessage().equals("java.io.IOException: keystore password was incorrect")) {
            JOptionPane.showMessageDialog(this, messages.getProperty("mensaje.error.clave_incorrecta"), "Error", 0);
         } else {
            LOGGER.log(Level.SEVERE, null, e.getMessage());
            JOptionPane.showMessageDialog(this, messages.getProperty("mensaje.error.certificado_formato_invalido"), "Error", 0);
         }

         this.jpfValidarClave.setText("");
      } catch (ProcessingException | ConexionException | TokenNoEncontradoException | CertificadoInvalidoException | EntidadCertificadoraNoValidaException | HoraServidorException | RubricaException | HeadlessException | IOException | InvalidKeyException | DocumentoNoExistenteException | TokenNoConectadoException | DocumentoNoPermitidoException | DocumentoException | RazonFirma ex) {
         this.enableControles(true);
         this.resetFirmarDocumento();
         this.resetJtblDocumentosFirmar();
         LOGGER.log(Level.SEVERE, null, ex.getMessage());
         if (ex.getClass() == NoRouteToHostException.class | ex.getClass() == ConexionException.class | ex.getClass() == ProcessingException.class) {
            JOptionPane.showMessageDialog(this, "Problemas en la red, no es posible conectarse", "Error", 0);
         } else {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 0);
         }
      }
   }

   private void resetValidacionCertificado() {
      this.pathCertificadoValidar = "";
      this.tipoValidarBtnGRP.clearSelection();
      this.jpfValidarClave.setText("");
      this.resetJtpValidarCertificado();
   }

   public void actualizar() {
      this.btnSi.setText("Sí");
      this.btnSi.setMnemonic(83);
      this.btnNo.setText("No");
      this.btnNo.setMnemonic(78);
      this.btnAceptar.setText("Aceptar");
      this.btnAceptar.setMnemonic(65);
      this.btnSi
         .addActionListener(
            evt -> {
               this.setCursor(Cursor.getPredefinedCursor(3));
               Object[] optionsAceptar = new Object[]{this.btnAceptar};

               try {
                  Update update = new Update();
                  Update.setAmbiente("--update");
                  File jar = update.actualizarFirmador();
                  update.updateFirmador(jar);
                  if (!OsUtils.isMac()) {
                     File clienteJar = update.actualizarCliente();
                     update.updateCliente(clienteJar);
                  }

                  JOptionPane.showOptionDialog(
                     this.getRootPane(), messages.getProperty("mensaje.actualizado"), "Mensaje", 0, 1, null, optionsAceptar, this.btnAceptar
                  );
                  System.exit(0);
               } catch (IllegalArgumentException e) {
                  JOptionPane.showOptionDialog(
                     this.getRootPane(), messages.getProperty("mensaje.error.actualizar_administracion"), "Error", 0, 0, null, optionsAceptar, this.btnAceptar
                  );
               } catch (IOException e) {
                  LOGGER.log(Level.SEVERE, "Error al actualizar:", e.getMessage());
                  JOptionPane.showOptionDialog(
                     this.getRootPane(),
                     messages.getProperty("mensaje.error.actualizar") + ": " + e.getMessage(),
                     "Error",
                     0,
                     0,
                     null,
                     optionsAceptar,
                     this.btnAceptar
                  );
               }

               this.setCursor(Cursor.getPredefinedCursor(0));
            }
         );
      this.btnNo.addActionListener(evt -> {
         Component component1 = (Component)evt.getSource();
         JDialog dialog = (JDialog)SwingUtilities.getRoot(component1);
         dialog.dispose();
      });
      this.btnAceptar.addActionListener(evt -> {
         Component component1 = (Component)evt.getSource();
         JDialog dialog = (JDialog)SwingUtilities.getRoot(component1);
         dialog.dispose();
      });
      Object[] options = new Object[]{this.btnNo, this.btnSi};
      String versionFirmaEC = "La versión actual es " + config.getProperty("version");
      versionFirmaEC = versionFirmaEC + " de la fecha " + config.getProperty("fecha") + ". ";
      JOptionPane.showOptionDialog(
         this.getRootPane(),
         versionFirmaEC + messages.getProperty("mensaje.desea_actualizar"),
         messages.getProperty("mensaje.confirmar"),
         2,
         3,
         null,
         options,
         options[1]
      );
   }

   private List<String> existeFirmarDocumentos() {
      List<String> documentos = new ArrayList<>();

      for (int i = 0; i < this.tableModelFirmarDocumentos.getRowCount(); i++) {
         String rutaDocumento = (String)this.tableModelFirmarDocumentos.getValueAt(i, 0);
         if (rutaDocumento != null) {
            this.documento = new File(rutaDocumento);
            documentos.add(rutaDocumento);
         } else {
            JOptionPane.showMessageDialog(null, MessageFormat.format(messages.getProperty("mensaje.error.documento_inexistente"), rutaDocumento), "Error", 0);
         }
      }

      return documentos;
   }

   private List<String> existeVerificarDocumentos() {
      List<String> documentos = new ArrayList<>();

      for (int i = 0; i < this.tableModelVerificarDocumentos.getRowCount(); i++) {
         String rutaDocumento = (String)this.tableModelVerificarDocumentos.getValueAt(i, 0);
         if (rutaDocumento != null) {
            this.documento = new File(rutaDocumento);
            documentos.add(rutaDocumento);
         } else {
            JOptionPane.showMessageDialog(null, MessageFormat.format(messages.getProperty("mensaje.error.documento_inexistente"), rutaDocumento), "Error", 0);
         }
      }

      return documentos;
   }

   private void verificarDocumento(List<String> rutaDocumentos) {
      this.setCursor(Cursor.getPredefinedCursor(3));
      List<Documento> documents = new ArrayList<>();
      new Thread(
            () -> {
               this.enableControles(false);
               ProgressMonitor pm = new ProgressMonitor(this, "Verificando", "Task starting", 1, rutaDocumentos.size());
               int i = 0;

               for (String rutaDocumento : rutaDocumentos) {
                  try {
                     i++;
                     File fileDocument = new File(rutaDocumento);
                     pm.setNote("<html><b>" + i + " de " + rutaDocumentos.size() + " documento(s)</b><br>" + fileDocument.toString() + "</html>");
                     pm.setProgress(i);
                     if (pm.isCanceled()) {
                        throw new CancelException("Se canceló la operación");
                     }

                     this.documento = new File(rutaDocumento);
                     this.tipoDeDocumentPermitido(this.documento);
                     Documento document = Utils.verificarDocumento(this.documento, PropertiesUtils.versionBase64());
                     documents.add(document);
                  } catch (NoSuchFileException ex) {
                     LOGGER.log(Level.SEVERE, null, ex.getMessage());
                     DialogUtils.showDialog(this.getRootPane(), messages.getProperty("mensaje.error.archivo_no_encontrado") + ":", ex.getMessage(), "Error", 0);
                     documents.add(
                        new Documento(false, false, new ArrayList<>(), messages.getProperty("mensaje.error.archivo_no_encontrado") + ":\n" + ex.getMessage())
                     );
                  } catch (com.itextpdf.io.exceptions.IOException ex) {
                     LOGGER.log(Level.SEVERE, null, ex.getMessage());
                     JOptionPane.showMessageDialog(
                        this,
                        messages.getProperty("mensaje.error.documento_problemas_verificacion") + ":\n" + this.documento + "\n" + ex.getMessage(),
                        "Error",
                        0
                     );
                     documents.add(
                        new Documento(
                           false,
                           false,
                           new ArrayList<>(),
                           messages.getProperty("mensaje.error.documento_problemas_verificacion") + ":\n" + this.documento + "\n" + ex.getMessage()
                        )
                     );
                  } catch (RubricaException ex) {
                     System.err.println("Error no se pudo conectar al servicio de OSCP para verificar el certificado ");
                     String msgError = ex.getMessage();
                     if (msgError.contains("Los datos indicados no se corresponden ")) {
                        msgError = "El archivo puede que este corrupto o que no contenga una firma";
                     }

                     if (msgError.contains("Los datos indicados no son una firma")) {
                        msgError = "El archivo puede que este corrupto o que no contenga una firma";
                     }

                     LOGGER.log(Level.SEVERE, null, ex.getMessage());
                     JOptionPane.showMessageDialog(this, msgError, "Error", 0);
                     documents.add(new Documento(false, false, new ArrayList<>(), msgError));
                  } catch (CancelException ex) {
                     LOGGER.log(Level.SEVERE, null, ex.getMessage());
                     JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 0);
                     this.resetVerificarDocumento();
                     break;
                  } catch (Exception ex) {
                     LOGGER.log(Level.SEVERE, null, ex);
                     JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 0);
                     documents.add(new Documento(false, false, new ArrayList<>(), ex.getMessage()));
                  } finally {
                     this.setCursor(Cursor.getDefaultCursor());
                  }
               }

               this.agregarVerificarListaDocumentos(documents);
               this.enableControles(true);
               this.resetFirmarDocumento();
            }
         )
         .start();
   }

   private void previewPdf() {
      this.razonFirma = null;
      this.localizacion = null;
      this.estampado = null;
      this.point = null;

      try {
         JPanelVisualizadorPdf jPanelVisualizadorPdf = new JPanelVisualizadorPdf(this.documento, 50, 50, 153, 50);
         JButton btnEstampar = new JButton();
         btnEstampar.setText("Estampar");
         btnEstampar.setMnemonic(65);
         Object[] options = new Object[]{btnEstampar};
         btnEstampar.addActionListener(evt -> {
            try {
               this.documento = jPanelVisualizadorPdf.getDocumento();
               this.razonFirma = jPanelVisualizadorPdf.getRazonFirma().trim();
               this.localizacion = jPanelVisualizadorPdf.getLocalizacion().trim();
               this.estampado = jPanelVisualizadorPdf.getEstampado();
               this.pagina = jPanelVisualizadorPdf.getPagina();
               this.point = jPanelVisualizadorPdf.getPoint();
               Component component1 = (Component)evt.getSource();
               JDialog dialog = (JDialog)SwingUtilities.getRoot(component1);
               dialog.dispose();
            } catch (IOException ex) {
               LOGGER.log(Level.SEVERE, null, ex.getMessage());
            }
         });
         JOptionPane.showOptionDialog(this, jPanelVisualizadorPdf, "Visualizador PDF para estampado de firma en formato A4", 0, -1, null, options, options[0]);
      } catch (HeadlessException e) {
         LOGGER.log(Level.SEVERE, messages.getProperty("mensaje.error.documento_problemas") + "\n" + this.documento, e.getMessage());
         JOptionPane.showMessageDialog(this, messages.getProperty("mensaje.error.documento_problemas") + "\n" + this.documento, "Error", 0);
      }
   }

   private static void cargarPropiedades() {
      messages = PropertiesUtils.getMessages();
      config = PropertiesUtils.getConfig();
      firmaDigitalConfig = PropertiesUtils.getFirmaDigitalConfig();
   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.mainPanel = new JTabbedPane();
      this.jplFirmarDocumento = new JPanel();
      this.jbtnFirmar = new JButton();
      this.jbtnRestablecerFirmar = new HoverButton();
      this.jtbFirmarToken = new HoverToggleButton();
      this.jtbFirmarArchivo = new HoverToggleButton();
      this.jlblCertificadoEnFimador = new JLabel();
      this.jbtnCertificadoFirmar = new HoverButton();
      this.jbtnDocumentoFirmar = new HoverButton();
      this.jlblDocumentosFirmar = new JLabel();
      this.jScrollPaneFirmarDocumentos = new JScrollPane();
      this.jtblFirmarDocumentos = new JTable();
      this.jchkBoxFirmaInvisible = new JCheckBox();
      this.jlblClaveFirmar = new JLabel();
      this.jpfFirmarClave = new JPasswordField();
      this.jSeparatorFirmarDocumento1 = new JSeparator();
      this.jSeparatorFirmarDocumento2 = new JSeparator();
      this.jplVerificarDocumento = new JPanel();
      this.jbtnRestablecerVerificar = new HoverButton();
      this.jbtnVerificar = new JButton();
      this.jbtnDocumentoVerificar = new HoverButton();
      this.jScrollPaneVerificarDocumentos = new JScrollPane();
      this.jtblVerificarDocumentos = new JTable();
      this.jlblDocumentosVerificar = new JLabel();
      this.jSeparatorVerificarDocumento = new JSeparator();
      this.jplValidarCertificado = new JPanel();
      this.jbtnRestablecerValidar = new HoverButton();
      this.jbtnValidar = new JButton();
      this.jtbValidarToken = new HoverToggleButton();
      this.jtbValidarArchivo = new HoverToggleButton();
      this.jlblCertificadoEnFimador1 = new JLabel();
      this.jSeparatorValidarCertificado = new JSeparator();
      this.jbtnCertificadoValidar = new HoverButton();
      this.jlblClaveValidar = new JLabel();
      this.jpfValidarClave = new JPasswordField();
      this.jlblResultadoValidacion = new JLabel();
      this.jSeparatorValidarCertificado1 = new JSeparator();
      this.jScrollPane2 = new JScrollPane();
      this.jtpValidarCertificado = new JTextPane();
      this.jmbMenuPrincipal = new JMenuBar();
      this.jmConfiguracion = new JMenu();
      this.jmiPanelConfiguracion = new JMenuItem();
      this.jmiActualizar = new JMenuItem();
      this.jmAyuda = new JMenu();
      this.jmiAyuda = new JMenuItem();
      this.jmiAcerca = new JMenuItem();
      this.setDefaultCloseOperation(3);
      this.jScrollPane1.setPreferredSize(new Dimension(984, 523));
      this.mainPanel.setPreferredSize(new Dimension(650, 450));
      this.jbtnFirmar.setMnemonic('f');
      this.jbtnFirmar.setText("Firmar");
      this.jbtnFirmar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jbtnFirmarActionPerformed(evt);
         }
      });
      this.jbtnRestablecerFirmar.setMnemonic('r');
      this.jbtnRestablecerFirmar.setText("Restablecer");
      this.jbtnRestablecerFirmar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jbtnRestablecerFirmarActionPerformed(evt);
         }
      });
      this.jtbFirmarToken.setText("Token");
      this.jtbFirmarToken.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jtbFirmarTokenActionPerformed(evt);
         }
      });
      this.jtbFirmarArchivo.setText("Archivo");
      this.jtbFirmarArchivo.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jtbFirmarArchivoActionPerformed(evt);
         }
      });
      this.jlblCertificadoEnFimador.setText("Certificado en");
      this.jbtnCertificadoFirmar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jbtnCertificadoFirmarActionPerformed(evt);
         }
      });
      this.jbtnDocumentoFirmar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jbtnDocumentoFirmarActionPerformed(evt);
         }
      });
      this.jlblDocumentosFirmar.setText("<html><b>0 DOCUMENTO(S) SELECCIONADO(S)</b></html>");
      this.jtblFirmarDocumentos.setModel(new DefaultTableModel(new Object[0][], new String[]{"Documento", "Abrir", "Quitar"}) {
         boolean[] canEdit = new boolean[]{false, false, false};

         @Override
         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.canEdit[columnIndex];
         }
      });
      this.jScrollPaneFirmarDocumentos.setViewportView(this.jtblFirmarDocumentos);
      this.jchkBoxFirmaInvisible.setText("Firma invisible (sólo para documentos PDF)");
      this.jlblClaveFirmar.setText("Contraseña");
      this.jpfFirmarClave.setEnabled(false);
      GroupLayout jplFirmarDocumentoLayout = new GroupLayout(this.jplFirmarDocumento);
      this.jplFirmarDocumento.setLayout(jplFirmarDocumentoLayout);
      jplFirmarDocumentoLayout.setHorizontalGroup(
         jplFirmarDocumentoLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jplFirmarDocumentoLayout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     jplFirmarDocumentoLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(this.jlblDocumentosFirmar)
                        .addComponent(this.jScrollPaneFirmarDocumentos, -1, 625, 32767)
                        .addComponent(this.jSeparatorFirmarDocumento1, Alignment.TRAILING)
                        .addComponent(this.jSeparatorFirmarDocumento2, Alignment.TRAILING)
                        .addGroup(
                           jplFirmarDocumentoLayout.createSequentialGroup()
                              .addComponent(this.jchkBoxFirmaInvisible)
                              .addPreferredGap(ComponentPlacement.RELATED, 190, 32767)
                              .addComponent(this.jbtnFirmar)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jbtnRestablecerFirmar)
                        )
                        .addGroup(
                           jplFirmarDocumentoLayout.createSequentialGroup()
                              .addComponent(this.jlblCertificadoEnFimador)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jtbFirmarArchivo)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jtbFirmarToken)
                              .addGap(0, 0, 32767)
                        )
                        .addGroup(
                           jplFirmarDocumentoLayout.createSequentialGroup()
                              .addGroup(
                                 jplFirmarDocumentoLayout.createParallelGroup(Alignment.LEADING, false)
                                    .addGroup(
                                       jplFirmarDocumentoLayout.createSequentialGroup()
                                          .addComponent(this.jlblClaveFirmar)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(this.jpfFirmarClave)
                                    )
                                    .addComponent(this.jbtnCertificadoFirmar, -2, 318, -2)
                              )
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jbtnDocumentoFirmar, -1, -1, 32767)
                        )
                  )
                  .addContainerGap()
            )
      );
      jplFirmarDocumentoLayout.setVerticalGroup(
         jplFirmarDocumentoLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jplFirmarDocumentoLayout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     jplFirmarDocumentoLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(this.jlblCertificadoEnFimador)
                        .addComponent(this.jtbFirmarArchivo)
                        .addComponent(this.jtbFirmarToken)
                  )
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jSeparatorFirmarDocumento1, -2, 7, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                     jplFirmarDocumentoLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                           jplFirmarDocumentoLayout.createSequentialGroup()
                              .addComponent(this.jbtnCertificadoFirmar, -2, 75, -2)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addGroup(
                                 jplFirmarDocumentoLayout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(this.jlblClaveFirmar)
                                    .addComponent(this.jpfFirmarClave, -2, 35, -2)
                              )
                        )
                        .addComponent(this.jbtnDocumentoFirmar, -2, 115, -2)
                  )
                  .addGap(5, 5, 5)
                  .addComponent(this.jlblDocumentosFirmar, -2, -1, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jScrollPaneFirmarDocumentos, -1, 247, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jSeparatorFirmarDocumento2, -2, 6, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                     jplFirmarDocumentoLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                           Alignment.TRAILING,
                           jplFirmarDocumentoLayout.createParallelGroup(Alignment.BASELINE)
                              .addComponent(this.jbtnFirmar)
                              .addComponent(this.jbtnRestablecerFirmar)
                        )
                        .addComponent(this.jchkBoxFirmaInvisible, Alignment.TRAILING)
                  )
                  .addContainerGap()
            )
      );
      this.mainPanel.addTab("<html><b>FIRMAR DOCUMENTO </b>(<u>1</u>)</html>", this.jplFirmarDocumento);
      this.jbtnRestablecerVerificar.setMnemonic('r');
      this.jbtnRestablecerVerificar.setText("Restablecer");
      this.jbtnRestablecerVerificar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jbtnRestablecerVerificarActionPerformed(evt);
         }
      });
      this.jbtnVerificar.setMnemonic('v');
      this.jbtnVerificar.setText("Verificar Archivo");
      this.jbtnVerificar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jbtnVerificarActionPerformed(evt);
         }
      });
      this.jbtnDocumentoVerificar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jbtnDocumentoVerificarActionPerformed(evt);
         }
      });
      this.jtblVerificarDocumentos.setModel(new DefaultTableModel(new Object[0][], new String[]{"Documento", "Abrir", "Quitar"}) {
         boolean[] canEdit = new boolean[]{false, false, false};

         @Override
         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.canEdit[columnIndex];
         }
      });
      this.jScrollPaneVerificarDocumentos.setViewportView(this.jtblVerificarDocumentos);
      this.jlblDocumentosVerificar.setText("<html><b>0 DOCUMENTO(S) SELECCIONADO(S)</b></html>");
      GroupLayout jplVerificarDocumentoLayout = new GroupLayout(this.jplVerificarDocumento);
      this.jplVerificarDocumento.setLayout(jplVerificarDocumentoLayout);
      jplVerificarDocumentoLayout.setHorizontalGroup(
         jplVerificarDocumentoLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jplVerificarDocumentoLayout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     jplVerificarDocumentoLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(this.jlblDocumentosVerificar, -1, 625, 32767)
                        .addComponent(this.jbtnDocumentoVerificar, -1, -1, 32767)
                        .addComponent(this.jScrollPaneVerificarDocumentos)
                        .addComponent(this.jSeparatorVerificarDocumento)
                        .addGroup(
                           Alignment.TRAILING,
                           jplVerificarDocumentoLayout.createSequentialGroup()
                              .addGap(0, 405, 32767)
                              .addComponent(this.jbtnVerificar)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jbtnRestablecerVerificar)
                        )
                  )
                  .addContainerGap()
            )
      );
      jplVerificarDocumentoLayout.setVerticalGroup(
         jplVerificarDocumentoLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jplVerificarDocumentoLayout.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(this.jbtnDocumentoVerificar, -2, 115, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jlblDocumentosVerificar, -2, -1, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jScrollPaneVerificarDocumentos, -1, 284, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jSeparatorVerificarDocumento, -2, 6, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                     jplVerificarDocumentoLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(this.jbtnRestablecerVerificar)
                        .addComponent(this.jbtnVerificar)
                  )
                  .addContainerGap()
            )
      );
      this.mainPanel.addTab("<html><b>VERIFICAR DOCUMENTO </b>(<u>2</u>)</html>", this.jplVerificarDocumento);
      this.jbtnRestablecerValidar.setMnemonic('r');
      this.jbtnRestablecerValidar.setText("Restablecer");
      this.jbtnRestablecerValidar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jbtnRestablecerValidarActionPerformed(evt);
         }
      });
      this.jbtnValidar.setMnemonic('v');
      this.jbtnValidar.setText("Validar");
      this.jbtnValidar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jbtnValidarActionPerformed(evt);
         }
      });
      this.jtbValidarToken.setText("Token");
      this.jtbValidarToken.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jtbValidarTokenActionPerformed(evt);
         }
      });
      this.jtbValidarArchivo.setText("Archivo");
      this.jtbValidarArchivo.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jtbValidarArchivoActionPerformed(evt);
         }
      });
      this.jlblCertificadoEnFimador1.setText("Certificado en");
      this.jbtnCertificadoValidar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jbtnCertificadoValidarActionPerformed(evt);
         }
      });
      this.jlblClaveValidar.setText("Contraseña");
      this.jpfValidarClave.setEnabled(false);
      this.jlblResultadoValidacion.setText("<html><b>RESULTADOS DE VERIFICACIÓN DE CERTIFICADO ELECTRÓNICO</b></html>");
      this.jScrollPane2.setViewportView(this.jtpValidarCertificado);
      GroupLayout jplValidarCertificadoLayout = new GroupLayout(this.jplValidarCertificado);
      this.jplValidarCertificado.setLayout(jplValidarCertificadoLayout);
      jplValidarCertificadoLayout.setHorizontalGroup(
         jplValidarCertificadoLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jplValidarCertificadoLayout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     jplValidarCertificadoLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                           jplValidarCertificadoLayout.createSequentialGroup()
                              .addComponent(this.jlblClaveValidar)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jpfValidarClave)
                        )
                        .addComponent(this.jbtnCertificadoValidar, -1, -1, 32767)
                        .addGroup(
                           jplValidarCertificadoLayout.createSequentialGroup()
                              .addComponent(this.jlblCertificadoEnFimador1)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jtbValidarArchivo)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jtbValidarToken)
                              .addGap(0, 0, 32767)
                        )
                        .addComponent(this.jSeparatorValidarCertificado)
                        .addComponent(this.jSeparatorValidarCertificado1)
                        .addComponent(this.jlblResultadoValidacion, -1, 643, 32767)
                        .addGroup(
                           Alignment.TRAILING,
                           jplValidarCertificadoLayout.createSequentialGroup()
                              .addGap(0, 0, 32767)
                              .addComponent(this.jbtnValidar)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jbtnRestablecerValidar)
                        )
                        .addComponent(this.jScrollPane2)
                  )
                  .addContainerGap()
            )
      );
      jplValidarCertificadoLayout.setVerticalGroup(
         jplValidarCertificadoLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jplValidarCertificadoLayout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     jplValidarCertificadoLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(this.jlblCertificadoEnFimador1)
                        .addComponent(this.jtbValidarArchivo)
                        .addComponent(this.jtbValidarToken)
                  )
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jSeparatorValidarCertificado, -2, 7, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jbtnCertificadoValidar, -2, 75, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                     jplValidarCertificadoLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(this.jpfValidarClave, -2, 36, -2)
                        .addComponent(this.jlblClaveValidar)
                  )
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jlblResultadoValidacion, -2, -1, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jScrollPane2, -1, 244, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                     jplValidarCertificadoLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(this.jbtnValidar, Alignment.TRAILING)
                        .addGroup(
                           Alignment.TRAILING,
                           jplValidarCertificadoLayout.createSequentialGroup()
                              .addComponent(this.jSeparatorValidarCertificado1, -2, 7, -2)
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jbtnRestablecerValidar)
                        )
                  )
                  .addContainerGap()
            )
      );
      this.mainPanel.addTab("<html><b>VALIDAR CERTIFICADO </b>(<u>3</u>)</html>", this.jplValidarCertificado);
      this.jScrollPane1.setViewportView(this.mainPanel);
      this.jmbMenuPrincipal.setBorderPainted(false);
      this.jmConfiguracion.setMnemonic('c');
      this.jmConfiguracion.setText("Configuración");
      this.jmConfiguracion.setHorizontalAlignment(4);
      this.jmConfiguracion.setHorizontalTextPosition(4);
      this.jmConfiguracion.setInheritsPopupMenu(true);
      this.jmiPanelConfiguracion.setMnemonic('p');
      this.jmiPanelConfiguracion.setText("Panel de Configuración");
      this.jmiPanelConfiguracion.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jmiPanelConfiguracionActionPerformed(evt);
         }
      });
      this.jmConfiguracion.add(this.jmiPanelConfiguracion);
      this.jmiActualizar.setMnemonic('z');
      this.jmiActualizar.setText("Actualizar");
      this.jmiActualizar.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jmiActualizarActionPerformed(evt);
         }
      });
      this.jmConfiguracion.add(this.jmiActualizar);
      this.jmbMenuPrincipal.add(this.jmConfiguracion);
      this.jmAyuda.setMnemonic('a');
      this.jmAyuda.setText("Ayuda");
      this.jmAyuda.setHorizontalAlignment(4);
      this.jmAyuda.setHorizontalTextPosition(4);
      this.jmAyuda.setInheritsPopupMenu(true);
      this.jmiAyuda.setMnemonic('y');
      this.jmiAyuda.setText("Ayuda en línea");
      this.jmiAyuda.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jmiAyudaActionPerformed(evt);
         }
      });
      this.jmAyuda.add(this.jmiAyuda);
      this.jmiAcerca.setMnemonic('d');
      this.jmiAcerca.setText("Acerca de");
      this.jmiAcerca.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            Main.this.jmiAcercaActionPerformed(evt);
         }
      });
      this.jmAyuda.add(this.jmiAcerca);
      this.jmbMenuPrincipal.add(this.jmAyuda);
      this.setJMenuBar(this.jmbMenuPrincipal);
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane1, -1, 657, 32767));
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jScrollPane1, -1, -1, 32767).addGap(0, 0, 0))
      );
      this.pack();
   }

   private void jmiAcercaActionPerformed(ActionEvent evt) {
      JPanelAcercaDe jplAcercaDe = new JPanelAcercaDe();
      JButton btnOkAcercaDe = new JButton();
      btnOkAcercaDe.setText("Aceptar");
      btnOkAcercaDe.setMnemonic(65);
      btnOkAcercaDe.addActionListener(evt1 -> {
         Component component1 = (Component)evt1.getSource();
         JDialog dialog = (JDialog)SwingUtilities.getRoot(component1);
         dialog.dispose();
      });
      Object[] options = new Object[]{btnOkAcercaDe};
      btnOkAcercaDe.addActionListener(evt1 -> {
         Component component1 = (Component)evt1.getSource();
         JDialog dialog = (JDialog)SwingUtilities.getRoot(component1);
         dialog.dispose();
      });
      JOptionPane.showOptionDialog(this.getRootPane(), jplAcercaDe, "Acerca de FirmaEC", 0, -1, null, options, options[0]);
   }

   private void jmiAyudaActionPerformed(ActionEvent evt) {
      if (Desktop.isDesktopSupported()) {
         try {
            Desktop dk = Desktop.getDesktop();
            dk.browse(new URI(PropertiesUtils.getConfig().getProperty("setting.url_ayuda_online")));
         } catch (IOException | URISyntaxException e) {
            System.out.println("Error al abrir URL: " + e.getMessage());
         }
      }
   }

   private void jmiActualizarActionPerformed(ActionEvent evt) {
      try {
         Update update = new Update();
         Update.setAmbiente("--update");
         if (update.update() == null) {
            JOptionPane.showMessageDialog(this.rootPane, "Se encuentra en su última versión, no es necesario actualizar");
         } else {
            this.actualizar();
         }
      } catch (IOException ex) {
         LOGGER.log(Level.SEVERE, null, ex.getMessage());
      }
   }

   private void jmiPanelConfiguracionActionPerformed(ActionEvent evt) {
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
      JOptionPane.showOptionDialog(this.getRootPane(), jPanelConfiguracion, "Panel de Configuración", 0, -1, null, options, options[0]);
   }

   private void jbtnRestablecerVerificarActionPerformed(ActionEvent evt) {
      this.resetVerificarDocumento();
   }

   private void jbtnVerificarActionPerformed(ActionEvent evt) {
      if (this.tableModelVerificarDocumentos.getRowCount() >= 0) {
         List<String> rutaDocumentos = this.existeVerificarDocumentos();
         if (!rutaDocumentos.isEmpty()) {
            this.verificarDocumento(rutaDocumentos);
         } else {
            JOptionPane.showMessageDialog(this, "Seleccione documento", "Error", 0);
         }
      }
   }

   private void jbtnRestablecerValidarActionPerformed(ActionEvent evt) {
      this.resetValidacionCertificado();
      this.resetCertificadoValidar();
   }

   private void jbtnValidarActionPerformed(ActionEvent evt) {
      if (!this.jtbValidarToken.isSelected() && !this.jtbValidarArchivo.isSelected()) {
         JOptionPane.showMessageDialog(this, "Seleccione tipo de certificado", "Error", 0);
      } else {
         this.setCursor(Cursor.getPredefinedCursor(3));
         KeyStore ks = null;

         try {
            if ((OsUtils.getOs().contains("LINUX") || OsUtils.getOs().contains("MAC"))
               && this.jtbValidarToken.isSelected()
               && this.jpfValidarClave.getPassword().length == 0) {
               throw new CertificadoInvalidoException(messages.getProperty("mensaje.error.linux_clave"));
            }

            if (this.jtbValidarToken.isSelected()) {
               String tipoKeyStoreProvider = null;
               if (this.jtbValidarToken.isSelected()) {
                  tipoKeyStoreProvider = "TOKEN";
               }

               ks = KeyStoreProviderFactory.getKeyStore(new String(this.jpfValidarClave.getPassword()), tipoKeyStoreProvider);
               if (ks == null) {
                  this.setCursor(Cursor.getDefaultCursor());
                  throw new TokenNoEncontradoException(
                     messages.getProperty("mensaje.error.token_contrasena_invalida") + " o " + messages.getProperty("mensaje.error.dispositivo_no_encontrado")
                  );
               }
            }

            if (this.jtbValidarArchivo.isSelected()) {
               File llave = new File(this.pathCertificadoValidar);
               if (llave.exists()) {
                  KeyStoreProvider ksp = new FileKeyStoreProvider(this.pathCertificadoValidar);
                  ks = ksp.getKeystore(this.jpfValidarClave.getPassword());
               } else {
                  this.setCursor(Cursor.getDefaultCursor());
                  DialogUtils.showDialog(this, "No se encontró el certificado digital:", llave.getPath(), "Error", 0);
               }
            }

            if (ks != null) {
               this.alias = null;
               this.alias = CertUtils.seleccionarAlias(ks, this.getRootPane());
               X509Certificate x509Certificate = CertUtils.getCert(ks, this.alias);
               this.agregarValidacionCertificado(x509Certificate);
               this.jpfValidarClave.setText("");
               this.setCursor(Cursor.getDefaultCursor());
            }
         } catch (KeyStoreException e) {
            this.setCursor(Cursor.getDefaultCursor());
            if (e.getMessage().equals("java.io.IOException: keystore password was incorrect")) {
               JOptionPane.showMessageDialog(this, messages.getProperty("mensaje.error.clave_incorrecta"), "Error", 0);
            } else {
               LOGGER.log(Level.SEVERE, null, e.getMessage());
               JOptionPane.showMessageDialog(this, messages.getProperty("mensaje.error.certificado_formato_invalido"), "Error", 0);
            }

            this.jpfValidarClave.setText("");
         } catch (ProcessingException | TokenNoEncontradoException | CertificadoInvalidoException | EntidadCertificadoraNoValidaException | RubricaException | HeadlessException | InvalidKeyException | IllegalArgumentException ex) {
            this.setCursor(Cursor.getDefaultCursor());
            LOGGER.log(Level.SEVERE, null, ex.getMessage());
            if (ex.getClass() == NoRouteToHostException.class | ex.getClass() == ConexionException.class | ex.getClass() == ProcessingException.class) {
               JOptionPane.showMessageDialog(this, "Problemas en la red, no es posible conectarse", "Error", 0);
            } else {
               JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 0);
            }

            if (ex.getClass() == IllegalArgumentException.class) {
               JOptionPane.showMessageDialog(this, "Problemas con la emisión del certificado digital", "Error", 0);
            }

            this.resetValidacionCertificado();
         }
      }
   }

   private void jbtnDocumentoVerificarActionPerformed(ActionEvent evt) {
      try {
         this.agregarVerificarDocumento(FileUtils.rutaFicheros(this.FILTRO_DOCUMENTOS, this.getRootPane()));
      } catch (Exception ex) {
         LOGGER.log(Level.SEVERE, null, ex.getMessage());
      }
   }

   private void jtbValidarTokenActionPerformed(ActionEvent evt) {
      this.selValidarConToken();
   }

   private void jtbValidarArchivoActionPerformed(ActionEvent evt) {
      this.selValidarConArchivo();
      this.pathCertificadoValidar = PropertiesUtils.getFirmaDigitalConfig().getProperty("user.home");
      this.jbtnCertificadoValidar();
   }

   private void jbtnCertificadoValidarActionPerformed(ActionEvent evt) {
      this.pathCertificadoValidar = FileUtils.rutaFichero(this.FILTRO_CERTIFICADOS, this.getRootPane());
      this.jbtnCertificadoValidar();
   }

   private void jbtnDocumentoFirmarActionPerformed(ActionEvent evt) {
      try {
         this.agregarFirmarDocumento(FileUtils.rutaFicheros(this.FILTRO_DOCUMENTOS, this.getRootPane()));
      } catch (Exception ex) {
         LOGGER.log(Level.SEVERE, null, ex.getMessage());
      }
   }

   private void jbtnCertificadoFirmarActionPerformed(ActionEvent evt) {
      this.pathCertificadoFirmar = FileUtils.rutaFichero(this.FILTRO_CERTIFICADOS, this.getRootPane());
      this.jbtnCertificadoFirmar();
   }

   private void jtbFirmarArchivoActionPerformed(ActionEvent evt) {
      this.selFirmarConArchivo();
      this.pathCertificadoFirmar = PropertiesUtils.getFirmaDigitalConfig().getProperty("user.home", "");
      this.jbtnCertificadoFirmar();
      PropertiesUtils.updateFirmaDigitalConfig(
         this.pathCertificadoFirmar,
         PropertiesUtils.getFirmaDigitalConfig().getProperty("uiScale", "1.0"),
         "archivo"
      );
   }

   private void jtbFirmarTokenActionPerformed(ActionEvent evt) {
      this.selFirmarConToken();
      Properties firmaDigitalConfig = PropertiesUtils.getFirmaDigitalConfig();
      PropertiesUtils.updateFirmaDigitalConfig(
         firmaDigitalConfig.getProperty("user.home", ""),
         firmaDigitalConfig.getProperty("uiScale", "1.0"),
         "token"
      );
   }

   private void jbtnRestablecerFirmarActionPerformed(ActionEvent evt) {
      this.resetFirmarDocumento();
   }

   private void jbtnFirmarActionPerformed(ActionEvent evt) {
      if (!this.jtbFirmarToken.isSelected() && !this.jtbFirmarArchivo.isSelected()) {
         JOptionPane.showMessageDialog(this, "Seleccione tipo de certificado", "Error", 0);
      } else if (this.tableModelFirmarDocumentos.getRowCount() >= 0) {
         List<String> rutaDocumentos = this.existeFirmarDocumentos();
         if (!rutaDocumentos.isEmpty()) {
            this.firmarDocumento(rutaDocumentos);
         } else {
            JOptionPane.showMessageDialog(this, "Seleccione documento", "Error", 0);
         }
      }
   }

   public static void main(String[] args) throws Exception {
      Log.initLogging();
      cargarPropiedades();

      try {
         System.setProperty("sun.net.inetaddr.ttl", "60");
         System.setProperty("sun.net.inetaddr.negative.ttl", "10");
         System.setProperty("networkaddress.cache.ttl", "60");
         System.setProperty("networkaddress.cache.negative.ttl", "10");
         System.setProperty("sun.net.client.defaultConnectTimeout", "5000");
         System.setProperty("sun.net.client.defaultReadTimeout", "30000");
         System.setProperty("java.net.preferIPv4Stack", "true");
         System.setProperty("java.net.preferIPv4Addresses", "true");
         boolean versionEnabled;
         try {
            versionEnabled = PropertiesUtils.getVersion();
         } catch (ConexionException ex) {
            LOGGER.log(Level.WARNING, "No se pudo contactar al servicio de versiones, iniciando en modo local: {0}", ex.getMessage());
            versionEnabled = true;
         }
         if (versionEnabled) {
            try {
               System.setProperty("sun.java2d.uiScale", firmaDigitalConfig.getProperty("uiScale"));
            } catch (NullPointerException npe) {
               System.setProperty("sun.java2d.uiScale", "1.0");
            }

            Update update = new Update();
            if (args == null || args.length <= 0 || !"--update".equals(args[0]) && !"--updatedes".equals(args[0])) {
               if (args != null && args.length > 0 && "--verify".equals(args[0])) {
                  System.out.println("comando: " + args[0]);
                  System.out.println("ruta del documento: " + args[1]);
                  File document = new File(args[1]);
                  if (document.exists()) {
                     Documento documento = Utils.verificarDocumento(document, PropertiesUtils.versionBase64());
                     System.out.println("JSON:");
                     System.out.println(Json.generarJsonDocumento(documento));
                  } else {
                     System.out.println("No se encontró el documento: " + document.getPath());
                  }
               } else {
                  try {
                     UIManager.setLookAndFeel(new NimbusLookAndFeel());
                  } catch (UnsupportedLookAndFeelException ex) {
                     LOGGER.log(Level.SEVERE, null, ex);
                  }

                  EventQueue.invokeLater(() -> new Main().setVisible(true));
                  update(VERSION);
                  LOGGER.log(Level.INFO, "Firmador: {0} JRE: {1} Sistema Operativo: {2}", new Object[]{VERSION, OsUtils.getJavaVersion(), OsUtils.getOs()});
               }
            } else {
               try {
                  Update.setAmbiente(args[0]);
                  File jar = update.actualizarFirmador();
                  update.updateFirmador(jar);
                  if (!OsUtils.isMac()) {
                     File clienteJar = update.actualizarCliente();
                     update.updateCliente(clienteJar);
                  }

                  System.exit(0);
               } catch (IllegalArgumentException | IOException e) {
                  LOGGER.log(Level.SEVERE, "Error al actualizar:", e.getMessage());
                  System.exit(1);
               }
            }
         } else {
            SwingLink link = new SwingLink(
               "Descargue la nueva versión disponible de", "FirmaEC", PropertiesUtils.getConfig().getProperty("setting.url_ayuda_online")
            );
            JOptionPane.showMessageDialog(null, link, "Atención", 2);
         }
      } catch (ConexionException ex) {
         LOGGER.log(Level.SEVERE, null, ex.getMessage());
         JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
      }
   }

   private static void update(String version) {
      Update update = new Update();
      if (!version.contains("BETA")) {
         try {
            Update.setAmbiente("--update");
            String mensaje = update.update();
            if (mensaje != null) {
               SwingLink link = new SwingLink(mensaje, "FirmaEC", PropertiesUtils.getConfig().getProperty("setting.url_ayuda_online"));
               JOptionPane.showMessageDialog(null, link);
            }
         } catch (IOException ex) {
            LOGGER.log(Level.INFO, "No se puede comprobar actualizaciones: {0}", ex.getMessage());
         }
      }
   }
}
