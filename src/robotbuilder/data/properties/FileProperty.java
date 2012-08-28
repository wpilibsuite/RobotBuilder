/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import robotbuilder.MainFrame;
import robotbuilder.data.RobotComponent;

/**
 * A file property represents a file or folder.
 *
 * @author Alex Henning
 */
public class FileProperty extends Property {
    protected String value, extension;
    protected boolean folder;
    protected FileDialog chooser;
    
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
            chooser = new FileDialog(MainFrame.getInstance());
            chooser.setDirectory(component.getRobotTree().getFilePath());
            if (folder) {
                chooser.setFilenameFilter(new FilenameFilter() {
                    @Override public boolean accept(File file, String string) {
                        return file.isDirectory();
                    }
                });
            } else {
                chooser.setFilenameFilter(new FilenameFilter() {
                    @Override public boolean accept(File file, String string) {
                        return file.getName().endsWith("."+extension);
                    }
                });
            }
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
//            chooser.setFile(getValue().toString());
//            value = chooser.getFile().toString();
            System.out.println(value);
        }
    }
}
