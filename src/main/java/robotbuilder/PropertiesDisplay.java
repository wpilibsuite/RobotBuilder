
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Label;
import java.awt.event.ActionEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import lombok.Getter;

import robotbuilder.data.RobotComponent;
import robotbuilder.data.properties.ParametersProperty;
import robotbuilder.data.properties.Property;
import robotbuilder.robottree.RobotTree;
import robotbuilder.utils.RelativePathAccessory;

/**
 *
 * @author brad
 * @author Sam Carlberg
 */
public class PropertiesDisplay extends JPanel {

    public JTable propTable;
    PropertiesTableModel propTableModel;
    @Getter
    RobotComponent currentComponent;
    List<String> keys;
    RobotTree robot;
    Label errorLabel;
    private int numRowsRemoved = 0;
    private JComponent currentDisplay;

    public PropertiesDisplay() {
        setLayout(new BorderLayout());
        errorLabel = new Label("Error: Hover over the red property names for details of how to fix.");
        errorLabel.setForeground(Color.red);
        errorLabel.setVisible(false);
        add(errorLabel, BorderLayout.NORTH);
        propTableModel = new PropertiesTableModel();
        propTable = new PropertiesTable(propTableModel);
        currentDisplay = new JScrollPane(propTable);
        add(currentDisplay, BorderLayout.CENTER);
        propTable.setFillsViewportHeight(true);
        propTable.getTableHeader().setReorderingAllowed(false);
    }

    public void setCurrentComponent(RobotComponent node) {
        if (propTable.getCellEditor() != null) {
            propTable.getCellEditor().stopCellEditing();
        }
        currentComponent = node;
        errorLabel.setVisible(!currentComponent.isValid());
        keys = currentComponent.getPropertyKeys();
        currentComponent.getRobotTree().getHistory().unfreeze();
        remove(currentDisplay);
        currentDisplay = new JScrollPane(propTable);
        add(currentDisplay, BorderLayout.CENTER);
        ((JSplitPane) getParent()).setDividerLocation(0.5);
        update();
    }

    /**
     * Matches the "Parameters" property for the current component (if it's a command)
     * to the parameters of the actual command to keep them in sync.
     */
    private void handleParameters() {
        if (currentComponent == null) {
            return;
        }
        keys = currentComponent.getPropertyKeys();
        Optional<ParametersProperty> existingParams =
            currentComponent.getPropertyKeys()
                  .stream()
                  .filter(k -> k.toLowerCase().endsWith("parameters"))
                  .map(currentComponent::getProperty)
                  .map(ParametersProperty.class::cast)
                  .findFirst();
        existingParams.ifPresent(p -> {
            ParametersProperty commandParams = Utils.getParameters(currentComponent);
            // Match the existing parameters to the ones in the command
            p.matchUpWith(commandParams);
        });
    }

    private void handleCommands() {
        numRowsRemoved = 0;
        // Remove commands property from the display (for command groups)
        if (currentComponent.getBaseType().equals("Command Group")) {
            keys = keys.stream().filter(k -> !k.equals("Commands")).collect(Collectors.toList());
            propTableModel.fireTableDataChanged();
            numRowsRemoved = 1;
        }
    }

    public void setEditName() {
        SwingUtilities.invokeLater(() -> {
            propTable.editCellAt(0, 1);
            propTable.requestFocusInWindow();
            ((JTextField) ((DefaultCellEditor) propTable.getCellEditor()).getComponent()).selectAll();
        });
    }

    public void update() {
        handleParameters();
        handleCommands();
        updateUI();
    }

    public void setRobotTree(RobotTree robot) {
        this.robot = robot;
    }

    class PropertiesTable extends JTable {

        private PropertiesTable(TableModel propTableModel) {
            super(propTableModel);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            final String name = (String) super.getValueAt(row, 0);
            if ("Parameters".equals(name)) {
                return !(currentComponent.getBaseType().equals("Setpoint Command") || currentComponent.getBaseType().equals("Timed Command")) && column == 1;
            }
            return getModel().isCellEditable(row, column);
        }

        @Override
        public TableCellEditor getCellEditor(final int row, final int column) {
            final String name = (String) super.getValueAt(row, 0);
            Object value = super.getValueAt(row, column);
            if (value != null) {
                if (name.toLowerCase().endsWith("parameters")) {
                    return new TableButtonEditor<>(
                            currentComponent.getBaseType().contains("Command")
                                    ? new ParameterAdderDialog(currentComponent, null, true)::showAndGetParameters
                                    : new ParameterEditorDialog(currentComponent, null, true)::showAndGetParameters);
                }
                if ("Constants".equals(name)) {
                    return new TableButtonEditor<>(new ConstantsAdderDialog(currentComponent, null, true)::showAndGet);
                }
                if ("Parameter presets".equals(name)) {
                    return new TableButtonEditor<>(new ParameterSetsEditorDialog(currentComponent, null, true)::showAndGet);
                }
                if (value instanceof JComboBox) {
                    return new DefaultCellEditor((JComboBox) value);
                } else if (value instanceof JFileChooser) {
                    return new FileCellEditor((JFileChooser) value);
                } else if (value instanceof Boolean) {
                    JCheckBox checkbox = new JCheckBox("", (Boolean) this.getValueAt(row, column));
                    checkbox.setOpaque(false);
                    TableCellEditor editor = new DefaultCellEditor(checkbox);
                    editor.getTableCellEditorComponent(propTable, null, true, row, column).setBackground(Color.BLUE);
                    return editor;
                }
                DefaultCellEditor editor = (DefaultCellEditor) getDefaultEditor(value.getClass());
                editor.setClickCountToStart(1);
                return editor;
            }
            final TableCellEditor editor = super.getCellEditor(row, column);
            editor.addCellEditorListener(new CellEditorListener() {
                @Override
                public void editingStopped(ChangeEvent ce) {
                    setValueAt(editor.getCellEditorValue(), row, column);
                }

                @Override
                public void editingCanceled(ChangeEvent ce) {
                    setValueAt(editor.getCellEditorValue(), row, column);
                }
            });
            return editor;
        }

        @Override
        @SuppressWarnings("Convert2Lambda")
        public TableCellRenderer getCellRenderer(final int row, final int column) {
            final Object value = super.getValueAt(row, column);
            if (column == 0) {
                return new TableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                        JLabel label = new JLabel(value.toString());
                        label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                        if (row == 0) {
                            return label;
                        }
                        Property property = currentComponent.getProperty(keys.get(row - 1));
                        if (!property.isValid()) {
                            label.setBackground(new Color(255, 150, 150));
                            label.setOpaque(true);
                            label.setToolTipText(property.getErrorMessage());

                        } else {
                            label.setForeground(Color.black);
                        }
                        return label;
                    }
                };
            }
            if (value != null) {
                final String name = (String) super.getValueAt(row, 0);
                if ("Parameters".equals(name) ||
                    "Default command parameters".equals(name) ||
                    "Autonomous command parameters".equals(name) ||
                    "Constants".equals(name) ||
                    "Parameter presets".equals(name)) {
                    if (currentComponent.getBaseType().equals("Setpoint Command") || currentComponent.getBaseType().equals("Timed Command")) {
                        return super.getCellRenderer(row, column);
                    }
                    return new TableButton();
                }
                if (value instanceof JComboBox) {
                    return new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                            try {
                                return new JLabel(((JComboBox) value).getSelectedItem().toString());
                            } catch (NullPointerException ex) {
                                return new JLabel("No Choices Available");
                            }
                        }
                    };
                } else if (value instanceof JFileChooser) {
                    return new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                            try {
                                JFileChooser chooser = (JFileChooser) value;
                                RelativePathAccessory acc = (RelativePathAccessory) chooser.getAccessory();
                                String path = acc.getPathName(chooser.getSelectedFile());
                                return new JLabel(path);
                            } catch (NullPointerException e) {
                                return new JLabel("Click to Select");
                            }
                        }
                    };
                } else if (value instanceof Boolean) {
                    return new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                            JCheckBox checkbox = new JCheckBox("", (Boolean) value);
                            checkbox.setOpaque(false);
                            return checkbox;
                        }
                    };
                }
                return getDefaultRenderer(value.getClass());
            }
            return super.getCellRenderer(row, column);
        }

    }

    class PropertiesTableModel extends DefaultTableModel {

        @Override
        public String getColumnName(int col) {
            if (col == 0) {
                return "Property";
            } else {
                return "Value";
            }
        }

        @Override
        public int getRowCount() {
            if (currentComponent == null) {
                return 0;
            } else {
                return currentComponent.getPropertyKeys().size() + 1 - numRowsRemoved;
            }
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (column == 0) {
                if (row == 0) {
                    return "Name";
                } else {
                    return keys.get(row - 1);
                }
            } else {
                if (row == 0) {
                    return currentComponent.getName();
                } else {
                    return currentComponent.getProperty(keys.get(row - 1)).getDisplayValue();
                }
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 1 && (row == 0 || currentComponent.getProperty(keys.get(row - 1)).isEditable());
        }

        @Override
        public void setValueAt(Object val, int row, int column) {
            if (column != 1) {
                return;
            }
            if (row == 0) {
                String name = (String) val;
                // Prevent top level components from being renamed
                if (currentComponent.getParent() == robot.getRoot()) {
                    JOptionPane.showMessageDialog(MainFrame.getInstance(),
                            "You cannot rename this component.", "Can't Rename", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Make sure the name is unique
                String subsystem = currentComponent.getSubsystem();
                if (!robot.hasName(subsystem + name)
                        || (subsystem + name).equalsIgnoreCase(currentComponent.getFullName())) {
                    currentComponent.setName(name);
                    robot.update();
                } else {
                    JOptionPane.showMessageDialog(MainFrame.getInstance(),
                            "You already have a component named: " + name, "Invalid Name", JOptionPane.ERROR_MESSAGE);
                }
            } else if (row > 0) {
                final String key = keys.get(row - 1);
                currentComponent.getProperty(key).setValueAndUpdate(val);
                update();
            }
            MainFrame.getInstance().updateStatus();
        }
    }

}
