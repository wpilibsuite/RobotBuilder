/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author Alex Henning
 */
public class ComponentSelectionProperty extends Property {
    String componentName;
    Object value;
    RobotComponent valueComponent;
    JComboBox combo;
    
    public ComponentSelectionProperty() {}
    
    public ComponentSelectionProperty(String name, Object defaultValue, String[] validators, RobotComponent component,
            String type, Object value) {
        super(name, defaultValue, validators, component);
        this.componentName = type;
        this.value = value;
        if (value != null) {
            valueComponent = component.getRobotTree().getComponentByName(value.toString());
        }
    }

    @Override
    public Property copy() {
        return new ComponentSelectionProperty(name, defaultValue, validators, component, componentName, value);
    }

    @Override
    public Object getValue() {
        if (valueComponent != null) { return valueComponent.getFullName(); }
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
        if (value != null && component != null && component.getRobotTree().getRoot() != null) {
            valueComponent = component.getRobotTree().getComponentByName(value.toString());
        }
    }
    
    
    @Override
    public void update() {
        super.update();
        Object selection = getValue();
        if (valueComponent != null) { selection = valueComponent.getFullName(); }
        Vector<String> options = component.getRobotTree().getRoot().getChildrenOfComponentNames(componentName);
        options.add(0, defaultValue.toString());
        if (combo == null) {
            combo = new JComboBox(options);
        } else {
            combo.setModel(new DefaultComboBoxModel(options));
        }
        combo.setSelectedIndex(0);
        if (options.contains(selection)) {
            combo.setSelectedItem(selection);
        }
        value = combo.getSelectedItem() != null ? combo.getSelectedItem() : value;
    }
    
    public String getComponentName() {
        return componentName;
    }
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
