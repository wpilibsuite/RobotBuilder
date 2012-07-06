
package robotbuilder;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

/**
 *
 * @author brad
 */
public final class ButtonBox extends Box {

    int dir;

    public ButtonBox(int dir) {
        super(dir);
        this.dir = dir;
        addGlue();
    }

    public ButtonBox() {
        this(BoxLayout.X_AXIS);
    }

    public void add(JComponent item) {
        super.add(item);
        addGlue();
    }

    void addGlue() {
        if (dir == BoxLayout.X_AXIS) {
            super.add(Box.createHorizontalGlue());
        } else {
            super.add(Box.createVerticalGlue());
        }
    }
}
