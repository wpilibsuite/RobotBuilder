/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 *
 * @author brad
 * @author Alex Henning
 */
public class PaletteComponent {
    private String name; //  The name of the palette component
    // The metadata for the component (type, etc.)
    private String type; // The type of the data
    private String help; // The help text for this component.
    /** Type and quantity of children that this type of component can support. */
    private Map<String, Integer> supports = new HashMap<String, Integer>(); 
    // set of properties for the component
    private Map<String, Property> properties = new HashMap<String, Property>();
    private LinkedList<String> propertiesKeys = new LinkedList<String>();
    
    public PaletteComponent(String name) {
        this.name = name;
    }
    
    public void addProperty(String propName, Property property) {
        System.out.println(property);
        properties.put(propName, property);
        propertiesKeys.add(propName);
    }
    
    public Property getProperty(String propName) {
        return properties.get(propName);
    }
    
    
    private Map<String, Property> getProperties() {
        return properties;
    }
    
    public LinkedList<String> getPropertiesKeys() {
        return propertiesKeys;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public void print() {
        System.out.println("Component: " + name);
        System.out.println("\tType: " + getType());
        System.out.println("\tHelp: " + getHelp());
        for (String key: properties.keySet()) {
            String k = (String) key;
            System.out.println("\t\t" + k + ": " + properties.get(k));
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getHelp() {
        return help;
    }
    
    public String getHelpFile() {
        return "help/"+name+".html";
    }
    
    public void addSupport(String key, Integer val) {
        supports.put(key, val);
    }
    
    public Map<String, Integer> getSupports() {
        return supports;
    }
}
