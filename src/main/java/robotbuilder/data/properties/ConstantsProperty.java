
package robotbuilder.data.properties;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import robotbuilder.data.RobotComponent;

/**
 *
 * @author Sam Carlberg
 */
@NoArgsConstructor
public class ConstantsProperty extends ListProperty<ValuedParameterDescriptor> {

    public ConstantsProperty(String name, List<ValuedParameterDescriptor> defaultValue, String[] validators, RobotComponent component) {
        super(name, defaultValue, validators, component);
    }

    @Override
    public ConstantsProperty copy() {
        ConstantsProperty cp = new ConstantsProperty(name, defaultValue, validators, component);
        cp.setValue(getValue());
        return cp;
    }

    public ValuedParameterDescriptor getConstantByName(@NonNull String name) {
        for (ValuedParameterDescriptor d : getValue()) {
            if (d.getName().equals(name)) {
                return d;
            }
        }
        return null;
    }

    @Override
    public boolean isValid() {
        if (value.isEmpty()) {
            return true;
        }
        List<String> names = new ArrayList<>();
        for (ParameterDescriptor param : value) {
            // check if the parameter itself is valid
            if (!param.isValid()) {
                return false;
            }
            // check for multiple ocurrences of a name
            if (names.contains(param.getName())) {
                return false;
            }
            names.add(param.getName());
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "Type mismatch, empty value, or constants with multiple names";
    }

    @Override
    public Object getDisplayValue() {
        if (value == null) {
            value = defaultValue;
        }
        Set<String> names = new LinkedHashSet<>();
        value.forEach(c -> names.add(c.getName()));
        return names.toString().substring(1, names.toString().length() - 1);
    }

}
