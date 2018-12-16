
package robotbuilder;

import robotbuilder.data.PaletteComponent;
import robotbuilder.palette.Palette;
import robotbuilder.robottree.RobotTree;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 *
 * @author brad
 * @author Sam Carlberg
 */
public class MainFrame extends JFrame {

    Palette palette;
    private final RobotTree robotTree;
    PropertiesDisplay properties;
    JEditorPane help;
    JToolBar toolBar;
    StatusPanel statusPanel;
    NewProjectDialog newProjectDialog;
    private static MainFrame instance = null;
    public Preferences prefs;

    String errorMessage = "Error! Please fix the red components. Hovering over them will provide more details.";
    String goodMessage = "Everything A OK.";

    public static synchronized MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    private MainFrame() {
        prefs = Preferences.userRoot().node(this.getClass().getName());

        setLocation(prefs.getInt("X", 200), prefs.getInt("Y", 300));

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
        help.addHyperlinkListener((HyperlinkEvent he) -> {
            if (he.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                try {
                    Set<String> localProtocols = new HashSet<>();
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
        });
        setHelp("/help/Introduction.html");
        JScrollPane helpScrollPane = new JScrollPane(help);
        helpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setSize(prefs.getInt("Width", 600), prefs.getInt("Height", 480));

        JSplitPane propertiesAndHelp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, properties, helpScrollPane);
        propertiesAndHelp.setDividerLocation(getWidth() / 4);
        JSplitPane robotStuff = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, robotTree, propertiesAndHelp);
        robotStuff.setDividerLocation(getWidth() / 5);
        JSplitPane allStuff = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, palette, robotStuff);
        allStuff.setDividerLocation(170);

        add(allStuff);

        ActionsClass actions = new ActionsClass();
        setJMenuBar(actions.getMenuBar());

        toolBar = actions.getToolBar();
        add(toolBar, BorderLayout.PAGE_START);

        statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        newProjectDialog = new NewProjectDialog(null);

        pack();

        setSize(prefs.getInt("Width", 600), prefs.getInt("Height", 480));
        setLocation(prefs.getInt("X", 0), prefs.getInt("Y", 0));

        setStatus(goodMessage);

        // save location and size on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                prefs.putInt("Width", getWidth());
                prefs.putInt("Height", getHeight());
                Point location = getLocationOnScreen();
                boolean minimized = location.x == -32000;
                prefs.putInt("X", minimized ? 0 : location.x);
                prefs.putInt("Y", minimized ? 0 : location.y);
            }
        });
    }

    public void openDefaultFile() {
        String fileName = prefs.get("FileName", "");
        if (fileName.length() > 0) {
            robotTree.load(new File(fileName));
        } else {
            newProjectDialog.display();
        }
        robotTree.takeSnapshot();
        robotTree.setSaved();
    }

    public void closeWindow() {
        if (robotTree.OKToClose()) {
            setVisible(false);
            System.exit(0);
        }
    }

    public RobotTree getCurrentRobotTree() {
        return robotTree;
    }

    public final void setHelp(String file) {
        try {
            help.setPage(Utils.getResource(file));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.WARNING, "Nonexistent help file: {0}", file);
        }
    }

    public final void setHelp(PaletteComponent c) {
        if (c.isExtension()) {
            try {
                help.setPage(new File(c.getHelpFile()).toURI().toURL());
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.WARNING, "Nonexistent help file: {0}", c.getHelpFile());
            }
        } else {
            setHelp(c.getHelpFile());
        }
    }

    public void setStatus(String status) {
        statusPanel.setStatus(status);
    }

    public void updateStatus() {
        if (getCurrentRobotTree().isRobotValid()) {
            if (statusPanel.getStatus().equals(goodMessage)
                    || statusPanel.getStatus().equals(errorMessage)) {
                setStatus(goodMessage);
            }
        } else {
            setStatus(errorMessage);
        }
    }

    public void showNewProjectDialog() {
        newProjectDialog.display();
    }
}
