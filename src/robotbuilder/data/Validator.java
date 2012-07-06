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
        Map<String, String> values = new HashMap<String, String>();
        for (String field : fields) {
            values.put(field, choices.get(field)[0]);
        }
        int fieldLocation = 0, choiceLocation = 0;

        while (true) {
            if (!used.contains(values)) {
                used.add(values);
                return values;
            }
            
            String field = fields.get(fieldLocation);
            values.put(field, choices.get(field)[choiceLocation]);
            choiceLocation++;
            
            if (choiceLocation >= choices.get(field).length) {
                choiceLocation = 0;
                fieldLocation++;
                if (fieldLocation >= fields.size()) {
                    throw new InvalidException();
                }
            }
        }
    }

    public static class InvalidException extends Throwable {
        public InvalidException() {
        }
    }
    
}
