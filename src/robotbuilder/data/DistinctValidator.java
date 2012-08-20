/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import robotbuilder.data.properties.Property;

/**
 *
 * @author Alex Henning
 */
public class DistinctValidator implements Validator {
    private String name;
    /** Fields that should be distinct */
    List<String> fields;
    /** Maps fields to values */
    Map<String, Object> claims = new HashMap<String, Object>();
    
    public DistinctValidator() {}

    DistinctValidator(String name, List<String> fields) {
        this.name = name;
        this.fields = fields;
        for (String field : fields) claims.put(field, new Object());
    }

    @Override
    public void update(RobotComponent component, String property, Object value) {
        assert fields.contains(property);
        claims.put(property, value);
    }

    @Override
    public boolean isValid(RobotComponent component, Property property) {
        int occurrences = 0;
        for (Object value : claims.values()) {
            if (claims.get(property.getName()).equals(value)) occurrences++;
        }
        return occurrences == 1;
    }

    @Override
    public String getError(RobotComponent component, Property property) {
        if (isValid(component, property)) return null;
        List<String> f = new LinkedList<String>();
        for (String s : fields) {
            if (!s.equals(property.getName())) {
                f.add(s);
            }
        }
        return "The value in this field overlaps with one of the following fields: "
                +f.toString()+".";
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
        claims = new HashMap<String, Object>();
        for (String field : fields) claims.put(field, new Object());
    }

    @Override
    public Validator copy() {
        return new DistinctValidator(name, fields);
    }
    
}
