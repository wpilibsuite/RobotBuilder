
package robotbuilder.data.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import robotbuilder.data.RobotComponent;
import robotbuilder.utils.UniqueList;

/**
 *
 * @author Sam Carlberg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterSet implements Validatable {

    /**
     * The name of this parameter set.
     */
    private @NonNull String name;

    /**
     * A set containing the parameters this takes.
     */
    private @NonNull List<ValuedParameterDescriptor> parameters;

    /**
     * Creates an {@link Optional} containing a parameter set with the given
     * name and matches the parameters in the given command.
     *
     * If the given command doesn't have a "Parameters" property, the returned
     * {@code Optional} is null.
     *
     * @param name the name of the ParameterSet to create
     * @param command the command containing the created ParameterSet
     * @return
     */
    public static Optional<ParameterSet> from(@NonNull String name, @NonNull RobotComponent command) {
        ParametersProperty paramProp = (ParametersProperty) command.getProperty("Parameters");
        if (paramProp == null) {
            return Optional.empty();
        }

        ParameterSet set = new ParameterSet(name, new UniqueList<>());
        List<ParameterDescriptor> value = (List) paramProp.getValue();
        value.stream()
                .map(ValuedParameterDescriptor::new)
                .forEach(set.parameters::add);
        return Optional.of(set);
    }

    @Override
    public boolean isValid() {
        return !name.isEmpty() && parameters.stream().allMatch(Validatable::isValid);
    }

    public Object[] toArray() {
        List l = new ArrayList();
        l.add(name);
        parameters.stream()
                .map(ValuedParameterDescriptor::getValue)
                .forEach(l::add);
        return l.toArray();
    }

}
