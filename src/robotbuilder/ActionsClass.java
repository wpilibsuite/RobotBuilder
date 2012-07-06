
package robotbuilder;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.json.JSONException;
import robotbuilder.actions.*;

/**
 *
 * @author brad
 */
public class ActionsClass {
    static File EXPORTERS_PATH = new File("export/");

    AbstractAction exitAction = new ExitAction();
    AbstractAction newAction = new NewAction();
    AbstractAction saveAction = new SaveAction();
    AbstractAction saveAsAction = new SaveAsAction();
    AbstractAction openAction = new OpenAction();
//    AbstractAction cppAction = new CppAction();
//    AbstractAction javaAction = new JavaAction();;
//    AbstractAction labViewAction = new LabViewAction();
    AbstractAction gettingStartedAction = new GettingStartedAction();
    AbstractAction aboutAction = new AboutAction();
//    AbstractAction wiringDiagramAction = new WiringDiagramAction();
    private LinkedList<ExporterAction> exporters;

    public ActionsClass() {
    }

    public JMenuBar getMenuBar() {
        JMenuBar menu = new JMenuBar();
        exporters = getExporters();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(newAction);
        fileMenu.add(saveAction);
        fileMenu.add(saveAsAction);
        fileMenu.add(openAction);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitAction);
        menu.add(fileMenu);
        
        JMenu generateMenu = new JMenu("Generate");
        for (ExporterAction action : exporters) {
            generateMenu.add(action);
        }
//        generateMenu.add(cppAction);
//        generateMenu.add(javaAction);
//        generateMenu.add(labViewAction);
//        generateMenu.add(new JSeparator());
//        generateMenu.add(wiringDiagramAction);
        menu.add(generateMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(gettingStartedAction);
        helpMenu.add(aboutAction);
        menu.add(helpMenu);
        
        return menu;
    }
    
    
    public JToolBar getToolBar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setRollover(true);
        
        bar.add(newAction);
        bar.add(saveAction);
        bar.add(openAction);
        for (ExporterAction action : exporters) {
            if (action.isOnToolbar()) {
                bar.add(action);
            }
        }
//        bar.add(cppAction);
//        bar.add(javaAction);
//        bar.add(labViewAction);
        bar.add(gettingStartedAction);
        return bar;
    }
    
    private LinkedList<ExporterAction> getExporters() {
        File[] dirs = EXPORTERS_PATH.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                for (String path : file.list()) {
                    if (path.equals("ExportDescription.yaml")) return true;
                }
                return false;
            }
        });
        
        LinkedList<ExporterAction> results = new LinkedList<ExporterAction>();
        for (File dir : dirs) {
            try {
                results.add(new ExporterAction(new File(dir.getAbsolutePath()+File.separator+"ExportDescription.yaml")));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ActionsClass.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(ActionsClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return results;
    }
}
