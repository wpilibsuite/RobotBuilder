/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.palette;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.lang.reflect.Method;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A transfer handler for that wraps the default transfer handler of Pallette.
 *
 * @author Alex Henning
 */
class PaletteTreeTransferHandler extends TransferHandler {
    private TransferHandler delegate;

    public PaletteTreeTransferHandler(TransferHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        return delegate.canImport(comp, transferFlavors);
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return false;
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        JTree tree = (JTree) c;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
        if (node.isLeaf()) {
            try {
                Method method = delegate.getClass().getDeclaredMethod("createTransferable", JComponent.class);
                method.setAccessible(true);
                return (Transferable) method.invoke(delegate, c);
            } catch (Exception e) {
                return super.createTransferable(c);
            }
        } else {
            return null;
        }
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent event, int action) {
        delegate.exportAsDrag(comp, event, action);
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        try {
            Method method = delegate.getClass().getDeclaredMethod("exportDone", JComponent.class, Transferable.class, int.class);
            method.setAccessible(true);
            method.invoke(delegate, source, data, action);
        } catch (Exception e) {
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
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
        return delegate.importData(support);
    }
    
}
