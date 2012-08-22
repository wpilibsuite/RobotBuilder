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
public class ParentProperty extends Property {
    protected RobotComponent value;
    
    public ParentProperty() {}
    
    public ParentProperty(String name, Object defaultValue, String[] validators, RobotComponent component, RobotComponent value) {
        super(name, defaultValue, validators, component);
        this.value = value;
    }

    @Override
    public Property copy() {
        return new ParentProperty(name, defaultValue, validators, component, value);
    }

    @Override
    public Object getValue() {
        return (value != null) ? value : defaultValue;
    }
    
    @Override
    public Object getDisplayValue() {
        return value.getFullName();
    }
    
    @Override
    public void update() {
        value = (RobotComponent) component.getParent();
    }
    
    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void _setValue(Object value) {
        // Can't edit this.
    }
}
