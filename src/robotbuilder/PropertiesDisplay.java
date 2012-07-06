
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.properties.Property;

/**
 *
 * @author brad
 */
class PropertiesDisplay extends JPanel {
    JTable propTable;
    TableModel propTableModel;
    RobotComponent currentComponent;
    String[] keys;
    RobotTree robot;
    
    public PropertiesDisplay() {
        setLayout(new BorderLayout());
	propTableModel = new PropertiesTableModel();
	propTable = new PropertiesTable(propTableModel);
        add(new JScrollPane(propTable), BorderLayout.CENTER);
        propTable.setFillsViewportHeight(true);
        propTable.getTableHeader().setReorderingAllowed(false);
    }

    void setCurrentComponent(DefaultMutableTreeNode node) {
        currentComponent = (RobotComponent) node;
        keys = currentComponent.getPropertyKeys();
        this.updateUI();
    }

    void setRobotTree(RobotTree robot) {
        this.robot = robot;
    }
    
    class PropertiesTable extends JTable {

        private PropertiesTable(TableModel propTableModel) {
            super(propTableModel);
        }
        
        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            Object value = super.getValueAt(row, column);
            if(value != null) {
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
                return getDefaultEditor(value.getClass());
            }
            return super.getCellEditor(row, column);
        }

        @Override
        public TableCellRenderer getCellRenderer(final int row, final int column) {
            final Object value = super.getValueAt(row, column);
            if (column == 0) {
                return new TableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                        JLabel label = new JLabel(value.toString());
                        if (row == 0) { return label; }
                        Property property = currentComponent.getProperty(keys[row-1]);
                        if (!property.isValid()) {
                            label.setForeground(Color.red);
                            label.setToolTipText(property.getError());
                        } else {
                            label.setForeground(Color.black);
                        }
                        return label;
                    }
                };
            }
            if (value != null) {
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
                                String path = ((JFileChooser) value).getSelectedFile().getPath();
                                setValueAt(path, row, column);
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
    
    class PropertiesTableModel extends AbstractTableModel {

        @Override
	public String getColumnName(int col) {
            if (col == 0)
                return "Property";
            else
                return "Value";
        }
        
        @Override
	public int getRowCount() {
            if (currentComponent == null) {
                return 0;
            } else {
                return currentComponent.getPropertyKeys().length+1;
            }
	}

	@Override
	public int getColumnCount() {
	    return 2;
	}

	@Override
	public Object getValueAt(int row, int column) {
            if (column == 0) {
                if (row == 0)
                    return "Name";
                else
                    return keys[row-1];
            } else {
                if (row == 0)
                    return currentComponent.getName();
                else
                    return currentComponent.getProperty(keys[row-1]).getDisplayValue();
            }
	}
        
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 1;
        }
        
        @Override
        public void setValueAt(Object val, int row, int column) {
            assert column == 1; // TODO: Deal with more cleanly
            if (row == 0) {
                String subsystem = currentComponent.getSubsystem();
                String name = (String) val;
                if (!robot.hasName(subsystem+name) || 
                        (subsystem+name).equals(currentComponent.getFullName())) {
                    robot.removeName(currentComponent.getFullName());
                    currentComponent.setName(name);
                    robot.addName(subsystem+name);
                } else {
                    JOptionPane.showMessageDialog(MainFrame.getInstance(),
                            "You already have a component named: "+name, "Invalid Name", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                final String key = keys[row-1];
                currentComponent.getProperty(key).setValue(val);
            }
            robot.update();
        }
    }
}
