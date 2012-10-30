/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import robotbuilder.data.RobotComponent;

/**
 * A String property represents a string.
 *
 * @author Alex Henning
 */
public class StringProperty extends Property {
    protected String value;
    protected boolean editable = true;
    
    public StringProperty() {}
    
    public StringProperty(String name, Object defaultValue, String[] validators, RobotComponent component, String value, boolean editable) {
        super(name, defaultValue, validators, component);
        this.value = value;
        this.editable = editable;
    }

    @Override
    public Property copy() {
        return new StringProperty(name, defaultValue, validators, component, value, editable);
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
    public void _setValue(Object value) {
        this.value = ((String) value);
    }
    
    
    /**
     * @return Whether this property is editable.
     */
    @Override
    public boolean isEditable() {
        return editable;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
