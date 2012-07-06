/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.HashMap;

/**
 *
 * @author brad
 * @author Alex Henning
 */
public class PaletteComponent {
    /** A lookup table of instances by className */
    private static Hashtable<String, PaletteComponent> instances = new Hashtable<String, PaletteComponent>();
    
    /**
     * @param name The className of the PaletteComponent
     * @return The palette component with the matching class name
     */
    public static PaletteComponent getComponent(String className) {
        return instances.get(className);
    }
    
    private String name; //  the name of the palette component
    // the metadata for the component (type, etc.)
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
}
