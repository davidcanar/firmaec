package ec.gob.firmadigital.firmador;

import ec.gob.firmadigital.libreria.certificate.to.Documento;
import ec.gob.firmadigital.utils.CellRendererButton;
import ec.gob.firmadigital.utils.CellRendererWordWrap;
import ec.gob.firmadigital.utils.PropertiesUtils;
import ec.gob.firmadigital.utils.SmartLongLabel;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.RowSorter.SortKey;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class JPanelFirmante extends JPanel {
   private static final Logger LOGGER = Logger.getLogger(JPanelFirmante.class.getName());
   private DefaultTableModel tableModelFirmante;
   private Font customFontWorkSansRegular;
   private final String IMAGE_CORRECTO = PropertiesUtils.getConfig().getProperty("image.correcto");
   private final String IMAGE_INCORRECTO = PropertiesUtils.getConfig().getProperty("image.incorrecto");
   private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(PropertiesUtils.getConfig().getProperty("setting.simple_date_format"));
   private final String FONT_WORKSANS_REGULAR = PropertiesUtils.getConfig().getProperty("font.worksans_regular");
   private ImageIcon imageIcon;
   private String mensaje = "";
   private JScrollPane jScrollPane3;
   private JLabel jblDocumento;
   private JLabel jblMensaje;
   private JTable jtblFirmante;

   public JPanelFirmante(Documento documento, String rutaDocumento) {
      this.initComponents();
      this.inicializarTabla();
      this.eventos();
      this.agregarFirmantes(documento, rutaDocumento);
   }

   public JTable getJtblFirmarDocumentos() {
      return this.jtblFirmante;
   }

   private void eventos() {
      this.jtblFirmante
         .addMouseListener(
            new MouseAdapter() {
               @Override
               public void mouseClicked(MouseEvent evt) {
                  if (evt.getClickCount() == 1) {
                     int column = JPanelFirmante.this.jtblFirmante.columnAtPoint(evt.getPoint());
                     int row = JPanelFirmante.this.jtblFirmante.rowAtPoint(evt.getPoint());
                     if (row < JPanelFirmante.this.jtblFirmante.getRowCount()
                        && row >= 0
                        && column < JPanelFirmante.this.jtblFirmante.getColumnCount()
                        && column >= 0
                        && JPanelFirmante.this.jtblFirmante.getValueAt(row, column) instanceof JButton jButton) {
                        jButton.doClick();
                     }
                  }
               }
            }
         );
   }

   private void inicializarTabla() {
      try {
         this.customFontWorkSansRegular = Font.createFont(0, this.getClass().getClassLoader().getResourceAsStream(this.FONT_WORKSANS_REGULAR));
      } catch (FontFormatException | IOException ex) {
         LOGGER.log(Level.SEVERE, null, ex);
      }

      this.jtblFirmante.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      String[] columnNames = new String[]{
         "<html><div style=\"text-align: center;\"><b>Cédula de Identidad /<br>Nombres y Apellidos</b></html>",
         "<html><div style=\"text-align: center;\"><b>Razón /<br>Localización</b></div></html>",
         "<html><div style=\"text-align: center;\"><b>Fecha de<br>Firmado</b></div></html>",
         "<html><div style=\"text-align: center;\"><b>Entidad<br>Certificadora</b></div></html>",
         "<html><div style=\"text-align: center;\"><b>Fecha de<br>Emisión</b></div></html>",
         "<html><div style=\"text-align: center;\"><b>Fecha de<br>Expiración</b></div></html>",
         "<html><div style=\"text-align: center;\"><b>Fecha de<br>Revocación</b></div></html>",
         "<html><div style=\"text-align: center;\"><b>Sellado de<br>Tiempo</b></div></html>",
         "<html><div style=\"text-align: center;\"><b>Válido</b></div></html>"
      };
      this.jtblFirmante.setModel(new DefaultTableModel(new Object[0][], columnNames) {
         @Override
         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
         }
      });
      JTableHeader jTableHeader = this.jtblFirmante.getTableHeader();
      ((DefaultTableCellRenderer)this.jtblFirmante.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
      jTableHeader.setReorderingAllowed(false);
      this.jtblFirmante.setUI(new BasicTableUI());
      this.tableModelFirmante = (DefaultTableModel)this.jtblFirmante.getModel();
      this.jtblFirmante.getColumnModel().getColumn(0).setCellRenderer(new CellRendererWordWrap());
      this.jtblFirmante.getColumnModel().getColumn(1).setCellRenderer(new CellRendererWordWrap());
      this.jtblFirmante.getColumnModel().getColumn(2).setCellRenderer(new CellRendererWordWrap());
      this.jtblFirmante.getColumnModel().getColumn(3).setCellRenderer(new CellRendererWordWrap());
      this.jtblFirmante.getColumnModel().getColumn(4).setCellRenderer(new CellRendererWordWrap());
      this.jtblFirmante.getColumnModel().getColumn(5).setCellRenderer(new CellRendererWordWrap());
      this.jtblFirmante.getColumnModel().getColumn(6).setCellRenderer(new CellRendererWordWrap());
      this.jtblFirmante.getColumnModel().getColumn(7).setCellRenderer(new CellRendererWordWrap());
      this.jtblFirmante.getColumnModel().getColumn(8).setCellRenderer(new CellRendererButton());
   }

   private void agregarFirmantes(Documento documento, String rutaDocumento) {
      this.tableModelFirmante = (DefaultTableModel)this.jtblFirmante.getModel();
      RowSorter<TableModel> sorter = new TableRowSorter<>(this.tableModelFirmante);
      this.jtblFirmante.setRowSorter(sorter);
      List<SortKey> sortKeys = new ArrayList<>();
      sortKeys.add(new SortKey(3, SortOrder.DESCENDING));
      sorter.setSortKeys(sortKeys);
      this.tableModelFirmante.setRowCount(0);
      documento.getCertificados()
         .stream()
         .map(
            certificado -> {
               String razonSocial = "";
               if (!certificado.getDatosUsuario().getRuc().isBlank()) {
                  razonSocial = "Ruc:\n"
                     + certificado.getDatosUsuario().getRuc()
                     + "\nRazón Social:\n"
                     + certificado.getDatosUsuario().getRazonSocial()
                     + "\nCargo:\n"
                     + certificado.getDatosUsuario().getCargo();
               }

               Object[] data = new Object[]{
                  certificado.getDatosUsuario().getCedula()
                     + "\n"
                     + certificado.getDatosUsuario().getNombre()
                     + "\n"
                     + certificado.getDatosUsuario().getApellido()
                     + "\n"
                     + razonSocial,
                  certificado.getDocReason() + "\n" + certificado.getDocLocation(),
                  SIMPLE_DATE_FORMAT.format(certificado.getSignGenerated().getTime())
                     + "\n"
                     + SIMPLE_DATE_FORMAT.getTimeZone().getDisplayName(Locale.getDefault()),
                  certificado.getIssuedBy() + " - " + certificado.getDatosUsuario().getTipoCertificado(),
                  SIMPLE_DATE_FORMAT.format(certificado.getValidFrom().getTime()) + "\n" + SIMPLE_DATE_FORMAT.getTimeZone().getDisplayName(Locale.getDefault()),
                  SIMPLE_DATE_FORMAT.format(certificado.getValidTo().getTime()) + "\n" + SIMPLE_DATE_FORMAT.getTimeZone().getDisplayName(Locale.getDefault()),
                  certificado.getRevocated() != null
                     ? SIMPLE_DATE_FORMAT.format(certificado.getRevocated().getTime())
                        + "\n"
                        + SIMPLE_DATE_FORMAT.getTimeZone().getDisplayName(Locale.getDefault())
                     : "No revocado",
                  certificado.getDocTimeStamp() != null
                     ? SIMPLE_DATE_FORMAT.format(certificado.getDocTimeStamp().getTime())
                        + "\n"
                        + SIMPLE_DATE_FORMAT.getTimeZone().getDisplayName(Locale.getDefault())
                        + "\n"
                        + certificado.getCnTimeStamp()
                        + "\nemitido por "
                        + certificado.getDocTimeStampIssuedBy()
                     : "No",
                  this.jbtnEstado(
                     (certificado.getSignVerify() == null || certificado.getSignVerify())
                        && certificado.getCertificateValidated()
                        && certificado.getDatosUsuario().isCertificadoDigitalValido()
                  ),
                  null
               };
               if (this.mensaje.isEmpty() && certificado.getSignVerify() != null && !certificado.getSignVerify()) {
                  this.mensaje = "Problemas en la integridad de la(s) firma(s) electrónica(s)\n";
               }

               if (!documento.getDocValidate()) {
                  this.mensaje = "Problemas en la integridad del documento\n";
               }

               return data;
            }
         )
         .forEachOrdered(data -> this.tableModelFirmante.addRow(data));
      this.jtblFirmante.setModel(this.tableModelFirmante);
      this.tableModelFirmante.fireTableDataChanged();
      this.ajustarAnchoColumna(this.jtblFirmante.getColumnModel().getColumn(0), 20);
      this.ajustarAnchoColumna(this.jtblFirmante.getColumnModel().getColumn(1), 20);
      this.ajustarAnchoColumna(this.jtblFirmante.getColumnModel().getColumn(2), 40);
      this.ajustarAnchoColumna(this.jtblFirmante.getColumnModel().getColumn(3), 20);
      this.ajustarAnchoColumna(this.jtblFirmante.getColumnModel().getColumn(4), 30);
      this.ajustarAnchoColumna(this.jtblFirmante.getColumnModel().getColumn(5), 20);
      this.ajustarAnchoColumna(this.jtblFirmante.getColumnModel().getColumn(6), 20);
      this.ajustarAnchoColumna(this.jtblFirmante.getColumnModel().getColumn(7), 20);
      this.ajustarAnchoColumna(this.jtblFirmante.getColumnModel().getColumn(8), 10);
      this.mensaje = "<b><p style=\"color:red;\">" + this.mensaje + "</p></b>";
      this.jblMensaje.setText("<html><b>Total de firmantes: </b>" + documento.getCertificados().size() + "<br>" + this.mensaje + "<b>Documento: </b></html>");
      this.jblDocumento.setText(rutaDocumento);
      SmartLongLabel.configureLongLabelUI(this.jblDocumento, 900);
   }

   private void ajustarAnchoColumna(TableColumn tableColumn, int margen) {
      TableColumn columna = tableColumn;
      TableCellRenderer headerRenderer = this.jtblFirmante.getTableHeader().getDefaultRenderer();
      Component headerComp = headerRenderer.getTableCellRendererComponent(this.jtblFirmante, columna.getHeaderValue(), false, false, 0, 0);
      int headerWidth = headerComp.getPreferredSize().width;
      columna.setPreferredWidth(headerWidth + margen);
      columna.setMinWidth(headerWidth);
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

   private void initComponents() {
      this.jScrollPane3 = new JScrollPane();
      this.jtblFirmante = new JTable();
      this.jblMensaje = new JLabel();
      this.jblDocumento = new JLabel();
      this.jtblFirmante.setModel(new DefaultTableModel(new Object[][]{{null}}, new String[]{"Doble clic sobre el documento para visualizar"}) {
         boolean[] canEdit = new boolean[]{false};

         @Override
         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.canEdit[columnIndex];
         }
      });
      this.jScrollPane3.setViewportView(this.jtblFirmante);
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addComponent(this.jScrollPane3, -1, 902, 32767)
            .addComponent(this.jblMensaje, -1, -1, 32767)
            .addComponent(this.jblDocumento, -1, -1, 32767)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               Alignment.TRAILING,
               layout.createSequentialGroup()
                  .addComponent(this.jblMensaje, -1, 15, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jblDocumento, -1, 15, 32767)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jScrollPane3, -2, 410, -2)
            )
      );
   }
}
