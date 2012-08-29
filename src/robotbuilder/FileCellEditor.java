/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JTable;
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
   
            fileChooser.showSaveDialog(table);

        return null;
    }

    @Override
    public Object getCellEditorValue() {
        try {
            return fileChooser.getSelectedFile().getPath();
        } catch (NullPointerException e) {
            return "";
        }
    }
    
}
