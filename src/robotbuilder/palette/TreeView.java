/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.palette;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import robotbuilder.MainFrame;
import robotbuilder.data.PaletteComponent;

/**
 *
 * @author alex
 */
public class TreeView extends JPanel implements TreeSelectionListener {
    Palette palette;
    JTree tree;

    public TreeView(Palette palette) {
        this.palette = palette;

        tree = new JTree(palette.getPaletteModel()) {
            @Override
            public String getToolTipText(MouseEvent e) {
                try {
                    TreePath path = getClosestPathForLocation(e.getX(), e.getY());
                    return ((PaletteComponent) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getHelp();
                } catch (ClassCastException ex) { // Ignore folders
                    return null;
                }
            }
        };
        tree.setRootVisible(false);
	tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setTransferHandler(new PaletteTreeTransferHandler(tree.getTransferHandler()));
        tree.setDragEnabled(true);
        ToolTipManager.sharedInstance().registerComponent(tree);
        tree.addTreeSelectionListener(this);
        
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        setLayout(new BorderLayout());
        add(tree, BorderLayout.CENTER);
    }

    @Override
    public void valueChanged(TreeSelectionEvent tse) {
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

	if (node == null) {
	    return;
	}
	if (node instanceof DefaultMutableTreeNode) {
            try {
                MainFrame.getInstance().setHelp(((PaletteComponent) node.getUserObject()).getHelpFile());
            } catch (ClassCastException ex) { /* Ignore folders */ }
	}
    }
}
