/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import robotbuilder.MainFrame;
import robotbuilder.palette.Palette;

/**
 *
 * @author alex
 */
public class TogglePaletteViewAction extends AbstractAction {
    Palette.Layouts view = Palette.Layouts.ICONS;
    
    public TogglePaletteViewAction() {
        putValue(Action.NAME, "Switch to tree view");
        putValue(Action.SHORT_DESCRIPTION, "Changes the view of the palette.");
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        putValue(Action.NAME, "Switch to "+view.toString().toLowerCase()+" view");
        if (view.equals(Palette.Layouts.TREE)) view = Palette.Layouts.ICONS;
        else if (view.equals(Palette.Layouts.ICONS)) view = Palette.Layouts.TREE;
        Palette.getInstance().setView(view);
    }
    
}
