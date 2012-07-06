
package robotbuilder.data;

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
    
    @Override
    public String toString() {
        return name;
    }
}
