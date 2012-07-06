/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

/**
 *
 * @author brad
 */
public class RobotComponent {
    private String key;
    private String ports;
    private String className;
    
    public RobotComponent(String key) {
        this.key = key;
    }
    
    public void addPorts(String ports) {
        this.ports = ports;
    }
    
    public void addClassName(String className) {
        this.className = className;
    }
    
    @Override
    public String toString() {
        return key;
    }
    
    public void print() {
        System.out.println("Component: " + key);
        System.out.println("\tPorts: " + ports);
        System.out.println("\tClassName: " + className);
    }
}
