
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author brad
 */
public class AboutAction extends AbstractAction {

    public AboutAction() {
        putValue(Action.NAME, "About...");
        putValue(Action.SHORT_DESCRIPTION, "See version information");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("About selected");
    }
   
}
