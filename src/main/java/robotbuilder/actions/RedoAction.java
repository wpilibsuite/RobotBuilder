
package robotbuilder.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import robotbuilder.MainFrame;

/**
 *
 * @author Sam
 */
public class RedoAction extends AbstractAction {

    public RedoAction() {
        putValue(Action.NAME, "Redo");
        putValue(Action.SHORT_DESCRIPTION, "Repeats the last action you undid");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        MainFrame.getInstance().getCurrentRobotTree().redo();
    }

}
