
package robotbuilder.data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Alex Henning
 */
public class RobotComponent {
    private String name;
    private PaletteComponent base;
    private Map<String, String> configuration = new HashMap<String, String>();

    public RobotComponent(String name, PaletteComponent base) {
        this.name = name;
        this.base = base;
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
    
    public void setName(String name) {
        this.name = name;
    }

    public void setProperty(String key, String val) {
        configuration.put(key, val);
    }
}
