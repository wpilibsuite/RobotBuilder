/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import robotbuilder.data.properties.Property;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import robotbuilder.data.properties.ChoicesProperty;

/**
 *
 * @author Alex Henning
 */
public class UniqueValidator implements Validator {
    private String name;
    LinkedList<String> fields;
    Map<Map<String, Object>, Pair> claims = new HashMap<Map<String,Object>, Pair>();
    
    public UniqueValidator() {}

    public UniqueValidator(String name, LinkedList<String> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public boolean isValid(RobotComponent component, Property property) {
        String prefix = getPrefix(property.getName());
        Pair pair = new Pair(component, prefix);
        return claims.containsValue(pair);
    }

    @Override
    public void update(RobotComponent component, String property, Object value) {
        try {
            release(component, getPrefix(property));
            claim(property, value, component);
        } catch (InvalidException ex) {
            //Logger.getLogger(UniqueValidator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getError(RobotComponent component, Property property) {
        Pair claimant = claims.get(getMap(component, getPrefix(property.getName())));
        if (claimant == null) return null;
        return "This port is in use by "+claimant.toString()+" please change this to an unused port.";
    }
    
    @Override
    public void delete(RobotComponent component, String property) {
        release(component, getPrefix(property));
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
    
    private Map<String, Object> getMap(RobotComponent comp, String prefix) {
        Map<String, Object> values = new HashMap<String, Object>();
        for (String prop : comp.getPropertyKeys()) {
            for (String field : fields) {
                if (prop.endsWith(field) && prop.startsWith(prefix)) {
                    values.put(field, comp.getProperty(prop).getValue());
                }
            }
        }
        return values;
    }

    /**
     * Claim a unique set of values.
     * @param key The key being claimed.
     * @param val It's new value.
     * @param comp The component making the claim.
     * @throws robotbuilder.data.UniqueValidator.InvalidException 
     */
    private void claim(String key, Object val, RobotComponent comp) throws InvalidException {
        // Get the prefix
        String prefix = getPrefix(key);
        Map<String, Object> values = getMap(comp, prefix);
        for (String field : fields) {
            if (key.endsWith(field)) {
                values.put(field, val);
            }
        }
        
        if (claims.containsKey(values)) {
            throw new InvalidException();
        }
            
        Pair pair = new Pair(comp, prefix);
        claims.put(values, pair);
    }

    /**
     * Release a claim.
     * @param comp The component holding the claim.
     * @param prefix The prefix associated with the hold
     */
    private void release(RobotComponent component, String prefix) {
        if (hasClaim(component, prefix)) {
            Pair pair = new Pair(component, prefix);
            List<Map<String, Object>> toRemove = new LinkedList<Map<String, Object>>();
            for (Map<String, Object> key : claims.keySet()) {
                if (claims.get(key).equals(pair)) {
                    toRemove.add(key);
                }
            }
            for (Map<String, Object> key : toRemove) {
                claims.remove(key);
            }
            //Map<String, Object> values = getMap(comp, prefix);
            //claims.remove(values);
        }
    }
    
    /**
     * Sets a component to be unique with respect to the prefix of this property.
     * @param component
     * @param property
     * @throws robotbuilder.data.UniqueValidator.InvalidException 
     */
    public void setUnique(RobotComponent component, String property) {
        String prefix = getPrefix(property);
        if (!hasClaim(component, prefix)) {
            Map<String, String[]> choices = new HashMap<String, String[]>();
            for (String field : fields) {
                choices.put(field,
                        ((ChoicesProperty) component.getProperty(prefix+field)).getChoices());
            }
            Map<String, String> selection;
            try {
                selection = getFree(choices);
            } catch (InvalidException ex) {
                Logger.getLogger(UniqueValidator.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            for (String prop : selection.keySet()) {
                component.getProperty(prefix+prop)._setValue(selection.get(prop));
                component.getProperty(prefix+prop).update();
            }
        }
    }
    
    /**
     * Whether or not a (component, prefix) pair has a claim.
     * @param component
     * @param prefix
     * @return 
     */
    private boolean hasClaim(RobotComponent component, String prefix) {
        Pair pair = new Pair(component, prefix);
        return claims.containsValue(pair);
    }
    
    /**
     * @return An unused port that can be claimed.
     */
    private Map<String, String> getFree(Map<String, String[]> choices) throws InvalidException {
        assert fields.size() <= 2; // Warning: Buggy with more than two fields
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

    @Override
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
    
    static class Pair {
        RobotComponent comp;
        String prefix;
        
        Pair(RobotComponent comp, String prefix) {
            this.comp = comp;
            this.prefix = prefix;
        }
        
        @Override
        public String toString() {
            return comp.getFullName() + ": " + prefix;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + (this.comp != null ? this.comp.hashCode() : 0);
            hash = 53 * hash + (this.prefix != null ? this.prefix.hashCode() : 0);
            return hash;
        }
        
        @Override
        public boolean equals(Object oth) {
            if (oth instanceof Pair) {
                Pair other = (Pair) oth;
                return comp.equals(other.comp) 
                        && prefix.equals(other.prefix);
            }
            return false;
        }
    }
}
