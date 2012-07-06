/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import robotbuilder.ActionsClass;

/**
 *
 * @author brad
 */
public class SaveAction extends AbstractAction {
    ActionsClass saveAction;

    public SaveAction() {
        putValue(Action.NAME, "Save");
        putValue(Action.SHORT_DESCRIPTION, "Save the robot map");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("Save selected");
    }
    
}
