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
    String url = "http://wpilib.screenstepslive.com/s/3120/m/7882/l/88538-overview-of-robotbuilder";
    
    public GettingStartedAction() {
        putValue(Action.NAME, "Getting Started");
        putValue(Action.SHORT_DESCRIPTION, "Show the getting started information.");
        
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
        Utils.browse(url);
        MainFrame.getInstance().prefs.putBoolean("getting_started.visible", false);
    }
    
}
