
package robotbuilder;

import java.io.InputStreamReader;

import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.yaml.snakeyaml.Yaml;

import robotbuilder.actions.AboutAction;
import robotbuilder.actions.ExitAction;
import robotbuilder.actions.ExporterAction;
import robotbuilder.actions.GettingStartedAction;
import robotbuilder.actions.NewAction;
import robotbuilder.actions.OpenAction;
import robotbuilder.actions.RedoAction;
import robotbuilder.actions.SaveAction;
import robotbuilder.actions.SaveAsAction;
import robotbuilder.actions.TogglePaletteViewAction;
import robotbuilder.actions.UndoAction;
import robotbuilder.actions.VerifyAction;

/**
 *
 * @author brad
 */
public class ActionsClass {

    public static final String EXPORTERS_PATH = "/export/";

    private final AbstractAction exitAction = new ExitAction();
    private final AbstractAction newAction = new NewAction();
    private final AbstractAction saveAction = new SaveAction();
    private final AbstractAction saveAsAction = new SaveAsAction();
    private final AbstractAction openAction = new OpenAction();
    private final AbstractAction gettingStartedAction = new GettingStartedAction();
    private final AbstractAction aboutAction = new AboutAction();
    private final AbstractAction undoAction = new UndoAction();
    private final AbstractAction redoAction = new RedoAction();
    private final AbstractAction verifyAction = new VerifyAction();
    private final AbstractAction togglePalettViewAction = new TogglePaletteViewAction();

    private LinkedList<ExporterAction> exporters;

    public ActionsClass() {
    }

    public JMenuBar getMenuBar() {
        JMenuBar menu = new JMenuBar();
        exporters = getExporters();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem(newAction);
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
        JMenuItem undoItem = new JMenuItem(undoAction);
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
        exporters.stream().forEach(exportMenu::add);
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
        exporters.stream()
                .filter(ExporterAction::isOnToolbar)
                .forEach(bar::add);
        bar.add(gettingStartedAction);
        return bar;
    }

    private LinkedList<ExporterAction> getExporters() {
        Yaml yaml = new Yaml();
        InputStreamReader in = new InputStreamReader(Utils.getResourceAsStream(EXPORTERS_PATH + "exporters.yaml"));
        List<String> exporterNames = (List<String>) yaml.load(in);

        LinkedList<ExporterAction> results = new LinkedList<>();
        exporterNames.stream()
                .map(exporter -> EXPORTERS_PATH + exporter + "/")
                .map(ExporterAction::new)
                .forEach(results::add);
        return results;
    }
}
