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
public class SaveAsAction extends AbstractAction {
    ActionsClass saveAsAction;

    public SaveAsAction() {
        putValue(Action.NAME, "Save as...");
        putValue(Action.SHORT_DESCRIPTION, "Save robot map to a new file");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("Save as selected");
    }
    
}
