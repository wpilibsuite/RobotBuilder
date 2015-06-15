
package robotbuilder;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author brad
 * @author Sam Carlberg
 */
public class CenteredDialog extends JDialog {

    JFrame parent;

    public CenteredDialog(JFrame parent, String dialogTitle) {
        super(parent, dialogTitle, true);
        this.parent = parent;
    }

    public CenteredDialog(JFrame parent) {
        super(parent);
        this.parent = parent;
    }

    @Override
    public void show() {
        setLocationRelativeTo(parent);
        super.show();
    }
}
