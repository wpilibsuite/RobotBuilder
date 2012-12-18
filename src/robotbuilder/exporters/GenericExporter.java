/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.exporters;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import robotbuilder.MainFrame;
import robotbuilder.RobotBuilder;
import robotbuilder.robottree.RobotTree;
import robotbuilder.Utils;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.RobotWalker;

/**
 *
 * @author Alex Henning
 */
public class GenericExporter {
    private List<String> instructions;
    
    private String name, type, filesPath;
    String path, begin_modification, end_modification, post_export_action;
    private boolean showOnToolbar;
    VelocityEngine ve;
    Context rootContext = new VelocityContext();
    private LinkedList<String> requires = new LinkedList<String>();
    private Map<String, String> vars = new HashMap<String, String>();
    private LinkedList<String> varKeys = new LinkedList<String>();
    private Map<String, Map<String, String>> componentInstructions;
    
    public GenericExporter(String path) {
        this.path = path;
        
        // Setup velocity engine
        ve = new VelocityEngine(Utils.getVelocityProperties());
        
        // Load YAML Description
        Yaml yaml = new Yaml();
        Map<String, Object> description = (Map<String, Object>) yaml.load(
                new InputStreamReader(Utils.getResourceAsStream(path+"ExportDescription.yaml")));
        name = (String) description.get("Name");
        type = (String) description.get("Type");
        filesPath = (String) description.get("Files");
        begin_modification = (String) description.get("Begin Modification");
        end_modification = (String) description.get("End Modification");
        String _ = evalResource(path+(String) description.get("Macros")); // Loads Macros Globally
        showOnToolbar = (Boolean) description.get("Toolbar");
        if (description.containsKey("Required Properties")) {
            for (String prop : ((ArrayList<String>) description.get("Required Properties"))) {
                requires.add(prop);
            }
        }
        Map<String, String> variables = ((Map<String, String>) description.get("Vars"));
        for (String var : variables.keySet()) {
            vars.put(var, variables.get(var));
            varKeys.add(var);
        }
        post_export_action = (String) description.get("Post Export Action");
        if (description.containsKey("Instructions")) {
            instructions = (List<String>) description.get("Instruction Names");
            loadExportDescription((Map<String, Map<String, String>>) description.get("Defaults"), 
                    (Map<String, Map<String, String>>) description.get("Instructions"));
        }
    }
    
    public void export(RobotTree robotTree) throws IOException {
        // Check that all necessary properties are filled in.
        RobotComponent robot = robotTree.getRoot();
        for (String prop : requires) {
            Object state = robot.getProperty(prop).getValue();
            if (state == null || state.equals("") || state.equals("None")) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                "You need to fill in the '"+prop+"' property of your robot for this export to work.\nYou can edit this with the main settings for your robot by clicking on "+robot.getName()+".",
                                "Missing Property", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Check that the robot is valid for export
        if (!robotTree.isRobotValid()) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                "Your robot is not ready for export, the red components are not quiet finished, please finish and try again.",
                                "Unfinished robot", JOptionPane.ERROR_MESSAGE);
                return;
        }
        
        // Prepare the main context
        rootContext.put("version", RobotBuilder.VERSION);
        rootContext.put("version-indicator", "RobotBuilder Version: "+RobotBuilder.VERSION);
        rootContext.put("robot", robot);
        rootContext.put("helper", this);
        rootContext.put("Collections", Collections.class);
        rootContext.put("file-separator", File.separator);
        rootContext.put("exporter-path", path);
        rootContext.put("components", getComponents(robot));
        rootContext.put("export-subsystems", robot.getProperty("Export Subsystems").getValue());
        rootContext.put("subsystems", robotTree.getSubsystems());
        rootContext.put("export-commands", robot.getProperty("Export Commands").getValue());
        rootContext.put("commands", robotTree.getCommands());
        for (String key : varKeys) {
            rootContext.put(key, eval(vars.get(key)));
        }
        
        // Export to all files
        Collection<ExportFile> newFiles = getFiles();
        for (ExportFile file : newFiles) {
            System.out.println(file.getSource()+" --> "+file.getExport());
            file.export(this);
        }
        
        MainFrame.getInstance().setStatus("Export succesful.");
        if (post_export_action != null) {
            String action = eval(post_export_action);
            if (action.startsWith("#")) {
                if (action.startsWith("#Browse:")) {
                    Utils.browse(action.replace("#Browse:", ""));
                } else if (action.startsWith("#Message")) {
                    MainFrame.getInstance().setStatus(action.replace("#Message:", ""));
                } else {
                    Logger.getLogger(Utils.class.getName()).log(Level.WARNING, null,
                            "No special action for "+action);
                }
            } else {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec(action);
            }
        }
    }
    
    /**
     * Loads the export description file that contains the instructions for export.
     * 
     * @param path The path to the export descriptions file.
     * @param properties The properties that each component must have.
     */
    private void loadExportDescription(Map<String, Map<String, String>> defaults, 
            Map<String, Map<String, String>> components) {
        // Load the intstructions to export items from the palette
        componentInstructions = new HashMap<String, Map<String, String>>();
        for (String key : components.keySet()) {
            Map<String, String> componentBase = components.get(key);
            Map<String, String> component = new HashMap<String, String>();
            String[] componentDefaults = componentBase.get("Defaults").split(",");
            for (String instructionKey : instructions) {
                String instruction = componentBase.get(instructionKey);
                // If the instruction isn't defined, load it from a default.
                // Earlier defaults take precedence.
                for (String aDefault : componentDefaults){
                    if (instruction == null) {
                        Map<String, String> defMap = defaults.get(aDefault);
                        if (defMap != null) instruction = defMap.get(instructionKey);
                    } else break;
                }
                assert instruction != null; // TODO: Deal with more elegantly
                instruction = instruction.replaceAll("\n", "\r\n");
                component.put(instructionKey, instruction);
            }
            componentInstructions.put(key, component);
        }
    }
    
    private ArrayList<ExportFile> getFiles() throws FileNotFoundException {
        String filesString = evalResource(path+filesPath);
        Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(ExportFile.class, "!File"));
        Yaml yaml = new Yaml(constructor);
        ArrayList<ExportFile> filesYaml = (ArrayList<ExportFile>) yaml.load(filesString);
        return filesYaml;
    }

    String evalResource(String resource, Context context) {
        InputStreamReader in;
        in = new InputStreamReader(Utils.getResourceAsStream(resource));
        StringWriter w = new StringWriter();
        ve.evaluate(context, w, name+" Exporter: "+resource, in);
        return w.toString();
    }
    String evalResource(String resource) {
        return evalResource(resource, rootContext);
    }
    
    String eval(String templateString, Context context) {
        StringWriter w = new StringWriter();
        ve.evaluate(context, w, name+" Exporter", templateString);
        return w.toString();
    }
    String eval(String templateString) {
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
        if (!comp.getSubsystem().isEmpty())
            context.put("Subsystem", comp.getSubsystem().substring(0, comp.getSubsystem().length()-1));
        context.put("Component", comp);
        for (String property : comp.getPropertyKeys()) {
            context.put(property.replace(" ", "_").replace("(", "").replace(")", ""),
                    comp.getProperty(property).getValue());
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
    
    // Getters and such
    public String getName() {
        return name;
    }

    public String getDescription() {
        return getName();
    }

    public boolean isOnToolbar() {
        return showOnToolbar;
    }
    
    //// Everything below is used by the java export as $helper.* and should be
    //// cleaned up and ported to macros.
    
    
    // TODO: make macro
    public Map<String, String> filterComponents(final String moduleFilter, final String portFilter, final String module, RobotComponent robot) {
        final Map<String, String> mapping = new HashMap<String, String>();
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                Map<String, String> modules = new HashMap<String, String>();
                Map<String, String> ports = new HashMap<String, String>();
                for (String property : self.getPropertyKeys()) {
                    if (property.endsWith(moduleFilter)) {
                        String key = property.replace(moduleFilter, "");
                        modules.put(key, self.getProperty(property).getValue().toString());
                    } else if (property.endsWith(portFilter)) {
                        String key = property.replace(portFilter, "");
                        ports.put(key, self.getProperty(property).getValue().toString());
                    }
                }
                for (String key : ports.keySet()) {
                    if (module.equals(modules.get(key))) {
                        mapping.put(ports.get(key), self.getFullName()+" "+key);
                    }
                }
            }
        });
        return mapping;
    }
    // TODO: make macro
    public Map<String, String> filterComponents(final String propertyName, RobotComponent robot) {
        final Map<String, String> mapping = new HashMap<String, String>();
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                for (String property : self.getPropertyKeys()) {
                    if (property.equals(propertyName)) {
                        mapping.put(self.getProperty(property).getValue().toString(), self.getFullName());
                    }
                }
            }
        });
        return mapping;
    }
    
    public String getInstruction(RobotComponent comp, String instruction) { // TODO: Make macro
        final Map<String, String> instructions = componentInstructions.get(comp.getBase().getName());
        return eval(instructions.get(instruction), getContext(comp));
    }
    
    /**
     * Helper method to generate imports for a given category category
     * @param robot The root robot component to start with.
     * @param category The category to look for.
     * @return The resulting imports.
     */
    public String getImports(RobotComponent robot, final String category) { // TODO: make macro
        final Set<String> imports = new TreeSet<String>();
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                final Map<String, String> instructions = componentInstructions.get(self.getBase().getName());
                if (category.equals(instructions.get("Export"))) {
                    String instruction = instructions.get("Import");
                    imports.add(eval(instruction, getContext(self)));
                }
            }
        });
        
        String out = "";
        for (String imp : imports) {
            if (!"".equals(imp)) out += imp + "\r\n";
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

    public RobotComponent getByName(final String name, RobotComponent robot) { // TODO: Make macro
        return robot.getRobotTree().getComponentByName(name);
    }
    
    // UTILITIES
    String openFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (path));
        String line  = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = "\r\n";//System.getProperty("line.separator");
        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }
        return stringBuilder.toString();
    }
}
