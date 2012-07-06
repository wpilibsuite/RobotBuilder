/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Alex Henning
 */
public class Macro {
    private String name;
    ArrayList<Expansion> expansions = new ArrayList<Expansion>();

    public Macro(String name, ArrayList<Expansion> expansions) {
        System.out.println("Creating macro "+name);
        this.name = name;
        this.expansions = expansions;
    }
    

    public List<Property> expand(List<Property> properties, Property property) {
        List<Property> out = new ArrayList<Property>();
        for (Expansion expansion : expansions) {
            Property expanded = new Property();
            expanded.setName(property.getName() +" "+expansion.name);
            expanded.setType(expansion.type);
            
            String defaultVal = property.getDefault();
            if (defaultVal != null) {
                // TODO: set to get the default from the object.
                expanded.setDefault(defaultVal);
            } else {
                expanded.setDefault(expansion.defaultDefault);
            }
            
            if (expansion.choices != null)
                expanded.setChoices(expansion.choices.toArray(new String[0]));
            expanded.setValidator(expansion.validator);
            
            out.add(expanded);
        }
        return out;
    }

    public String getName() {
        return name;
    }
    
    /**
     * A utility object for nicely loading Yaml macros.
     */
    public static class Expansion {
        String name, type, defaultValue, defaultDefault, validator;
        ArrayList<String> choices;

        public ArrayList<String> getChoices() {
            return choices;
        }
        public void setChoices(ArrayList<String> choices) {
            this.choices = choices;
        }

        public String getDefaultDefault() {
            return defaultDefault;
        }
        public void setDefaultDefault(String defaultDefault) {
            this.defaultDefault = defaultDefault;
        }

        public String getDefault() {
            return defaultValue;
        }
        public void setDefault(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }

        public String getValidator() {
            return validator;
        }
        public void setValidator(String validator) {
            this.validator = validator;
        }
    }
}
