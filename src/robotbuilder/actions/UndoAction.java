/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import robotbuilder.MainFrame;

/**
 *
 * @author Sam
 */
public class UndoAction extends AbstractAction {
    public UndoAction() {
        putValue(Action.NAME, "Undo");
        putValue(Action.SHORT_DESCRIPTION, "Reverts your last change");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        MainFrame.getInstance().getCurrentRobotTree().undo();
    }
    
}

