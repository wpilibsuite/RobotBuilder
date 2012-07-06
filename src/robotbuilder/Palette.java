
package robotbuilder;

import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author brad
 */
public class Palette extends JPanel {
    
    public Palette() {
        FileReader file;
        try {
            file = new FileReader("/Users/brad/Dropbox/Projects/NetBeansProjects/RobotBuilder/src/robotbuilder/PaletteDescription.json");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        JSONTokener tokener;
        tokener = new JSONTokener(file);
        JSONObject json;
        try {
            json = new JSONObject(tokener);
        } catch (JSONException ex) {
            Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Palette");
        try {
            createTree(root, json.getJSONObject("Palette"));
        } catch (JSONException ex) {
            Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
        }
        JTree paletteTree = new JTree(root);
        paletteTree.setDragEnabled(true);
        
        add(paletteTree);
     }

    /**
     * Build the palette tree recursively by traversing the JSON data object
     * @param root The parent tree node
     * @param jSONObject The JSON object that corresponds to this level
     */
    private void createTree(DefaultMutableTreeNode root, JSONObject jSONObject) {
        for (Iterator i = jSONObject.keys(); i.hasNext(); ) {
            String key = (String) i.next();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
            root.add(node);
            if (!jSONObject.isNull(key)) {
                try {
                    createTree(node, jSONObject.getJSONObject(key));
                } catch (JSONException ex) {
                    Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
