/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.AbstractCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Alex Henning
 */
public class FileCellEditor extends AbstractCellEditor implements TableCellEditor {
    FileDialog fileChooser;

    
    public FileCellEditor(FileDialog fileChooser) {
        this.fileChooser = fileChooser;
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected, int row, int column) {
        
        fileChooser.show();
        System.out.println(fileChooser.getFile());
        System.out.println(fileChooser.getDirectory()+(fileChooser.getFile() != null ? fileChooser.getFile() : ""));

        return null;
    }

    @Override
    public Object getCellEditorValue() {
        try {
            return fileChooser.getDirectory()+(fileChooser.getFile() != null ? fileChooser.getFile() : "");
        } catch (NullPointerException e) {
            return "";
        }
    }
    
}
