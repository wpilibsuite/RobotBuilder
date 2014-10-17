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
    /**
     * Called when the properties value is updated to handle the new value.
     * 
     * @param component The robot component the property belongs to.
     * @param property The property name.
     * @param value The value of the property.
     */
    public void update(RobotComponent component, String property, Object value);
    /**
     * @param component The robot component the property belongs to.
     * @param property The property name.
     * @return Whether this validator believes that the property is valid.
     */
    public boolean isValid(RobotComponent component, Property property);
    /**
     * @param component The robot component the property belongs to.
     * @param property The property name.
     * @return The error message for this validator or null if valid.
     */
    public String getError(RobotComponent component, Property property);
    /**
     * Called when the properties component is deleted.
     * @param component The robot component the property belongs to.
     * @param property The property name.
     */
    public void delete(RobotComponent component, String property);
    
    /**
     * @return The name of this validator.
     */
    public String getName();
    /**
     * @return A copy of this validator.
     */
    public Validator copy();
}
