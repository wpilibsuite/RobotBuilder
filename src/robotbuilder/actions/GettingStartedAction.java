/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
//                System.out.println(he.getURL().toExternalForm());
//                System.out.println(he.getEventType());
                if (he.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        help.setPage(he.getURL());
                    } catch (IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JFrame frame = new JFrame("Getting Started");
        frame.add(helpScrollPane);
        frame.pack();
        frame.setVisible(true);
    }
    
}
