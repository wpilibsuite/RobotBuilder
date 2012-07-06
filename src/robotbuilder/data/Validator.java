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
    
    private Map<String, String> getMap(RobotComponent comp) {
        Map<String, String> values = new HashMap<String, String>();
        for (String prop : comp.getPropertyKeys()) {
            if (comp.getBase().getProperties().get(prop).getValidator().equals(name)) {
                for (String field : fields) {
                    if (prop.endsWith(field)) {
                        values.put(field, comp.getProperty(prop));
                    }
                }
            }
        }
        return values;
    }

    public void claim(String key, String val, RobotComponent comp) throws InvalidException {
        assert type.equals("unique"); // TODO: Deal with better
        Map<String, String> values = getMap(comp);
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


    public static class InvalidException extends Throwable {
        public InvalidException() {
        }
    }
    
}
