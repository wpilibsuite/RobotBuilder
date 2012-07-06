package robotbuilder.data;

/**
 * A property is describing a single property in a PaletteComponent.
 * For example, a Victor PaletteComponent has a set of properties and each property is
 * described by an attribute such as name, a type (PWMInput), maybe a default value
 * such as 1, 2, or 3. 
 * @author brad
 */
public class Property {
    private String name;
    private String type;
    private String defaultValue;
    private String[] choices;
    
    public Property(String name, String type) {
        this.name = name;
        this.type = type;
        defaultValue = null;
        choices = null;
    }
    
    public Property(String name, String type, String defaultValue) {
        this(name, type);
        this.defaultValue = defaultValue;
        choices = null;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDefault() {
        return defaultValue;
    }
    
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public String[] getChoices() {
        return choices;
    }
}
