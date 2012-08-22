/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.robottree;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import robotbuilder.MainFrame;
import robotbuilder.Palette;
import robotbuilder.data.PaletteComponent;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author alex
 */
public class RightClickMouseAdapter extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            // Right click only
            JTree tree = MainFrame.getInstance().getCurrentRobotTree().tree;
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            tree.setSelectionPath(path);
            Rectangle bounds = tree.getUI().getPathBounds(tree, path);
            if (bounds != null && bounds.contains(e.getX(), e.getY())) {
                final RobotComponent component = (RobotComponent) path.getLastPathComponent();
                JPopupMenu menu = generatePopupMenu(component);
                menu.show(tree, bounds.x, bounds.y + bounds.height);
            }
        }
    }

    private JPopupMenu generatePopupMenu(RobotComponent component) {
        JPopupMenu menu = new JPopupMenu();
        
        for (String type : component.getBase().getSupports().keySet()) {
            menu.add(generateMenu(new JMenu("Add "+type), type, component));
        }
        
        if (isDeletable()) {
            if (menu.getSubElements().length > 0) {
                menu.addSeparator();
            }
            menu.add(new DeleteItemAction("Delete", component));
        }
                
        return menu;
    }

    private JMenu generateMenu(JMenu menu, String type, RobotComponent parent) {
        for (PaletteComponent component : Palette.getInstance().getPaletteComponents()) {
            if (type.equals(component.getType())) {
                menu.add(new AddItemAction(component.getName(), parent, component));
            }
        }
        
        return menu;
    }
    
    private boolean isDeletable() {
        return true; // FIXME: Base off of whether or not the component is deletable.
    }
}
