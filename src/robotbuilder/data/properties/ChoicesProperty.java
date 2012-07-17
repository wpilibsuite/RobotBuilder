/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import javax.swing.JComboBox;
import robotbuilder.data.RobotComponent;

/**
 * A choices property allows selection from a number of known choices.
 *
 * @author Alex Henning
 */
public class ChoicesProperty extends Property {
    protected String[] choices;
    protected Object value;
    protected JComboBox combo;
    
    public ChoicesProperty() {}
    
    public ChoicesProperty(String name, Object defaultValue, String[] validators, RobotComponent component,
            String[] choices, Object value) {
        super(name, defaultValue, validators, component);
        this.choices = choices;
        this.value = value;
    }

    @Override
    public Property copy() {
        return new ChoicesProperty(name, defaultValue, validators, component, choices, value);
    }

    @Override
    public Object getValue() {
        return (value != null) ? value : defaultValue;
    }
    
    @Override
    public Object getDisplayValue() {
        if (combo == null) {
            combo = new JComboBox(getChoices());
        }
        update();
        return combo;
    }

    @Override
    public void _setValue(Object value) {
        this.value = value;
    }
    
    @Override
    public void update() {
        super.update();
        if (combo != null) {
            combo.setSelectedItem(getValue());
            value = combo.getSelectedItem();
        }
    }

    public String[] getChoices() {
        return choices;
    }
    
    public void setChoices(String[] choices) {
        this.choices = choices;
    }
}
