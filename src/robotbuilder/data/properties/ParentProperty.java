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
        this.defaultValue = "parent";
    }

    @Override
    public Property copy() {
        return new ParentProperty(name, defaultValue, validators, component, value);
    }

    @Override
    public Object getValue() {
        if (component == null || component.getParent() == null) return null;
        return (value != null) ? value.getFullName() : ((RobotComponent) component.getParent()).getFullName();
    }
    
    @Override
    public Object getDisplayValue() {
        return getValue();
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
        // Doesn't do anything, shouldn't be set.
    }
}
