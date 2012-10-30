/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import robotbuilder.data.properties.Property;

/**
 *
 * @author alex
 */
public class UniqueNameValidator implements Validator {
    String name;
    private Map<String, RobotComponent> usedNames = new HashMap<String, RobotComponent>();

    public UniqueNameValidator() {}
    
    public UniqueNameValidator(String name) {
        this.name = name;
    }

    @Override
    public void update(RobotComponent component, String property, Object value) {
        if (!usedNames.containsKey(component.getFullName())) {
            usedNames.put(component.getFullName(), component);
        }
    }

    @Override
    public boolean isValid(RobotComponent component, Property property) {
        return usedNames.get(component.getFullName()) == component;
    }

    @Override
    public String getError(RobotComponent component, Property property) {
        return "Another component has the same name: "+component.getFullName();
    }

    @Override
    public void delete(RobotComponent component, String property) {
        usedNames.remove(component.getFullName());
    }

    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Validator copy() {
        return new UniqueNameValidator(name);
    }
    
}
