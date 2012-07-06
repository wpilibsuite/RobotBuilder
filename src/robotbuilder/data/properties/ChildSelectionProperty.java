/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import java.util.Vector;
import javax.swing.JComboBox;
import robotbuilder.data.RobotComponent;

/**
 * The type selection property allows you to select a component of a certain type.
 *
 * @author Alex Henning
 */
public class ChildSelectionProperty extends Property {
    protected String type;
    protected Object value;
    protected JComboBox combo;
    
    public ChildSelectionProperty() {}
    
    public ChildSelectionProperty(String name, Object defaultValue, String[] validators, RobotComponent component,
            String type, Object value) {
        super(name, defaultValue, validators, component);
        this.type = type;
        this.value = value;
    }

    @Override
    public Property copy() {
        return new ChildSelectionProperty(name, defaultValue, validators, component, type, value);
    }

    @Override
    public Object getValue() {
        return (value != null) ? value : defaultValue;
    }
    
    @Override
    public Object getDisplayValue() {
        update();
        return combo;
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        this.value = value;
    }
    
    @Override
    public void update() {
        super.update();
        Object selection = getValue();
        Vector<String> options = component.getChildrenOfTypeNames(type);
        combo = new JComboBox(options);
        if (options.contains(selection)) {
            combo.setSelectedItem(value);
        } else if (defaultValue instanceof Integer &&
                ((Integer) defaultValue) < options.size()) {
            combo.setSelectedIndex((Integer) defaultValue);
        } else if (defaultValue instanceof Integer) {
        } else {
            combo.setSelectedItem(defaultValue);
        }
        value = combo.getSelectedItem();
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
