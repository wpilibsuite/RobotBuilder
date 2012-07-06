/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

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
    
    private String name;
    private HashMap<String, String> metaData = new HashMap<String, String>();
    private HashMap<String, Property[]> properties = new HashMap<String, Property[]>();
    
    public PaletteComponent(String name) {
        this.name = name;
    }
    
    public void addProperty(String propName, String propType) {
        properties.put(propName, null);
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
