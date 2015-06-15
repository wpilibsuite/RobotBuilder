
package robotbuilder.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import robotbuilder.MainFrame;
import robotbuilder.Utils;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.CommandGroupEntry;

/**
 * A graph to edit logic flow for command groups.
 *
 * @author Sam Carlberg
 */
@Getter
public class CommandGraph extends mxGraph {

    /**
     * Style for edges.
     */
    private static final StyleMap edgeStyle
            = new StyleMap()
            .set("strokeColor", "black")
            .set("exitX", 1)
            .set("exitY", 0.5)
            .set("entryX", 0)
            .set("entryY", 0.5)
            .set("rounded", true)
            .set("edgeStyle", "orthogonalEdgeStyle")
            .set("curved", true)
            .set("startArrow", "none")
            .set("startFill", false)
            .set("endArrow", "classic")
            .set("endFill", true);

    /**
     * Base style for vertices.
     */
    private static final StyleMap vertexBaseStyle
            = new StyleMap()
            .set("rounded", true)
            .set("fontSize", 11)
            .set("arcLength", 30)
            .set("shape", "label")
            .set("whiteSpace", "wrap");

    /**
     * Style for the "Start" block.
     */
    private static final StyleMap startStyle
            = new StyleMap()
            .set("fillColor", "#DDFFDD") // light green, almost white
            .set("strokeColor", "#AAAAAA");

    /**
     * Style for parallel commands.
     */
    private static final StyleMap parallelStyle
            = new StyleMap()
            .set("fillColor", "#CCFFFF") // light blue
            .set("strokeColor", "#AAAAAA");

    /**
     * Style for sequential commands.
     */
    private static final StyleMap sequentialStyle
            = new StyleMap()
            .set("fillColor", "#FFFFE3") // light yellow
            .set("strokeColor", "#AAAAAA");

    private static final StyleMap invalidParallelStyle
            = Utils.deepCopy(parallelStyle)
            .set("fillColor", "#CC6699") // red overlay on light blue
            .set("strokeWidth", 2)
            .set("strokeColor", "#CC0000"); // cherry red

    private static final StyleMap invalidSequentialStyle
            = Utils.deepCopy(sequentialStyle)
            .set("fillColor", "#CC6600") // red overlay on light yellow
            .set("strokeWidth", 2)
            .set("strokeColor", "#CC0000"); // cherry red

    /**
     * The default width of cells in the graph.
     */
    public static final int CELL_WIDTH = 90;

    /**
     * The default height of cells in the graph.
     */
    public static final int CELL_HEIGHT = 50;

    private CommandGraphLayout layout;
    private List<CommandGroupEntry> commands;
    private AbstractCell rootCell;

    // maps command cells to their commands
    private Map<CommandGroupEntry, CommandCell> cellMap = new HashMap<>();

    private mxIEventListener save = (sender, event) -> save();
    private mxIEventListener remap = (sender, event) -> remapCells(event.getProperty("cells"));

    @SneakyThrows
    public CommandGraph(RobotComponent commandGroup) {
        setCellsCloneable(true);
        setCloneInvalidEdges(false);
        setAllowDanglingEdges(false);
        setAllowLoops(false);
        setKeepEdgesInBackground(true);
        setDisconnectOnMove(false);
        setResetEdgesOnMove(true);
        setResetEdgesOnConnect(true);
        setResetEdgesOnResize(true);

        layout = new CommandGraphLayout(this);
        commands = (List<CommandGroupEntry>) commandGroup.getProperty("Commands").getValue();

        addListener(mxEvent.CELLS_ADDED, remap);

        mxStylesheet styles = getStylesheet();
        styles.getDefaultEdgeStyle().putAll(edgeStyle);
        styles.getDefaultVertexStyle().putAll(vertexBaseStyle);
        styles.putCellStyle("terminal", startStyle);
        styles.putCellStyle("parallel", parallelStyle);
        styles.putCellStyle("sequential", sequentialStyle);
        styles.putCellStyle("parallel-invalid", invalidParallelStyle);
        styles.putCellStyle("sequential-invalid", invalidSequentialStyle);

        mxGraphTransferable.enableImageSupport = false; // fixes "Data translation failed: not an image format" error on OSX

        rootCell = (StartCell) insertVertex(getDefaultParent(), null, "Start", 0, 0, CELL_WIDTH, CELL_HEIGHT, "terminal");

        // Add nodes to the graph and connect them in a logical order
        // The layout manager will organize them in the graph -- this is just
        // to add them in the first place
        commands.forEach((command) -> {
            AbstractCell previous = cellFor(command.getPrevious());
            AbstractCell cell = cellFor(command);
            insertEdge(getDefaultParent(), null, "", previous, cell);
        });
        organizeCells();

        addListener(mxEvent.CELLS_ADDED, (s, e) -> {
            Object[] cells = (Object[]) e.getProperty("cells");
            Arrays.stream(cells)
                    .filter(AbstractCell.class::isInstance)
                    .map(AbstractCell.class::cast)
                    .forEach(c -> c.addListener(save));
        });

        // Set the previous command of the target cell
        addListener(mxEvent.CELL_CONNECTED, (s, e) -> {
            if (!(Boolean) e.getProperty("source")) {
                mxCell edge = (mxCell) e.getProperty("edge");
                mxCell source = (mxCell) edge.getSource();
                mxCell target = (mxCell) edge.getTarget();
                if (source instanceof CommandCell && target instanceof CommandCell) {
                    ((CommandCell) target).getValue().setPrevious(((CommandCell) source).getValue());
                }
            }
        });

        addListener(mxEvent.CELLS_REMOVED, (s, e) -> {
            Object[] cells = (Object[]) e.getProperty("cells");
            mxCell[] castCells = Arrays.copyOf(cells, cells.length, mxCell[].class);
            for (mxCell cell : castCells) {
                if (cell.isEdge()) {
                    Object target = cell.getTarget();
                    if (target instanceof CommandCell) {
                        ((CommandCell) target).removeFromPrevious();
                    }
                } else if (cell instanceof CommandCell) {
                    CommandCell c = (CommandCell) cell;
                    c.removeFromNext();
                }
            }
        });

        // save when:
        //  cells are added
        //  cells are removed
        //  cells are connected/disconnected
        //  a cell value changed
        addListener(mxEvent.CELLS_ADDED, save);
        addListener(mxEvent.CELLS_REMOVED, save);
        addListener(mxEvent.CELL_CONNECTED, save);
        model.addListener(mxEvent.LABEL_CHANGED, save);

        // repaint on remove
        addListener(mxEvent.CELLS_REMOVED, fullRepaintHandler);
        addListener(mxEvent.CELL_CONNECTED, fullRepaintHandler);
    }

    /**
     * Gets the commands from the graph and saves them to the command group
     * component.
     */
    public void save() {
        List<List<CommandCell>> allCells = getCells();
        // move sequential cells to the ends of blocks
        allCells.forEach(block -> {
            CommandCell first = block.get(0); // sequential cells are always at the start of the block
            if (first.getValue().isSequential()) {
                block.add(first); // add the cell to the end of the list
                block.remove(first); // remove the first instance and leave the one we just added
            }
        });
        commands.clear();
        allCells.stream()
                .flatMap(List::stream)
                .peek(cell -> {
                    // Make sure that the previous command points
                    // to the actual object, not some random copy
                    // (This should fix the weird duplicate command
                    // bug when saving to file)
                    Object prev = cell.getInputCell().getValue();
                    if (prev instanceof CommandGroupEntry) {
                        cell.getValue().setPrevious((CommandGroupEntry) prev);
                    }
                })
                .map(CommandCell::getValue)
                .forEachOrdered(commands::add);
        MainFrame.getInstance().getCurrentRobotTree().takeSnapshot();
    }

    /**
     * Remaps the commands for all the given cells to match the commands in the
     * robot tree.
     *
     * @param cells the cells to map
     */
    private void remapCells(@NonNull Object cells) {
        Arrays.stream((Object[]) cells)
                .filter(CommandCell.class::isInstance)
                .map(CommandCell.class::cast)
                .map(CommandCell::getValue)
                .forEach(this::remapValue);
    }

    /**
     * Remaps the given command to match the appropriate command in the robot
     * tree.
     *
     * @param command the command to map
     */
    private void remapValue(@NonNull CommandGroupEntry command) {
        RobotComponent matchingComponent = MainFrame.getInstance().getCurrentRobotTree().getComponentByName(command.getCommand().getName());
        if (matchingComponent != null) {
            command.setCommand(matchingComponent.getModel());
            command.setHasMatch(true);
        } else {
            command.setHasMatch(false);
        }
    }

    @Override
    public String getToolTipForCell(Object cell) {
        if (cell instanceof CommandCell) {
            CommandCell c = (CommandCell) cell;
            if (!c.getValue().isValid()) {
                return "Invalid parameters:" + c.getValue().getParameters().getValue().stream()
                        .filter(p -> !p.isValid())
                        .map(p -> "\n" + p.getName())
                        .reduce("", String::concat);
            }
        }
        return "";
    }

    /**
     * Gets a list of all the cells in the graph. This is arranged in blocks,
     * where each block is a list of commands that begin execution at the same
     * time.
     * <p>
     *
     * For example, if the graph were structured like:
     * <pre><code>
     * start -> A -> D
     *        ∟ B  ∟ E
     *        ∟ C
     * </code></pre> this method would return
     * <pre><code>
     * [[A, B, C], [D, E]]
     * </code></pre>
     */
    public List<List<CommandCell>> getCells() {
        return getCellsAfter(rootCell);
    }

    /**
     * Gets a list of all the cells connected, directly or indirectly, to an
     * output of the given root cell. This is arranged in blocks, where each
     * block is a list of commands that begin execution at the same time.
     */
    private List<List<CommandCell>> getCellsAfter(AbstractCell<?> root) {
        List<List<CommandCell>> list = new ArrayList<>();
        List<CommandCell> children = root.getNextCommands();
        if (!children.isEmpty()) {
            list.add(children);
            children.stream()
                    .filter(c -> c.getValue().isSequential())
                    .map(this::getCellsAfter)
                    .forEach(list::addAll);
        }
        return list;
    }

    @Override
    public boolean isCellEditable(Object cell) {
        return cell instanceof CommandCell && super.isCellEditable(cell);
    }

    /**
     * Gets a cell for the given command. If a cell for the command does not
     * already exist in the graph, a new cell is created and added to the graph
     * and returned. If the given command is null, returns the start cell.
     *
     * @param command the command to get a cell for
     * @return the {@code AbstractCell} corresponding to the given command
     */
    public AbstractCell<?> cellFor(CommandGroupEntry command) {
        return cellFor(command, 0, 0);
    }

    /**
     * Gets a cell for the given command. If a cell for the command does not
     * already exist in the graph, a new cell is created and added to the graph
     * at the given point and returned. If the given command is null, returns
     * the start cell.
     *
     * @param command the command to get a cell for
     * @return the {@code AbstractCell} corresponding to the given command
     */
    public AbstractCell<?> cellFor(CommandGroupEntry command, int x, int y) {
        if (command == null) {
            return rootCell;
        }
        if (!cellMap.containsKey(command)) {
            cellMap.put(command, (CommandCell) insertVertex(getDefaultParent(), "", command, x, y, CELL_WIDTH, CELL_HEIGHT, command.getOrder().toLowerCase()));
        }
        return cellMap.get(command);
    }

    @Override
    @SuppressWarnings("element-type-mismatch")
    public AbstractCell<?> createVertex(Object parent, String id, Object value, double x, double y, double width, double height, String style, boolean relative) {
        if (cellMap.containsKey(value)) {
            return cellMap.get(value);
        }
        mxGeometry geometry = new mxGeometry(x, y, width, height);
        geometry.setRelative(relative);

        AbstractCell<?> cell;
        if (value instanceof CommandGroupEntry) {
            CommandGroupEntry entry = (CommandGroupEntry) value;
            if (entry.isValid()) {
                cell = new CommandCell(entry, geometry, style);
            } else {
                cell = new CommandCell(entry, geometry, style + "-invalid");
            }
        } else if (value instanceof String) {
            cell = new StartCell((String) value, geometry, style);
        } else {
            //TODO error (couldn't make a cell)
            return null;
        }
        cell.setId(id);
        cell.addListener(save);
        return cell;
    }

    /**
     * Organizes the cells in the graph according to the layout manager.
     */
    public void organizeCells() {
        layout.execute(getDefaultParent());
    }

    @Override
    public String convertValueToString(Object cell) {
        if (cell instanceof mxCell) {
            Object value = ((mxCell) cell).getValue();
            if (value instanceof CommandGroupEntry) {
                return ((CommandGroupEntry) value).getCommand().getName();
            }
        }
        return super.convertValueToString(cell);
    }

    @Override
    public boolean isCellDeletable(Object cell) {
        if (cell instanceof StartCell) {
            return false;
        }
        return super.isCellDeletable(cell);
    }

    @Override
    public boolean isValidSource(Object cell) {
        if (cell instanceof AbstractCell) {
            return ((AbstractCell) cell).isValidSource();
        }
        return false;
    }

    @Override
    public boolean isValidTarget(Object cell) {
        if (cell instanceof AbstractCell) {
            return ((AbstractCell) cell).isValidTarget();
        }
        return false;
    }

    @Override
    public boolean isValidConnection(Object source, Object target) {
        if (!(source instanceof AbstractCell && target instanceof CommandCell)) {
            return false;
        }
        AbstractCell<?> sourceCell = (AbstractCell<?>) source;
        CommandCell targetCell = (CommandCell) target;
        if (sourceCell.getNextCommands().contains(targetCell) || targetCell.getInputCell() == source) {
            return true;
        }
        boolean validSequence = !(sourceCell.hasSequentialOutput() && targetCell.getValue().isSequential());
        LoopDetector loopDetector = new LoopDetector(sourceCell, targetCell);
        return super.isValidConnection(source, target) && !loopDetector.hasLoop() && validSequence;
    }

    /**
     * Helper class to detect loops in the graph.
     */
    private static class LoopDetector {

        /**
         * List of cells visited by the getRoot() method.
         */
        private final List<AbstractCell<?>> visited = new ArrayList<>();

        /**
         * The cell to check for being in a loop.
         */
        private final AbstractCell<?> source;

        /**
         * Creates a LoopDetector instance that checks if the two given cells
         * are in a loop (i.e. each is an ancestor of the other).
         */
        public LoopDetector(AbstractCell<?> source, AbstractCell<?> target) {
            this.source = source;
            visited.add(target);
        }

        /**
         * Checks if the source cell is in a loop.
         */
        public boolean hasLoop() {
            return getRoot(source) == null;
        }

        private AbstractCell<?> getRoot(AbstractCell<?> source) {
            if (source.getInputCell() != null) {
                AbstractCell<?> previous = source.getInputCell();
                if (previous != null) {
                    if (visited.contains(previous)) {
                        // we've already visited this cell,
                        // so there's a loop and therefore no root
                        return null;
                    }
                    visited.add(previous);
                    return getRoot(previous);
                }
            }
            return source;
        }

    }

}
