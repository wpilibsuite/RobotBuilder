
package robotbuilder.data;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import robotbuilder.palette.Palette;
import robotbuilder.data.properties.Property;
import robotbuilder.extensions.Extensions;
import robotbuilder.extensions.ExtensionComponent;

/**
 *
 * @author brad
 * @author Alex Henning
 */
public class PaletteComponent implements Serializable {

    private String name; //  The name of the palette component
    // The metadata for the component (type, etc.)
    private String type; // The type of the data
    private String help; // The help text for this component.
    /**
     * Type and quantity of children that this type of component can support.
     */
    private Map<String, Integer> supports = new HashMap<>();
    /**
     * String representations of PaletteComponent types
     */
    private static Map<String, PaletteComponent> types = new HashMap<>();
    // set of properties for the component
    private Map<String, Property> properties = new HashMap<>();
    private List<String> propertiesKeys = new ArrayList<>();

    private boolean isExtension = false;

    public PaletteComponent() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PaletteComponent getComponentByName(String type) {
        return (PaletteComponent) types.get(type);
    }

    public void setType(String type) {
        this.type = type;
        types.put(type, this);
    }

    public String getType() {
        return type;
    }

    public void setSupports(Map<String, Integer> supports) {
        this.supports = supports;
    }

    public Map<String, Integer> getSupports() {
        return supports;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getHelp() {
        return help;
    }

    public void setProperties(List<Property> properties) {
        this.properties = new HashMap<>();
        this.propertiesKeys = new ArrayList<>();
        properties.forEach((property) -> {
            this.properties.put(property.getName(), property);
            this.propertiesKeys.add(property.getName());
        });
    }

    public List<Property> getProperties() {
        List<Property> out = new ArrayList<>();
        propertiesKeys.stream()
                .map(properties::get)
                .forEach(out::add);
        return out;
    }

    public Property getProperty(String propName) {
        return properties.get(propName);
    }

    public List<String> getPropertiesKeys() {
        return propertiesKeys;
    }

    public boolean isExtension() {
        return isExtension;
    }

    public void setIsExtension(boolean isExtension) {
        this.isExtension = isExtension;
    }

    @Override
    public String toString() {
        return name;
    }

    public void print() {
        for (String key : properties.keySet()) {
            String k = (String) key;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PaletteComponent other = (PaletteComponent) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.type, other.type);
    }

    public String getHelpFile() {
        if (isExtension) {
            return Extensions.EXTENSIONS_FOLDER_PATH + name + "/" + ExtensionComponent.HTML_HELP_FILE_NAME;
        } else {
            return "/help/" + name + ".html";
        }
    }

    public boolean supportsChildren() {
        for (String key : supports.keySet()) {
            if (supports.get(key) == Palette.UNLIMITED) {
                return true;
            } else if (supports.get(key) > 0) {
                return true;
            }
        }
        return false;
    }
}
