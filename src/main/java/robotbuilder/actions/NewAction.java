
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import robotbuilder.ActionsClass;
import robotbuilder.MainFrame;
import robotbuilder.palette.Palette;

/**
 *
 * @author brad
 */
public class NewAction extends AbstractAction {
    ActionsClass newAction;

    public NewAction() {
        putValue(Action.NAME, "New");
        putValue(Action.SHORT_DESCRIPTION, "Create a new robot map");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
	MainFrame.getInstance().showNewProjectDialog();
    }
    
}
