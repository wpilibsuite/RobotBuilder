
package robotbuilder.data.properties;

import robotbuilder.data.RobotComponent;

/**
 * A boolean property is a property that can be true or false. It is represented
 * by a checkbox.
 *
 * @author Alex Henning
 */
public class BooleanProperty extends Property {

    protected Boolean value;

    public BooleanProperty() {
    }

    public BooleanProperty(String name, Object defaultValue, String[] validators, RobotComponent component, Boolean value) {
        super(name, defaultValue, validators, component);
        this.value = value;
    }

    @Override
    public Property copy() {
        return new BooleanProperty(name, defaultValue, validators, component, value);
    }

    @Override
    public Object getValue() {
        return (value != null) ? value : defaultValue;
    }

    @Override
    public Object getDisplayValue() {
        return getValue();
    }

    @Override
    public void setValue(Object value) {
        this.value = ((Boolean) value);
    }
}
