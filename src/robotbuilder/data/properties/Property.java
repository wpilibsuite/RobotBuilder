package robotbuilder.data.properties;

import java.util.Arrays;
import robotbuilder.MainFrame;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.UniqueValidator;
import robotbuilder.data.Validator;

/**
 * A property is one of the many values that can be set in a palette or robot component.
 * For example, a Victor PaletteComponent has a set of properties described by a name
 * and a set of values for each name. For example, a gyro has two properties, AChannel
 * and BChannel. Each property has a number of attributes that describe the property
 * such as the name, type, defaultValue, set of possible choices, etc.
 * 
 * The abstract Property class implements the core functionality that a number of
 * subclasses expand upon to provide properties that are edited differently and
 * have additional validation rules.
 * 
 * @author brad
 * @author Alex Henning
 */
public abstract class Property {
    protected String name;
    protected Object defaultValue;
    protected String[] validators;
    protected RobotComponent component;
    
    public Property() {}
    
    public Property(String name, Object defaultValue, String[] validators, RobotComponent component) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.validators = validators;
        this.component = component;
    }
    
    /**
     * @return An identical copy of this property.
     */
    public abstract Property copy();
    
    /**
     * @return The value of this property.
     */
    public abstract Object getValue();
    
    /**
     * This is called to set the value of this property.
     * 
     * @param value The previous value.
     */
    public abstract void _setValue(Object value);
    
    /**
     * @return Whether this property is editable.
     */
    public boolean isEditable() {
        return true;
    }
    
    /**
     * This is called to update changes and support undo.
     * 
     * @param prevValue The value.
     */
    public void setValue(Object value) {
        Object prevValue = getValue();
        _setValue(value);
        if (component != null) {
            update();
            if (!prevValue.equals(value)) {
                component.getRobotTree().takeSnapshot();
            }
        }
        MainFrame.getInstance().getCurrentRobotTree().update();
    }
    
    /**
     * @return The value to display in th properties table.
     */
    public abstract Object getDisplayValue();


    /**
     * Called to allow the property to update to changes.
     */
    public void update() {
        if (validators == null) return;
        for (String validatorName : validators) {
            Validator validator = component.getRobotTree().getValidator(validatorName);
            if (validator != null) {
                validator.update(component, name, getValue());
            }
        }
    }
    
    /**
     * A special method to deal with the UniqueValidator.
     */
    public void setUnique() {
        if (validators == null) return;
        for (String validatorName : validators) {
            Validator validator = component.getRobotTree().getValidator(validatorName);
            if (validator != null && validator instanceof UniqueValidator) {
                ((UniqueValidator) validator).setUnique(component, name);
            }
        }
    }

    /**
     * @return Whether or not this property is valid.
     */
    public boolean isValid() {
        if (validators == null) return true;
        for (String validatorName : validators) {
            Validator validator = component.getRobotTree().getValidator(validatorName);
            if (validator != null && !validator.isValid(component, this)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Assumes that isValid() == false, otherwise the behavior is undefined.
     * @return Description of any errors relating to this property.
     */
    public String getErrorMessage() {
        if (validators == null) return null;
        String out = "";
        for (String validatorName : validators) {
            Validator validator = component.getRobotTree().getValidator(validatorName);
            if (validator != null && !validator.isValid(component, this))
                out += validator.getError(component, this) + " ";
        }
        return "".equals(out) ? null : out;
    }
    
    public boolean equals(Object oth) {
        if (oth instanceof Property) {
            Property other = (Property) oth;
            return getClass().equals(oth.getClass()) &&
                    getName().equals(other.getName()) &&
                    getValue().equals(other.getValue()) &&
                    Arrays.equals(getValidators(), other.getValidators());
        }
        return false;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setDefault(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    public Object getDefault() {
        return defaultValue;
    }

    public void setValidators(String[] validators) {
        this.validators = validators;
    }
    public String[] getValidators() {
        return validators;
    }
    
    @Override
    public String toString() {
        return name + " -- {value: " + getValue() + ", default: " + defaultValue + "}";
    }

    public void setComponent(RobotComponent component) {
        this.component = component;
    }
}
