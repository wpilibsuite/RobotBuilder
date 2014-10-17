/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.robottree;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import robotbuilder.MainFrame;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author alex
 */
class DeleteItemAction extends AbstractAction {
    private RobotComponent target;

    public DeleteItemAction(String name, RobotComponent target) {
        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, "Delete this component.");
        this.target = target;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RobotTree tree = MainFrame.getInstance().getCurrentRobotTree();
        tree.delete(target);
        tree.update();
        tree.takeSnapshot();
    }
    
}
