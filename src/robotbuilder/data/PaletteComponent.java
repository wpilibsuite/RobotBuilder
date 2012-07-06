/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<String> propertiesKeys = new ArrayList<String>();
    
    public PaletteComponent() {}
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    
    public void setSupports(Map<String, Integer> supports) {
        this.supports = supports;
    }
    public Map<String, Integer> getSupports() {
        return supports;
    }
        
    public void setHelp(String help) {
        this.help = help;
    }
    public String getHelp() {
        return help;
    }
    
    public void setProperties(List<Property> properties) {
        this.properties = new HashMap<String, Property>();
        this.propertiesKeys = new ArrayList<String>();
        for (Property property : properties) {
            this.properties.put(property.getName(), property);
            this.propertiesKeys.add(property.getName());
        }
    }
    public List<Property> getProperties() {
        List<Property> out = new ArrayList<Property>();
        for (String key : propertiesKeys) {
            out.add(properties.get(key));
        }
        return out;
    }
    
    public Property getProperty(String propName) {
        return properties.get(propName);
    }
    
    
    
    public List<String> getPropertiesKeys() {
        return propertiesKeys;
    }
    
    @Override
    public String toString() {
        //return "<PaletteComponent: "+name+">";
        return name;
    }
    
    public void print() {
//        System.out.println("Component: " + name);
//        System.out.println("\tType: " + getType());
//        System.out.println("\tHelp: " + getHelp());
        for (String key: properties.keySet()) {
            String k = (String) key;
//            System.out.println("\t\t" + k + ": " + properties.get(k));
        }
    }

    
    public String getHelpFile() {
        return "/help/"+name+".html";
    }
}
