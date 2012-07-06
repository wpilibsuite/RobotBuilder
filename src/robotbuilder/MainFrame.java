
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.html.HTMLDocument;

/**
 *
 * @author brad
 */
public class MainFrame extends JFrame {

    Palette palette;
    RobotTree robotTree;
    PropertiesDisplay properties;
    JEditorPane help;
    StatusPanel statusPanel;
    JToolBar toolBar;
    private JFrame frame;
    private static MainFrame instance = null;

    public static MainFrame getInstance() {
        if (instance == null)
            instance = new MainFrame();
        return instance;
    }
    
    private MainFrame() {
        frame = this;
        setTitle("FRC RobotBuilder");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        palette = Palette.getInstance();
        properties = new PropertiesDisplay();
        robotTree = new RobotTree(properties);
        help = new JEditorPane();
        try {
            help.setPage(new File("help/Limit Switch.html").toURL());
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        JScrollPane helpScrollPane = new JScrollPane(help);
        helpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JSplitPane propertiesAndHelp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, properties, helpScrollPane);
        propertiesAndHelp.setDividerLocation(300);
        JSplitPane robotStuff = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, robotTree, propertiesAndHelp);
        robotStuff.setDividerLocation(200);
        add(palette, BorderLayout.WEST);
        add(robotStuff, BorderLayout.CENTER);
        
        ActionsClass actions = new ActionsClass();
        setJMenuBar(actions.getMenuBar());
        
        statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
        
        toolBar = actions.getToolBar();
        add(toolBar, BorderLayout.PAGE_START);
        
        statusPanel.setStatus("Everything A OK");
        
        pack();
    }
    
    public void closeWindow() {
        if (robotTree.OKToClose()) {
            setVisible(false);
            System.exit(0);
        }
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    public RobotTree getCurrentRobotTree() {
	return robotTree;
    }
}
