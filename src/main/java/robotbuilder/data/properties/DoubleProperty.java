/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import robotbuilder.data.RobotComponent;

/**
 * The Double property represents a number of the type double.
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
    public void _setValue(Object value) {
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
    
    @Override
    public String getErrorMessage() {
        try {
            // Check that it's a valid double
            Double.parseDouble(getValue().toString());
            return super.getErrorMessage();
        } catch (NumberFormatException ex) {
            String error = super.getErrorMessage();
            return name+" expects a real number. "+ (error!=null ? error : "");
        }
    }
}
