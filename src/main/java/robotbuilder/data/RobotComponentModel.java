
package robotbuilder.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.yaml.snakeyaml.Yaml;

import robotbuilder.data.properties.Property;

/**
 * Model class holding the data used by {@link RobotComponent}.
 *
 * @author Sam Carlberg
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RobotComponentModel implements Serializable {

    private String name;
    private PaletteComponent base;
    private Map<String, Property> properties;

    public RobotComponentModel(String name, PaletteComponent base) {
        this(name, base, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RobotComponentModel) {
            // compare serialization to avoid recursion
            RobotComponentModel other = (RobotComponentModel) o;
            Yaml yaml = new Yaml();
            return yaml.dump(this).equals(yaml.dump(other));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.base);
        hash = 53 * hash + Objects.hashCode(this.properties);
        return hash;
    }

    public Property getProperty(String key) {
        return properties.get(key);
    }

    public void setName(String name) {
        this.name = (name == null) ? null : name.trim();
    }
}
