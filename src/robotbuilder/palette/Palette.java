
package robotbuilder.palette;

import java.awt.CardLayout;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import robotbuilder.Utils;
import robotbuilder.data.*;
import robotbuilder.data.properties.*;

/**
 * The Palette is the set of components that can be used to create the robot
 * map. Each palette item represents a motor, sensor, or other component. These
 * are dragged to the robot tree.
 * @author brad
 */
public class Palette extends JPanel  {
    public static final int UNLIMITED = -1;
    
    private TreeModel model;
    static private Palette instance = null;
    private Map<String, PaletteComponent> paletteItems = new HashMap<String, PaletteComponent>();
    private Map<String, Validator> validators = new HashMap<String, Validator>();
    
    public enum Layouts {
        TREE, ICONS;
    }
    
    private Palette() {
        InputStreamReader in;
        in = new InputStreamReader(Utils.getResourceAsStream("/PaletteDescription.yaml"));
        
        // Apply macros, if any
        StringWriter writer = new StringWriter();
        VelocityEngine ve = new VelocityEngine();
        Context context = new VelocityContext();
        context.put("home", System.getProperty("user.home").replace("\\", "\\\\")+Matcher.quoteReplacement(File.separator));
        ve.evaluate(context, writer, "RobotBuilder:PaletteDescription.yaml", in);
        
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
        constructor.addTypeDescription(new TypeDescription(ComponentSelectionProperty.class, "!ComponentSelectionProperty"));
        constructor.addTypeDescription(new TypeDescription(ParentProperty.class, "!ParentProperty"));
        
        constructor.addTypeDescription(new TypeDescription(DistinctValidator.class, "!DistinctValidator"));
        constructor.addTypeDescription(new TypeDescription(ExistsValidator.class, "!ExistsValidator"));
        constructor.addTypeDescription(new TypeDescription(UniqueValidator.class, "!UniqueValidator"));
        Yaml yaml = new Yaml(constructor);
        Map<String, Object> description = (Map<String, Object>) yaml.load(writer.toString());
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Palette");
        createPalette(root, (ArrayList<Map<String, ArrayList<PaletteComponent>>>) description.get("Palette"));
        loadValidators((ArrayList<Validator>) description.get("Validators"));
        model = new DefaultTreeModel(root);
        
	setLayout(new CardLayout());
        JScrollPane iconView = new JScrollPane(new IconView(this));
        iconView.getVerticalScrollBar().setUnitIncrement(32);
        add(iconView, Layouts.ICONS.toString());
        add(new JScrollPane(new TreeView(this)), Layouts.TREE.toString());
     }
    
    /**
     * Singleton getInstance method returns the single instance of the palette.
     * @return Palette instance
     */
    public static Palette getInstance() {
        if (instance == null) {
            instance = new Palette();
        }
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
    
    public Collection<PaletteComponent> getPaletteComponents() {
        return paletteItems.values();
    }
    
    public TreeModel getPaletteModel() {
        return model;
    }

    public void setView(Layouts view) {
        ((CardLayout) getLayout()).show(this, view.toString());
    }
}
