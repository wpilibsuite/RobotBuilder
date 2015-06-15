
package robotbuilder;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
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

    public ParameterEditorTable() {
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
            case "boolean":
                editor = new DefaultCellEditor(new JCheckBox("", value instanceof Boolean ? (Boolean) value : false));
                break;
            default:
                editor = new DefaultCellEditor(new JTextField());
                break;
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
        final Object valueHere = super.getValueAt(row, column);
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JComponent component;
                if (valueHere instanceof Boolean) {
                    JCheckBox checkBox = new JCheckBox("", (Boolean) valueHere);
                    component = checkBox;
                } else {
                    JLabel label = new JLabel(String.valueOf(valueHere));
                    label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                    component = label;
                }
                ParameterDescriptor param = parameterForRow(row);
                if (!param.isValid()) {
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
        };
    }

}
