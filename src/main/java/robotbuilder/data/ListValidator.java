
package robotbuilder.data;

import java.util.List;

import lombok.Data;

import robotbuilder.Utils;
import robotbuilder.data.properties.ListProperty;
import robotbuilder.data.properties.Property;
import robotbuilder.data.properties.Validatable;

/**
 *
 * @author Sam Carlberg
 */
@Data
public class ListValidator implements Validator {

    private String name;

    @Override
    public void update(RobotComponent component, String property, Object value) {
        // ignore
    }

    @Override
    public boolean isValid(RobotComponent component, Property property) {
        assert property instanceof ListProperty;
        ListProperty<?> prop = (ListProperty<?>) property;
        return prop.getValue().stream().allMatch(Validatable::isValid);
    }

    @Override
    public String getError(RobotComponent component, Property property) {
        assert property instanceof ListProperty;
        ListProperty<?> prop = (ListProperty<?>) property;
        return prop.getValue().stream()
                .filter(v -> !v.isValid())
                .map(v -> "\n    Invalid component: " + v.getName())
                .reduce("", String::concat);
    }

    @Override
    public void delete(RobotComponent component, String property) {
        // ignore
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Validator copy() {
        return Utils.deepCopy(this);
    }

}
