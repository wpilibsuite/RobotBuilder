
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import robotbuilder.ActionsClass;
import robotbuilder.MainFrame;

/**
 *
 * @author brad
 */
public class OpenAction extends AbstractAction {
    ActionsClass saveAction;

    public OpenAction() {
        putValue(Action.NAME, "Open");
        putValue(Action.SHORT_DESCRIPTION, "Open a saved robot map");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        MainFrame.getInstance().getCurrentRobotTree().load();
    }
    
}
