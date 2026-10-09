package ec.gob.firmadigital.firmador;

import ec.gob.firmadigital.libreria.utils.FileUtils;
import ec.gob.firmadigital.utils.CellRendererButton;
import ec.gob.firmadigital.utils.CellRendererWordWrap;
import ec.gob.firmadigital.utils.PropertiesUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.RowSorter.SortKey;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class JPanelDocumento extends JPanel {
   private static final Logger LOGGER = Logger.getLogger(JPanelDocumento.class.getName());
   private DefaultTableModel tableModelDocumentos;
   private ImageIcon imageIcon;
   private Font customFontWorkSansRegular;
   private final String IMAGE_ABRIR = PropertiesUtils.getConfig().getProperty("image.abrir");
   private final String FONT_WORKSANS_REGULAR = PropertiesUtils.getConfig().getProperty("font.worksans_regular");
   private ButtonGroup buttonGroup;
   private JScrollPane jScrollPane3;
   private JLabel jblMensaje;
   private JTable jtblDocumento;

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

      List<String> documentos = new ArrayList<>();
      documentos.add("documento 1");
      documentos.add("documento 2");
      documentos.add("documento 3");
      JPanelDocumento jPanelDocumento = new JPanelDocumento(documentos);
      JButton btnSalir = new JButton();
      btnSalir.setText("Salir");
      btnSalir.setMnemonic(83);
      Object[] options = new Object[]{btnSalir};
      btnSalir.addActionListener(evt1 -> {
         Component component1 = (Component)evt1.getSource();
         JDialog dialog = (JDialog)SwingUtilities.getRoot(component1);
         dialog.dispose();
      });
      JOptionPane.showOptionDialog(null, jPanelDocumento, "Documentos firmados", 0, -1, null, options, options[0]);
   }

   public JPanelDocumento(List<String> documentos) {
      this.initComponents();
      this.inicializarTabla();
      this.eventos();
      this.agregarDocumentos(documentos);
   }

   public JTable getJtblFirmarDocumentos() {
      return this.jtblDocumento;
   }

   private Dimension newDimension(Component component, int widthAditional, int height) {
      return new Dimension((int)component.getPreferredSize().getWidth() + widthAditional, (int)component.getPreferredSize().getHeight() + height);
   }

   private JButton jbtnAbrirDocumento() {
      JButton jbtnAbrirDocumento = new JButton();
      jbtnAbrirDocumento.setText("");
      this.imageIcon = new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource(this.IMAGE_ABRIR)).getImage().getScaledInstance(15, 15, 1));
      jbtnAbrirDocumento.setIcon(this.imageIcon);
      jbtnAbrirDocumento.setPreferredSize(this.newDimension(jbtnAbrirDocumento, 0, 0));
      jbtnAbrirDocumento.addActionListener(e -> {
         String ruta = (String)this.tableModelDocumentos.getValueAt(this.jtblDocumento.convertRowIndexToModel(this.jtblDocumento.getSelectedRow()), 0);
         if (this.jtblDocumento.getSelectedRows().length > 0 && ruta != null && !ruta.isEmpty()) {
            try {
               FileUtils.abrirDocumento(ruta);
            } catch (IOException ex) {
               LOGGER.log(Level.SEVERE, null, ex);
            }
         }
      });
      return jbtnAbrirDocumento;
   }

   private void eventos() {
      this.jtblDocumento
         .addMouseListener(
            new MouseAdapter() {
               @Override
               public void mouseClicked(MouseEvent evt) {
                  if (evt.getClickCount() == 1) {
                     int column = JPanelDocumento.this.jtblDocumento.columnAtPoint(evt.getPoint());
                     int row = JPanelDocumento.this.jtblDocumento.rowAtPoint(evt.getPoint());
                     if (row < JPanelDocumento.this.jtblDocumento.getRowCount()
                        && row >= 0
                        && column < JPanelDocumento.this.jtblDocumento.getColumnCount()
                        && column >= 0
                        && JPanelDocumento.this.jtblDocumento.getValueAt(row, column) instanceof JButton jButton) {
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

      this.jtblDocumento.setFont(this.customFontWorkSansRegular.deriveFont(13.0F));
      String[] columnNames = new String[]{"<html><b>Documento</b></html>", "<html><b>Abrir</b></html>"};
      this.jtblDocumento.setModel(new DefaultTableModel(new Object[0][], columnNames) {
         @Override
         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
         }
      });
      JTableHeader jTableHeader = this.jtblDocumento.getTableHeader();
      ((DefaultTableCellRenderer)this.jtblDocumento.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
      jTableHeader.setReorderingAllowed(false);
      this.jtblDocumento.setUI(new BasicTableUI());
      this.tableModelDocumentos = (DefaultTableModel)this.jtblDocumento.getModel();
      this.jtblDocumento.getColumnModel().getColumn(1).setMaxWidth(60);
      this.jtblDocumento.getColumnModel().getColumn(0).setCellRenderer(new CellRendererWordWrap());
      this.jtblDocumento.getColumnModel().getColumn(1).setCellRenderer(new CellRendererButton());
   }

   private void agregarDocumentos(List<String> pathDocuments) {
      this.tableModelDocumentos = (DefaultTableModel)this.jtblDocumento.getModel();
      RowSorter<TableModel> sorter = new TableRowSorter<>(this.tableModelDocumentos);
      this.jtblDocumento.setRowSorter(sorter);
      List<SortKey> sortKeys = new ArrayList<>();
      sortKeys.add(new SortKey(0, SortOrder.DESCENDING));
      sorter.setSortKeys(sortKeys);
      this.tableModelDocumentos.setRowCount(0);
      pathDocuments.stream()
         .map(pathDocument -> new Object[]{pathDocument, this.jbtnAbrirDocumento(), null})
         .forEachOrdered(data -> this.tableModelDocumentos.addRow(data));
      this.jtblDocumento.setModel(this.tableModelDocumentos);
      this.tableModelDocumentos.fireTableDataChanged();
      this.jblMensaje.setText("<html><b>Total de documentos firmados electrónicamente: " + pathDocuments.size() + "</b></html>");
   }

   private void initComponents() {
      this.buttonGroup = new ButtonGroup();
      this.jScrollPane3 = new JScrollPane();
      this.jtblDocumento = new JTable();
      this.jblMensaje = new JLabel();
      this.jtblDocumento.setModel(new DefaultTableModel(new Object[][]{{null}}, new String[]{"Doble clic sobre el documento para visualizar"}) {
         boolean[] canEdit = new boolean[]{false};

         @Override
         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.canEdit[columnIndex];
         }
      });
      this.jScrollPane3.setViewportView(this.jtblDocumento);
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane3, -1, 659, 32767).addComponent(this.jblMensaje, -1, -1, 32767)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               Alignment.TRAILING,
               layout.createSequentialGroup()
                  .addComponent(this.jblMensaje, -2, 15, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jScrollPane3, -1, 431, 32767)
            )
      );
   }
}
