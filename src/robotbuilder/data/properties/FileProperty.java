/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import robotbuilder.MainFrame;
import robotbuilder.data.RobotComponent;
import robotbuilder.utils.RelativePathAccessory;

/**
 * A file property represents a file or folder.
 *
 * @author Alex Henning
 */
public class FileProperty extends Property {
    protected String value, extension;
    protected boolean folder;
    boolean relative;
    protected JFileChooser chooser;
    
    public FileProperty() {}
    
    public FileProperty(String name, Object defaultValue, String[] validators, RobotComponent component,
            String value, String extension, boolean folder) {
        super(name, defaultValue, validators, component);
        this.value = value;
        this.extension = extension;
        this.folder = folder;
    }

    @Override
    public Property copy() {
        return new FileProperty(name, defaultValue, validators, component, value, extension, folder);
    }

    @Override
    public Object getValue() {
        return (value != null) ? value : defaultValue;
    }
    
    @Override
    public Object getDisplayValue() {
        if (chooser == null) {
            chooser = new JFileChooser(component.getRobotTree().getFilePath());
            if (folder) {
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setApproveButtonText("Select folder");
                chooser.setDialogTitle("Choose folder to save project");
            } else {
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setFileFilter(new FileNameExtensionFilter(extension+" file", extension));
            }
            RelativePathAccessory acc = new RelativePathAccessory(MainFrame.getInstance().getCurrentRobotTree());
            acc.setRelative(relative);
            acc.attachTo(chooser);
            // chooser.addActionListener(new ActionListenerImpl(this));
        }
        update();
        return chooser;
    }

    @Override
    public void _setValue(Object value) {
        if (extension != null && value != null && !value.equals("")
                && !((String) value).endsWith("."+extension)) {
            value = ((String) value)+"."+extension;
        }
        this.value = (String) value;
        relative = !(getValue().toString().startsWith("/") // Absolute paths start with "/"
                    || getValue().toString().matches("^.:\\\\.*")); // and the more general form of C:\
    }

    public String getExtension() {
        return extension;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean getFolder() {
        return folder;
    }
    public void setFolder(boolean folder) {
        this.folder = folder;
    }
    
    @Override
    public void update() {
        super.update();
        if (chooser != null && !getValue().equals("")) {
            File file;
            if (getValue().toString().startsWith("/") // Absolute paths start with "/"
                    || getValue().toString().matches("^.:\\\\.*")) { // and the more general form of C:\
                file = new File(getValue().toString());
            } else {
                System.out.println("Parent File: "+MainFrame.getInstance().getCurrentRobotTree().getFilePath());
                file = new File(new File(MainFrame.getInstance().getCurrentRobotTree().getFilePath()).getParentFile(),
                        getValue().toString());
            }
            chooser.setSelectedFile(file);
            //value = chooser.getSelectedFile().toString();
        }
    }
    
}
