
package robotbuilder;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.velocity.app.VelocityEngine;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import robotbuilder.data.*;
import robotbuilder.data.properties.*;

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
    private Map<String, PaletteComponent> paletteItems = new HashMap<String, PaletteComponent>();
    private Map<String, Validator> validators = new HashMap<String, Validator>();
    
    private Palette() {
        InputStreamReader in;
        in = new InputStreamReader(Utils.getResourceAsStream("/PaletteDescription.yaml"));
        
        // Apply macros, if any
        StringWriter writer = new StringWriter();
        VelocityEngine ve = new VelocityEngine();
        ve.evaluate(null, writer, "RobotBuilder:PaletteDescription.yaml", in);
        
//        System.out.println(writer.toString());

        Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(PaletteComponent.class, "!Component"));

        // Properties
        constructor.addTypeDescription(new TypeDescription(StringProperty.class, "!StringProperty"));
        constructor.addTypeDescription(new TypeDescription(BooleanProperty.class, "!BooleanProperty"));
        constructor.addTypeDescription(new TypeDescription(IntegerProperty.class, "!IntegerProperty"));
        constructor.addTypeDescription(new TypeDescription(DoubleProperty.class, "!DoubleProperty"));
        constructor.addTypeDescription(new TypeDescription(FileProperty.class, "!FileProperty"));
        constructor.addTypeDescription(new TypeDescription(ChoicesProperty.class, "!ChoicesProperty"));
        constructor.addTypeDescription(new TypeDescription(ChildSelectionProperty.class, "!ChildSelectionProperty"));
        constructor.addTypeDescription(new TypeDescription(TypeSelectionProperty.class, "!TypeSelectionProperty"));
        
        constructor.addTypeDescription(new TypeDescription(DistinctValidator.class, "!DistinctValidator"));
        constructor.addTypeDescription(new TypeDescription(ExistsValidator.class, "!ExistsValidator"));
        constructor.addTypeDescription(new TypeDescription(UniqueValidator.class, "!UniqueValidator"));
        Yaml yaml = new Yaml(constructor);
        Map<String, Object> description = (Map<String, Object>) yaml.load(writer.toString());
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Palette");
        createPalette(root, (ArrayList<Map<String, ArrayList<PaletteComponent>>>) description.get("Palette"));
        loadValidators((ArrayList<Validator>) description.get("Validators"));

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
    private void createPalette(DefaultMutableTreeNode root, ArrayList<Map<String, ArrayList<PaletteComponent>>> sections) {
        // Allow order to be imposed on the palette
        for (Map<String, ArrayList<PaletteComponent>> section : sections) {
            String key = section.keySet().iterator().next();
            ArrayList<PaletteComponent> items = section.get(key);
            System.out.println(key);
            DefaultMutableTreeNode node = null;
            if (!key.equals("Hidden")) {
                node = new DefaultMutableTreeNode(key);
                root.add(node);
            }
            for (PaletteComponent item : items) {
                createPaletteComponent(node, item);
            }
        }
    }
    private void createPaletteComponent(DefaultMutableTreeNode root, PaletteComponent component) {
//        System.out.println("\t"+component.getName());
        paletteItems.put(component.getName(), component);
        
        if (root != null) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(component);
            root.add(node);
        }
    }
    
    private void loadValidators(ArrayList<Validator> validatorsToAdd) {
        for (Validator validator : validatorsToAdd) {
            validators.put(validator.getName(), validator);
        }
    }
    
    public Map<String, Validator> getValidators() {
        Map<String, Validator> copy = new HashMap<String, Validator>();
        for (String key : validators.keySet()) {
            copy.put(key, validators.get(key).copy());
        }
        return copy;
    }

    @Override
    public void valueChanged(TreeSelectionEvent tse) {
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) paletteTree.getLastSelectedPathComponent();

	if (node == null) {
	    return;
	}
	if (node instanceof DefaultMutableTreeNode) {
            try {
                MainFrame.getInstance().setHelp(((PaletteComponent) node.getUserObject()).getHelpFile());
            } catch (ClassCastException ex) { /* Ignore folders */ }
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
