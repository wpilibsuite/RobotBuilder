
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import robotbuilder.data.PaletteComponent;
import robotbuilder.data.Property;

/**
 * The Palette is the set of components that can be used to create the robot
 * map. Each palette item represents a motor, sensor, or other component. These
 * are dragged to the robot tree.
 * @author brad
 */
public class Palette extends JPanel {
    
    private JTree paletteTree;
    static private Palette instance = null;
    
    private Palette() {
        FileReader file;
        try {
            file = new FileReader((new File("PaletteDescription.json")).getAbsolutePath());
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
	paletteTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        paletteTree.setTransferHandler(new PaletteTransferHandler(paletteTree.getTransferHandler()));
        paletteTree.setDragEnabled(true);
        
        for (int i = 0; i < paletteTree.getRowCount(); i++) {
            paletteTree.expandRow(i);
        }
	setLayout(new BorderLayout());
        add(new JScrollPane(paletteTree), BorderLayout.CENTER);
     }
    
    /**
     * Singleton getInstance method returns the single instance of the palette.
     * @return Palette instance
     */
    public static Palette getInstance() {
        if (instance == null)
            instance = new Palette();
        return instance;
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
            if (!child.has("Properties")) {
                try {
                    //TODO: create the PaletteItem here
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
                    root.add(node);
                    createTree(node, jSONObject.getJSONObject(key));
                } catch (JSONException ex) {
                    Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
                PaletteComponent component = createPaletteComponent(key, child);
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(component);
                root.add(node);
            }
        }
    }

    private PaletteComponent createPaletteComponent(String key, JSONObject child) {
        PaletteComponent component = new PaletteComponent(key);
        try {
            JSONObject props = child.getJSONObject("Properties");
            for (Iterator i = props.keys(); i.hasNext();) {
                String name = (String) i.next();
                JSONObject values = props.getJSONObject(name);
                for (Iterator v = values.keys(); v.hasNext(); ) {
                    System.out.println((String)v.next());
                }
                component.addProperty(name, new Property(name, values.getString("Type"), values.optString("Default", null)));
            }
        } catch (JSONException ex) {
            Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
        }
        component.print();
        return component;
    }

    /**
     * A transfer handler for that wraps the default transfer handler of Pallette.
     * 
     * @author Alex Henning
     */
    private class PaletteTransferHandler extends TransferHandler {
        private TransferHandler delegate;
        
        public PaletteTransferHandler(TransferHandler delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
            return delegate.canImport(comp, transferFlavors);
        }
        
        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return false;
        }
        
        @Override
        protected Transferable createTransferable(final JComponent c) {
            JTree tree = (JTree) c;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
            
            if (node.isLeaf()) {
                try {
                    Method method = delegate.getClass().getDeclaredMethod("createTransferable", JComponent.class);
                    method.setAccessible(true);
                    return (Transferable) method.invoke(delegate, c);
                } catch (Exception e) {
                    return super.createTransferable(c);
                }
            } else {
                return null;
            }
        }
        
        @Override
        public void exportAsDrag(JComponent comp, InputEvent event, int action) {
            delegate.exportAsDrag(comp, event, action);
        }
        
        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            try {
                Method method = delegate.getClass().getDeclaredMethod("exportDone", JComponent.class, Transferable.class,
                        int.class);
                method.setAccessible(true);
                method.invoke(delegate, source, data, action);
            } catch (Exception e) {
            }
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return delegate.getSourceActions(c);
        }
        
        @Override
        public Icon getVisualRepresentation(Transferable t) {
            return delegate.getVisualRepresentation(t);
        }
        
        @Override
        public boolean importData(JComponent comp, Transferable t) {
            return delegate.importData(comp, t);
        }
        
        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            return delegate.importData(support);
        }
    }
}
