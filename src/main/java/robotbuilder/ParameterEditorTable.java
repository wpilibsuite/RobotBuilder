
package robotbuilder;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import robotbuilder.data.properties.ParameterDescriptor;
import robotbuilder.data.properties.ValuedParameterDescriptor;

/**
 *
 * @author Sam Carlberg
 */
public class ParameterEditorTable extends JTable {

    public static final Color SELECTED_COLOR = new Color(200, 200, 255); // light blue
    public static final Color INVALID_COLOR = new Color(255, 150, 150); // medium red
    public static final Color SELECTED_INVALID_COLOR = new Color(0xEC4659);

    private final String requiredSubsystemName;
    private final List<ValuedParameterDescriptor> constants;

    public ParameterEditorTable(String requiredSubsystemName, List<ValuedParameterDescriptor> constants) {
        setModel(new DefaultTableModel(new String[]{"Name", "Type", "Value"}, 0) {
            Class[] types = new Class[]{
                String.class, String.class, Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 2;
            }
        });
        this.requiredSubsystemName = requiredSubsystemName;
        this.constants = constants;
    }

    @Override
    public DefaultTableModel getModel() {
        return (DefaultTableModel) super.getModel();
    }

    private DefaultCellEditor getValueCellEditor(int row) {
        String type = (String) getValueAt(row, 1);
        Object value = getValueAt(row, 2);
        DefaultCellEditor editor;
        switch (type) {
            case "boolean": {
                String[] possibleNames
                        = constants.stream()
                        .filter(d -> d.getType().equals(type))
                        .map(p -> ("None".equals(requiredSubsystemName) ? "" : requiredSubsystemName + ".") + p.getName())
                        .toArray(String[]::new);
                Object[] options = new Object[possibleNames.length + 2];
                options[0] = ValuedParameterDescriptor.BOOLEAN_TRUE;
                options[1] = ValuedParameterDescriptor.BOOLEAN_FALSE;
                System.arraycopy(possibleNames, 0, options, 2, possibleNames.length);
                JComboBox combo = new JComboBox(options);
                editor = new DefaultCellEditor(combo);
                break;
            }
            default: {
                String[] possibleNames
                        = constants.stream()
                        .filter(d -> d.getType().equals(type))
                        .map(p -> ("None".equals(requiredSubsystemName) ? "" : requiredSubsystemName + ".") + p.getName())
                        .toArray(String[]::new);
                JComboBox combo = new JComboBox(possibleNames);
                combo.setEditable(true);
                editor = new DefaultCellEditor(combo);
                break;
            }
        }
        editor.setClickCountToStart(2);
        return editor;
    }

    private ValuedParameterDescriptor parameterForRow(int row) {
        String name = (String) getValueAt(row, 0);
        String type = (String) getValueAt(row, 1);
        Object value = getValueAt(row, 2);
        return new ValuedParameterDescriptor(name, type, value);
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (column == 2) {
            return getValueCellEditor(row);
        }
        return super.getCellEditor(row, column); // nothing else should be editable
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return new ParameterTableRenderer(r -> parameterForRow(r).isValid());
    }

    /**
     * Resizes the columns in the table to best fit their contents.
     */
    public void resizeToFit() {
        final int minWidth = 50;
        for (int column = 0; column < getColumnCount(); column++) {
            int width = minWidth;
            for (int row = 0; row < getRowCount(); row++) {
                TableCellRenderer renderer = getCellRenderer(row, column);
                Component comp = prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

}
