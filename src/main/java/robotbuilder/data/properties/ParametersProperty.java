
package robotbuilder.data.properties;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.NoArgsConstructor;

import robotbuilder.data.RobotComponent;

/**
 *
 * @author Sam Carlberg
 */
@NoArgsConstructor
public class ParametersProperty extends Property<List<? extends ParameterDescriptor>> {

    private List<? extends ParameterDescriptor> value = new ArrayList<>();

    public ParametersProperty(String name, List<? extends ParameterDescriptor> defaultValue, String[] validators, RobotComponent component) {
        super(name, defaultValue, validators, component);
        setValue(defaultValue);
    }

    /**
     * Matches up the parameters in this property with the parameters in another
     * property. This only works if the parameters in this property are
     * {@link ValuedParameterDescriptor ValuedParameterDescriptors}.
     *
     * <p>
     * This method results in:
     * <ul>
     * <li>The parameters for this property match up with the parameters for the
     * other one (i.e. they have the same names and types)
     * <li>The parameters for this property appear in the order they appear in
     * the other property.
     * </ul>
     *
     * @param other the property to match up with
     */
    public void matchUpWith(ParametersProperty other) {
        if (other == null || (value.size() > 0 && !(value.get(0) instanceof ValuedParameterDescriptor))) {
            return;
        }
        List<? extends ParameterDescriptor> otherParams = other.getValue();

        if (otherParams.isEmpty()) {
            value.clear();
        }

        List<ValuedParameterDescriptor> accum = new ArrayList<>();

        otherParams.forEach(p -> {
            String name = p.getName();
            String type = p.getType();
            ValuedParameterDescriptor existing = this.getParameterByName(name);

            if (existing == null) { // no existing parameter for this name
                existing = new ValuedParameterDescriptor(p);

            } else if (!existing.getType().equals(type)) { // correct name, wrong type
                existing.setType(type);
            }
            accum.add(existing);
        });

        value.clear();
        value.addAll((List) accum);
    }

    /**
     * Gets the parameter descriptor with the given name, or <code>null</code>
     * if the backing list does not contain a descriptor with that name.
     */
    public <T extends ParameterDescriptor> T getParameterByName(String name) {
        for (T param : (List<T>) value) {
            if (param.getName().equals(name)) {
                return param;
            }
        }
        return null;
    }

    @Override
    public ParametersProperty copy() {
        ParametersProperty copy = new ParametersProperty(name, new ArrayList<>(defaultValue == null ? value : defaultValue), validators, component);
        return copy;
    }

    @Override
    public List<? extends ParameterDescriptor> getValue() {
        // if parameters is not null, return it
        // otherwise set it to the default value and return it
        return value != null ? value : (value = defaultValue);
    }

    @Override
    public void setValue(List<? extends ParameterDescriptor> value) {
        this.value = value == null ? defaultValue : new ArrayList<>(value);
    }

    @Override
    public Object getDisplayValue() {
        if (value == null) {
            value = defaultValue;
        }
        Set<String> names = new LinkedHashSet<>();
        value.forEach(param -> names.add(param.getName()));
        return names.toString().substring(1, names.toString().length() - 1);
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
        return "Type mismatch or parameters with multiple names";
    }

}
