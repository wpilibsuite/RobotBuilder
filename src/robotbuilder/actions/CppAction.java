
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author brad
 */
public class CppAction extends AbstractAction {

    public CppAction() {
        putValue(Action.NAME, "C++");
        putValue(Action.SHORT_DESCRIPTION, "Generate C++ code");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("Generate C++ code");
    }
    
}
