/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author brad
 * @author Alex Henning
 */
public class PaletteComponent {
    private String name; //  the name of the palette component
    // The metadata for the component (type, etc.)
    private String type; // the type of the data
    // Children that this can support having
    private HashMap<String, Integer> supports = new HashMap<String, Integer>(); 
    
    private HashMap<String, String> metaData = new HashMap<String, String>(); 
    // set of properties for the component
    private HashMap<String, Property> properties = new HashMap<String, Property>();
    
    public PaletteComponent(String name) {
        this.name = name;
    }
    
    public void addProperty(String propName, Property property) {
        System.out.println(property);
        properties.put(propName, property);
    }
    
    public Map<String, Property> getProperties() {
        return properties;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public void print() {
        System.out.println("Component: " + name);
        for (String key: properties.keySet()) {
            String k = (String) key;
            System.out.println("\t\t" + k + ": " + properties.get(k));
        }
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void addSupport(String key, Integer val) {
        supports.put(key, val);
    }
}
