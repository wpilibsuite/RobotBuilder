/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.exporters;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.sound.midi.Patch;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import robotbuilder.RobotTree;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.RobotWalker;
import sun.misc.Regexp;

/**
 *
 * @author Alex Henning
 */
public class GenericExporter {
    private final static String[] DESCRIPTION_PROPERTIES = {"Export", "Import", "Declaration",
        "Construction", "Extra", "ClassName", "Subsystem Export", "Template"};
    
    private String name, type, newFilesPath, modifiedFilesPath, begin_modification, end_modification;
    private boolean showOnToolbar;
    private String path;
    private Context rootContext = new VelocityContext();
    private Map<String, String> vars = new HashMap<String, String>();
    private LinkedList<String> varKeys = new LinkedList<String>();
    private Map<String, Map<String, String>> componentInstructions;
    
    public GenericExporter(File descriptionFile) throws FileNotFoundException, JSONException {
        path = descriptionFile.getParent()+File.separator;
        JSONObject description = openJson(descriptionFile);
        name = description.getString("Name");
        type = description.getString("Type");
        newFilesPath = description.getString("New Files");
        modifiedFilesPath = description.getString("Modified Files");
        begin_modification = description.getString("Begin Modification");
        end_modification = description.getString("End Modification");
        String _ = eval(new File(path+description.getString("Macros")));
        showOnToolbar = description.getBoolean("Toolbar");
        for (Object pair : description.getJSONArray("Vars").getIterable()) {
            JSONObject obj = (JSONObject) pair;
            vars.put(obj.getString("Name"), obj.getString("Value"));
            varKeys.add(obj.getString("Name"));
        }
        loadExportDescription(description.getJSONObject("Defaults"), 
                description.getJSONObject("Instructions"));
    }
    
    public void export(RobotTree robotTree) throws IOException {
        // Prepare the main context
        RobotComponent robot = robotTree.getRoot();
        rootContext.put("robot", robot);
        rootContext.put("helper", this);
        rootContext.put("exporter-path", path);
        rootContext.put("components", getComponents(robot));
        rootContext.put("export-subsystems", robot.getProperty("Export Subsystems").equals("true"));
        rootContext.put("subsystems", robotTree.getSubsystems());
        rootContext.put("export-commands", robot.getProperty("Export Commands").equals("true"));
        rootContext.put("commands", robotTree.getCommands());
        for (String key : varKeys) {
            System.out.println("Var: "+key+" = "+eval(vars.get(key)));
            rootContext.put(key, eval(vars.get(key)));
        }
        System.out.println();
        
        // Generate new files
        LinkedList<ExportFile> newFiles = getNewFiles();
        for (ExportFile file : newFiles) {
            file.export();
        }
        
        // Update existing files
        LinkedList<ModifiedFile> modifiedFiles = getModifiedFiles();
        for (ModifiedFile file : modifiedFiles) {
            file.export();
        }
        
        System.out.println(name+" Export Finished");
    }
    
    private JSONObject openJson(File file) throws FileNotFoundException, JSONException {
        FileReader fileReader;
        fileReader = new FileReader(file);
        JSONTokener tokener;
        tokener = new JSONTokener(fileReader);
        return new JSONObject(tokener);
    }
    
    /**
     * Loads the export description file that contains the instructions for export.
     * 
     * @param path The path to the export descriptions file.
     * @param properties The properties that each component must have.
     */
    private void loadExportDescription(JSONObject defaultsObject, JSONObject componentsObject) throws JSONException {
        // Load the defaults
        Map<String, Map<String, String>> defaults = new HashMap<String, Map<String, String>>();
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
        for (Object key : componentsObject.names().getIterable()) {
            Map<String, String> componentMap = new HashMap<String, String>();
            JSONObject component = componentsObject.getJSONObject((String) key);
            JSONArray componentDefaults = component.getJSONArray("Defaults");
            for (String instructionKey : DESCRIPTION_PROPERTIES) {
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
            componentInstructions.put((String) key, componentMap);
        }
    }
    
    private LinkedList<ExportFile> getNewFiles() throws FileNotFoundException {
        LinkedList<ExportFile> files = new LinkedList<ExportFile>();
        String filesString = eval(new File(path+newFilesPath));
        for (String line : filesString.split("\n")) {
            System.out.println("Line: "+line);
            if (line.contains(":")) {
                String[] split = line.split(":");
                ExportFile file = new ExportFile(split[1], new File(path+split[0]));
                for (int i = 2; i < split.length; i++) {
                    file.addVar(split[i]);
                }
                files.add(file);
            }
        }
        return files;
    }
    
    private LinkedList<ModifiedFile> getModifiedFiles() throws FileNotFoundException {
        LinkedList<ModifiedFile> files = new LinkedList<ModifiedFile>();
        String filesString = eval(new File(path+modifiedFilesPath));
        for (String line : filesString.split("\n")) {
            System.out.println("Line: "+line);
            if (line.contains(":")) {
                String[] split = line.split(":");
                ModifiedFile file = new ModifiedFile(split[1], split[0]);
                for (int i = 2; i < split.length; i++) {
                    file.addVar(split[i]);
                }
                files.add(file);
            }
        }
        return files;
    }

    private String eval(File file, Context context) throws FileNotFoundException {
        FileReader fileReader;
        fileReader = new FileReader(file);
        StringWriter w = new StringWriter();
        Velocity.evaluate(context, w, name+" Exporter", fileReader);
        return w.toString();
    }
    private String eval(File file) throws FileNotFoundException {
        return eval(file, rootContext);
    }
    
    private String eval(String templateString, Context context) {
        StringWriter w = new StringWriter();
        Velocity.evaluate(context, w, name+" Exporter", templateString);
        return w.toString();
    }
    private String eval(String templateString) {
        return eval(templateString, rootContext);
    }
    
    /**
     * Get the context of a robot component with all it's properties filled in.
     * @param comp The component to base the context off of.
     * @return The context, which also inherits from the rootContext
     */
    private Context getContext(RobotComponent comp) {
        Context context = new VelocityContext(rootContext);
        final Map<String, String> instructions = componentInstructions.get(comp.getBase().getName());
        context.put("ClassName", instructions.get("ClassName"));
        context.put("Name", comp.getFullName());
        context.put("Short_Name", comp.getName());
        for (String property : comp.getPropertyKeys()) {
            context.put(property.replace(" ", "_").replace("(", "").replace(")", ""),
                    comp.getProperty(property));
        }
        return context;
    }
    
    private LinkedList<RobotComponent> getComponents(RobotComponent robot) {
        final LinkedList<RobotComponent> components = new LinkedList<RobotComponent>();
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                components.add(self);
            }
        });
        
        return components;
    }
    
    //// Everything below is used by the java export as $helper.* and should be
    //// cleaned up and ported to macros.
    
    public String classOf(RobotComponent comp) { // TODO: Make macro
        final Map<String, String> instructions = componentInstructions.get(comp.getBase().getName());
        return instructions.get("ClassName");
    }
    
    /**
     * Helper method to generate imports for a given category category
     * @param robot The root robot component to start with.
     * @param category The category to look for.
     * @return The resulting imports.
     */
    public String getImports(RobotComponent robot, final String category) { // TODO: make macro
        System.out.println("Getting imports");
        final Set<String> imports = new TreeSet<String>();
        System.out.println(robot);
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                System.out.println("\t"+self);
                final Map<String, String> instructions = componentInstructions.get(self.getBase().getName());
                if (category.equals(instructions.get("Export"))) {
                    String instruction = instructions.get("Import");
                    imports.add(eval(instruction, getContext(self)));
                }
            }
        });
        
        String out = "";
        for (String imp : imports) {
            if (!"".equals(imp)) out += imp + "\n";
        }
        return out;
    }
    
    /**
     * @param category The category to filter for.
     * @param comp The component.
     * @return Whether or not it is a member of the specified category.
     */
    public boolean exportsTo(String category, RobotComponent comp) { // TODO: Make macro
        return category.equals(componentInstructions.get(comp.getBase().getName()).get("Export"));
    }
    
    /**
     * @param comp The robot component as the base.
     * @return The declaration string
     */
    public String getDeclaration(RobotComponent comp) { // TODO: Make macro
        final Map<String, String> instructions = componentInstructions.get(comp.getBase().getName());
        return eval(instructions.get("Declaration"), getContext(comp));
    }
    
    /**
     * @param comp The robot component as the base.
     * @return The declaration string
     */
    public String getConstructor(RobotComponent comp) { // TODO: Make macro
        final Map<String, String> instructions = componentInstructions.get(comp.getBase().getName());
        return eval(instructions.get("Construction"), getContext(comp));
    }
    
    /**
     * @param comp The robot component as the base.
     * @return The declaration string
     */
    public String getExtra(RobotComponent comp) { // TODO: Make macro
        final Map<String, String> instructions = componentInstructions.get(comp.getBase().getName());
        return eval(instructions.get("Extra"), getContext(comp));
    }
    
    public RobotComponent getByName(final String name, RobotComponent robot) { // TODO: Make macro
        final RobotComponent[] component = new RobotComponent[1];
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                if (self.getName().equals(name)) {
                    component[0] = self;
                }
            }
        });
        return component[0];
    }
    
    class ExportFile extends File {
        File template;
        Map <String, String> vars = new HashMap<String, String>();

        public ExportFile(String pathname, File template) {
            super(pathname);
            this.template = template;
        }
        
        public void export() throws IOException {
            if (!this.exists()) {
                Context fileContext = new VelocityContext(rootContext);
                for (String key : vars.keySet()) {
                    fileContext.put(key, eval(vars.get(key), fileContext));
                }
                
                FileWriter writer = new FileWriter(this);
                writer.write(eval(template, fileContext));
                writer.close();
            }
        }

        private void addVar(String var) {
            String[] split = var.split("=");
            vars.put(split[0], split[1]);
        }
    }
    
    // UTILITIES
    protected String openFile(String path) throws IOException {
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
    
    class ModifiedFile extends File {
        Map <String, String> sources = new HashMap<String, String>();
        Map <String, String> vars = new HashMap<String, String>();

        public ModifiedFile(String pathname, String ids) {
            super(pathname);
            System.out.println(ids);
            for (String pair : ids.split(",")) {
                String[] split = pair.split("=");
                sources.put(split[0], path+split[1]);
            }
        }
        
        public void export() throws IOException {
            if (this.exists()) {
                Context fileContext = new VelocityContext(rootContext);
                for (String key : vars.keySet()) {
                    fileContext.put(key, eval(vars.get(key), fileContext));
                }
                
                String file = openFile(this.getAbsolutePath());
                
                for (String id : sources.keySet()) {
                    Context idContext = new VelocityContext(fileContext);
                    idContext.put("id", id);
                    String beginning = eval(begin_modification, idContext);
                    String end = eval(end_modification, idContext);
                    System.out.println(id);
                    System.out.println(this);
                    System.out.println("("+beginning+")([\\s\\S]*?)("+end+")");
                    System.out.println(eval(new File(sources.get(id)), idContext));
                    file = file.replaceAll("("+beginning+")([\\s\\S]*?)("+end+")",
                            "$1\n"+eval(new File(sources.get(id)), idContext)+"\n    $3");
                }
                
                
                FileWriter out = new FileWriter(this);
                out.write(file);
                out.close();
            }
        }

        private void addVar(String var) {
            String[] split = var.split("=");
            vars.put(split[0], split[1]);
        }
    }
}
