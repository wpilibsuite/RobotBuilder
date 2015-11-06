
package robotbuilder.robottree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;

import java.io.IOException;

import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.TreePath;

import lombok.SneakyThrows;

import robotbuilder.data.PaletteComponent;
import robotbuilder.data.RobotComponent;
import robotbuilder.palette.Palette;

/**
 * A transfer handler for that wraps the default transfer handler of RobotTree.
 *
 * @author Alex Henning
 * @author Sam Carlberg
 */
class TreeTransferHandler extends TransferHandler {

    private TransferHandler delegate;
    private RobotTree robotTree;

    public TreeTransferHandler(TransferHandler delegate, RobotTree robotTree) {
        this.delegate = delegate;
        this.robotTree = robotTree;
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        return delegate.canImport(comp, transferFlavors);
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }
        support.setShowDropLocation(true);
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath path = dl.getPath();
        if (path == null) {
            return false;
        }
        RobotComponent target = (RobotComponent) path.getLastPathComponent();
        if (support.getTransferable().isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String data;
            try {
                data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                return false;
            }
            PaletteComponent base = Palette.getInstance().getItem(data);
            assert base != null; // TODO: Handle more gracefully
            return target.supports(base);
        } else if (support.getTransferable().isDataFlavorSupported(RobotTree.ROBOT_COMPONENT_FLAVOR)) {
            RobotComponent data;
            try {
                data = (RobotComponent) support.getTransferable().getTransferData(RobotTree.ROBOT_COMPONENT_FLAVOR);
            } catch (UnsupportedFlavorException e) {
                System.out.println("UnsupportedFlavor");
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException");
                return false;
            }
            if (dl.getChildIndex() == -1
                && target.getChildren().contains(data)) {
                // Cannot drag something into its own parent
                // (This allows for reordering)
                return false;
            }
            Set<String> invalid = new HashSet();
            invalid.add("Robot");
            invalid.add("Subsystems");
            invalid.add("OI");
            invalid.add("Commands");
            if (data == null) {
                return false;
            }
            if (invalid.contains(data.getBase().getType())) {
                return false;
            }
            return target.supports(data);
        } else {
            System.out.println("Unsupported flavor. The flavor you have chosen is not sufficiently delicious.");
            return false;
        }
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        return new Transferable() {
            DataFlavor[] flavors = {RobotTree.ROBOT_COMPONENT_FLAVOR};

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor df) {
                for (DataFlavor flavor : flavors) {
                    if (flavor.equals(df)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Object getTransferData(DataFlavor df) {
                return robotTree.getDndData();
            }
        };
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent event, int action) {
        delegate.exportAsDrag(comp, event, action);
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        robotTree.update();
        robotTree.selectEditingComponent();
    }

    @Override
    public int getSourceActions(JComponent c) {
        //return COPY_OR_MOVE;
        return delegate.getSourceActions(c);
    }

    @Override
    public Icon getVisualRepresentation(Transferable t) {
        return delegate.getVisualRepresentation(t);
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        return delegate.importData(comp, t);
    }

    /**
     * Handles DnD from the palette to the tree or reordering of components in
     * the tree.
     */
    @Override
    @SneakyThrows
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath path = dl.getPath();
        int childIndex = dl.getChildIndex();
        if (childIndex == -1) {
            childIndex = robotTree.tree.getModel().getChildCount(path.getLastPathComponent());
        }
        RobotComponent parentNode = (RobotComponent) path.getLastPathComponent();
        RobotComponent newNode;

        if (support.getTransferable().isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String data;
            try {
                data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException e) {
                System.out.println("UFE");
                return false;
            } catch (IOException e) {
                System.out.println("IOE");
                return false;
            }
            PaletteComponent base = Palette.getInstance().getItem(data);
            if (base == null) {
                // very unlikely, but notify the user anyway
                JOptionPane.showMessageDialog(robotTree, "A robot component could not be found for the pallet item " + base, "", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            newNode = new RobotComponent(robotTree.getDefaultComponentName(base, ((RobotComponent) parentNode).getSubsystem()), base, robotTree);
        } else if (support.getTransferable().isDataFlavorSupported(RobotTree.ROBOT_COMPONENT_FLAVOR)) {
            try {
                newNode = (RobotComponent) support.getTransferable().getTransferData(RobotTree.ROBOT_COMPONENT_FLAVOR);
            } catch (UnsupportedFlavorException | IOException e) {
                return false;
            }
        } else {
            return false;
        }

        if (!parentNode.getChildren().contains(newNode)
            && robotTree.getComponentByName(newNode.getFullName()) != null) {
            // If a component is dragged from one folder to another (e.g. between subsystems),
            // DnD will not remove it from the tree, so we have to do it manually
            robotTree.delete(newNode);
            robotTree.update();
        }

        robotTree.treeModel.insertNodeInto(newNode, parentNode, childIndex);
        robotTree.treeModel.reload(parentNode); // reloads the tree without reverting to the root
        robotTree.update();

        robotTree.tree.makeVisible(path.pathByAddingChild(newNode));

        robotTree.selectRobotComponent(newNode);
        robotTree.tree.setSelectionPath(path.pathByAddingChild(newNode));
        robotTree.properties.setCurrentComponent(newNode);
        robotTree.properties.setEditName();

        robotTree.tree.scrollRectToVisible(robotTree.tree.getPathBounds(path.pathByAddingChild(newNode)));
        robotTree.takeSnapshot();
        return true;
    }
}
