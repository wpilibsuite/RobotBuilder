
package robotbuilder.data;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.tree.DefaultMutableTreeNode;

import lombok.Getter;

import robotbuilder.data.properties.FileProperty;
import robotbuilder.palette.Palette;
import robotbuilder.robottree.RobotTree;
import robotbuilder.data.properties.Property;

/**
 *
 * @author Alex Henning
 */
@Getter
public class RobotComponent extends DefaultMutableTreeNode {

    private static final Map<String, RobotComponent> registry = new HashMap<>();

    private RobotTree robot;

    private RobotComponentModel model;

    public RobotComponent() {
        super();
        model = new RobotComponentModel();
    }

    /**
     * Creates a new RobotComponent.
     *
     * @param name The name of the new component.
     * @param base The PaletteComponent that will be exported.
     * @param robot The RobotTree that contains this.
     */
    public RobotComponent(String name, PaletteComponent base, RobotTree robot) {
        super();
        if (base == null) {
            throw new NullPointerException("The base component cannot be null!");
        }
        this.model = new RobotComponentModel(name, base);
        this.robot = robot;
        base.getPropertiesKeys().stream()
                .forEach(propName -> {
                    model.getProperties().put(propName, base.getProperty(propName).copy());
                    model.getProperties().get(propName).setComponent(this);
                });
        base.getPropertiesKeys().stream().forEach(propName -> model.getProperties().get(propName).setUnique());
        robot.addName(name);
        registry.put(name, this);
    }

    /**
     * Creates a new RobotComponent.
     *
     * @param name The name of the new component.
     * @param type The type of the new component (like in the {@link Palette}).
     * @param robot The RobotTree that this will be created in.
     */
    public RobotComponent(String name, String type, RobotTree robot) {
        this(name, Palette.getInstance().getItem(type), robot);
    }

    public Property getProperty(String key) {
        return model.getProperties().get(key);
    }

    /**
     * Return the absolute file path as a string to the file property.
     */
    public String getPropertyAbsolutePath(String key) {
        Property prop = getProperty(key);
        if (prop instanceof FileProperty) {
            return ((JFileChooser) prop.getDisplayValue()).getSelectedFile().getAbsolutePath();
        } else {
            return ""; // TODO: No path. Should throw error
        }
    }

    public List<String> getPropertyKeys() {
        return model.getBase().getPropertiesKeys();
    }

    public boolean isValid() {
        return model.getProperties().values().stream()
                .peek(Property::update)
                .allMatch(Property::isValid);
    }

    @Override
    public boolean equals(Object oth) {
        if (oth instanceof RobotComponent) {
            RobotComponent other = (RobotComponent) oth;
            boolean equal = getFullName().equals(other.getFullName())
                    && getBaseType().equals(other.getBaseType())
                    && getProperties().equals(other.getProperties())
                    && getChildren().size() == other.getChildren().size();
            if (equal) {
                for (int i = 0; i < getChildren().size(); i++) {
                    equal = equal
                            && getChildren().elementAt(i).equals(other.getChildren().elementAt(i));
                }
            }
            return equal;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return model.getBase().hashCode();
    }

    @Override
    public String toString() {
        return model.getName();
    }

    public String getName() {
        return model.getName();
    }

    public final void setName(String name) {
        if (model.getName() != null) {
            robot.removeName(getFullName());
            model.setName(name);
            robot.addName(getFullName());
        } else {
            model.setName(name);
        }
    }

    public PaletteComponent getBase() {
        return model.getBase();
    }

    public void setProperty(String key, Object val) {
        model.getProperties().get(key).setValueAndUpdate(val);
    }

    public Map<String, Property> getProperties() {
        return model.getProperties();
    }

    public void setProperties(Map<String, Property> properties) {
        model.setProperties(properties);
    }

    public Vector<RobotComponent> getChildren() {
        if (children != null) {
            return children;
        } else {
            return new Vector<>();
        }
    }

    public void setChildren(Vector<DefaultMutableTreeNode> children) {
        this.children = children;
    }

    public String getBaseType() {
        return model.getBase().getName();
    }

    public void setBaseType(String baseType) {
        model.setBase(Palette.getInstance().getItem(baseType));
    }

    /**
     * @param component The component type to check.
     * @return Whether it can support adding another component of that type.
     */
    public boolean supports(PaletteComponent component) {
        String type = component.getType();
        if (model.getBase().getSupports().containsKey(type)) {
            if (model.getBase().getSupports().get(type) == Palette.UNLIMITED) {
                return true;
            } else {
                int typeCount = 0;
                for (Enumeration i = children(); i.hasMoreElements();) {
                    if (type.equals(((RobotComponent) i.nextElement()).getBase().getType())) {
                        typeCount++;
                    }
                }
                return typeCount < model.getBase().getSupports().get(type);
            }
        }
        return false;
    }

    /**
     * @param data The component type to check.
     * @return Whether it can support adding another component of that type.
     */
    public boolean supports(RobotComponent data) {
        return (children != null && children.contains(data)) || this.supports(data.getBase());
    }

    public boolean supportsChildren() {
        return model.getBase().supportsChildren();
    }

    public void walk(RobotWalker walker) {
        for (Enumeration i = this.children(); i.hasMoreElements();) {
            RobotComponent child = (RobotComponent) i.nextElement();
            child.walk(walker);
        }
        walker.handleRobotComponent(this);
    }

    public <T> T visit(RobotVisitor<T> visitor, Object... extra) {
        return visitor.visit(this, extra);
    }

    public String getSubsystem() {
        if (getBase().getType().equals("Subsystem")) {
            return getName() + " ";
        } else if (getParent() == null) {
            return "";
        } else {
            return ((RobotComponent) getParent()).getSubsystem();
        }
    }

    /**
     * @return The full name of this component including it's subsystem name.
     */
    public String getFullName() {
        if (getBase().getType().equals("Subsystem")) {
            return getName();
        } else {
            return /*getSubsystem() + */getName();
        }
    }

    public Vector<String> getChildrenOfTypeNames(String type) {
        if (children == null) {
            return new Vector<>();
        }
        Vector<String> names = new Vector<>();
        children.forEach(child -> {
            if (type.equals(((RobotComponent) child).getBase().getType())) {
                names.add(((RobotComponent) child).getFullName());
            }
            names.addAll(((RobotComponent) child).getChildrenOfTypeNames(type));
        });
        return names;
    }

    public Vector<String> getChildrenOfComponentNames(String componentName) {
        if (children == null) {
            return new Vector<>();
        }
        Vector<String> names = new Vector<>();
        children.forEach(child -> {
            if (componentName.equals(((RobotComponent) child).getBase().getName())) {
                names.add(((RobotComponent) child).getFullName());
            }
            names.addAll(((RobotComponent) child).getChildrenOfComponentNames(componentName));
        });
        return names;
    }

    public void setRobotTree(RobotTree robot) {
        this.robot = robot;
    }

    public RobotTree getRobotTree() {
        return robot;
    }

    public void addChild(RobotComponent child) {
        if (this.allowsChildren && this.supports(child)) {
            this.add(child);
        }
    }

    public String getErrorMessage() {
        String message = "";
        message = getPropertyKeys().stream()
                .map(this::getProperty)
                .filter(property -> !property.isValid())
                .map(property -> property.getName() + ": " + property.getErrorMessage() + "\n")
                .reduce(message, String::concat);
        if (children != null) {
            for (Object comp : children) {
                String m = ((RobotComponent) comp).getErrorMessage();
                if (m != null && !m.equals("")) {
                    message += "" + ((RobotComponent) comp).getFullName() + ":\n" + m;
                }
            }
        }
        return message;
    }

    /**
     * Handle being deleted by cleaning up validators and so forth.
     */
    public void handleDelete() {
        model.getProperties().values().stream()
                .filter(prop -> prop.getValidators() != null)
                .forEach(prop -> {
                    for (String validatorName : prop.getValidators()) {
                        Validator validator = getRobotTree().getValidator(validatorName);
                        if (validator != null) {
                            validator.delete(this, prop.getName());
                        }
                    }
                });
    }
}
