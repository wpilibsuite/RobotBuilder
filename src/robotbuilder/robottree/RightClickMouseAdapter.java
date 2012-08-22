/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.robottree;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import robotbuilder.MainFrame;
import robotbuilder.palette.Palette;
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
        
        List<JMenu> submenus = new LinkedList<JMenu>();
        TreeModel model = Palette.getInstance().getPaletteModel();
        Enumeration children = ((DefaultMutableTreeNode)model.getRoot()).children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            JMenu submenu = generateMenu(new JMenu("Add "+child.getUserObject()), child, component);
            if (submenu.getSubElements().length > 0) {
                submenus.add(submenu);
            }
        }
        
        if (submenus.size() > 1) {
            for (JMenu submenu : submenus) {
                menu.add(submenu);
            }
        } else if (submenus.size() == 1) {
            for (Component element : submenus.get(0).getMenuComponents()) {
                menu.add(element);
            }
        }

        
        if (isDeletable(component)) {
            if (menu.getSubElements().length > 0) {
                menu.addSeparator();
            }
            menu.add(new DeleteItemAction("Delete", component));
        }
                
        return menu;
    }

    private JMenu generateMenu(JMenu menu, DefaultMutableTreeNode node, RobotComponent parent) {
        Enumeration children = node.children();
        while (children.hasMoreElements()) {
            PaletteComponent child = (PaletteComponent) ((DefaultMutableTreeNode) children.nextElement()).getUserObject();
            if (parent.supports(child)) {
                menu.add(new AddItemAction("Add "+child.getName(), parent, child));
            }
        }
        
        return menu;
    }
    
    private boolean isDeletable(RobotComponent component) {
        return component.getParent() != null && component.getParent().getParent() != null;
    }
}
