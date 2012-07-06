/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.exporters;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.tree.DefaultMutableTreeNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import robotbuilder.Palette;
import robotbuilder.RobotTree;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author Alex Henning
 */
public abstract class AbstractExporter {
    /**
     * Exports the project.
     * @param robot
     * @param exportPath The path of the project to export to.
     */
    public abstract void export(RobotTree robot, String exportPath) throws IOException;
    

    protected Map<String, Map<String, String>> componentInstructions;
    
    /**
     * Loads the export description file that contains the instructions for export.
     * 
     * @param path The path to the export descriptions file.
     * @param properties The properties that each component must have.
     */
    protected void loadExportDescription(String path, String[] properties) {
        FileReader file;
        try {
            file = new FileReader(path);
            JSONTokener tokener;
            tokener = new JSONTokener(file);
            JSONObject json;
            json = new JSONObject(tokener);
       
            // Load the defaults
            Map<String, Map<String, String>> defaults = new HashMap<String, Map<String, String>>();
            JSONObject defaultsObject = json.getJSONObject("Defaults");
            for (Object key : defaultsObject.names().getIterable()) {
                Map<String, String> defaultMap = new HashMap<String, String>();
                JSONObject defaultObject = defaultsObject.getJSONObject((String) key);
                for (Object objectKey : defaultObject.names().getIterable()) {
                    defaultMap.put((String) objectKey, defaultObject.getString((String) objectKey));
                }
                defaults.put((String) key, defaultMap);
            }
            
            // Load the intstructions to export items from the palette 
            componentInstructions = new HashMap<String, Map<String, String>>();
            JSONObject componentsObject = json.getJSONObject("Palette");
            for (Object key : componentsObject.names().getIterable()) {
                Map<String, String> componentMap = new HashMap<String, String>();
                JSONObject component = componentsObject.getJSONObject((String) key);
                JSONArray componentDefaults = component.getJSONArray("Defaults");
                for (String instructionKey : properties) {
                    String instruction = component.optString(instructionKey, null);
                    // If the instruction isn't defined, load it from a default.
                    // Earlier defaults take precedence.
                    for (Object aDefault : componentDefaults.getIterable()) {
                        if (instruction == null) {
                            Map<String, String> defMap = defaults.get((String) aDefault);
                            if (defMap != null) instruction = defMap.get(instructionKey);
                        } else break;
                    }
                    assert instruction != null; // TODO: Deal with more elegantly
                    componentMap.put(instructionKey, instruction);
                }
                System.out.println();
                componentInstructions.put((String) key, componentMap);
            }
            
        } catch (JSONException ex) {
            Logger.getLogger(AbstractExporter.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }
    
    protected String loadTemplate(String path) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (path));
        String line  = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }
        return stringBuilder.toString();
    }
    
    protected String substitute(String template, String key, String val) {
        System.out.println("${"+key+"}"+" => "+val);
        return template.replace("${"+key+"}", val);
    }
    
    protected String substitute(String template, RobotComponent comp, String className) {
        System.out.println(comp);
        template = substitute(template, "Name", comp.getFullName());
        template = substitute(template, "ClassName", className);
        for (String property : comp.getPropertyKeys()) {
            System.out.println("\t"+property);
            template = substitute(template, property, comp.getProperty(property));
        }
        return template;
    }
}
