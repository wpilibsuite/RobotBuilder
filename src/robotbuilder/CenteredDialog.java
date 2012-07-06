/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author brad
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
        Dimension ourSize = getSize();
        if (parent != null) {
            Dimension parentSize = parent.getSize();
            Point parentPostion = parent.getLocation();
            if (parentSize.width > ourSize.width && parentSize.height > ourSize.height) {
                this.setLocation(parentPostion.x + (parentSize.width - ourSize.width) / 2,
                        parentPostion.y + (parentSize.height - ourSize.height) / 2);
            }
        }
        super.show();
    }
}
