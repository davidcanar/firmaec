package ec.gob.firmadigital.utils;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class CellRendererWordWrap extends JTextArea implements TableCellRenderer {
   private static final Color CUSTOM_COLOR_ORIGINAL = new Color(181, 186, 219);
   private static final Color CUSTOM_COLOR = new Color(98, 92, 163);
   Color customColorOriginal;

   public CellRendererWordWrap() {
      this.setLineWrap(true);
      this.setWrapStyleWord(true);
      this.customColorOriginal = this.getBackground();
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value != null) {
         this.setText(value.toString());
      } else {
         this.setText("");
      }

      if (hasFocus) {
         this.setBackground(CUSTOM_COLOR_ORIGINAL);
      } else {
         this.setBackground(CUSTOM_COLOR);
      }

      this.setSize(table.getColumnModel().getColumn(column).getWidth(), this.getPreferredSize().height);
      this.setBorder(new EmptyBorder(5, 5, 5, 5));
      if (isSelected) {
         this.setBackground(CUSTOM_COLOR);
         this.setForeground(table.getSelectionForeground());
      } else {
         this.setBackground(CUSTOM_COLOR_ORIGINAL);
         this.setForeground(table.getForeground());
      }

      if (table.getRowHeight(row) < this.getPreferredSize().height) {
         table.setRowHeight(row, this.getPreferredSize().height);
      }

      return this;
   }
}
