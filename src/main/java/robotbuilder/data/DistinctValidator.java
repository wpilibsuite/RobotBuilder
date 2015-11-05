
package robotbuilder.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import robotbuilder.data.properties.Property;

/**
 *
 * @author Alex Henning
 */
public class DistinctValidator implements Validator {

    private String name;

    /**
     * Fields that should be distinct
     */
    List<String> fields;

    /**
     * Maps fields to values
     */
    Map<String, Object> claims = new HashMap<>();

    public DistinctValidator() {
    }

    DistinctValidator(String name, List<String> fields) {
        this.name = name;
        this.fields = fields;
        fields.stream().forEach(field -> claims.put(field, new Object()));
    }

    @Override
    public void update(RobotComponent component, String property, Object value) {
        if (!fields.contains(property)) {
            System.out.println("Fields does not contain property " + property);
            return;
        }
        claims.put(property, value);
    }

    @Override
    public boolean isValid(RobotComponent component, Property property) {
        return claims.values().stream()
                .filter(v -> Objects.equals(claims.get(property.getName()), v))
                .count() == 1;
    }

    @Override
    public String getError(RobotComponent component, Property property) {
        if (isValid(component, property)) {
            return null;
        }
        List<String> f = new LinkedList<>();
        fields.stream()
                .filter(s -> !s.equals(property.getName()))
                .forEach(f::add);
        return "The value in this field overlaps with one of the following fields: "
                + f.toString() + ".";
    }

    @Override
    public void delete(RobotComponent component, String property) {
        // Do nothing
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
        claims = new HashMap<>();
        fields.stream().forEach((field) -> claims.put(field, new Object()));
    }

    @Override
    public Validator copy() {
        return new DistinctValidator(name, fields);
    }

}
