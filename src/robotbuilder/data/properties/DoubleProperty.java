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
public class DoubleProperty extends Property {
    protected String value;
    
    public DoubleProperty() {}
    
    public DoubleProperty(String name, Object defaultValue, String[] validators, RobotComponent component, String value) {
        super(name, defaultValue, validators, component);
        this.value = value;
    }

    @Override
    public Property copy() {
        return new DoubleProperty(name, defaultValue, validators, component, value);
    }

    @Override
    public Object getValue() {
        try {
            return Double.parseDouble((value != null) ? value : defaultValue.toString());
        } catch (NumberFormatException ex) {
            return value;
        }
    }
    
    @Override
    public Object getDisplayValue() {
        return ""+getValue();
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        this.value = value.toString();
    }
    
    @Override
    public boolean isValid() {
        try {
            // Check that it's a valid double
            Double.parseDouble((value != null) ? value : defaultValue.toString());
            return super.isValid();
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
