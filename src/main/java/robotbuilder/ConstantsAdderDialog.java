
package robotbuilder;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import static robotbuilder.ParameterEditorTable.INVALID_COLOR;
import static robotbuilder.ParameterEditorTable.SELECTED_COLOR;
import static robotbuilder.ParameterEditorTable.SELECTED_INVALID_COLOR;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.properties.ConstantsProperty;
import robotbuilder.data.properties.ParameterDescriptor;
import robotbuilder.data.properties.ValuedParameterDescriptor;

/**
 * Dialog for adding constants to subsystems.
 *
 * @author Sam Carlberg
 */
public class ConstantsAdderDialog extends CenteredDialog {

    /**
     * The constants property being edited.
     */
    private final ConstantsProperty constantsProperty;

    /**
     * Convenience list for changing constants.
     */
    private final List<ValuedParameterDescriptor> constantsList;

    /**
     * ComboBox for selecting parameter types.
     */
    private final JComboBox<String> typeBox = new JComboBox<>(ParameterDescriptor.SUPPORTED_TYPES);

    private DefaultCellEditor currentCellEditor;

    public ConstantsAdderDialog(RobotComponent command, JFrame owner, boolean modal) {
        super(owner, "Add constants");
        this.constantsProperty = (ConstantsProperty) command.getProperty("Constants");
        initComponents();
        setBackground(Color.WHITE);
        setForeground(Color.WHITE);
        constantsTable.setShowHorizontalLines(true);
        constantsTable.setShowVerticalLines(true);
        constantsTable.setRowHeight(25);
        constantsTable.setBackground(new Color(240, 240, 240));
        constantsTable.setGridColor(Color.BLACK);
        constantsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE
                        || e.getKeyChar() == KeyEvent.VK_DELETE) {
                    deleteSelectedRows();
                }
            }

        });
        constantsList = constantsProperty.getValue();
        constantsList.stream().forEach(p -> getTableModel().addRow(p.toArray()));
    }

    public List<ValuedParameterDescriptor> showAndGet() {
        setVisible(true);
        return getParameters();
    }

    /**
     * Saves the data in the table to the constants property. This will clear
     * any data that previously existed in the property.
     */
    private void save() {
        Vector<Vector> dataVector = getTableModel().getDataVector();
        constantsList.clear();
        dataVector.stream().forEach((dataRow) -> {
            String name = (String) dataRow.get(0);
            String type = (String) dataRow.get(1);
            Object value = dataRow.get(2);
            ValuedParameterDescriptor newParam = new ValuedParameterDescriptor(name, type, value);
            constantsList.add(newParam);
        });
        constantsProperty.setValueAndUpdate(constantsList); // almost certainly redundant
    }

    /**
     * Deletes the selected rows in the table. This does not effect the
     * constants property.
     */
    private void deleteSelectedRows() {
        int[] rows = constantsTable.getSelectedRows();
        constantsTable.clearSelection();
        currentCellEditor.cancelCellEditing();
        for (int i = rows.length - 1; i >= 0; i--) {
            if (rows[i] > -1) {
                getTableModel().removeRow(rows[i]);
            }
        }
    }

    /**
     * Checks if the given row is valid. A row is valid if the name of the
     * constant in the row is unique, i.e. no other constant in the table has
     * the same name.
     *
     * @param row the row to validate
     * @return true if the row is valid, false otherwise
     */
    private boolean isRowValid(int row) {
        String name = (String) ((Vector) getTableModel().getDataVector().get(row)).get(0);
        int count = 0;
        count = ((Vector<Vector>) getTableModel().getDataVector())
                .stream()
                .filter(v -> v.get(0).equals(name))
                .map(i -> 1)
                .reduce(count, Integer::sum);
        if (count != 1) {
            return false;
        }
        return constantsProperty.isValid();
    }

    public List<ValuedParameterDescriptor> getParameters() {
        return constantsProperty.getValue();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        constantsTable = new ParameterDeclarationTable();
        addButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        constantsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Type", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        constantsTable.setDragEnabled(true);
        constantsTable.setShowGrid(true);
        constantsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(constantsTable);

        addButton.setText("Add constant");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Save and close");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(saveButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        ValuedParameterDescriptor p = new ValuedParameterDescriptor("[change me]", "String", null);
        getTableModel().addRow(p.toArray());
    }//GEN-LAST:event_addButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        save();
        dispose();
    }//GEN-LAST:event_saveButtonActionPerformed

    /**
     * Helper method to get and cast the table model to avoid casting it
     * everywhere it's used.
     */
    private DefaultTableModel getTableModel() {
        return (DefaultTableModel) constantsTable.getModel();
    }

    private ValuedParameterDescriptor constantForRow(int row) {
        Vector<Object> rowData = (Vector) getTableModel().getDataVector().get(row);
        String name = (String) rowData.get(0);
        ValuedParameterDescriptor p = constantsProperty.getConstantByName(name);
        if (p == null) {
            p = new ValuedParameterDescriptor(name, (String) rowData.get(1), rowData.get(2));
        }
        return p;
    }

    private class ParameterDeclarationTable extends JTable {

        public ParameterDeclarationTable() {
            setTransferHandler(new TableRowTransferHandler(this));
        }

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            DefaultCellEditor editor;
            switch (column) {
                case 0: // name
                    editor = new DefaultCellEditor(new JTextField());
                    break;
                case 1: // type
                    editor = new DefaultCellEditor(typeBox);
                    break;
                case 2: // value
                    String type = (String) getValueAt(row, 1);
                    switch (type) {
                        case "boolean":
                            editor = new DefaultCellEditor(new JCheckBox());
                            break;
                        case "String":
                            editor = new DefaultCellEditor(new JTextField());
                            break;
                        default: // number
                            editor = new DefaultCellEditor(new JTextField("0"));
                            break;
                    }
                    break;
                default:
                    editor = (DefaultCellEditor) super.getCellEditor(row, column);
                    break;
            }
            editor.setClickCountToStart(2);
            currentCellEditor = editor;
            return editor;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (0 <= row && row < this.getRowCount()) {
                super.setValueAt(aValue, row, column);
            }
        }

        @Override
        @SuppressWarnings("Convert2Lambda")
        public TableCellRenderer getCellRenderer(int row, int column) {
            final Object valueHere = super.getValueAt(row, column);
            return new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = new JLabel(String.valueOf(valueHere));
                    label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                    ParameterDescriptor param = constantForRow(row);
                    if (!param.isValid() || !isRowValid(row)) {
                        if (isSelected) {
                            label.setBackground(SELECTED_INVALID_COLOR);
                        } else {
                            label.setBackground(INVALID_COLOR);
                        }
                    } else if (isSelected) {
                        label.setBackground(SELECTED_COLOR);
                    }
                    label.setOpaque(true);
                    return label;
                }
            };
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTable constantsTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
