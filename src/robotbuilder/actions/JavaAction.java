/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

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
        System.out.append("Generate Java code");
    }
    
}
