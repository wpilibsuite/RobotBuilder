
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import robotbuilder.RobotTree;
import robotbuilder.exporters.JavaExporter;

/**
 *
 * @author brad
 */
public class JavaAction extends AbstractAction {
    RobotTree robot;

    public JavaAction(RobotTree robot) {
        putValue(Action.NAME, "Java");
        putValue(Action.SHORT_DESCRIPTION, "Generate Java code");
        this.robot = robot;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.append("Generate Java code");
        try {
            new JavaExporter().export(robot, "/home/alex/scratch/RobotBuilderTest");
        } catch (IOException ex) {
            Logger.getLogger(JavaAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
