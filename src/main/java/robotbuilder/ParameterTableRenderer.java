
package robotbuilder;

import java.awt.Color;
import java.awt.Component;
import java.util.function.Predicate;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import lombok.AllArgsConstructor;

/**
 *
 * @author Sam Carlberg
 */
@AllArgsConstructor
public class ParameterTableRenderer implements TableCellRenderer {

    /**
     * The color of a selected row with all valid values.
     */
    public static final Color SELECTED_COLOR = new Color(200, 200, 255); // light blue

    /**
     * The color of an unselected row with at least one invalid value.
     */
    public static final Color INVALID_COLOR = new Color(255, 150, 150); // medium red

    /**
     * The color of a selected row with at least one invalid value.
     */
    public static final Color SELECTED_INVALID_COLOR = new Color(236, 70, 89); // light red/pink

    private final Predicate<Integer> rowValidator;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final JComponent component;
        if (value instanceof Boolean) {
            JCheckBox checkBox = new JCheckBox("", (Boolean) value);
            component = checkBox;
        } else {
            JLabel label = new JLabel(String.valueOf(value));
            label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
            component = label;
        }
        if (!rowValidator.test(row)) {
            if (isSelected) {
                component.setBackground(SELECTED_INVALID_COLOR);
            } else {
                component.setBackground(INVALID_COLOR);
            }
        } else if (isSelected) {
            component.setBackground(SELECTED_COLOR);
        }
        component.setOpaque(true);
        return component;
    }

}
