/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

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
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
