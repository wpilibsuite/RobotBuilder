/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import robotbuilder.data.properties.Property;

/**
 *
 * @author Alex Henning
 */
public interface Validator {
    public void update(RobotComponent component, String property, Object value);
    public boolean isValid(RobotComponent component, Property property);
    public String getError(RobotComponent component, Property property);
    
    public String getName();
    public Validator copy();
}
