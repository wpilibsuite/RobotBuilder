/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.utils;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author alex
 */
public class RelativePathAccessory extends JPanel implements PropertyChangeListener {
    File basepath;
    ButtonGroup options;
    JRadioButton relative, absolute;
    JTextField relativePreview, absolutePreview;
    
    public RelativePathAccessory(File basepath) {
        this.basepath = basepath;
        addComponents();
    }
    
    private void addComponents() {
        setLayout(new GridLayout(4, 1));
        
        relative = new JRadioButton("Use path relative to the RobotBuilder save file.");
        relative.setSelected(true);
        relativePreview = new JTextField(".");
        relativePreview.setEditable(false);
        relativePreview.setEnabled(false);
        
        absolute = new JRadioButton("Use absolute path.");
        absolutePreview = new JTextField(basepath.getAbsolutePath());
        absolutePreview.setEditable(false);
        absolutePreview.setEnabled(false);
        
        options = new ButtonGroup();
        options.add(relative);
        options.add(absolute);
        
        add(relative);
        add(relativePreview);
        add(absolute);
        add(absolutePreview);
    }
    
    public void attachTo(JFileChooser chooser) {
        System.out.println("Accessory set.");
        chooser.setAccessory(this);
        chooser.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        //File file = (File) e.getNewValue();
        System.out.println("Testing... "+e);
    }
    
}
