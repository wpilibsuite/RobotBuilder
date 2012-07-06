
package robotbuilder;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author brad
 */
public class MainFrame extends JFrame {

    Palette palette;
    RobotTree robotTree;
    PropertiesDisplay properties;
    StatusPanel statusPanel;
    public static JFrame frame;

    public MainFrame() {
        frame = this;
        setTitle("FRC RobotBuilder");
        
        ActionsClass actions = new ActionsClass();
        setJMenuBar(actions.getMenuBar());
        
        palette = new Palette();
        robotTree = new RobotTree();
        properties = new PropertiesDisplay();
        JSplitPane robotStuff = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, robotTree, properties);
        add(palette, BorderLayout.WEST);
        add(robotStuff, BorderLayout.CENTER);
        
        statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
        
        statusPanel.setStatus("Everything A OK");
        
        pack();
    }
}
