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
 */
public class RobotComponent {
    private String key;
    private String ports;
    private Hashtable<String, String> properties;
    
    public RobotComponent(String key) {
        properties = new Hashtable<String, String>();
        this.key = key;
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
        for (Enumeration keys = properties.keys(); keys.hasMoreElements(); ) {
            String k = (String) keys.nextElement();
            System.out.println("\t\t" + k + ": " + properties.get(k));
        }
    }
}
