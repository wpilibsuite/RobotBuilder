
package robotbuilder.data.properties;

import java.util.Arrays;
import java.util.Optional;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import robotbuilder.Utils;

/**
 *
 * @author Sam Carlberg
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ParameterSetProperty extends ListProperty<ParameterSet> {

    @Override
    public ParameterSetProperty copy() {
        return Utils.deepCopy(this);
    }

    /**
     * Gets an {@code Optional} containing the parameter set with the given
     * name. If no such set exists, the {@code Optional} is empty.
     *
     * @param name the name of the parameter set to get
     * @return
     */
    public Optional<ParameterSet> get(@NonNull String name) {
        return getValue().stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst();
    }

    @Override
    public Object getDisplayValue() {
        return Utils.substring(
                Arrays.toString(getValue().stream()
                        .map(ParameterSet::getName)
                        .toArray(String[]::new)),
                1, -1);
    }

    @Override
    public boolean isValid() {
        return super.isValid()
                && getValue().stream().allMatch(ParameterSet::isValid);
    }

    @Override
    public String getErrorMessage() {
        return "Invalid parameter presets for " + component.getName();
    }

    @Override
    public boolean isEditable() {
        return super.isEditable()
                && !Utils.getParametersProperty(component).getValue().isEmpty();
    }

}
