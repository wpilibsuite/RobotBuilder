/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author Alex Henning
 */
public class DefaultProperty extends Property {
    protected String type;
    protected Object value;
    private JComboBox combo;
    private JFileChooser filechooser;
    
    public DefaultProperty() {}

    private DefaultProperty(String name, Object defaultValue, String[] validators, RobotComponent component,
            String type, Object value) {
        super(name, defaultValue, validators, component);
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Property copy() {
        return new DefaultProperty(name, defaultValue, validators, component, type, value);
    }

    @Override
    public Object getValue() {
        return (value != null) ? value : defaultValue;
    }

    @Override
    public Object getDisplayValue() {
        update();
        if (getType().equals("Actuator") ||
                getType().equals("Sensor") ||
                getType().equals("Joystick") ||
                getType().equals("Command") ||
                getType().equals("Subsystem")) {
            return combo;
        } else {
            return getValue();
        }
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        this.value = value;
    }
}
