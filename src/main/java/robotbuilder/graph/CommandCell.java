
package robotbuilder.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

import robotbuilder.data.CommandGroupEntry;

/**
 * A cell in a {@link CommandGraph} representing a single command or command
 * group in the command group being edited.
 *
 * @author Sam Carlberg
 */
public class CommandCell extends AbstractCell<CommandGroupEntry> {

    public CommandCell(CommandGroupEntry value, mxGeometry geometry, String style) {
        super(value, geometry, style);
    }

    public CommandCell(mxCell source) {
        this((CommandGroupEntry) source.getValue(), source.getGeometry(), source.getStyle());
        setId(source.getId());
        connectable = source.isConnectable();
        collapsed = source.isCollapsed();
    }

    @Override
    public boolean isValidSource() {
        return getValue().isSequential();
    }

    @Override
    public boolean isValidTarget() {
        return getInputCell() == null;
    }

    /**
     * Sets the previous command of the command in this cell to null.
     */
    public void removeFromPrevious() {
        getValue().setPrevious(null);
    }

    public void removeFromNext() {
        getNextCommands().stream().forEach(CommandCell::removeFromPrevious);
    }

    @Override
    public String toString() {
        return "CommandCell(" + getValue().getName() + ")";
    }

}
