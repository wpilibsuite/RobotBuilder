
package robotbuilder.data;

import java.io.File;
import java.util.*;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.tree.DefaultMutableTreeNode;
import robotbuilder.Palette;
import robotbuilder.RobotTree;
import robotbuilder.data.UniqueValidator.InvalidException;

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

    public RobotComponent() {
        super();
    }
    
    public RobotComponent(String name, PaletteComponent base, RobotTree robot) {
        super();
        this.name = name;
        this.base = base;
        this.robot = robot;
        robot.addName(name);
    }
    
    public RobotComponent(String name, PaletteComponent base, RobotTree robot, boolean autogenerate) throws InvalidException {
        this(name, base, robot);
        
        if (autogenerate) {
            // Initialize validation of the properties
            for (String property : getBase().getPropertiesKeys()) {
                System.out.println(property);
                String validatorName = getBase().getProperty(property).getValidator();
                System.out.println(validatorName);
                Validator validator = robot.getValidator(validatorName);
                System.out.println(validator);
                if (validator != null) {
                    validator.update(this, property, getProperty(property));
                    System.out.println("Updated..");
                }
            }
        }
    }
    
    public String getProperty(String key) {
        String val = configuration.get(key);
        if (val == null) {
            val = base.getProperty(key).getDefault();
        }
        return val;
    }
    
    public String[] getPropertyKeys() {
        return base.getPropertiesKeys().toArray(new String[0]);
    }
    
    /**
     * @return The value to render.
     */
    public Object getValue(String key) {
        Property property = base.getProperty(key);
        updateComboBoxes();
        if (property.getChoices() != null) {
            // Provide a JComboBox for choicse
            if (combos.get(key) == null) {
                combos.put(key, new JComboBox(property.getChoices()));
                combos.get(key).setSelectedItem(getProperty(key));
            }
            return combos.get(key);
        } else if (property.getType().equals("Boolean")) {
            return getProperty(key).equals("true");
        } else if (property.getType().equals("Double")) {
            return Double.parseDouble(getProperty(key));
        } else if (property.getType().equals("Integer")) {
            return Integer.parseInt(getProperty(key));
        } else if (property.getType().equals("Actuator") ||
                property.getType().equals("Sensor") ||
                property.getType().equals("Joystick") ||
                property.getType().equals("Command") ||
                property.getType().equals("Subsystem")) {
            return combos.get(key);
        } else if (property.getType().equals("File")) {
            // Provide a file chooser for files
            if (filechoosers.get(key) == null) {
                JFileChooser fc = new JFileChooser();
//                System.out.println("File: "+getProperty(key));
                if (!getProperty(key).equals("")) {
                    fc.setSelectedFile(new File(getProperty(key)));
                }
                fc.setSelectedFile(new File(getProperty(key)));
                filechoosers.put(key, fc);
            }
            return filechoosers.get(key);
        } else if (property.getType().equals("Folder")) {
            // Provide a file chooser for folders
            if (filechoosers.get(key) == null) {
                JFileChooser fc = new JFileChooser();;
//                System.out.println("Folder: "+getProperty(key));
                if (!getProperty(key).equals("")) {
                    fc.setSelectedFile(new File(getProperty(key)));
                }
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                filechoosers.put(key, fc);
            }
            return filechoosers.get(key);
        } else {
            return getProperty(key);
        }
    }

    public void setValue(String key, String val) {
        if (combos.get(key) != null) combos.get(key).setSelectedItem(val);
        if(val != getProperty(key)){
            System.out.println("Snapshot of "+this+" taken: tKey: "+key+"\tVal: "+val);
            setProperty(key, val);
            robot.takeSnapshot();
        }

//        System.out.println(key+" => "+val);
    }
    
    public void updateComboBoxes() {
        // Update list of actuators
        for (String key : getBase().getPropertiesKeys()) {
            Property property = base.getProperty(key);
            if (property.getType().equals("Actuator") ||
                    property.getType().equals("Sensor")) {
                // Provide a JComboBox for this actuator or sensor
                String old;
                if (combos.get(key) != null) {
                    old = (String) combos.get(key).getSelectedItem();
                } else {
                    old = getProperty(key);
                }
                final Vector<String> childrenNames = getChildrenOfTypeNames(property.getType());
                JComboBox combo = new JComboBox(childrenNames);
                combos.put(key, combo);
                if (childrenNames.contains(old)) {
                    combo.setSelectedItem(old);
                } else if (!childrenNames.isEmpty()) {
                    int defaultSelection = Integer.parseInt(getBase().getProperty(key).getDefault());
                    if (defaultSelection < childrenNames.size()) {
                        combo.setSelectedIndex(defaultSelection);
                    }
                    setProperty(key, (String) combo.getSelectedItem());
                }
            } else if (property.getType().equals("Joystick") ||
                    property.getType().equals("Command") ||
                    property.getType().equals("Subsystem")) {
                String old;
                if (combos.get(key) != null) {
                    old = (String) combos.get(key).getSelectedItem();
                } else {
                    old = getProperty(key);
                }
                Vector<String> choices = null;
                if (property.getType().equals("Joystick")) {
                    choices = robot.getJoystickNames();
                } else if (property.getType().equals("Command")) {
                    choices = robot.getCommandNames();
                    choices.add(0, "None");
                } else if (property.getType().equals("Subsystem")) {
                    choices = robot.getSubsystemNames();
                    choices.add(0, "None");
                }
                JComboBox combo = new JComboBox(choices);
                combos.put(key, combo);
                if (choices.contains(old)) {
                    combo.setSelectedItem(old);
                } else if (!choices.isEmpty()) {
                    int defaultSelection = Integer.parseInt(getBase().getProperty(key).getDefault());
                    if (defaultSelection < choices.size()) {
                        combo.setSelectedIndex(defaultSelection);
                    }
                    setProperty(key, (String) combo.getSelectedItem());
                }
            }
        }
    }
    
    public boolean isValid() {
        for (String propName : getPropertyKeys()) {
            Validator validator = robot.getValidator(getBase().getProperty(propName).getValidator());
            if (validator != null && !validator.isValid(this, propName)) {
                return false;
            }
        }
        return true;
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
//        System.out.println(key+" => "+val);
    }
    
    public Map<String, String> getConfiguration() {
        return configuration;
    }
    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }
    
    public Vector<DefaultMutableTreeNode> getChildren() {
        if (children != null)
            return children;
        else
            return new Vector<DefaultMutableTreeNode>();
    }
    public void setChildren(Vector<DefaultMutableTreeNode> children) {
        this.children = children;
    }
    
    public String getBaseType() {
        return base.getName();
    }
    public void setBaseType(String baseType) {
        this.base = Palette.getInstance().getItem(baseType);
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
        return (children != null && children.contains(data)) || this.supports(data.getBase());
    }
    
    public void walk(RobotWalker walker) {
        for (Enumeration i = this.children(); i.hasMoreElements();) {
            RobotComponent child = (RobotComponent) i.nextElement();
            child.walk(walker);
        }
        walker.handleRobotComponent(this);
    }
    
    public <T> T visit(RobotVisitor<T> visitor, Object...extra) {
        return visitor.visit(this, extra);
    }

    public String getSubsystem() {
        if (getBase().getType().equals("Subsystem")) 
            return getName()+" ";
        else if (getParent() == null)
            return "";
        else
            return ((RobotComponent) getParent()).getSubsystem();
    }
    
    /**
     * @return The full name of this component including it's subsystem name.
     */
    public String getFullName() {
        return getSubsystem()+name;
    }
    
    public Vector<String> getChildrenOfTypeNames(String type) {
        if (children == null) return new Vector<String>();
        Vector<String> names = new Vector<String>();
        for (Object child : children) {
            if (type.equals(((RobotComponent) child).getBase().getType())) {
                names.add(((RobotComponent) child).getFullName());
            }
        }
        return names;
    }

    public void setRobotTree(RobotTree robot) {
        this.robot = robot;
    }
}
