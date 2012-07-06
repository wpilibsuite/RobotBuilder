/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author brad
 */
public class MainFrame extends JFrame {

    Palette palette;
    RobotTree robotTree;
    PropertiesDisplay properties;

    public MainFrame() {
        setTitle("FRC RobotBuilder");
        
        ActionsClass actions = new ActionsClass();
        setJMenuBar(actions.getMenuBar());
        
        palette = new Palette();
        robotTree = new RobotTree();
        properties = new PropertiesDisplay();
        JPanel robotStuff = new JPanel(new BorderLayout());
        robotStuff.add(robotTree, BorderLayout.WEST);
        robotStuff.add(properties, BorderLayout.CENTER);
        add(palette, BorderLayout.WEST);
        add(robotStuff, BorderLayout.CENTER);
        pack();
    }
}
