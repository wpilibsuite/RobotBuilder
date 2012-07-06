
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import robotbuilder.MainFrame;
import robotbuilder.RobotTree;
import robotbuilder.exporters.JavaExporter;

/**
 *
 * @author brad
 */
public class JavaAction extends AbstractAction {
    RobotTree robot;

    public JavaAction() {
        putValue(Action.NAME, "Java");
        putValue(Action.SHORT_DESCRIPTION, "Generate Java code");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.append("Generate Java code");
        try {
            new JavaExporter().export(MainFrame.getInstance().getCurrentRobotTree());
        } catch (IOException ex) {
            Logger.getLogger(JavaAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
