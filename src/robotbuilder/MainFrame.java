
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
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
    public Preferences prefs;

    public static MainFrame getInstance() {
        if (instance == null)
            instance = new MainFrame();
        return instance;
    }
    
    private MainFrame() {
        prefs = Preferences.userRoot().node(this.getClass().getName());
        
        setLocation(prefs.getInt("X", 200), prefs.getInt("Y", 300));
        
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
        setHelp("help/Limit Switch.html");
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
        
        setSize(prefs.getInt("Width", 100), prefs.getInt("Height", 100));
    }
    
    public void openDefaultFile() {
        String fileName = prefs.get("FileName", "");
        if (fileName.length() > 0)
            robotTree.load(fileName);
    }
    
    public void closeWindow() {
        if (robotTree.OKToClose()) {
            prefs.putInt("Width", getWidth());
            prefs.putInt("Height", getHeight());
            Point location = this.getLocationOnScreen();
            prefs.putInt("X", location.x);
            prefs.putInt("Y", location.y);
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
    
    public void setHelp(String file) {
        try {
            help.setPage(new File(file).toURL());
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
