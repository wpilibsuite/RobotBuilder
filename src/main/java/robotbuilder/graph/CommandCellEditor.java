
package robotbuilder.graph;

import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.view.mxCellState;

import java.util.EventObject;

import robotbuilder.Utils;
import robotbuilder.data.CommandGroupEntry;

/**
 * Implementation of {@link mxCellEditor} for editing command cells.
 *
 * @author Sam Carlberg
 */
public class CommandCellEditor extends mxCellEditor {

    private String originalOrder;
    private CommandCellEditorDialog dialog;

    public CommandCellEditor(CommandGraphComponent graphComponent) {
        super(graphComponent);
        this.graphComponent = graphComponent;
    }

    private CommandCell editingCell() {
        return (CommandCell) editingCell;
    }

    @Override
    public void startEditing(Object cell, EventObject evt) {
        // only edit command cells
        if (!(cell instanceof CommandCell)) {
            stopEditing(true);
            return;
        }
        // stop editing the current cell
        if (editingCell != null) {
            stopEditing(true);
        }

        mxCellState state = graphComponent.getGraph().getView().getState(cell);

        if (state != null) {
            editingCell = cell;
            trigger = evt;
            originalOrder = ((CommandCell) cell).getValue().getOrder();
            dialog = new CommandCellEditorDialog((CommandGraph) graphComponent.getGraph(), Utils.deepCopy(editingCell()));
            dialog.setVisible(true);
            // the dialog is on the same thread, so this will execute after the dialog is closed
            stopEditing(!dialog.didSave());
        }
    }

    @Override
    public void stopEditing(boolean cancel) {
        if (editingCell != null) {
            CommandCell cell = (CommandCell) editingCell;
            editingCell = null;

            if (cancel) {
                mxCellState state = graphComponent.getGraph().getView().getState(cell);
                graphComponent.redraw(state);
            } else {
                CommandGroupEntry newValue = dialog.getCommand();

                boolean orderChanged = !newValue.getOrder().equals(originalOrder);

                if (orderChanged) {
                    AbstractCell<?> input = cell.getInputCell();
                    if (newValue.isSequential()
                            && input != null
                            && input.hasSequentialOutput()
                            && input.getNextCommands().get(0) != cell) {
                        graphComponent.getGraph().removeCells(cell.getIncomingEdges().toArray());
                    }

                    if (newValue.isParallel()) {
                        graphComponent.getGraph().removeCells(cell.getOutgoingEdges().toArray());
                    }
                }
                if (newValue.isValid()) {
                    graphComponent.getGraph().setCellStyle(newValue.getOrder().toLowerCase(), new Object[]{cell});
                } else {
                    graphComponent.getGraph().setCellStyle(newValue.getOrder().toLowerCase() + "-invalid", new Object[]{cell});
                }
                EventObject evt = trigger;
                trigger = null;
                graphComponent.labelChanged(cell, newValue, evt);
            }

            graphComponent.requestFocusInWindow();
            originalOrder = null;
        }
    }

}
