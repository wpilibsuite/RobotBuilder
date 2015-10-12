
package robotbuilder;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author alex
 */
public class NewProjectDialog extends CenteredDialog {

    private final JLabel messageLabel, nameLabel, teamLabel;
    private final JTextField nameField;
    private final JTextField teamField;
    private final JButton createButton, openButton;

    /**
     * Maximum number of digits allowed in the team number.
     */
    private static final int maxTeamNumberLength = 5;

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
        nameField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (!str.matches("[0-9a-zA-Z ]")) {
                    // only numbers, letters, or spaces
                    return;
                }
                if (offs == 0 && str.matches("[0-9 ]")) {
                    // cannot start with a number or space
                    return;
                }
                super.insertString(offs, str, a);
            }
        });
        nameField.setPreferredSize(new Dimension(256, 26));
        c.gridx = 1;
        c.gridy = 1;
        add(nameField, c);

        teamField = new JTextField();
        teamField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String str, AttributeSet attributes) throws BadLocationException {
                if (offset >= maxTeamNumberLength) {
                    // can't have more than 5 digits
                    return;
                }
                if (!str.matches("[0-9]")) {
                    // only numbers allowed
                    return;
                }
                super.insertString(offset, str, attributes);
            }
        });
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
