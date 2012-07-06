/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import robotbuilder.data.RobotComponent;

/**
 *
 * @author Alex Henning
 */
public class StringProperty extends Property {
    protected String value;
    
    public StringProperty() {}
    
    public StringProperty(String name, Object defaultValue, String[] validators, RobotComponent component, String value) {
        super(name, defaultValue, validators, component);
        this.value = value;
    }

    @Override
    public Property copy() {
        return new StringProperty(name, defaultValue, validators, component, value);
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
        super.setValue(value);
        this.value = ((String) value);
    }
}
