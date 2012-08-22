/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.*;
import robotbuilder.palette.Palette;
import robotbuilder.data.properties.Property;

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
    /** String representations of PaletteComponent types */
    private static Map<String, PaletteComponent> types = new HashMap<String, PaletteComponent>();
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
    
    public static PaletteComponent getComponentByName(String type) {
        return (PaletteComponent)types.get(type);
    }

    public void setType(String type) {
        this.type = type;
        types.put(type, this);
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
        for (String key: properties.keySet()) {
            String k = (String) key;
        }
    }
    
    @Override
    public int hashCode() {
        return 3 * name.hashCode() +
                5 * type.hashCode();
    }

    
    public String getHelpFile() {
        return "/help/"+name+".html";
    }

    public boolean supportsChildren() {
        for (String key : supports.keySet()) {
            if (supports.get(key) == Palette.UNLIMITED) {
                return true;
            } else if (supports.get(key) > 0) {
                return true;
            }
        }
        return false;
    }
}
