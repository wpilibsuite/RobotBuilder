
package robotbuilder.data.properties;

import robotbuilder.data.RobotComponent;

/**
 * A String property represents a string.
 *
 * @author Alex Henning
 */
public class StringProperty extends Property<String> {

    protected String value;

    public StringProperty() {
    }

    public StringProperty(String name, String defaultValue, String[] validators, RobotComponent component, String value) {
        super(name, defaultValue, validators, component);
        this.value = value;
    }

    @Override
    public Property copy() {
        return new StringProperty(name, defaultValue, validators, component, value);
    }

    @Override
    public String getValue() {
        return (value != null) ? value : defaultValue;
    }

    @Override
    public Object getDisplayValue() {
        return getValue();
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
