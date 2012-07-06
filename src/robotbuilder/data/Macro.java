/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import robotbuilder.Palette;

/**
 *
 * @author Alex Henning
 */
public class Macro {
    private String name;
    LinkedList<Expansion> expansions = new LinkedList<Expansion>();

    public Macro(String name, JSONObject macroDef) {
        System.out.println("Creating macro "+name);
        this.name = name;
        try {
            JSONArray props = macroDef.getJSONArray("Properties");
            for (Object i : props.getIterable()) {
                JSONObject property = (JSONObject) i;
                LinkedList<Object> choices = null;
                if (property.has("Choices")) {
                    choices = new LinkedList<Object>();
                    for (Object c : property.optJSONArray("Choices").getIterable()) {
                        choices.add(c);
                    }
                }
                System.out.println("Adding expansion: "+property.getString("Name")
                        +" "+property.getString("Type")+" "+property.optString("Default")
                        +" "+property.optString("DefaultDefault")+" "+choices);
                addExpansion(property.getString("Name"),
                        property.getString("Type"), property.optString("Default"),
                        property.optString("DefaultDefault"), choices, property.optString("Validator"));
            }
        } catch (JSONException ex) {
            Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addExpansion(String name, String type, String defaultValue, String defaultDefault, LinkedList<Object> choices, String validator) {
        expansions.add(new Expansion(name, type, defaultValue, defaultDefault, choices, validator));
    }

    /**
     * 
     * @param key
     * @param prop
     * @param props 
     */
    public JSONArray expand(String key, JSONObject prop, JSONArray props) {
        for (Expansion expansion : expansions) {
            try {
                JSONObject expanded = new JSONObject();
                if (!prop.getString("Name").equals("")) {
                    expanded.put("Name", prop.get("Name") +" "+expansion.name);
                } else {
                    expanded.put("Name", expansion.name);
                }
                expanded.put("Type", expansion.type);
                String defaultVal = prop.optString(expansion.defaultValue, null);
                if (defaultVal != null) {
                    expanded.put("Default", defaultVal);
                } else {
                    expanded.put("Default", expansion.defaultDefault);
                }
                if (expansion.choices != null) {
                    expanded.put("Choices", expansion.choices);
                }
                if (expansion.validator != null) {
                    expanded.put("Validator", expansion.validator);
                }
                props = props.put(expanded);
            } catch (JSONException ex) {
                Logger.getLogger(Macro.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return props;
    }
    
    private class Expansion {
        String name, type, defaultValue, defaultDefault, validator;
        LinkedList<Object> choices;

        public Expansion(String name, String type, String defaultValue, String defaultDefault,
                LinkedList<Object> choices, String validator) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.defaultDefault = defaultDefault;
            this.choices = choices;
            this.validator = validator;
        }
    }
}
