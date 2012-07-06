
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import robotbuilder.data.RobotComponent;

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
                } else if (value instanceof JFileChooser) {
                    return new FileCellEditor((JFileChooser) value);
                }
                return getDefaultEditor(value.getClass());
            }
            return super.getCellEditor(row, column);
        }

        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            final Object value = super.getValueAt(row, column);
            if (value != null) {
                if (value instanceof JComboBox) {
                    return new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                            return new JLabel(((JComboBox) value).getSelectedItem().toString());
                        }
                    };
                } else if (value instanceof JFileChooser) {
                    return new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                            System.out.println("Render component fetched.");
                            try {
                                System.out.println("Filechooser: "+value);
                                System.out.println("Directory: "+((JFileChooser) value).getCurrentDirectory());
                                System.out.println("Selection: "+((JFileChooser) value).getSelectedFile());
                                System.out.println("Path: "+((JFileChooser) value).getSelectedFile().getPath());
                                return new JLabel(((JFileChooser) value).getSelectedFile().getPath());
                            } catch (NullPointerException e) {
                                return new JLabel("Select Folder");
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
            System.out.println("Getting value for (" + row + "' " + column + ")");
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
            if (row == 0)
                currentComponent.setName((String) val);
            else
                currentComponent.setValue(keys[row-1], (String) val);
            robot.update();
        }
    }
}
