/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.*;

/**
 *
 * @author Alex Henning
 */
public class UniqueValidator implements Validator {
    private String name;
    LinkedList<String> fields;
    Map<Map<String, String>, String> claims = new HashMap<Map<String,String>, String>();
    
    public UniqueValidator() {}

    public UniqueValidator(String name, LinkedList<String> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public boolean isValid(RobotComponent component, String property) {
        String prefix = getPrefix(property);
        return claims.containsValue(component.toString()+"-"+prefix);
    }

    @Override
    public void update(RobotComponent component, String property, String value) {
        try {
            release(component, getPrefix(property));
            claim(property, value, component);
        } catch (InvalidException _) {}
    }
    
    @Override
    public UniqueValidator copy() {
        LinkedList<String> newFields = new LinkedList<String>();
        for (String item : fields) {
            newFields.add(item);
        }
        return new UniqueValidator(name, newFields);
    }
    
    /**
     * Get the prefix of the property. In other words, anything that's not
     * the suffix.
     * @param key
     * @return 
     */
    private String getPrefix(String key) {
        for (String field : fields) {
            if (key.endsWith(field)) {
                return key.replace(field, "");
            }
        }
        return null;
    }
    
    private Map<String, String> getMap(RobotComponent comp, String prefix) {
        Map<String, String> values = new HashMap<String, String>();
        for (String prop : comp.getPropertyKeys()) {
            String validatorName = comp.getBase().getProperty(prop).getValidator();
            if (validatorName != null && validatorName.equals(name)) {
                for (String field : fields) {
                    if (prop.endsWith(field) && (prefix == null || prop.startsWith(prefix))) {
                        values.put(field, comp.getProperty(prop));
                    }
                }
            }
        }
        return values;
    }

    private void claim(String key, String val, RobotComponent comp) throws InvalidException {
        // Get the prefix
        String prefix = getPrefix(key);
        Map<String, String> values = getMap(comp, prefix);
        for (String field : fields) {
            if (key.endsWith(field)) {
                values.put(field, val);
            }
        }
        
        if (claims.containsKey(values)) {
            throw new InvalidException();
        }
            
        claims.put(values, comp.toString()+"-"+prefix);
    }

    private void release(RobotComponent comp, String prefix) {
        Map<String, String> values = getMap(comp, prefix);
        claims.remove(values);
    }
    
    /**
     * @return An unused port that you need to claim.
     */
    public Map<String, String> getFree(Map<String, String[]> choices) throws InvalidException {
        assert fields.size() <= 2; // TODO: buggy with more than two fields
        Map<String, Integer> locations = new HashMap<String, Integer>();
        for (String field : fields) {
            locations.put(field, 0);
        }
        int fieldLocation = 0;

        while (true) {
            // Generate values
            Map<String, String> values = new HashMap<String, String>();
            for (String field : fields) {
                values.put(field, choices.get(field)[locations.get(field)]);
            }
            
            // Return it if acceptable
            if (!claims.containsKey(values)) {
                return values;
            }
            
            // Change locations
            String field = fields.get(fieldLocation);
            locations.put(field, locations.get(field)+1);
            
            if (locations.get(field) >= choices.get(field).length) {
                locations.put(field, 0);
                fieldLocation++;
                locations.put(fields.get(fieldLocation), locations.get(fields.get(fieldLocation))+1);
                if (locations.get(fields.get(fields.size()-1)) >= choices.get(fields.get(fields.size()-1)).length) {
                    System.out.println("Error!!!");
                    throw new InvalidException();
                }
            }
            
            if (fieldLocation > 0) {
                fieldLocation--;
            }
        }
    }
    
    //// YAML Getters and Setters
    public LinkedList<String> getFields() {
        return fields;
    }
    public void setFields(LinkedList<String> fields) {
        this.fields = fields;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
     * An exception for invalid claims.
     */
    public static class InvalidException extends Throwable {
        public InvalidException() {
        }
    }
}
