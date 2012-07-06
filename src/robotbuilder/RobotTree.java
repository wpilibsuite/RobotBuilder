
package robotbuilder;

import robotbuilder.data.RobotComponent;
import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import robotbuilder.data.PaletteComponent;

/**
 *
 * @author brad
 * RobotTree is the tree representation of the robot map. It will contain
 * nodes that represent the various components that can be used to build
 * the robot. This is constructed by the user by dragging palette items to the tree.
 */
class RobotTree extends JPanel implements TreeSelectionListener {
    
    private JTree tree;
    private DefaultTreeModel treeModel;
    private PropertiesDisplay properties;
    
    /** Names used by components during name auto-generation */
    private Set<String> usedNames = new HashSet<String>();
    
    public RobotTree(PropertiesDisplay properties) {
	this.properties = properties;
        setLayout(new BorderLayout());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Team190Robot");
        DefaultMutableTreeNode motors = new DefaultMutableTreeNode("Motors");
        motors.add(new DefaultMutableTreeNode("Left Front"));
        motors.add(new DefaultMutableTreeNode("Right Front"));
        root.add(motors);
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
	tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	tree.addTreeSelectionListener(this);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        add(new JScrollPane(tree), BorderLayout.CENTER);
        tree.setTransferHandler(new TransferHandler() {
            public boolean importData(TransferSupport support) {
                if (!canImport(support))
                    return false;
                JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
                TreePath path = dl.getPath();
                int childIndex = dl.getChildIndex();
                String data;
                try {
                    data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                }
                catch (UnsupportedFlavorException e) {
                    return false;
                }
                catch (IOException e) {
                    return false;
                }
                System.out.println("Data: " + data);
                if (childIndex == -1) {
                    childIndex = tree.getModel().getChildCount(path.getLastPathComponent());
                }
                PaletteComponent base = PaletteComponent.getComponent(data);
                assert data != null; // TODO: Handle more gracefully
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new RobotComponent(getDefaultComponentName(base), base));
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)path.getLastPathComponent();
                treeModel.insertNodeInto(newNode, parentNode, childIndex);
                tree.makeVisible(path.pathByAddingChild(newNode));
                tree.scrollRectToVisible(tree.getPathBounds(path.pathByAddingChild(newNode)));
                return true;
            }
            
            public boolean canImport(TransferSupport support) {
                if (!support.isDrop()) return false;
                support.setShowDropLocation(true);
                if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    System.out.println("Only string is supported");
                    return false;
                }
                JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
                TreePath path = dl.getPath();
                if (path == null) return false;
                return true;
            }
        });
    }
    
    /**
     * @param component The type of component to generate a default name for.
     * @return The default name.
     */
    private String getDefaultComponentName(PaletteComponent componentType) {
        int i = 0;
        String name;
        while (true) {
            i++;
            name = componentType.toString() + " " + i;
            if (!usedNames.contains(name)) {
                usedNames.add(name);
                return name;
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent tse) {
	DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       tree.getLastSelectedPathComponent();

	if (node == null) return;
	if (node instanceof DefaultMutableTreeNode)
	    properties.setCurrentComponent(node);

    }
}
