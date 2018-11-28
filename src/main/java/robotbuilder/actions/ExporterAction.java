
package robotbuilder.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import robotbuilder.MainFrame;
import robotbuilder.exporters.GenericExporter;

/**
 *
 * @author brad
 */
public class ExporterAction extends AbstractAction {

    GenericExporter exporter;

    public ExporterAction(String description) {
        exporter = new GenericExporter(description);
        putValue(Action.NAME, exporter.getName());
        putValue(Action.SHORT_DESCRIPTION, exporter.getDescription());
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        boolean newProject = false;
        MainFrame.getInstance().setCursor(Cursor.WAIT_CURSOR);
        try {
            newProject = exporter.export(MainFrame.getInstance().getCurrentRobotTree());
        } catch (IOException ex) {
            Logger.getLogger(ExporterAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        MainFrame.getInstance().setCursor(Cursor.DEFAULT_CURSOR);
        if (newProject)
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                "Project successfully exported for the first time. Open project directory in"
                + " VS Code.", "New Project Exported",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean isOnToolbar() {
        return exporter.isOnToolbar();
    }
}
