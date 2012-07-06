
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
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
        String[] type = {};
        keys = currentComponent.getProperties().keySet().toArray(type);
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
                if(value instanceof JComboBox) {
                    return new DefaultCellEditor((JComboBox) value);
                }
                return getDefaultEditor(value.getClass());
            }
            return super.getCellEditor(row, column);
        }

        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            final Object value = super.getValueAt(row, column);
            if(value != null) {
                if(value instanceof JComboBox) {
                    return new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                            System.out.println("Render component fetched.");
                            return new JLabel(((JComboBox) value).getSelectedItem().toString());
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
                return currentComponent.getProperties().size()+1;
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
                    return currentComponent.getProperties().get(keys[row-1]).getName();
            } else {
                if (row == 0)
                    return currentComponent.getName();
                else
                    //String[] choices = {"1", "2", "3"};
                    //return new JComboBox(choices);}
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
