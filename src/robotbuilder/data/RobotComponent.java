
package robotbuilder.data;

import java.util.HashMap;
import java.util.Map;
import robotbuilder.RobotTree;

/**
 *
 * @author Alex Henning
 */
public class RobotComponent {
    private String name;
    private PaletteComponent base;
    private RobotTree robot;
    private Map<String, String> configuration = new HashMap<String, String>();

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

    public boolean supports(PaletteComponent component) {
        String type = component.getType();
        if (base.getSupports().containsKey(type)) {
            return true;
        }
        return false;
    }

    public boolean supports(RobotComponent data) {
        return this.supports(data.getBase());
    }
}
