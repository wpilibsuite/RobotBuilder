
package robotbuilder;

import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import org.yaml.snakeyaml.Yaml;
import robotbuilder.actions.*;

/**
 *
 * @author brad
 */
public class ActionsClass {
    static String EXPORTERS_PATH = "/export/";

    AbstractAction exitAction = new ExitAction();
    AbstractAction newAction = new NewAction();
    AbstractAction saveAction = new SaveAction();
    AbstractAction saveAsAction = new SaveAsAction();
    AbstractAction openAction = new OpenAction();
    AbstractAction gettingStartedAction = new GettingStartedAction();
    AbstractAction aboutAction = new AboutAction();
    AbstractAction undoAction = new UndoAction();
    AbstractAction redoAction = new RedoAction();
    AbstractAction verifyAction = new VerifyAction();
    AbstractAction togglePalettViewAction = new TogglePaletteViewAction();

    private LinkedList<ExporterAction> exporters;

    public ActionsClass() {
    }

    public JMenuBar getMenuBar() {
        JMenuBar menu = new JMenuBar();
        exporters = getExporters();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem  = new JMenuItem(newAction);
        JMenuItem saveItem = new JMenuItem(saveAction);
        JMenuItem openItem = new JMenuItem(openAction);
        JMenuItem saveAsItem = new JMenuItem(saveAsAction);
        
        newItem.setAccelerator(KeyStroke.getKeyStroke("control N"));
        saveItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke("control shift S"));
        openItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
        
        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(openItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitAction);
        menu.add(fileMenu);
        
        
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem  = new JMenuItem(undoAction);
        JMenuItem redoItem = new JMenuItem(redoAction);
        
        undoItem.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        redoItem.setAccelerator(KeyStroke.getKeyStroke("control Y"));
        
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        menu.add(editMenu);
        
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(togglePalettViewAction);
        menu.add(viewMenu);
        
        JMenu exportMenu = new JMenu("Export");
        exportMenu.add(verifyAction);
        exportMenu.add(new JSeparator());
        for (ExporterAction action : exporters) {
            exportMenu.add(action);
        }
        menu.add(exportMenu);

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
        
        JButton undoButton = new JButton(undoAction);
        JButton redoButton = new JButton(redoAction);
        
        bar.add(undoButton);
        bar.add(redoButton);
        bar.add(verifyAction);
        for (ExporterAction action : exporters) {
            if (action.isOnToolbar()) {
                bar.add(action);
            }
        }
        bar.add(gettingStartedAction);
        return bar;
    }
    
    private LinkedList<ExporterAction> getExporters() {
        Yaml yaml = new Yaml();
        InputStreamReader in = new InputStreamReader(Utils.getResourceAsStream(EXPORTERS_PATH+"exporters.yaml"));
        List<String> exporterNames = (List<String>) yaml.load(in);
        
        LinkedList<ExporterAction> results = new LinkedList<ExporterAction>();
        for (String exporter : exporterNames) {
            String resource = EXPORTERS_PATH+exporter+"/";
            results.add(new ExporterAction(resource));
        }
        return results;
    }
}
