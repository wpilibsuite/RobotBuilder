/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import robotbuilder.MainFrame;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.RobotWalker;

/**
 *
 * @author alex
 */
public class VerifyAction extends AbstractAction {

    public VerifyAction() {
        putValue(Action.NAME, "Verify");
        putValue(Action.SHORT_DESCRIPTION, "Verifies that the robot is valid to export.");
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String message = MainFrame.getInstance().getCurrentRobotTree().getRoot().getErrorMessage();
        
        if (message.equals("")) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "Your robot is valid and ready to export.", 
                    "Valid Robot", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    message, "Invalid Robot", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
