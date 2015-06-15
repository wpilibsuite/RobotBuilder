
package robotbuilder.data;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

import robotbuilder.MainFrame;
import robotbuilder.data.properties.ParameterDescriptor;
import robotbuilder.data.properties.ParametersProperty;
import robotbuilder.data.properties.Validatable;
import robotbuilder.robottree.RobotTree;

/**
 *
 * @author Sam Carlberg
 */
@Data
@NoArgsConstructor
public class CommandGroupEntry implements Validatable, Serializable {

    private static final long serialVersionUID = -1811590357861066730L;

    // Strings because snakeyaml doesn't like enums
    public static final String SEQUENTIAL = "Sequential";
    public static final String PARALLEL = "Parallel";

    /**
     * The command that this command runs after.
     */
    private CommandGroupEntry previous = null;

    /**
     * The model for the command this contains.
     */
    private RobotComponentModel command;

    /**
     * The order this command runs in. Defaults to sequential.
     */
    private String order = SEQUENTIAL;

    /**
     * The parameters passed to the command.
     */
    private ParametersProperty parameters;

    /**
     * Flag for this entry having a matching command in the robot tree.
     */
    private boolean hasMatch = true;

    public CommandGroupEntry(RobotComponent command) {
        this.command = command.getModel();
        this.parameters = new ParametersProperty("ParametersProperty", new ArrayList<>(), null, command);
        this.parameters.matchUpWith((ParametersProperty) command.getProperty("Parameters"));
    }

    @Override
    public String getName() {
        return command.getName();
    }

    @Override
    public String toString() {
        return "CommandGroupEntry(name=" + getName() + ",order=" + order + ")";
    }

    public boolean isSequential() {
        return SEQUENTIAL.equals(order);
    }

    public boolean isParallel() {
        return PARALLEL.equals(order);
    }

    @Override
    public boolean isValid() {
        return hasMatch && parameters.getValue().stream().allMatch(ParameterDescriptor::isValid);
    }

    public void setOrder(String order) {
        if (SEQUENTIAL.equals(order) || PARALLEL.equals(order)) {
            this.order = order;
        } else {
            // maybe log it?
        }
    }

    /**
     * Matches the parameters of this command to the parameters of the command
     * in the tree corresponding to this one. If no command in the tree has the
     * same name as this one, there is no match and this method returns
     * {@code false}.
     *
     * @return true if the parameters were matched, false if they weren't
     */
    public boolean matchToTree() {
        RobotTree robot = MainFrame.getInstance().getCurrentRobotTree();
        RobotComponent commandInTree = robot.getComponentByName(command.getName());
        if (commandInTree == null) {
            return false;
        }
        parameters.matchUpWith((ParametersProperty) commandInTree.getProperty("Parameters"));
        return true;
    }

}
