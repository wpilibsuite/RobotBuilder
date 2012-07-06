/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;


/**
 *
 * @author alex
 */
public class RobotComponent {
    PaletteComponent base;

    public RobotComponent(PaletteComponent base) {
        this.base = base;
    }
    
    public String toString() {
        return base.toString()+" 1";
    }
}
