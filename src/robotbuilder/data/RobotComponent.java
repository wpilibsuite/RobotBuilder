
package robotbuilder.data;

import java.util.*;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import robotbuilder.Palette;
import robotbuilder.RobotTree;

/**
 *
 * @author Alex Henning
 */
public class RobotComponent extends DefaultMutableTreeNode {
    private String name;
    private PaletteComponent base;
    private RobotTree robot;
    private Map<String, String> configuration = new HashMap<String, String>();
    private Map<String, JComboBox> combos = new HashMap<String, JComboBox>();
    private Map<String, JFileChooser> filechoosers = new HashMap<String, JFileChooser>();

    public RobotComponent(String name, PaletteComponent base, RobotTree robot) {
        this.name = name;
        this.base = base;
        this.robot = robot;
        robot.addName(name);
    }
    
    public String getProperty(String key) {
        String val = configuration.get(key);
        if (val == null) {
            val = base.getProperties().get(key).getDefault();
        }
        return val;
    }
    
    public String[] getPropertyKeys() {
        return base.getProperties().keySet().toArray(new String[0]);
    }
    
    /**
     * @return The value to render.
     */
    public Object getValue(String key) {
        Property property = base.getProperties().get(key);
        childChange();
        if (property.getChoices() != null) {
            // Provide a JComboBox for choicse
            if (combos.get(key) == null) {
                combos.put(key, new JComboBox(property.getChoices()));
                combos.get(key).setSelectedItem(getProperty(key));
            }
            return combos.get(key);
        } else if (property.getType().equals("Actuator")) {
            return combos.get(key);
        } else if (property.getType().equals("Folder")) {
            // Provide a file chooser for 
            if (filechoosers.get(key) == null) {
                JFileChooser fc;
                System.out.println("File: "+getProperty(key));
                if (getProperty(key).equals("")) {
                    fc = new JFileChooser((String) null);
                } else {
                    fc = new JFileChooser(getProperty(key));
                }
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                filechoosers.put(key, fc);
            }
            return filechoosers.get(key);
        } else {
            return getProperty(key);
        }
    }
    
    @Override
    public void add(MutableTreeNode node) {
        super.add(node);
        childChange();
    }
    @Override
    public void remove(MutableTreeNode node) {
        super.remove(node);
        childChange();
    }
    @Override
    public void remove(int index) {
        super.remove(index);
        childChange();
    }
    @Override
    public void removeAllChildren() {
        super.removeAllChildren();
        childChange();
    }
    
    public void childChange() {
        // Update list of actuators
        for (String key : getBase().getProperties().keySet()) {
            Property property = base.getProperties().get(key);
            if (property.getType().equals("Actuator")) {
                // Provide a JComboBox of actuators
                System.out.println("Updating: "+key);
                Object old;
                if (combos.get(key) != null) {
                    old = combos.get(key).getSelectedItem();
                } else {
                    old = getProperty(key);
                }
                final Vector<String> childrenNames = getChildrenNames();
                JComboBox combo = new JComboBox(childrenNames);
                combos.put(key, combo);
                if (childrenNames.contains(old)) {
                    combo.setSelectedItem(old);
                } else if (childrenNames.size() != 0) {
                    setProperty(key, (String) combo.getSelectedItem());
                }
            }
        }
    }

    public void setValue(String key, String val) {
        if (combos.get(key) != null) combos.get(key).setSelectedItem(val);
        setProperty(key, val);
        System.out.println(key+" => "+val);
    }
    
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
    
    public final void setName(String name) {
        robot.removeName(this.name);
        this.name = name;
        robot.addName(name);
    }
    
    public PaletteComponent getBase() {
        return base;
    }

    public void setProperty(String key, String val) {
        configuration.put(key, val);
    }

    /**
     * @param component The component type to check.
     * @return Whether it can support adding another component of that type.
     */
    public boolean supports(PaletteComponent component) {
        String type = component.getType();
        if (base.getSupports().containsKey(type)) {
            if (base.getSupports().get(type) == Palette.UNLIMITED) {
                return true;
            } else {
                int typeCount = 0;
                for (Enumeration i = children(); i.hasMoreElements();) {
                    if (type.equals(((RobotComponent) i.nextElement()).getBase().getType())) {
                        typeCount++;
                    }
                }
                return typeCount < base.getSupports().get(type);
            }
        }
        return false;
    }

    /**
     * @param component The component type to check.
     * @return Whether it can support adding another component of that type.
     */
    public boolean supports(RobotComponent data) {
        return this.supports(data.getBase());
    }

    /**
     * Encode this RobotComponent and all it's subcomponents as a JSONObject.
     * @return A JSONObject representing this component.
     */
    public JSONObject encodeAsJSON() throws JSONException {
        JSONObject self = new JSONObject();
        self.put("Name", name);
        self.put("Base", base.getName());
        self.put("Configuration", configuration);
        JSONArray children = new JSONArray();
        for (Enumeration i = this.children(); i.hasMoreElements();) {
            RobotComponent child= (RobotComponent) i.nextElement();
            children.put(child.encodeAsJSON());
        }
        self.put("Children", children);
        
        return self;
    }

    /**
     * Decode this RobotComponent and all it's subcomponents from a JSONObject.
     * @return A RobotComponent representing this component.
     */
    public static RobotComponent decodeFromJSON(JSONObject json, RobotTree robot) throws JSONException {
        RobotComponent self = new RobotComponent(json.getString("Name"), 
                Palette.getInstance().getItem(json.getString("Base")),
                robot);
        JSONObject configuration = json.getJSONObject("Configuration");
        System.out.println(configuration);
        System.out.println(configuration.names());
        if (configuration.names() != null) {
            for (Object config : configuration.names().getIterable()) {
                System.out.println((String) config + "=>" + configuration.getString((String) config));
                self.setProperty((String) config, configuration.getString((String) config));
            }
        }
        JSONArray children = json.getJSONArray("Children");
        for (Object child : children.getIterable()) {
            self.add(decodeFromJSON((JSONObject) child, robot));
        }
        return self;
    }
    
    public void walk(RobotWalker walker) {
        for (Enumeration i = this.children(); i.hasMoreElements();) {
            RobotComponent child = (RobotComponent) i.nextElement();
            child.walk(walker);
        }
        walker.handleRobotComponent(this);
    }

    public String getFullName() {
        return name.toUpperCase();
    }
    
    public Vector<String> getChildrenNames() {
        if (children == null) return new Vector<String>();
        Vector<String> names = new Vector<String>();
        for (Object child : children) {
            names.add(((RobotComponent) child).getFullName());
        }
        return names;
    }
}
