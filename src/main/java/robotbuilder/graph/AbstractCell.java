
package robotbuilder.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;

/**
 * A subclass of {@link mxCell} that has better support for custom values and
 * for getting the inputs and outputs of the cell.
 *
 * @author Sam Carlberg
 */
public abstract class AbstractCell<T extends Serializable> extends mxCell implements Cloneable {

    public static final String VALUE_CHANGED = "valueChanged";
    private final transient mxEventSource eventSource = new mxEventSource();

    public AbstractCell(T value, mxGeometry geometry, String style) {
        super(value, geometry, style);
        setVertex(true);
        setConnectable(true);
    }

    /**
     * Checks if this cell is a valid source for an edge.
     *
     * @return true if this is a valid source, false otherwise
     */
    public abstract boolean isValidSource();

    /**
     * Checks if this cell is a valid target for an edge.
     *
     * @return true if this is a valid target, false otherwise
     */
    public abstract boolean isValidTarget();

    /**
     * Adds the given listener be notified of changes to the value in this cell.
     *
     * @param listener the listener to add
     */
    public final void addListener(mxEventSource.mxIEventListener listener) {
        eventSource.addListener(VALUE_CHANGED, listener);
    }

    /**
     * Removes the given listener from being notified of changes to the value in
     * this cell.
     *
     * @param listener the listener to remove
     */
    public final void removeListener(mxEventSource.mxIEventListener listener) {
        eventSource.removeListener(listener);
    }

    @Override
    public T getValue() {
        return (T) value;
    }

    /**
     * Sets the value of this cell and fires an event to notify any change
     * listeners.
     */
    @Override
    public void setValue(@NonNull Object value) {
        if (eventSource != null) {
            eventSource.fireEvent(new mxEventObject(VALUE_CHANGED,
                    "old", this.value,
                    "new", value));
        }
        this.value = (T) value;
    }

    /**
     * Checks if a cell after this one contains a sequential command.
     */
    public boolean hasSequentialOutput() {
        List<CommandCell> next = getNextCommands();
        return next.size() > 0 && next.get(0).getValue().isSequential();
    }

    /**
     * Gets a list of edges connected to this cell.
     */
    public List<mxCell> getEdges() {
        return (List) edges;
    }

    /**
     * Gets a list of edges coming into this cell. This should either be empty
     * or contain 1 element.
     */
    public List<mxCell> getIncomingEdges() {
        return edges.stream()
                .map(mxCell.class::cast)
                .filter(e -> e.getTarget() == this)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of edges originating from this cell.
     */
    public List<mxCell> getOutgoingEdges() {
        return edges.stream()
                .map(mxCell.class::cast)
                .filter(e -> e.getSource() == this)
                .collect(Collectors.toList());
    }

    /**
     * Gets the cell that inputs to this one, or null if no such cell exists.
     */
    public AbstractCell<?> getInputCell() { //TODO memoize?
        if (edges == null) {
            edges = new ArrayList<>();
            return null;
        }
        return (AbstractCell<?>) edges.stream()
                .map(mxCell.class::cast)
                .filter(e -> e.getTarget() == this)
                .map(mxCell::getSource)
                .findFirst().orElse(null);
    }

    /**
     * Gets a list of command cells that this cell directly outputs to.
     */
    public List<CommandCell> getNextCommands() { //TODO memoize?
        if (edges == null) {
            edges = new ArrayList<>();
            return new ArrayList<>();
        }
        return edges.stream()
                .map(mxCell.class::cast)
                .filter(e -> e.getSource() == this && e.getTarget() != null)
                .map(mxCell::getTarget)
                .map(CommandCell.class::cast)
                .sorted((c1, c2)
                        -> c1.getValue().isSequential()
                                ? Integer.MIN_VALUE // c1 is the sequential command
                                : c2.getValue().isSequential()
                                        ? Integer.MAX_VALUE // c2 is the sequential command
                                        : c1.getValue().getName().compareTo(c2.getValue().getName())) // both parallel, sort alphabetically
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(value=" + getValue() + ")";
    }

}
