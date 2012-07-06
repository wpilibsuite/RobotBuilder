/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.*;

/**
 *
 * @author alex
 */
public class Validator {
    private String name, type;
    LinkedList<String> fields;
    Set<Map<String, String>> used = new HashSet<Map<String,String>>();

    public Validator(String name, String type, LinkedList<String> fields) {
        this.name = name;
        this.type = type;
        this.fields = fields;
    }
    
    public Validator copy() {
        LinkedList<String> newFields = new LinkedList<String>();
        for (String item : fields) {
            newFields.add(item);
        }
        return new Validator(name, type, newFields);
    }
    
    public String getPrefix(String key) {
        for (String field : fields) {
            if (key.endsWith(field)) {
                return key.replace(field, "");
            }
        }
        return null;
    }
    
    private Map<String, String> getMap(RobotComponent comp) {
        return getMap(comp, null);
    }
    
    public Map<String, String> getMap(RobotComponent comp, String prefix) {
        Map<String, String> values = new HashMap<String, String>();
        for (String prop : comp.getPropertyKeys()) {
            if (comp.getBase().getProperty(prop).getValidator().equals(name)) {
                for (String field : fields) {
                    if (prop.endsWith(field) && (prefix == null || prop.startsWith(prefix))) {
                        System.out.println("\tPrefix: "+prefix);
                        values.put(field, comp.getProperty(prop));
                    }
                }
            }
        }
        return values;
    }

    public void claim(String key, String val, RobotComponent comp) throws InvalidException {
        assert type.equals("unique"); // TODO: Deal with better
        
        // Get the prefix
        String prefix = getPrefix(key);
        Map<String, String> values = getMap(comp, prefix);
        for (String field : fields) {
            if (key.endsWith(field)) {
                System.out.println("\t- "+field);
                values.put(field, val);
            }
        }
        
        System.out.println("\t"+used);
        System.out.println("\t"+values);
        
        if (used.contains(values)) {
            throw new InvalidException();
        }
            
        used.add(values);
        System.out.println("\t"+used);
    }
    public void claim(RobotComponent comp) throws InvalidException {
        claim("", "", comp);
    }


    public void release(RobotComponent comp) {
        assert type.equals("unique"); // TODO: Deal with better
        Map<String, String> values = getMap(comp);
        used.remove(values);
    }
    
    /**
     * @return A unused port, that has just been claimed for you
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
            
            //System.out.println(used);
            //System.out.println(values);
            System.out.println(fieldLocation+"--"+locations);
        
            // Return it if acceptable
            if (!used.contains(values)) {
                used.add(values);
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

    public static class InvalidException extends Throwable {
        public InvalidException() {
        }
    }
    
}
