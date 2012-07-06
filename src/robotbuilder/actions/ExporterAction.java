
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
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
        System.out.println("Exporting with "+exporter.getName());
        try {
            exporter.export(MainFrame.getInstance().getCurrentRobotTree());
        } catch (IOException ex) {
            Logger.getLogger(ExporterAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isOnToolbar() {
        return exporter.isOnToolbar();
    }
}
