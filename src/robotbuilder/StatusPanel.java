package robotbuilder;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author brad
 */
public class StatusPanel extends Box {
    
    JLabel statusLabel;
    
    public StatusPanel() {
        super(BoxLayout.X_AXIS);
        statusLabel = new JLabel("Status panel");
        add(Box.createHorizontalStrut(10));
        add(statusLabel);
        add(Box.createHorizontalGlue());
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public String getStatus() {
        return statusLabel.getText();
    }
}