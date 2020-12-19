
package robotbuilder;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import lombok.RequiredArgsConstructor;

import robotbuilder.data.RobotComponent;
import robotbuilder.data.properties.*;
import robotbuilder.utils.UniqueList;

/**
 *
 * @author Sam Carlberg
 */
class ParameterSetsTable extends JTable {

    private static final String NAME_COLUMN_TITLE = "Name";
    private static final Class<?> NAME_COLUMN_TYPE = String.class;

    private List<ValuedParameterDescriptor> constants;
    private ParametersProperty parametersProperty;
    private String requiredSubsystemName;

    public ParameterSetsTable() {
        setTransferHandler(new TableRowTransferHandler(this));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE
                        || e.getKeyChar() == KeyEvent.VK_DELETE) {
                    deleteSelectedRows();
                }
            }

        });
    }

    private void deleteSelectedRows() {
        int[] rows = getSelectedRows();
        clearSelection();
        if (getCellEditor() != null) {
            getCellEditor().cancelCellEditing();
        }
        for (int i = rows.length - 1; i >= 0; i--) {
            if (rows[i] > -1) {
                getModel().removeRow(rows[i]);
            }
        }
    }

    public void generateFrom(RobotComponent command) {
        System.out.println("Generating from command " + command.getName());
        if (!command.getBaseType().equals("Command Group")) {
            requiredSubsystemName = (String) command.getProperty("Requires").getValue();
            RobotComponent requiredSubsystem = MainFrame.getInstance().getCurrentRobotTree().getComponentByName(requiredSubsystemName);
            if (requiredSubsystem != null) {
                constants = (List) requiredSubsystem.getProperty("Constants").getValue();
            }
        }
        parametersProperty = (ParametersProperty) command.getProperty("Parameters");
        List<ParameterDescriptor> parameters = (List) parametersProperty.getValue();
        UniqueList<String> titles = new UniqueList<>();
        List<Class> classes = new ArrayList<>();
        titles.add(NAME_COLUMN_TITLE);
        classes.add(NAME_COLUMN_TYPE);
        parameters.stream()
                .map(ParameterDescriptor::getName)
                .forEachOrdered(titles::add);
        parameters.stream()
                .map(this::getClassForParameter)
                .forEachOrdered(classes::add);
        PresetsTableModel model = new PresetsTableModel(classes.toArray(new Class[0]));
        model.setDataVector(new Object[0][0], titles.toArray());
        setModel(model);

        ParameterSetProperty p = (ParameterSetProperty) command.getProperty("Parameter presets");
        p.getValue().stream()
                .map(ParameterSet::toArray)
                .forEachOrdered(getModel()::addRow);
    }

    private Class<?> getClassForParameter(ParameterDescriptor param) {
        String type = param.getType();
        switch (type) {
            case "String":
                return String.class;
            case "double":
                return Double.TYPE;
            case "int":
                return Integer.TYPE;
            case "long":
                return Long.TYPE;
            case "byte":
                return Byte.TYPE;
            case "boolean":
                return Boolean.TYPE;
            default:
                return Object.class;
        }
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        DefaultCellEditor editor;
        if (NAME_COLUMN_TITLE.equals(getModel().getColumnName(column))) {
            PlainDocument doc = new PlainDocument() {
                @Override
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                    if (str.isEmpty()) {
                        return;
                    }
                    if (str.contains("\"") || str.contains("\\")) {
                        return;
                    }
                    super.insertString(offs, str, a);
                }
            };
            JTextField f = new JTextField();
            f.setDocument(doc);
            editor = new DefaultCellEditor(f);
        } else {
            String type = parametersProperty.getValue().get(column - 1).getType();
            switch (type) {
                case "boolean": {
                    String[] possibleNames = {};
                    if (constants != null) {
                        possibleNames = constants.stream()
                                .filter(d -> d.getType().equals(type))
                                .map(p -> ("None".equals(requiredSubsystemName) ? "" : requiredSubsystemName + ".") + p.getName())
                                .toArray(String[]::new);
                    }
                    Object[] options = new Object[possibleNames.length + 2];
                    options[0] = ValuedParameterDescriptor.BOOLEAN_TRUE;
                    options[1] = ValuedParameterDescriptor.BOOLEAN_FALSE;
                    System.arraycopy(possibleNames, 0, options, 2, possibleNames.length);
                    JComboBox combo = new JComboBox(options);
                    combo.setEditable(false);
                    editor = new DefaultCellEditor(combo);
                    break;
                }
                default: {
                    String[] possibleNames = {};
                    if (constants != null) {
                        possibleNames = constants.stream()
                                .filter(d -> d.getType().equals(type))
                                .map(p -> ("None".equals(requiredSubsystemName) ? "" : requiredSubsystemName + ".") + p.getName())
                                .toArray(String[]::new);
                    }
                    JComboBox combo = new JComboBox(possibleNames);
                    combo.setEditable(true);
                    combo.setEditor(new BasicComboBoxEditor() {

                        @Override
                        protected JTextField createEditorComponent() {
                            JTextField f = new JTextField();
                            PlainDocument doc = new PlainDocument() {

                                @Override
                                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                                    if (!validateInput(type, offs, str, f.getText())) {
                                        return;
                                    }
                                    super.insertString(offs, str, a);
                                }

                            };
                            f.setDocument(doc);
                            return f;
                        }

                    });
                    editor = new DefaultCellEditor(combo);
                    break;
                }
            }
        }
        editor.setClickCountToStart(2);
        return editor;
    }

    private boolean validateInput(String type, int offset, String input, String existing) {
        if (input.isEmpty()) {
            return false;
        }
        if (isReference (existing)) {
            return true;
        }
        if (isReference(input)) {
            return true;
        }
        switch (type) {
            case "String":
                return !input.contains("\"") && !input.contains("\\");
            case "double":
                if (".".equals(input) && existing.contains(".")) {
                    // only allow one dot in a double
                    return false;
                }
                if (offset == 0 && input.startsWith("-")) {
                    return input.length() == 1 || input.substring(1).matches("[0-9]*\\.?[0-9]*");
                }
                return input.matches("[0-9]*\\.?[0-9]*");
            case "int":
            case "byte":
            case "long":
                if (offset == 0 && input.startsWith("-")) {
                    return input.length() == 1 || input.substring(1).matches("[0-9]+");
                }
                return input.matches("[0-9]+");
            case "boolean":
                return input.matches("false") || input.matches("true");
            default:
                return false;
        }
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return new ParameterTableRenderer(this::isRowValid);
    }

    @Override
    public DefaultTableModel getModel() {
        return (DefaultTableModel) super.getModel();
    }

    public void editName(ParameterSet set) {
        if (isEditing()) {
            getCellEditor().stopCellEditing();
        }
        editCellAt(rowFor(set), 0);
    }

    public int rowFor(ParameterSet set) {
        for (int i = 0; i < getModel().getRowCount(); i++) {
            if (getValueAt(i, 0) == set.getName()) {
                return i;
            }
        }
        return -1;
    }

    private boolean isRowValid(int row) {
        String name = (String) getValueAt(row, 0);
        return isNameValid(name) && areParametersValid(row);
    }

    private boolean isNameValid(String name) {
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        return ParameterDescriptor.isValidParamName(name) &&
                 getModel().getDataVector().stream()
                .map(row -> ((List) row).get(0))
                .noneMatch(n -> name != n && trimmed.equals(n));
    }

    private boolean areParametersValid(int rowNum) {
        List<String> row = (List<String>) getModel().getDataVector().get(rowNum);
        row = row.subList(1, row.size());
        for (int i = 0; i < row.size(); i++) {
            ValuedParameterDescriptor vpd = new ValuedParameterDescriptor(parametersProperty.getValue().get(i));
            vpd.setValue(row.get(i));
            if (!vpd.isValid()) {
                return false;
            }
        }
        return true;
    }

    @RequiredArgsConstructor
    public static class PresetsTableModel extends DefaultTableModel {

        private final Class<?>[] types;

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex >= types.length) {
                return Object.class;
            }
            return types[columnIndex];
        }

        @Override
        public Vector<Vector> getDataVector() {
            return super.getDataVector();
        }

    }

    /**
     * Checks if this parameter references a constant, a variable, or
     * expression.
     *
     * @return
     */
    public boolean isReference(String value) {
            if (value.startsWith("$")) {
                // Literal escape
                return true;
            } else if (isSubsystemConstant(value)) {
                return true;
            }
        return false;
    }

    /**
     * Checks if this parameter is a reference to a subsystem constant (e.g.
     * "Arm.UP").
     *
     * @return
     */
    public boolean isSubsystemConstant(String value) {
            if (value.contains(".")) {
                String[] split = value.split("\\.");
                if (split.length != 2) {
                    return false;
                }
                RobotComponent subsystem = MainFrame.getInstance().getCurrentRobotTree().getComponentByName(split[0]);
                if (subsystem == null) {
                    return false;
                }
                ConstantsProperty cp = (ConstantsProperty) subsystem.getProperty("Constants");
                if (cp == null) {
                    return false;
                }
                List<ValuedParameterDescriptor> constants = cp.getValue();
                return constants.stream()
                        .map(ValuedParameterDescriptor::getName)
                        .anyMatch(split[1]::equals);
            }
        return false;
    }

}
