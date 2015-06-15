
package robotbuilder;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import lombok.NonNull;

import robotbuilder.data.RobotComponent;
import robotbuilder.data.CommandGroupEntry;
import robotbuilder.graph.AbstractCell;
import robotbuilder.graph.CommandCell;
import robotbuilder.graph.CommandGraph;
import robotbuilder.graph.CommandGraphComponent;
import static robotbuilder.data.CommandGroupEntry.PARALLEL;
import static robotbuilder.data.CommandGroupEntry.SEQUENTIAL;
import static robotbuilder.graph.CommandGraph.CELL_HEIGHT;
import static robotbuilder.graph.CommandGraph.CELL_WIDTH;

/**
 *
 * @author Sam Carlberg
 * @see com.mxgraph.examples.swing.editor.BasicGraphEditor
 */
public class CommandGraphEditor extends BasicGraphEditor {

    // maps editors to their command groups
    private static final Map<RobotComponent, CommandGraphEditor> editorMap = new HashMap<>();

    private RobotComponent commandGroup;
    private CommandGraph graph;

    // JGraph stuff
    private CommandGraphComponent graphComponent;

    /**
     * Gets the editor for the given command group.
     */
    public static CommandGraphEditor editorFor(@NonNull RobotComponent commandGroup) {
        if (!editorMap.containsKey(commandGroup)) {
            editorMap.put(commandGroup, new CommandGraphEditor(commandGroup));
        }
        return editorMap.get(commandGroup);
    }

    private CommandGraphEditor(RobotComponent commandGroup) {
        super("", new CommandGraphComponent(commandGroup));
        this.commandGroup = commandGroup;
        this.graphComponent = (CommandGraphComponent) super.graphComponent;
        graph = graphComponent.getGraph();

        setLayout(new BorderLayout());
        add(graphComponent, BorderLayout.CENTER);

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mouseReleased(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);
                    Object cell = graphComponent.getCellAt(pt.x, pt.y);
                    if (cell instanceof CommandCell) {
                        JPopupMenu menu = addCellMenu((CommandCell) cell, pt);
                        menu.show(graphComponent, pt.x, pt.y);
                    } else if (cell == null) {
                        defaultPopupMenu(e.getPoint()).show(graphComponent, pt.x, pt.y);
                    }
                }
                e.consume();
            }

        });
    }

    /**
     * Base popup menu for adding sequential or parallel commands.
     *
     * @param cell the cell to add commands after (can be null)
     * @param pt the point to add cells at
     */
    private JPopupMenu baseAddCellMenu(CommandCell cell, Point pt) {
        JPopupMenu menu = new JPopupMenu("Add commands");
        JMenu seq = new JMenu("Add sequential...");
        JMenu par = new JMenu("Add parallel...");

        commandGroup.getRobotTree().getCommands().stream()
                .filter(c -> !commandGroup.equals(c))
                .sorted((c1, c2) -> c1.getName().compareTo(c2.getName()))
                .map(CommandGroupEntry::new)
                .forEachOrdered(c -> {
                    // action for adding the command sequentially
                    AbstractAction addSeq = new AbstractAction(c.getName()) {

                        @Override
                        public void actionPerformed(ActionEvent event) {
                            c.setOrder(SEQUENTIAL);
                            CommandCell v = (CommandCell) graph.insertVertex(graph.getDefaultParent(),
                                    null,
                                    Utils.deepCopy(c),
                                    pt.x, pt.y,
                                    CELL_WIDTH, CELL_HEIGHT,
                                    "sequential");

                            if (cell != null) {
                                if (cell.hasSequentialOutput()) {
                                    graph.insertEdge(graph.getDefaultParent(), null, null, v,
                                            cell.getNextCommands().stream()
                                            .filter(c -> c.getValue().isSequential())
                                            .findFirst().get());
                                    graph.removeCells(
                                            cell.getNextCommands().stream()
                                            .filter(c -> c.getValue().isSequential())
                                            .flatMap(c -> c.getEdges().stream())
                                            .filter(e -> e.getSource().equals(cell))
                                            .toArray(mxCell[]::new),
                                            true);
                                }
                                graph.insertEdge(graph.getDefaultParent(), null, null, cell, v);
                            }
                            graph.organizeCells();
                            graph.refresh();

                        }
                    };

                    // action for adding the command in parallel
                    AbstractAction addPar = new AbstractAction(c.getName()) {

                        @Override
                        public void actionPerformed(ActionEvent event) {

                            c.setOrder(PARALLEL);
                            CommandCell v = (CommandCell) graph
                            .insertVertex(graph.getDefaultParent(),
                                    null,
                                    Utils.deepCopy(c),
                                    pt.x, pt.y,
                                    CELL_WIDTH, CELL_HEIGHT,
                                    "parallel");
                            if (cell != null) {
                                graph.insertEdge(graph.getDefaultParent(), null, null, cell, v);
                            }
                            graph.organizeCells();
                            graph.refresh();

                        }
                    };

                    seq.add(addSeq);
                    par.add(addPar);
                });

        menu.add(seq);
        menu.add(par);

        return menu;
    }

    /**
     * Creates a menu for adding cells after the given cell and changing some of
     * its properties.
     */
    private JPopupMenu addCellMenu(CommandCell cell, Point pt) {
        JPopupMenu menu;
        if (cell.getValue().isSequential()) {
            menu = baseAddCellMenu(cell, pt);
            menu.add(new JPopupMenu.Separator());
        } else {
            menu = new JPopupMenu();
        }

        final String order = cell.getValue().getOrder();
        final String newOrder = order.equals(SEQUENTIAL) ? PARALLEL : SEQUENTIAL;
        menu.add(new AbstractAction("Set " + newOrder) {

            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractCell<?> input = cell.getInputCell();

                if (newOrder.equals(SEQUENTIAL)
                        && input != null
                        && input.hasSequentialOutput()
                        && input.getNextCommands().get(0) != cell) {
                    graph.removeCells(cell.getIncomingEdges().toArray());
                }

                if (newOrder.equals(PARALLEL)) {
                    graph.removeCells(cell.getOutgoingEdges().toArray());
                }
                if (cell.getStyle().equalsIgnoreCase(order)) {
                    graph.setCellStyle(newOrder.toLowerCase(), new Object[]{cell});
                } else {
                    graph.setCellStyle(newOrder.toLowerCase() + "-invalid", new Object[]{cell});
                }
                CommandGroupEntry newValue = Utils.deepCopy(cell.getValue());
                newValue.setOrder(newOrder);
                graphComponent.labelChanged(cell, newValue, new EventObject(CommandGraphEditor.this));

            }

        });

        menu.add(new AbstractAction("Reset size") {

            @Override
            public void actionPerformed(ActionEvent e) {
                mxGeometry g = cell.getGeometry();
                graph.getModel().setGeometry(cell, new mxGeometry(g.getX(), g.getY(), CELL_WIDTH, CELL_HEIGHT));
            }
        });

        return menu;
    }

    /**
     * Creates a menu for adding and organizing cells in the graph,
     */
    private JPopupMenu defaultPopupMenu(Point pt) {
        JPopupMenu menu = baseAddCellMenu(null, pt);

        menu.add(new JPopupMenu.Separator());

        menu.add(new AbstractAction("Organize") {

            @Override
            public void actionPerformed(ActionEvent e) {

                graph.organizeCells();

            }
        });
        return menu;
    }

    /**
     * Flags commands in the graph without matching components in the robot tree
     * as invalid.
     */
    public void refresh() {
        mxCell parent = (mxCell) graph.getDefaultParent();

        for (int i = 0; i < parent.getChildCount(); i++) {
            mxICell child = parent.getChildAt(i);
            if (child instanceof CommandCell) {
                CommandCell c = (CommandCell) child;
                if (c.getValue().matchToTree()) {
                    c.getValue().setHasMatch(true);
                } else {
                    c.getValue().setHasMatch(false);
                }
                if (c.getValue().isValid()) {
                    graph.setCellStyle(c.getValue().getOrder().toLowerCase(), new Object[]{c});
                } else {
                    graph.setCellStyle(c.getValue().getOrder().toLowerCase() + "-invalid", new Object[]{c});
                }
            }
        }
        graph.refresh();

    }

}
