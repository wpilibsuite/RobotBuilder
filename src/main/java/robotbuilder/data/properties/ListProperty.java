
package robotbuilder.data.properties;

import java.util.List;
import lombok.NoArgsConstructor;
import robotbuilder.Utils;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author Sam Carlberg
 */
@NoArgsConstructor
public class ListProperty<T extends Validatable> extends Property<List<T>> {

    protected List<T> value;

    public ListProperty(String name, List<T> defaultValue, String[] validators, RobotComponent component) {
        super(name, Utils.deepCopy(defaultValue), validators, component);
    }

    @Override
    public Property copy() {
        ListProperty copy = new ListProperty(name, defaultValue, validators, component);
        copy.setValue(Utils.deepCopy(value));
        return copy;
    }

    @Override
    public List<T> getValue() {
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    @Override
    public void setValue(List<T> value) {
        this.value = value;
    }

    @Override
    public Object getDisplayValue() {
        return value.toString();
    }

}
