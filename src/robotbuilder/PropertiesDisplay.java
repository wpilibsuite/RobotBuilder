
package robotbuilder;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
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
	propTable = new JTable(propTableModel);
        add(propTable, BorderLayout.CENTER);
    }

    void setCurrentComponent(DefaultMutableTreeNode node) {
        currentComponent = (RobotComponent) node.getUserObject();
        String[] type = {};
        keys = currentComponent.getProperties().keySet().toArray(type);
        this.updateUI();
    }

    void setRobotTree(RobotTree robot) {
        this.robot = robot;
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
            if (column == 0) {
                if (row == 0)
                    return "Name";
                else
                    return currentComponent.getProperties().get(keys[row-1]).getName();
            } else {
                if (row == 0)
                    return currentComponent.getName();
                else
                    return currentComponent.getProperty(keys[row-1]);
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
                currentComponent.setProperty(keys[row-1], (String) val);
            robot.update();
        }
    }
}
