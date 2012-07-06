
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.json.JSONException;
import robotbuilder.MainFrame;
import robotbuilder.exporters.GenericExporter;

/**
 *
 * @author brad
 */
public class WiringDiagramAction extends AbstractAction {

    public WiringDiagramAction() {
        putValue(Action.NAME, "Wiring diagram");
        putValue(Action.SHORT_DESCRIPTION, "Create a wiring diagram for robot");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.append("Generate Wiring");
        try {
            GenericExporter exporter = new GenericExporter(new File("export/wiring/ExportDescription.json"));
            System.out.println(exporter);
            exporter.export(MainFrame.getInstance().getCurrentRobotTree());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JavaAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(JavaAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
