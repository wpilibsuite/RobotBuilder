
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
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
    private final JLabel trash;
    
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
        trash = new Trash();
        
        BorderLayout layout = new BorderLayout();
        JPanel paletteAndTrash = new JPanel(layout);//JSplitPane(JSplitPane.VERTICAL_SPLIT, palette, trash);
        paletteAndTrash.add(palette, BorderLayout.CENTER);
        paletteAndTrash.add(trash, BorderLayout.SOUTH);
        
        properties = new PropertiesDisplay();
        robotTree = new RobotTree(properties, palette);
        help = new JEditorPane();
        help.setEditable(false);
        help.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent he) {
                System.out.println(he.getURL().toExternalForm());
                System.out.println(he.getEventType());
                if (he.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        help.setPage(he.getURL());
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

        add(paletteAndTrash, BorderLayout.WEST);
        add(robotStuff, BorderLayout.CENTER);
        
        ActionsClass actions = new ActionsClass();
        setJMenuBar(actions.getMenuBar());
        
        statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
        
        toolBar = actions.getToolBar();
        add(toolBar, BorderLayout.PAGE_START);
        
        statusPanel.setStatus("Everything A OK");
        
        pack();
        
        setSize(prefs.getInt("Width", 600), prefs.getInt("Height", 480));
    }
    
    public void openDefaultFile() {
        String fileName = prefs.get("FileName", "");
        if (fileName.length() > 0)
            robotTree.load(new File(fileName));
        else
            robotTree.takeSnapshot();
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
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
