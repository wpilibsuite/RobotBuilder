
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import robotbuilder.MainFrame;
import robotbuilder.exporters.WiringExporter;

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
            new WiringExporter().export(MainFrame.getInstance().getCurrentRobotTree());
        } catch (IOException ex) {
            Logger.getLogger(JavaAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
