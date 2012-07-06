
package robotbuilder.data;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import robotbuilder.Palette;
import robotbuilder.RobotTree;
import robotbuilder.data.properties.Property;

/**
 *
 * @author Alex Henning
 */
public class RobotComponent extends DefaultMutableTreeNode {
    private String name;
    private PaletteComponent base;
    private RobotTree robot;
    private Map<String, Property> properties;
    

    public RobotComponent() {
        super();
    }
    
    /**
     * Creates a new RobotComponent.
     * @param name The name of the new component.
     * @param base The PaletteComponent that will be exported.
     * @param robot The RobotTree that contains this.
     */
    public RobotComponent(String name, PaletteComponent base, RobotTree robot) {
        super();
        this.name = name;
        this.base = base;
        this.robot = robot;
        properties = new HashMap<String, Property>();
        for (String propName : base.getPropertiesKeys()) {
            properties.put(propName, base.getProperty(propName).copy());
            properties.get(propName).setComponent(this);
        }
        for (String propName : base.getPropertiesKeys()) {
            properties.get(propName).setUnique();
        }
        robot.addName(name);
    }
    
    /**
     * Creates a new RobotComponent.
     * @param name The name of the new component.
     * @param type The type of the new component (like in the {@link Palette}).
     * @param robot The RobotTree that this will be created in.
     */
    public RobotComponent(String name, String type, RobotTree robot) {
        this(name, Palette.getInstance().getItem(type), robot);
    }
    
    /**
     * Gets the PaletteComponent of the specified type.
     * @param type The type of the component.
     */
    public static PaletteComponent getPaletteComponent(String type) {
        return Palette.getInstance().getItem(type);
    }
    
    public Property getProperty(String key) {
        return properties.get(key);
    }
    
    public String[] getPropertyKeys() {
        return base.getPropertiesKeys().toArray(new String[0]);
    }
    
    public boolean isValid() {
        for (Property property : properties.values()) {
            property.update();
            if (!property.isValid()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(Object oth) {
        if (oth instanceof RobotComponent) {
            RobotComponent other = (RobotComponent) oth;
            boolean equal = getFullName().equals(other.getFullName()) &&
                    getBaseType().equals(other.getBaseType()) &&
                    getProperties().equals(other.getProperties()) &&
                    getChildren().size() == other.getChildren().size();
            if (equal) {
                for (int i = 0; i < getChildren().size(); i++) {
                    equal = equal && 
                            getChildren().elementAt(i).equals(other.getChildren().elementAt(i));
                }
            }
            return equal;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return base.hashCode();
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

    public void setProperty(String key, Object val) {
        properties.get(key).setValue(val);
//        System.out.println(key+" => "+val);
    }
    
    public Map<String, Property> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
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
        if (getBase().getType().equals("Subsystem")) 
            return name;
        else
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

    public RobotTree getRobotTree() {
        return robot;
    }
    
    public void addChild(RobotComponent child) {
        if(this.allowsChildren && this.supports(child)) this.add(child);
    }
}
