
package robotbuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author brad
 */
public class Palette extends JPanel {
    
    private JTree paletteTree;
    
    public Palette() {
        FileReader file;
        try {
            file = new FileReader("/Users/brad/Dropbox/Projects/NetBeansProjects/RobotBuilder/PaletteDescription.json");
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
        paletteTree = new JTree(root);
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
            JSONObject child;
            try {
                child = (JSONObject) jSONObject.get(key);
            } catch (JSONException ex) {
                Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            if (!child.has("ClassName")) {
                try {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
                    root.add(node);
                    createTree(node, jSONObject.getJSONObject(key));
                } catch (JSONException ex) {
                    Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
                RobotComponent component = createRobotComponent(key, child);
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(component);
                root.add(node);
            }
        }
    }

    private RobotComponent createRobotComponent(String key, JSONObject child) {
        RobotComponent component = new RobotComponent(key);
        try {
            component.addPorts(child.getString("Ports"));
            component.addClassName(child.getString("ClassName"));
        } catch (JSONException ex) {
            Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
        }
        component.print();
        return component;
    }
}
