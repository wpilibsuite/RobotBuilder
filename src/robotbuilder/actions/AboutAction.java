
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import robotbuilder.AboutDialog;
import robotbuilder.MainFrame;

/**
 *
 * @author brad
 */
public class AboutAction extends AbstractAction {

    public AboutAction() {
        putValue(Action.NAME, "About...");
//        putValue(Action.SMALL_ICON, getIcon("About16.gif"));
        putValue(Action.SHORT_DESCRIPTION, "About RobotBuilder");
        putValue(Action.LONG_DESCRIPTION, "Information about FRC RobotBuilder");
        putValue(Action.MNEMONIC_KEY, new Integer('A'));
        putValue(Action.ACTION_COMMAND_KEY, "about-command");
        putValue(Action.NAME, "About...");
        putValue(Action.SHORT_DESCRIPTION, "See version information");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
                AboutDialog aboutDialog = new AboutDialog(MainFrame.getInstance().getFrame(),
                                        "About RobotBuilder",
                                        "This application builds robot maps",
                                        1.0);
                aboutDialog.show();
    }
   
}
