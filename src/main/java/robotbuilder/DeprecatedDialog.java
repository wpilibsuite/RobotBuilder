
package robotbuilder;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class DeprecatedDialog extends CenteredDialog {

    public DeprecatedDialog(JFrame parent, String applicationName) {
        super(parent, applicationName);
        Box deprecatedWindow = new Box(BoxLayout.Y_AXIS);
        deprecatedWindow.add(new JLabel("Warning: RobotBuilder is deprecated and will not be updated to generate projects for 2027 "));
        deprecatedWindow.add(Box.createVerticalStrut(15));
        ButtonBox buttonBox = new ButtonBox(BoxLayout.X_AXIS);
        JButton okButton = new JButton("OK");
        this.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(e -> setVisible(false));
        buttonBox.add(okButton);
        deprecatedWindow.add(buttonBox);
        this.getContentPane().add(deprecatedWindow, "Center");
        this.pack();
    }
}
