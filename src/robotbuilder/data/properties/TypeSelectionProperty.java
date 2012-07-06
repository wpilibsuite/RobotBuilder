/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import java.util.List;
import java.util.Vector;
import javax.swing.JComboBox;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author Alex Henning
 */
public class TypeSelectionProperty extends Property {
    protected String type;
    protected Object value;
    protected JComboBox combo;
    
    public TypeSelectionProperty() {}
    
    public TypeSelectionProperty(String name, Object defaultValue, String[] validators, RobotComponent component,
            String type, Object value) {
        super(name, defaultValue, validators, component);
        this.type = type;
        this.value = value;
    }

    @Override
    public Property copy() {
        return new TypeSelectionProperty(name, defaultValue, validators, component, type, value);
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
        Vector<String> options = component.getRobotTree().getNamesOfType(type);
        options.add(0, (String) defaultValue);
        combo = new JComboBox(options);
        combo.setSelectedIndex(0);
        if (options.contains(selection)) {
            combo.setSelectedItem(selection);
        }
        value = combo.getSelectedItem();
        System.out.println("Value: "+value+"|"+combo.getSelectedIndex());
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
