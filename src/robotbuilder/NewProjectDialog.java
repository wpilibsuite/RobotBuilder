/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.text.*;

/**
 *
 * @author alex
 */
public class NewProjectDialog extends JDialog {
    
    JLabel messageLabel, nameLabel, teamLabel;
    JTextField nameField;
    JFormattedTextField teamField;
    JButton createButton, openButton;
    
    public NewProjectDialog(Frame frame) {
        super(frame, "New Project Settings", true);
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
        final NewProjectDialog self = this;
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                MainFrame.getInstance().getCurrentRobotTree().newFile(nameField.getText(), teamField.getText());
                self.setVisible(false);
            }
        });
        c.gridx = 1;
        c.gridy = 3;
        c.fill = GridBagConstraints.BOTH;
        add(createButton, c);
        
        openButton = new JButton("Open Existing Project");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                self.setVisible(false);
                MainFrame.getInstance().getCurrentRobotTree().load();
            }
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
