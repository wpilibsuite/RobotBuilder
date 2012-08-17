/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import robotbuilder.data.properties.Property;
import java.util.List;

/**
 * Validates that the property has a value set that is not in the list of
 * "Unset" values
 *
 * @author Alex Henning
 */
public class ExistsValidator implements Validator {
    private String name;
    /** Values to ignore, they do not count as valid values */
    List<Object> ignore;
    
    public ExistsValidator() {}

    ExistsValidator(String name, List<Object> ignore, String error) {
        this.name = name;
        this.ignore = ignore;
        this.error = error;
    }

    @Override
    public void update(RobotComponent component, String property, Object value) {}

    @Override
    public boolean isValid(RobotComponent component, Property property) {
        return !ignore.contains(property.getValue());
    }

    @Override
    public String getError(RobotComponent component, Property property) {
        return error;
    }
    
    @Override
    public void delete(RobotComponent component, String property) {
        // Do nothing
    }
    
    String error = "You need to set this value.";
    
    // Yaml setters and getters
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

    public List<Object> getIgnore() {
        return ignore;
    }
    public void setIgnore(List<Object> ignore) {
        this.ignore = ignore;
    }

    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Validator copy() {
        return new ExistsValidator(name, ignore, error);
    }
}
