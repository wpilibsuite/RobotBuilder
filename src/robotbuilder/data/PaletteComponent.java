/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.Enumeration;
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
    
    private String key;
    private String ports;
    private String className;
    private Hashtable<String, String> properties;
    
    public PaletteComponent(String key) {
        properties = new Hashtable<String, String>();
        this.key = key;
    }
    
    public void addClassName(String className) {
        // TODO: Deal with className changes
        assert this.className == null;
        this.className = className;
        instances.put(className, this);
    }
    
    public void addProperty(String propName, String propType) {
        properties.put(propName, propType);
    }
    
    @Override
    public String toString() {
        return key;
    }
    
    public void print() {
        System.out.println("Component: " + key);
        System.out.println("\tClassName: " + className);
        for (Enumeration keys = properties.keys(); keys.hasMoreElements(); ) {
            String k = (String) keys.nextElement();
            System.out.println("\t\t" + k + ": " + properties.get(k));
        }
    }
}
