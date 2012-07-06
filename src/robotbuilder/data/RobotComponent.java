
package robotbuilder.data;

import java.util.Map;

/**
 *
 * @author Alex Henning
 */
public class RobotComponent {
    private String name;
    private PaletteComponent base;

    public RobotComponent(String name, PaletteComponent base) {
        this.name = name;
        this.base = base;
    }
    
    public Map<String, Property> getProperties() {
        return base.getProperties();
    }
    
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}
