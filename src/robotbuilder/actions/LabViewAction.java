
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author brad
 */
public class LabViewAction extends AbstractAction {

    public LabViewAction() {
        putValue(Action.NAME, "LabVIEW");
        putValue(Action.SHORT_DESCRIPTION, "Generate LabVIEW code");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.append("Generate LabVIEW");
    }
    
}
