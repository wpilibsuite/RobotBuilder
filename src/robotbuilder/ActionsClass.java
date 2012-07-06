/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import robotbuilder.actions.AboutAction;
import robotbuilder.actions.LabViewAction;
import robotbuilder.actions.JavaAction;
import robotbuilder.actions.CppAction;
import robotbuilder.actions.ExitAction;
import robotbuilder.actions.NewAction;
import robotbuilder.actions.SaveAction;
import robotbuilder.actions.SaveAsAction;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

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
    
    public ActionsClass() {
    }

    public JMenuBar getMenuBar() {
        JMenuBar menu = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(newAction);
        fileMenu.add(saveAction);
        fileMenu.add(saveAsAction);
        fileMenu.add(exitAction);
        menu.add(fileMenu);
        
        JMenu generateMenu = new JMenu("Generate");
        generateMenu.add(cppAction);
        generateMenu.add(javaAction);
        generateMenu.add(labViewAction);
        menu.add(generateMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(aboutAction);
        menu.add(helpMenu);
        
        return menu;
    }
}
