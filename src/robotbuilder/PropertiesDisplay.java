
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
    
    public PropertiesDisplay() {
        setLayout(new BorderLayout());
	propTableModel = new PropertiesTableModel();
	propTable = new JTable(propTableModel);
        add(propTable, BorderLayout.CENTER);
    }

    void setCurrentComponent(DefaultMutableTreeNode node) {
	System.out.println("Current component is: " + node);
        currentComponent = (RobotComponent) node.getUserObject();
        String[] type = {};
        keys = currentComponent.getProperties().keySet().toArray(type);
//        this.repaint();
        this.updateUI();
    }
    
    class PropertiesTableModel extends AbstractTableModel {

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
                if (row == 0) return "Name";
                else return currentComponent.getProperties().get(keys[row-1]).getName();
            } else {
                if (row == 0) return currentComponent.getName();
                else return currentComponent.getProperties().get(keys[row-1]).getDefault();
            }
	}
    }
}
