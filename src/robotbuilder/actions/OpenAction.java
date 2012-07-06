
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import robotbuilder.ActionsClass;
import robotbuilder.RobotTree;

/**
 *
 * @author brad
 */
public class OpenAction extends AbstractAction {
    ActionsClass saveAction;
    RobotTree robot;

    public OpenAction(RobotTree robotTree) {
        putValue(Action.NAME, "Open");
        putValue(Action.SHORT_DESCRIPTION, "Open a saved robot map");
        this.robot = robotTree;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("Open selected");
        robot.load("save.json");
    }
    
}
