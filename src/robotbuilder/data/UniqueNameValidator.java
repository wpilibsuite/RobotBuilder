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
    private Map<RobotComponent, String> nameMap = new HashMap<RobotComponent, String>();
    private Set<String> usedNames = new HashSet<String>();

    public UniqueNameValidator() {}
    
    public UniqueNameValidator(String name) {
        this.name = name;
    }

    @Override
    public void update(RobotComponent component, String property, Object value) {
        System.out.println(usedNames);
        if (component.getFullName().toLowerCase() != nameMap.get(component)) {
            delete(component, property);
        }
        if (!usedNames.contains(component.getFullName().toLowerCase())) {
            System.out.println("Claiming "+component.getFullName().toLowerCase()+" for "+component);
            System.out.println(usedNames);
            nameMap.put(component, component.getFullName().toLowerCase());
            usedNames.add(component.getFullName().toLowerCase());
        }
    }

    @Override
    public boolean isValid(RobotComponent component, Property property) {
        return component.getFullName().toLowerCase().equals(nameMap.get(component));
    }

    @Override
    public String getError(RobotComponent component, Property property) {
        return "Another component has the same name: "+component.getFullName();
    }

    @Override
    public void delete(RobotComponent component, String property) {
        if (nameMap.get(component) != null) {
            usedNames.remove(nameMap.get(component));
            nameMap.remove(component);
        }
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

    public void setUnique(RobotComponent component) {
        System.out.println("Setting unique name for component.");
        System.out.println("\t"+usedNames);
        delete(component, null);
        
        int i = 0;
	String name, fullName;
        do {
            i++;
	    name = component.getBaseType().toString() + (i == 1 ? "" : " " + i);
            if (component.getBaseType().equals("Subsystem")) {
                fullName = name.toLowerCase();
            } else {
                fullName = (component.getSubsystem()+(component.getSubsystem() == "" ? "" : " ")+name).toLowerCase();
            }
            System.out.println("\t"+i+": "+fullName);
            System.out.println("\t"+usedNames);
	} while (usedNames.contains(fullName));
        
        component.setName(name);
        System.out.println("\t"+usedNames.contains(component.getFullName().toLowerCase()));
        update(component, null, null);
        System.out.println("\t"+usedNames);
        System.out.println("\t"+component.getFullName().toLowerCase());
    }
    
}
