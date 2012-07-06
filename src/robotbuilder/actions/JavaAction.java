
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.json.JSONException;
import robotbuilder.MainFrame;
import robotbuilder.RobotTree;
import robotbuilder.exporters.GenericExporter;
import robotbuilder.exporters.JavaExporter;

/**
 *
 * @author brad
 */
public class JavaAction extends AbstractAction {

    public JavaAction() {
        putValue(Action.NAME, "Java");
        putValue(Action.SHORT_DESCRIPTION, "Generate Java code");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("Generate Java code");
        try {
            //        try {
            //            new JavaExporter().export(MainFrame.getInstance().getCurrentRobotTree());
            //        } catch (IOException ex) {
            //            Logger.getLogger(JavaAction.class.getName()).log(Level.SEVERE, null, ex);
            //        }
            GenericExporter exporter = new GenericExporter(new File("export/java/ExportDescription.json"));
            System.out.println(exporter);
            exporter.export(MainFrame.getInstance().getCurrentRobotTree());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JavaAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(JavaAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
