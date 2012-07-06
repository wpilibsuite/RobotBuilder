package robotbuilder.data.properties;

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
     * Must implement in subclasses!!!!
     * This is called to set the value of this property.
     * 
     * @param value 
     */
    public void setValue(Object value) {
        update();
        if (!getValue().equals(value)) {
            component.getRobotTree().takeSnapshot();
        }
    }
    
    /**
     * @return The value to display in th properties table.
     */
    public Object getDisplayValue() {
        return getValue();
    }

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
        System.out.println(name);
        for (String validatorName : validators) {
            Validator validator = component.getRobotTree().getValidator(validatorName);
            if (validator != null && !validator.isValid(component, this)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return Description of any errors relating to this property.
     */
    public String getError() {
        if (validators == null) return null;
        String out = "";
        for (String validatorName : validators) {
            Validator validator = component.getRobotTree().getValidator(validatorName);
            if (validator != null && !validator.isValid(component, this))
                out += validator.getError(component, this) + " ";
        }
        return "".equals(out) ? null : out;
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
        return name + "  --  {type: " + this.getClass() + ", default: " + defaultValue + "}";
    }

    public void setComponent(RobotComponent component) {
        this.component = component;
    }
}
