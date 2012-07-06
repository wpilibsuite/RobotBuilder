
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import robotbuilder.ActionsClass;
import robotbuilder.MainFrame;

/**
 *
 * @author brad
 */
public class SaveAsAction extends AbstractAction {
    ActionsClass saveAsAction;

    JFileChooser fileChooser = new JFileChooser();
    public SaveAsAction() {
        putValue(Action.NAME, "Save as...");
        putValue(Action.SHORT_DESCRIPTION, "Save robot map to a new file");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("Save as selected");
        String filePath;
        int result = fileChooser.showSaveDialog(MainFrame.getInstance().getFrame());
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        else if (result == JFileChooser.ERROR_OPTION) {
            return;
        }
        else if (result == JFileChooser.APPROVE_OPTION) {
            filePath = fileChooser.getSelectedFile().getName();
            if (!filePath.endsWith(".yaml"))
                filePath += ".yaml";
            
            MainFrame.getInstance().getCurrentRobotTree().save(filePath);
        }
    }
    
}
