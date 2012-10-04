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

/**
 *
 * @author Alex Henning
 */
public class FileCellEditor extends AbstractCellEditor implements TableCellEditor, MouseListener {
    JFileChooser fileChooser;
    JLabel button;
    JTable table;
    
    public FileCellEditor(JFileChooser fileChooser) {
        this.fileChooser = new JFileChooser();
        button = new JLabel();
        button.addMouseListener(this);
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected, int row, int column) {
        this.table = table;
        button.setText(value.toString());
        JFileChooser valueChooser = ((JFileChooser)value);
        fileChooser.setSelectedFile(valueChooser.getSelectedFile());
        fileChooser.setFileSelectionMode(valueChooser.getFileSelectionMode());
        fileChooser.setApproveButtonText(valueChooser.getApproveButtonText());
        fileChooser.setDialogTitle(valueChooser.getDialogTitle());
        fileChooser.setFileFilter(valueChooser.getFileFilter());
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        try {
            return fileChooser.getSelectedFile().getPath();
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
