
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
import robotbuilder.data.Validator;

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
                } else if (value instanceof JCheckBox) {
                    return new DefaultCellEditor((JCheckBox) value);
                }else if (value instanceof JFileChooser) {
                    return new FileCellEditor((JFileChooser) value);
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
                        String validatorName = currentComponent.getBase().getProperty(keys[row-1]).getValidator();
                        Validator validator = robot.getValidator(validatorName);
                        if (validator != null && !validator.isValid(currentComponent, keys[row-1])) {
                            label.setForeground(Color.red);
                            label.setToolTipText(validator.getError(currentComponent, keys[row-1]));
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
                } else if (value instanceof JCheckBox) {
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
                    return currentComponent.getValue(keys[row-1]);
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
                String validatorName = currentComponent.getBase().getProperty(key).getValidator();
                if (validatorName != null && !"".equals(validatorName)) {
                    Validator validator = robot.getValidator(validatorName);
                    if (validator != null) {
                        validator.update(currentComponent, key, val.toString());
                    }
                }
                System.out.println("\tAssigning..");
                currentComponent.setValue(key, val.toString());
            }
            robot.update();
        }
    }
}
