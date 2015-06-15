
package robotbuilder.graph;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.util.mxRectangle;

import java.util.List;

import robotbuilder.data.CommandGroupEntry;

/**
 * Layout manager for a CommandGraph.
 *
 * @author Sam Carlberg
 */
public class CommandGraphLayout extends mxGraphLayout {

    public static final int EDGE_SPACE = 20;
    public static final int H_DISTANCE = 30; // edge-to-edge horizontal distance, pixels
    public static final int V_DISTANCE = 0; // edge-to-edge verical distance, pixels
    public static final int V_GAP = 10; // vertical gap between parallel and sequential commands

    public CommandGraphLayout(CommandGraph graph) {
        super(graph);
    }

    @Override
    public void execute(Object parent) {
        super.execute(parent);
        layout(((CommandGraph) graph).getRootCell(), ((CommandGraph) graph).getCells());
    }

    private void layout(AbstractCell root, List<List<CommandCell>> blocks) {

        // the command added just prior to the current one
        CommandGroupEntry previousCommand = null;

        // the width of the widest cell in the previous block
        mxRectangle lastWidest = getVertexBounds(root);
        // the size of the previous cell
        mxRectangle previousSize = lastWidest;

        // x,y coordinates for vertices to be placed
        int x = EDGE_SPACE;
        int y = EDGE_SPACE;

        moveCell(root, x, y);

        for (List<CommandCell> commandCells : blocks) {
            x += lastWidest.getWidth() + H_DISTANCE;
            for (int i = 0; i < commandCells.size(); i++) {
                CommandCell cell = commandCells.get(i);
                CommandGroupEntry command = cell.getValue();
                if (i == 0) { // may be sequential or parallel
                    y = EDGE_SPACE;
                } else { // guaranteed to be parallel
                    // move the command down a bit to make a gap between
                    // sequential and parallel commands in the same block
                    if (i == 1 && previousCommand != null && previousCommand.isSequential()) {
                        y += V_GAP;
                    }
                    y += previousSize.getHeight() + V_DISTANCE;
                }
                moveCell(cell, x, y);
                previousCommand = command;
                previousSize = getVertexBounds(cell);
                if (command.isSequential() || previousSize.getWidth() > lastWidest.getWidth()) {
                    lastWidest = previousSize;
                }
            }
            previousSize = new mxRectangle(0, 0, 0, 0);
        }

    }

    @Override
    public void moveCell(Object node, double x, double y) {
        int currentX = (int) graph.getCellBounds(node).getX();
        int currentY = (int) graph.getCellBounds(node).getY();
        graph.cellsMoved(new Object[]{node}, x - currentX, y - currentY, false, false);
    }

}
