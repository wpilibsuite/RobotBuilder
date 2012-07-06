/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Alex Henning
 */
public class FileCellEditor extends AbstractCellEditor implements TableCellEditor {
    JFileChooser fileChooser;

    
    public FileCellEditor(JFileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected, int row, int column) {
        
        //System.out.println("Value: "+value);
        //System.out.println("Type: "+value.getClass());
        
        fileChooser.showDialog(table, "Select");
        return null;
    }

    @Override
    public Object getCellEditorValue() {
        try {
            //System.out.println("Filechooser: "+fileChooser);
            //System.out.println("Selection: "+fileChooser.getSelectedFile());
            //System.out.println("Path: "+fileChooser.getSelectedFile().getPath());
            return fileChooser.getSelectedFile().getPath();
        } catch (NullPointerException e) {
            return "";
        }
    }
    
}
