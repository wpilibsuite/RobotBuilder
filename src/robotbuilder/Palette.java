
package robotbuilder;

import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author brad
 */
public class Palette extends JPanel {
    
    public Palette() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Palette");
        DefaultMutableTreeNode sensorsNode = new DefaultMutableTreeNode("Sensors");
        sensorsNode.add(new DefaultMutableTreeNode("Gyro"));
        sensorsNode.add(new DefaultMutableTreeNode("Accelerometer"));
        sensorsNode.add(new DefaultMutableTreeNode("Quadrature encoder"));
        sensorsNode.add(new DefaultMutableTreeNode("Potentiometer"));
        sensorsNode.add(new DefaultMutableTreeNode("LimitSwitch"));
        DefaultMutableTreeNode motorsNode = new DefaultMutableTreeNode("Speed controllers");
        motorsNode.add(new DefaultMutableTreeNode("Victor"));
        motorsNode.add(new DefaultMutableTreeNode("Jaguar"));
        DefaultMutableTreeNode OINode = new DefaultMutableTreeNode("Operator input");
        OINode.add(new DefaultMutableTreeNode("Joystick analog"));
        OINode.add(new DefaultMutableTreeNode("Joystick button"));
        OINode.add(new DefaultMutableTreeNode("Extended I/O digital input"));
        OINode.add(new DefaultMutableTreeNode("Extended I/O analog input"));
        root.add(sensorsNode);
        root.add(motorsNode);
        root.add(OINode);

        JTree paletteTree = new JTree(root);
        paletteTree.setDragEnabled(true);
        
        add(paletteTree);
     }
}
