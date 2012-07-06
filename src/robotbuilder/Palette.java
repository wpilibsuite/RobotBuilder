/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author brad
 */
public class Palette extends JPanel {
    public Palette() {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	JPanel grid = new JPanel(new GridLayout(2, 10));
	add(grid);
        grid.add(new JLabel("OneThing"));
	grid.add(new JLabel("anotherThing"));
	grid.add(new JLabel("Third thing"));
	grid.add(new JLabel("Forth thing"));
	add(Box.createVerticalGlue());
    }
}
