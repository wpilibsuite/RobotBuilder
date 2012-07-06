
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
public class SaveAction extends AbstractAction {
    ActionsClass saveAction;
    RobotTree robot;

    public SaveAction(RobotTree robotTree) {
        putValue(Action.NAME, "Save");
        putValue(Action.SHORT_DESCRIPTION, "Save the robot map");
        this.robot = robotTree;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("Save selected");
        robot.save("save.json");
    }
    
}
