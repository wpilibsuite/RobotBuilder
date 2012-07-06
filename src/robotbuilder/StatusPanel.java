package robotbuilder;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

//TODO: need to draw this with lines

/**
 *
 * @author brad
 */
public class StatusPanel extends Box {
    
    JLabel statusLabel;
    Box statusBox;
    
    public StatusPanel() {
        super(BoxLayout.Y_AXIS);
        add(new JSeparator(SwingConstants.HORIZONTAL));
        
        statusBox = new Box(BoxLayout.X_AXIS);
        statusLabel = new JLabel("Status panel");
        statusBox.add(Box.createHorizontalStrut(10));
        statusBox.add(statusLabel);
        statusBox.add(Box.createHorizontalGlue());
        add(statusBox);
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}
