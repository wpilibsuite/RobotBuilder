/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.actions;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import robotbuilder.MainFrame;
import robotbuilder.Utils;

/**
 *
 * @author alex
 */
public class GettingStartedAction extends AbstractAction {
    JEditorPane help = new JEditorPane();
    private final JScrollPane helpScrollPane;
    
    public GettingStartedAction() {
        putValue(Action.NAME, "Getting Started");
        putValue(Action.SHORT_DESCRIPTION, "Show the getting started information.");
        
        help.setEditable(false);
        help.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent he) {
                if (he.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    Utils.browse(he.getURL().toString());
                }
            }
        });
        try {
            help.setPage(Utils.getResource("/help/Introduction.html"));
        } catch (IOException ex) {
            Logger.getLogger(GettingStartedAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        helpScrollPane = new JScrollPane(help);
        helpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                if (MainFrame.getInstance().prefs.getBoolean("getting_started.visible", true)) {
                    actionPerformed(null);
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JFrame frame = new JFrame("Getting Started");
        frame.add(helpScrollPane);
        frame.addWindowListener(new WindowListener() {

            @Override public void windowOpened(WindowEvent we) {
                MainFrame.getInstance().prefs.putBoolean("getting_started.visible", true);
                try {
                    MainFrame.getInstance().prefs.sync();
                } catch (BackingStoreException ex) {
                    Logger.getLogger(GettingStartedAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


            @Override public void windowClosing(WindowEvent we) {
                if (MainFrame.getInstance().prefs.getBoolean("getting_started.warning", true)) {
                    JOptionPane.showMessageDialog(MainFrame.getInstance(),
                            "You are leaving the getting started guide. You can get back at anytime by clicking getting started in the toolbar.", 
                            "Getting Started", JOptionPane.INFORMATION_MESSAGE);
                    MainFrame.getInstance().prefs.putBoolean("getting_started.warning", false);
                }

                MainFrame.getInstance().prefs.putBoolean("getting_started.visible", false);
                try {
                    MainFrame.getInstance().prefs.sync();
                } catch (BackingStoreException ex) {
                    Logger.getLogger(GettingStartedAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            // Don't care about these.
            @Override public void windowClosed(WindowEvent we) {}
            @Override public void windowIconified(WindowEvent we) {}
            @Override public void windowDeiconified(WindowEvent we) {}
            @Override public void windowActivated(WindowEvent we) {}
            @Override public void windowDeactivated(WindowEvent we) {}
        });
        frame.pack();
        frame.setVisible(true);
    }
    
}
