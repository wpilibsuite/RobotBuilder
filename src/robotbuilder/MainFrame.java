
package robotbuilder;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMessages;
import robotbuilder.palette.Palette;
import robotbuilder.robottree.RobotTree;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author brad
 */
public class MainFrame extends JFrame {

    Palette palette;
    RobotTree robotTree;
    PropertiesDisplay properties;
    JEditorPane help;
    JToolBar toolBar;
    StatusPanel statusPanel;
    NewProjectDialog newProjectDialog;
    private JFrame frame;
    private static MainFrame instance = null;
    public Preferences prefs;
    
    String errorMessage = "Error! Please fix the red components. Hovering over them will provide more details.";
    String goodMessage = "Everything A OK.";

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
        robotTree = new RobotTree(properties, palette);
        help = new JEditorPane();
        help.setEditable(false);
        help.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent he) {
                if (he.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        Set<String> localProtocols = new HashSet<String>();
                        localProtocols.add("jar");
                        localProtocols.add("file");
                        if (localProtocols.contains(he.getURL().getProtocol())) {
                            help.setPage(he.getURL());
                        } else {
                            Utils.browse(he.getURL());
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        setHelp("/help/Introduction.html");
        JScrollPane helpScrollPane = new JScrollPane(help);
        helpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setSize(prefs.getInt("Width", 600), prefs.getInt("Height", 480));
        
        JSplitPane propertiesAndHelp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, properties, helpScrollPane);
        propertiesAndHelp.setDividerLocation(getWidth()/4);
        JSplitPane robotStuff = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, robotTree, propertiesAndHelp);
        robotStuff.setDividerLocation(getWidth()/5);

        add(palette, BorderLayout.WEST);
        add(robotStuff, BorderLayout.CENTER);
        
        ActionsClass actions = new ActionsClass();
        setJMenuBar(actions.getMenuBar());
        
        toolBar = actions.getToolBar();
        add(toolBar, BorderLayout.PAGE_START);
        
        statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
        
        newProjectDialog = new NewProjectDialog(null);
        
        pack();
        
        setSize(prefs.getInt("Width", 600), prefs.getInt("Height", 480));
        
        setStatus(goodMessage);
    }
    
    public void openDefaultFile() {
        String fileName = prefs.get("FileName", "");
        if (fileName.length() > 0) {
            robotTree.load(new File(fileName));
        } else {
            newProjectDialog.display();
        }
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
    
    public final void setHelp(String file) {
        try {
            help.setPage(Utils.getResource(file));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.WARNING, "Nonexistent help file: "+file);
        }
    }
    
    public void setStatus(String status) {
        statusPanel.setStatus(status);
    }
    
    public void updateStatus() {
        if (getCurrentRobotTree().isRobotValid()) {
            if (statusPanel.getStatus() == goodMessage
                    || statusPanel.getStatus() == errorMessage)
                setStatus(goodMessage);
        } else {
            setStatus(errorMessage);
        }
    }

    public void showNewProjectDialog() {
        newProjectDialog.display();
    }
}
