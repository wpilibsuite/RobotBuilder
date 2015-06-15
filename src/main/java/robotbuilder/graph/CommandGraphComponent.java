
package robotbuilder.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.util.mxEvent;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import lombok.NonNull;
import lombok.SneakyThrows;

import robotbuilder.data.CommandGroupEntry;
import robotbuilder.data.RobotComponent;

/**
 * A Swing component displaying a {@link CommandGraph}.
 *
 * @author Sam Carlberg
 */
public class CommandGraphComponent extends mxGraphComponent {

    private final RobotComponent commandGroup;

    public CommandGraphComponent(@NonNull RobotComponent commandGroup) {
        super(new CommandGraph(commandGroup));
        this.commandGroup = commandGroup;
        setToolTips(true);
        addListener(mxEvent.LABEL_CHANGED, (s, e) -> ((CommandGraph) graph).save());
    }

    @Override
    protected TransferHandler createTransferHandler() {
        return new GraphTransferHandler();
    }

    @Override
    public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {
        // map the passed cells to CommandCells. This is safe because importCells is only called
        // when importing from DnD or another kind of transfer where we control what gets imported
        Object[] copy = Arrays.stream(cells).map(mxCell.class::cast).map(CommandCell::new).toArray();
        return super.importCells(copy, dx, dy, target, location);
    }

    @Override
    protected mxICellEditor createCellEditor() {
        return new CommandCellEditor(this);
    }

    @Override
    public CommandGraph getGraph() {
        return (CommandGraph) super.getGraph();
    }

    /**
     * Transfer handler for DnD from the RobotTree.
     */
    private class GraphTransferHandler extends mxGraphTransferHandler {

        private Point dropPoint = new Point(0, 0);

        @Override
        @SneakyThrows({UnsupportedFlavorException.class, IOException.class})
        public boolean canImport(TransferSupport support) {
            if (!support.isDrop()) {
                return false;
            }
            Object data = support.getTransferable().getTransferData(support.getDataFlavors()[0]); // No UFE or IOE will ever occur here
            if (commandGroup.equals(data)) {
                return false;
            }
            if (data instanceof RobotComponent) {
                RobotComponent comp = (RobotComponent) data;
                return !comp.getBaseType().equals("Commands") && comp.getBaseType().contains("Command");
            }
            return super.canImport(support);
        }

        @Override
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
            // can always import from the tree
            return true;
        }

        @Override
        @SneakyThrows(UnsupportedFlavorException.class)
        public boolean importData(TransferSupport support) {
            Object data;
            try {
                data = support.getTransferable().getTransferData(support.getDataFlavors()[0]); // No UFE will ever occur here
            } catch (IOException e) {
                Logger.getLogger(GraphTransferHandler.class.getName()).log(Level.WARNING, "IOException while trying to import into the command group editor. Did you try to paste something from the clipboard?");
                return false;
            }
            dropPoint = support.getDropLocation().getDropPoint();
            // moving cells around in the graph
            if (super.importData(support)) {
                return true;
            }
            if (!canImport(support)) {
                return false;
            }
            // import from tree
            if (data instanceof RobotComponent) {
                RobotComponent command = (RobotComponent) data;
                CommandGraph graph = (CommandGraph) getGraph();
                graph.cellFor(new CommandGroupEntry(command), dropPoint.x, dropPoint.y);
                repaint();
                return true;
            }
            return false;
        }

        @Override
        @SneakyThrows({UnsupportedFlavorException.class, IOException.class})
        public boolean importData(JComponent comp, Transferable t) {
            Object data = t.getTransferData(t.getTransferDataFlavors()[0]); // No UFE or IOE will ever occur here
            if (!canImport(new TransferSupport(comp, t))) {
                return false;
            }
            // moving cells around in the graph
            if (super.importData(comp, t)) {
                return true;
            }
            // import from tree
            if (data instanceof RobotComponent) {
                RobotComponent command = (RobotComponent) data;
                CommandGraph graph = (CommandGraph) getGraph();
                graph.cellFor(new CommandGroupEntry(command), dropPoint.x, dropPoint.y);
                graph.refresh();
                repaint();
                return true;
            }
            return false;
        }

    }

}
