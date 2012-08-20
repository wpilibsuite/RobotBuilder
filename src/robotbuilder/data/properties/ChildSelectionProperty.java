/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import robotbuilder.data.RobotComponent;

/**
 * The type selection property allows you to select a component of a certain type.
 *
 * @author Alex Henning
 */
public class ChildSelectionProperty extends Property {
    String type;
    Object value;
    RobotComponent valueComponent;
    JComboBox combo;
    
    public ChildSelectionProperty() {}
    
    ChildSelectionProperty(String name, Object defaultValue, String[] validators, RobotComponent component,
            String type, Object value) {
        super(name, defaultValue, validators, component);
        this.type = type;
        this.value = value;
        if (value != null) valueComponent = component.getRobotTree().getComponentByName(value.toString());
    }

    @Override
    public Property copy() {
        return new ChildSelectionProperty(name, defaultValue, validators, component, type, value);
    }

    @Override
    public Object getValue() {
        if (valueComponent != null && component.getChildren().contains(valueComponent))
            return valueComponent.getFullName();
        return (value != null) ? value : defaultValue;
    }
    
    @Override
    public Object getDisplayValue() {
        update();
        return combo;
    }

    @Override
    public void _setValue(Object value) {
        this.value = value;
        if (value != null && component != null)
            valueComponent = component.getRobotTree().getComponentByName(value.toString());
    }
    
    @Override
    public void update() {
        super.update();
        Object selection = getValue();
        if (valueComponent != null) selection = valueComponent.getFullName();
        Vector<String> options = component.getChildrenOfTypeNames(type);
        if (combo == null) {
            combo = new JComboBox(options);
        } else {
            combo.setModel(new DefaultComboBoxModel(options));
        }
        if (options.indexOf(selection) != -1) {
            combo.setSelectedItem(selection);
        } else if (defaultValue instanceof Integer &&
                ((Integer) defaultValue) < options.size()) {
            combo.setSelectedIndex((Integer) defaultValue);
        } else if (defaultValue instanceof Integer) {
        } else {
            combo.setSelectedItem(defaultValue);
        }
        value = combo.getSelectedItem();
        if (value != null) valueComponent = component.getRobotTree().getComponentByName(value.toString());
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
