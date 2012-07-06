
package robotbuilder;

import javax.swing.*;
import robotbuilder.actions.*;

/**
 *
 * @author brad
 */
public class ActionsClass {

    AbstractAction exitAction = new ExitAction();
    AbstractAction newAction = new NewAction();
    AbstractAction saveAction = new SaveAction();
    AbstractAction saveAsAction = new SaveAsAction();
    AbstractAction cppAction = new CppAction();
    AbstractAction javaAction = new JavaAction();
    AbstractAction labViewAction = new LabViewAction();
    AbstractAction aboutAction = new AboutAction();
    AbstractAction wiringDiagramAction = new WiringDiagramAction();
    
    public ActionsClass() {
    }

    public JMenuBar getMenuBar() {
        JMenuBar menu = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(newAction);
        fileMenu.add(saveAction);
        fileMenu.add(saveAsAction);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitAction);
        menu.add(fileMenu);
        
        JMenu generateMenu = new JMenu("Generate");
        generateMenu.add(cppAction);
        generateMenu.add(javaAction);
        generateMenu.add(labViewAction);
        generateMenu.add(new JSeparator());
        generateMenu.add(wiringDiagramAction);
        menu.add(generateMenu);

        JMenu helpMenu = new JMenu("Help");
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
        bar.add(cppAction);
        bar.add(javaAction);
        bar.add(labViewAction);
        return bar;
    }
}
