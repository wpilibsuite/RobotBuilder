
package robotbuilder.robottree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import robotbuilder.data.RobotComponent;

/**
 *
 * @author alex
 * @author Sam Carlberg
 */
public class RobotTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        RobotComponent comp = (RobotComponent) value;

        if (comp.isValid()) {
            setForeground(Color.black);
        } else {
            setForeground(Color.red);
        }

        if (comp.supportsChildren() && leaf) {
            setIcon(getOpenIcon());
        }

        return this;
    }
}
