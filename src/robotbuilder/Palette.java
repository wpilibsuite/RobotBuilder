
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import robotbuilder.data.Macro;
import robotbuilder.data.PaletteComponent;
import robotbuilder.data.Property;
import robotbuilder.data.RobotComponent;

/**
 * The Palette is the set of components that can be used to create the robot
 * map. Each palette item represents a motor, sensor, or other component. These
 * are dragged to the robot tree.
 * @author brad
 */
public class Palette extends JPanel implements TreeSelectionListener {
    public static final int UNLIMITED = -1;
    
    private JTree paletteTree;
    static private Palette instance = null;
    private Map<String, Macro> macros = new HashMap<String, Macro>();
    private Map<String, PaletteComponent> paletteItems = new HashMap<String, PaletteComponent>();
    
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
            generateMacros(json.getJSONObject("Macros"));
            createTree(root, json.getJSONObject("Palette"));
        } catch (JSONException ex) {
            Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
        }
        paletteTree = new JTree(root) {
            @Override
            public String getToolTipText(MouseEvent e) {
                try {
                    TreePath path = getClosestPathForLocation(e.getX(), e.getY());
                    return ((PaletteComponent) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getHelp();
                } catch (ClassCastException ex) { // Ignore folders
                    return null;
                }
            }
        };
        paletteTree.setRootVisible(false);
	paletteTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        paletteTree.setTransferHandler(new PaletteTransferHandler(paletteTree.getTransferHandler()));
        paletteTree.setDragEnabled(true);
        ToolTipManager.sharedInstance().registerComponent(paletteTree);
        paletteTree.addTreeSelectionListener(this);
        
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
     * Get the paletteItem that corresponds to a name.
     * Each item on the palette has a unique name and this method returns the PaletteItem object that
     * corresponds to the given name.
     * @param name The name of the palette item
     * @return The PaletteItem for the given name
     */
    public PaletteComponent getItem(String name) {
        PaletteComponent item = paletteItems.get(name);
        return item;
    }
    
    /**
     * Build the palette tree recursively by traversing the JSON data object
     * @param root The parent tree node
     * @param jSONObject The JSON object that corresponds to this level
     */
    private void createTree(DefaultMutableTreeNode root, JSONObject jSONObject) {
        // Allow order to be imposed on the palette
        Iterator<String> i;
        JSONArray order = jSONObject.optJSONArray("Order");
        if (order != null) {
            i = order.getIterator();
        } else {
            i = jSONObject.keys();
        }
            
        while (i.hasNext()) {
            String key = i.next();
            System.out.println(key);
            JSONObject child;
            try {
                child = (JSONObject) jSONObject.get(key);
            } catch (JSONException ex) {
                Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            if (!child.has("Properties")) {
                try {
                    DefaultMutableTreeNode node = null;
                    if (!key.equals("Hidden") && root != null) {
                        //TODO: create the PaletteItem here
                        node = new DefaultMutableTreeNode(key);
                        root.add(node);
                    }
                    createTree(node, jSONObject.getJSONObject(key));
                } catch (JSONException ex) {
                    Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PaletteComponent component = createPaletteComponent(key, child);
                if (root != null) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(component);
                    root.add(node);
                }
            }
        }
    }

    private PaletteComponent createPaletteComponent(String key, JSONObject child) {
        PaletteComponent component = new PaletteComponent(key);
        paletteItems.put(key, component);
        component.setType(child.optString("Type"));
        component.setHelp(child.optString("Help"));
        // Add Drop support
        JSONObject supports = child.optJSONObject("Supports");
        if (supports != null) {
            for (Iterator i = supports.keys(); i.hasNext();) {
                try {
                    String name = (String) i.next();
                    Object val = supports.get(name);
                    if (val instanceof String) {
                        assert ((String) val).equals("unlimited");
                        component.addSupport(name, UNLIMITED);
                    } else if (val instanceof Integer) {
                        component.addSupport(name, (Integer) val);
                    } else {
                        System.out.println("Unsupported class: "+val.getClass());
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
                
        // Add Properties
        try {
            JSONArray props = macroExpand(child.getJSONArray("Properties"));
            for (Object i : props.getIterable()) {
                JSONObject property = (JSONObject) i;
                Property prop = new Property(property.getString("Name"), property.getString("Type"), property.optString("Default"));
                JSONArray jsonchoices = property.optJSONArray("Choices");
                String[] choices = null;
                if (jsonchoices != null) {
                    choices = new String[jsonchoices.length()];
                    for (int j = 0; j < jsonchoices.length(); j++) {
                        choices[j] = jsonchoices.getString(j);
                    }
                }
                prop.setChoices(choices);
                component.addProperty(property.getString("Name"), prop);
            }
        } catch (JSONException ex) {
            Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
        }
        component.print();
        return component;
    }

    /**
     * Expand the properties based on any loaded macros.
     * @param props The properties to expand
     * @return The expanded properties
     */
    private JSONArray macroExpand(JSONArray props) {
        JSONArray out = new JSONArray();
        for (Object i : props.getIterable()) {
            try {
                JSONObject prop = (JSONObject) i;
                if (macros.containsKey(prop.optString("Type"))) {
                    System.out.println("Expanding property "+prop.getString("Name"));
                    Macro macro = macros.get(prop.optString("Type"));
                    out = macro.expand(prop.getString("Name"), prop, out);
                } else {
                    out = out.put(prop);
                }
            } catch (JSONException ex) {
                Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return out;
    }

    /**
     * Generate a series of macros described in json.
     * @param jsonObject 
     */
    private void generateMacros(JSONObject json) {
        for (Iterator macroNames = json.keys(); macroNames.hasNext(); ) {
            String macroName = (String) macroNames.next();
            JSONObject macroDef = json.optJSONObject(macroName);
            
            Macro macro = new Macro(macroName);
            try {
                JSONArray props = macroDef.getJSONArray("Properties");
                for (Object i : props.getIterable()) {
                    JSONObject property = (JSONObject) i;
                    LinkedList<Object> choices = new LinkedList<Object>();
                    for (Object c : property.optJSONArray("Choices").getIterable()) {
                        choices.add(c);
                    }
                    System.out.println("Adding expansion: "+property.getString("Name") +" "+property.getString("Type")+" "+property.optString("Default")+" "+property.optString("DefaultDefault")+" "+choices);
                    macro.addExpansion(property.getString("Name"), 
                            property.getString("Type"), property.optString("Default"),
                            property.optString("DefaultDefault"), choices);
                }
            } catch (JSONException ex) {
                Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
            }
            macros.put(macroName, macro);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent tse) {
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) paletteTree.getLastSelectedPathComponent();

	if (node == null) {
	    return;
	}
	if (node instanceof DefaultMutableTreeNode) {
            MainFrame.getInstance().setHelp(((PaletteComponent) node.getUserObject()).getHelpFile());
	}
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
