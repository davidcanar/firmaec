package ec.gob.firmadigital.utils;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CellRendererButton extends JButton implements TableCellRenderer {
   public CellRendererButton() {
      this.setOpaque(true);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value != null) {
         this.setText(value.toString());
      } else {
         this.setText("");
      }

      this.setSize(table.getColumnModel().getColumn(column).getWidth(), this.getPreferredSize().height);
      if (table.getRowHeight(row) < this.getPreferredSize().height) {
         table.setRowHeight(row, this.getPreferredSize().height);
      }

      return (JButton)value;
   }
}
