package robotbuilder.data;

import robotbuilder.RobotTree;

/**
 * A property is one of the many values that can be set in a palette or robot component.
 * For example, a Victor PaletteComponent has a set of properties described by a name
 * and a set of values for each name. For example, a gyro has two properties, AChannel
 * and BChannel. Each property has a number of attributes that describe the property
 * such as the name, type, defaultValue, set of possible choices, etc.
 * @author brad
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
    
    public abstract Property copy();
    public abstract Object getValue();
    
    /**
     * Must implement in subclasses!!!!
     * @param value 
     */
    public void setValue(Object value) {
        if (!getValue().equals(value)) {
            component.getRobotTree().takeSnapshot();
        }
    }
    public Object getDisplayValue() {
        return getValue();
    }

    public void update() {
        if (validators == null) return;
        for (String validatorName : validators) {
            Validator validator = component.getRobotTree().getValidator(validatorName);
            if (validator != null) {
                if (validator instanceof UniqueValidator) {
                    ((UniqueValidator) validator).setUnique(component, name);
                }
                validator.update(component, name, getValue());
            }
        }
    }

    public boolean isValid() {
        if (validators == null) return true;
        for (String validatorName : validators) {
            Validator validator = component.getRobotTree().getValidator(validatorName);
            if (validator != null && !validator.isValid(component, this))
                return false;
        }
        return true;
    }

    public String getError(RobotComponent currentComponent) {
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

    void setComponent(RobotComponent component) {
        this.component = component;
    }
}
