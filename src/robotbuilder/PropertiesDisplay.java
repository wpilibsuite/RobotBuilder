
package robotbuilder;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author brad
 */
class PropertiesDisplay extends JPanel {
    JTable propTable;
    TableModel propTableModel;
    
    public PropertiesDisplay() {
	propTableModel = new PropertiesTableModel();
	propTable = new JTable(propTableModel);
        add(propTable);
    }

    void setCurrentComponent(DefaultMutableTreeNode node) {
	System.out.println("Current component is: " + node);
    }
    
    class PropertiesTableModel extends AbstractTableModel {

	@Override
	public int getRowCount() {
	    return 0;
	}

	@Override
	public int getColumnCount() {
	    return 2;
	}

	@Override
	public Object getValueAt(int i, int i1) {
	    return null;
	}
    }
}
