
package robotbuilder;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import robotbuilder.data.properties.Property;

/**
 * Cell renderer for a button in a {@linkplain JTable}.
 *
 * @author Sam Carlberg
 */
class TableButton extends JButton implements TableCellRenderer {

    public TableButton() {
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (hasFocus) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        if (value == null) {
            setText("");
        } else if (value instanceof Property) {
            setText(((Property) value).getDisplayValue().toString());
        } else {
            setText(value.toString());
        }
        return this;
    }

}
