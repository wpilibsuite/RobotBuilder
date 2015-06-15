
package robotbuilder.graph;

import com.mxgraph.model.mxGeometry;

/**
 * A special cell that takes no inputs, cannot be deleted, and does not contain
 * a command. Only one of these should be in any given {@link CommandGraph}.
 *
 * @author Sam Carlberg
 */
public class StartCell extends AbstractCell<String> {

    public StartCell(String value, mxGeometry geometry, String style) {
        super(value, geometry, style);
    }

    @Override
    public boolean isValidSource() {
        return true;
    }

    @Override
    public boolean isValidTarget() {
        return false;
    }

}
