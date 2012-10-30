/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import robotbuilder.MainFrame;
import robotbuilder.palette.Palette;
import robotbuilder.data.PaletteComponent;
import robotbuilder.data.RobotComponent;

/**
 * A transfer handler for that wraps the default transfer handler of RobotTree.
 *
 * @author Alex Henning
 */
class TreeTransferHandler extends TransferHandler {
    private TransferHandler delegate;

    public TreeTransferHandler(TransferHandler delegate) {
        this.delegate = delegate;
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
            } catch (UnsupportedFlavorException e) {
                System.out.println("UFE");
                return false;
            } catch (IOException e) {
                System.out.println("IOE");
                return false;
            }
            PaletteComponent base = Palette.getInstance().getItem(data);
            assert base != null; // TODO: Handle more gracefully
            // TODO: Handle more gracefully
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
            System.out.println("Unsupported flavor. The flavor you have chosen is no sufficiently delicious.");
            return false;
        }
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        return new Transferable() {
            DataFlavor[] flavors = {RobotTree.ROBOT_COMPONENT_FLAVOR};
            Object data = ((JTree) c).getSelectionPath().getLastPathComponent();

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
            public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
                //                    System.out.print("Transfer data: "+data);
                return data;
            }
        };
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent event, int action) {
        delegate.exportAsDrag(comp, event, action);
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        MainFrame.getInstance().getCurrentRobotTree().update();
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

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        RobotTree robottree = MainFrame.getInstance().getCurrentRobotTree();
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath path = dl.getPath();
        int childIndex = dl.getChildIndex();
        if (childIndex == -1) {
            childIndex = robottree.tree.getModel().getChildCount(path.getLastPathComponent());
        }
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        DefaultMutableTreeNode newNode;
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
            assert base != null; // TODO: Handle more gracefully
            // TODO: Handle more gracefully
            newNode = new RobotComponent(robottree.getDefaultComponentName(base, ((RobotComponent) parentNode).getSubsystem()), base, robottree);
        } else if (support.getTransferable().isDataFlavorSupported(RobotTree.ROBOT_COMPONENT_FLAVOR)) {
            try {
                newNode = (RobotComponent) support.getTransferable().getTransferData(RobotTree.ROBOT_COMPONENT_FLAVOR);
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
        robottree.treeModel.insertNodeInto(newNode, parentNode, childIndex);
        robottree.treeModel.reload(parentNode); // reloads the tree without reverting to the root
        // reloads the tree without reverting to the root
        robottree.update();
        robottree.tree.makeVisible(path.pathByAddingChild(newNode));
        
        robottree.tree.setSelectionPath(path.pathByAddingChild(newNode));
        robottree.properties.setCurrentComponent(newNode);
        robottree.properties.setEditName();
        
        robottree.tree.scrollRectToVisible(robottree.tree.getPathBounds(path.pathByAddingChild(newNode)));
        robottree.takeSnapshot();
        return true;
    }
    
}
