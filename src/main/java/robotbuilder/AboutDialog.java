
package robotbuilder;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AboutDialog extends CenteredDialog {

    public AboutDialog(JFrame parent, String applicationName, String description, String version, String wpilibVersion) {
        super(parent, applicationName);
        Box aboutWindow = new Box(BoxLayout.Y_AXIS);
        JLabel productName = new JLabel(applicationName);
        Font defaultFont = productName.getFont();
        productName.setFont(new Font(defaultFont.getName(), Font.BOLD, defaultFont.getSize() + 8));
        aboutWindow.add(Box.createVerticalStrut(10));
        aboutWindow.add(productName);
        aboutWindow.add(new JLabel("Version " + version));
        aboutWindow.add(Box.createVerticalStrut(15));
        aboutWindow.add(new JLabel("Exports to WPILib Version " + wpilibVersion));
        aboutWindow.add(Box.createVerticalStrut(15));
        aboutWindow.add(new JLabel(description));
        aboutWindow.add(Box.createVerticalStrut(15));
        aboutWindow.add(new JLabel("FIRST/WPI Robotics Research Group"));
        aboutWindow.add(Box.createVerticalStrut(5));
        ButtonBox buttonBox = new ButtonBox(BoxLayout.X_AXIS);
        JButton okButton = new JButton("OK");
        this.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(e -> setVisible(false));
        buttonBox.add(okButton);
        aboutWindow.add(buttonBox);
        this.getContentPane().add(aboutWindow, "Center");
        this.pack();
    }
}
