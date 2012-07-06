/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

/**
 *
 * @author Alex Henning
 */
public interface Validator {
    public void update(RobotComponent component, String property, String value);
    public boolean isValid(RobotComponent component, String property);
    public String getError(RobotComponent component, String property);
    
    public String getName();
    public Validator copy();
}
