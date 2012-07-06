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

/**
 *
 * @author Alex Henning
 */
public class Macro {
    private String name;
    LinkedList<Expansion> expansions = new LinkedList<Expansion>();

    public Macro(String name) {
        System.out.println("Creating macro "+name);
        this.name = name;
    }
    
    public void addExpansion(String name, String type, String defaultValue, String defaultDefault, LinkedList<Object> choices) {
        expansions.add(new Expansion(name, type, defaultValue, defaultDefault, choices));
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
                expanded.put("Name", prop.get("Name") +" "+expansion.name);
                expanded.put("Type", expansion.type);
                String defaultVal = prop.optString(expansion.defaultValue, null);
                if (defaultVal != null) {
                    expanded.put("Default", defaultVal);
                } else {
                    expanded.put("Default", expansion.defaultDefault);
                }
                expanded.put("Choices", expansion.choices);
                props = props.put(expanded);
            } catch (JSONException ex) {
                Logger.getLogger(Macro.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return props;
    }
    
    private class Expansion {
        String name, type, defaultValue, defaultDefault;
        LinkedList<Object> choices;

        public Expansion(String name, String type, String defaultValue, String defaultDefault, LinkedList<Object> choices) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.defaultDefault = defaultDefault;
            this.choices = choices;
        }
    }
}
