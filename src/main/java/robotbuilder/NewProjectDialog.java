
package robotbuilder;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author alex
 */
public class NewProjectDialog extends CenteredDialog {

    JLabel messageLabel, nameLabel, teamLabel;
    JTextField nameField;
    JFormattedTextField teamField;
    JButton createButton, openButton;

    public NewProjectDialog(JFrame frame) {
        super(frame, "New Project Settings");
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        messageLabel = new JLabel("Please fill in the following properties.");
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        add(messageLabel, c);
        c.gridwidth = 1;

        nameLabel = new JLabel("Name:");
        nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = 1;
        add(nameLabel, c);

        teamLabel = new JLabel("Team Number:");
        teamLabel.setHorizontalAlignment(JLabel.RIGHT);
        c.gridx = 0;
        c.gridy = 2;
        add(teamLabel, c);

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(256, 26));
        c.gridx = 1;
        c.gridy = 1;
        add(nameField, c);

        NumberFormat intFormat = NumberFormat.getIntegerInstance();
        intFormat.setGroupingUsed(false);
        teamField = new JFormattedTextField(new NumberFormatter(intFormat));
        teamField.setPreferredSize(new Dimension(256, 26));
        c.gridx = 1;
        c.gridy = 2;
        add(teamField, c);

        createButton = new JButton("Create Project");
        createButton.addActionListener(event -> {
            MainFrame.getInstance().getCurrentRobotTree().newFile(nameField.getText(), teamField.getText());
            setVisible(false);
        });
        c.gridx = 1;
        c.gridy = 3;
        c.fill = GridBagConstraints.BOTH;
        add(createButton, c);

        openButton = new JButton("Open Existing Project");
        openButton.addActionListener(event -> {
            setVisible(false);
            MainFrame.getInstance().getCurrentRobotTree().load();
        });
        c.gridx = 0;
        c.gridy = 3;
        add(openButton, c);

        setResizable(false);
    }

    public void display() {
        nameField.setText("New Robot");
        teamField.setText("0");
        pack();
        setVisible(true);
    }
}
