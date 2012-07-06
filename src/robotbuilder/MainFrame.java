
package robotbuilder;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

/**
 *
 * @author brad
 */
public class MainFrame extends JFrame {

    Palette palette;
    RobotTree robotTree;
    PropertiesDisplay properties;
    StatusPanel statusPanel;
    JToolBar toolBar;
    public static JFrame frame;

    public MainFrame() {
        frame = this;
        setTitle("FRC RobotBuilder");
        
        palette = Palette.getInstance();
        properties = new PropertiesDisplay();
        robotTree = new RobotTree(properties);
        JSplitPane robotStuff = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, robotTree, properties);
        robotStuff.setDividerLocation(200);
        add(palette, BorderLayout.WEST);
        add(robotStuff, BorderLayout.CENTER);
        
        ActionsClass actions = new ActionsClass(robotTree);
        setJMenuBar(actions.getMenuBar());
        
        statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
        
        toolBar = actions.getToolBar();
        add(toolBar, BorderLayout.PAGE_START);
        
        statusPanel.setStatus("Everything A OK");
        
        pack();
    }
}
