package robotbuilder.data;

/**
 * A property is one of the many values that can be set in a palette or robot component.
 * For example, a Victor PaletteComponent has a set of properties described by a name
 * and a set of values for each name. For example, a gyro has two properties, AChannel
 * and BChannel. Each property has a number of attributes that describe the property
 * such as the name, type, defaultValue, set of possible choices, etc.
 * @author brad
 */
public class Property {
    private String name;
    private String type;
    private String defaultValue;
    private String[] choices;
    private String validator;
    
    public Property() {}
    
    public Property(String name, String type) {
        this.name = name;
        this.type = type;
        defaultValue = null;
        choices = null;
    }
    
    public Property(String name, String type, String defaultValue) {
        this(name, type);
        this.defaultValue = defaultValue;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    public String getDefault() {
        return defaultValue;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }    
    public String[] getChoices() {
        return choices;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }
    public String getValidator() {
        return validator;
    }
    
    @Override
    public String toString() {
        return name + "  --  {type: " + type + ", default: " + defaultValue + "}";
    }
}
