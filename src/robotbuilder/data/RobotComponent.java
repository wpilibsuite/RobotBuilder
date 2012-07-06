
package robotbuilder.data;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.tree.DefaultMutableTreeNode;
import robotbuilder.Palette;
import robotbuilder.RobotTree;

/**
 *
 * @author Alex Henning
 */
public class RobotComponent extends DefaultMutableTreeNode {
    private String name;
    private PaletteComponent base;
    private RobotTree robot;
    private Map<String, String> configuration = new HashMap<String, String>();
    private Map<String, JComboBox> combos = new HashMap<String, JComboBox>();

    public RobotComponent(String name, PaletteComponent base, RobotTree robot) {
        //setName(name);
        this.name = name;
        this.base = base;
        this.robot = robot;
        robot.addName(name);
    }
    
    public Map<String, Property> getProperties() {
        return base.getProperties();
    }
    
    public String getProperty(String key) {
        String val = configuration.get(key);
        if (val == null) {
            val = base.getProperties().get(key).getDefault();
        }
        return val;
    }
    
    /**
     * @return The value to render.
     */
    public Object getValue(String key) {
        if (combos.get(key) == null && base.getProperties().get(key).getChoices() != null) {
            combos.put(key, new JComboBox(base.getProperties().get(key).getChoices()));
        }
        if (combos.get(key) != null) {
            return combos.get(key);
        } else {
            return configuration.get(key);
        }
    }

    public void setValue(String key, String val) {
        if (combos.get(key) != null) combos.get(key).setSelectedItem(val);
        configuration.put(key, val);
        System.out.println(key+" ==> "+val+" ==> "+combos.get(key).getSelectedItem());
    }
    
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
    
    public final void setName(String name) {
        robot.removeName(this.name);
        this.name = name;
        robot.addName(name);
    }
    
    public PaletteComponent getBase() {
        return base;
    }

    public void setProperty(String key, String val) {
        configuration.put(key, val);
    }

    /**
     * @param component The component type to check.
     * @return Whether it can support adding another component of that type.
     */
    public boolean supports(PaletteComponent component) {
        String type = component.getType();
        if (base.getSupports().containsKey(type)) {
            if (base.getSupports().get(type) == Palette.UNLIMITED) {
                return true;
            } else {
                int typeCount = 0;
                for (Enumeration i = children(); i.hasMoreElements();) {
                    if (type.equals(((RobotComponent) i.nextElement()).getBase().getType())) {
                        typeCount++;
                    }
                }
                return typeCount < base.getSupports().get(type);
            }
        }
        return false;
    }

    /**
     * @param component The component type to check.
     * @return Whether it can support adding another component of that type.
     */
    public boolean supports(RobotComponent data) {
        return this.supports(data.getBase());
    }
}
