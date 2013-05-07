/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import robotbuilder.utils.RelativePathAccessory;

/**
 *
 * @author Alex Henning
 */
public class FileCellEditor extends AbstractCellEditor implements TableCellEditor, MouseListener {
    JFileChooser fileChooser;
    JLabel button;
    JTable table;
    
    public FileCellEditor(JFileChooser fileChooser) {
        this.fileChooser = fileChooser;
        button = new JLabel();
        button.addMouseListener(this);
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected, int row, int column) {
        this.table = table;
        JFileChooser valueChooser = ((JFileChooser)value);
        if (valueChooser.getSelectedFile() != null)
            button.setText(valueChooser.getSelectedFile().getAbsolutePath());
        else
            button.setText("No File Selected");
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        try {
            System.out.println("Setting value: "+((RelativePathAccessory) fileChooser.getAccessory()).getPathName(fileChooser.getSelectedFile()));
            return ((RelativePathAccessory) fileChooser.getAccessory()).getPathName(fileChooser.getSelectedFile());
        } catch (NullPointerException e) {
            return "";
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        fileChooser.showSaveDialog(table);
        fireEditingStopped();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
