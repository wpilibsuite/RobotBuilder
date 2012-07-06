/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

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
    public void _setValue(Object value) {
        this.value = value;
    }
    
    /**
     * Called to update this with info from the palette.
     * @param parent The property that this is a clone of.
     */
    @Override
    public void update(Property parent) {
        super.update(parent);
        if (parent instanceof TypeSelectionProperty)
            setType(((TypeSelectionProperty) parent).getType());
    }
    
    
    @Override
    public void update() {
        super.update();
        Object selection = getValue();
        Vector<String> options = component.getRobotTree().getNamesOfType(type);
        options.add(0, defaultValue.toString());
        combo = new JComboBox(options);
        combo.setSelectedIndex(0);
        if (options.contains(selection)) {
            combo.setSelectedItem(selection);
        }
        value = combo.getSelectedItem() != null ? combo.getSelectedItem() : value;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
