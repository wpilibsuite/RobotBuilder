
package robotbuilder;

import java.awt.Component;
import java.util.function.Supplier;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 * Push button editor for a cell in a {@linkplain JTable}.
 *
 * @author Sam Carlberg
 */
class TableButtonEditor<T> extends DefaultCellEditor {

    private final TableButton button;
    private boolean isPushed;
    private T value;
    private final Supplier<T> supplier;

    /**
     *
     * @param supplier the supplier of the value when this button is pressed.
     * This can be something like {@code new MyDialog::showAndGetValue}, where
     * {@code showAndGetValue} is a method that displays the dialog and returns
     * some user input.
     */
    public TableButtonEditor(Supplier<T> supplier) {
        super(new JCheckBox());
        this.supplier = supplier;
        button = new TableButton();
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        button.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            value = supplier.get();
        }
        isPushed = false;
        return value;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }

}
