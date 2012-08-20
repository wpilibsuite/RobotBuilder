
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
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
	fileChooser.setFileFilter(new FileNameExtensionFilter("YAML save file", "yml"));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        fileChooser.setSelectedFile(new File(MainFrame.getInstance().getCurrentRobotTree().getFilePath()));
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
            if (!filePath.endsWith(".yml"))
                filePath += ".yml";
            
            MainFrame.getInstance().getCurrentRobotTree().save(filePath);
        }
    }
    
}
