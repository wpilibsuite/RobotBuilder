/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.exporters;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.json.JSONException;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import robotbuilder.MainFrame;
import robotbuilder.RobotTree;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.RobotWalker;

/**
 *
 * @author Alex Henning
 */
public class GenericExporter {
    private final static String[] DESCRIPTION_PROPERTIES = {"Export", "Import", "Declaration",
        "Construction", "Extra", "ClassName", "Subsystem Export", "Template"};
    
    private String name, type, filesPath;
    String path, begin_modification, end_modification;
    private boolean showOnToolbar;
    Context rootContext = new VelocityContext();
    private LinkedList<String> requires = new LinkedList<String>();
    private Map<String, String> vars = new HashMap<String, String>();
    private LinkedList<String> varKeys = new LinkedList<String>();
    private Map<String, Map<String, String>> componentInstructions;
    
    public GenericExporter(File descriptionFile) throws FileNotFoundException, JSONException {
        path = descriptionFile.getParent()+File.separator;
        System.out.println("PATH: "+path+" -- "+(new File("")).getAbsolutePath());
        path = path.replace((new File("")).getAbsolutePath()+File.separator, "");
        System.out.println("PATH: "+path+" -- "+(new File("")).getAbsolutePath());
        Yaml yaml = new Yaml();
        Map<String, Object> description = (Map<String, Object>) yaml.load(new FileReader(descriptionFile));;
        name = (String) description.get("Name");
        type = (String) description.get("Type");
        filesPath = (String) description.get("Files");
        begin_modification = (String) description.get("Begin Modification");
        end_modification = (String) description.get("End Modification");
        String _ = eval(new File(path+(String) description.get("Macros"))); // Loads Macros Globally
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
        if (description.containsKey("Instructions")) {
            loadExportDescription((Map<String, Map<String, String>>) description.get("Defaults"), 
                    (Map<String, Map<String, String>>) description.get("Instructions"));
        }
    }
    
    public void export(RobotTree robotTree) throws IOException {
        // Check that all necessary properties are filled in.
        RobotComponent robot = robotTree.getRoot();
        for (String prop : requires) {
            String state = robot.getProperty(prop);
            if (state == null || state.equals("") || state.equals("None")) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                "You need to fill in the '"+prop+"' property of your robot for this export to work.",
                                "Missing Property", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Prepare the main context
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
        
        // Export to all files
        Collection<ExportFile> newFiles = getFiles();
        for (ExportFile file : newFiles) {
            file.export(this);
        }
        
        System.out.println(name+" Export Finished");
    }
    
    /**
     * Loads the export description file that contains the instructions for export.
     * 
     * @param path The path to the export descriptions file.
     * @param properties The properties that each component must have.
     */
    private void loadExportDescription(Map<String, Map<String, String>> defaults, 
            Map<String, Map<String, String>> components) throws JSONException {
        // Load the intstructions to export items from the palette
        componentInstructions = new HashMap<String, Map<String, String>>();
        for (String key : components.keySet()) {
            Map<String, String> componentBase = components.get(key);
            Map<String, String> component = new HashMap<String, String>();
            String[] componentDefaults = componentBase.get("Defaults").split(",");
            for (String instructionKey : DESCRIPTION_PROPERTIES) {
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
                component.put(instructionKey, instruction);
            }
//            System.out.println("\t"+key+": "+component+"\n\n");
            componentInstructions.put(key, component);
        }
    }
    
    private ArrayList<ExportFile> getFiles() throws FileNotFoundException {
        String filesString = eval(new File(path+filesPath));
        Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(ExportFile.class, "!File"));
        Yaml yaml = new Yaml(constructor);
        ArrayList<ExportFile> filesYaml = (ArrayList<ExportFile>) yaml.load(filesString);
        return filesYaml;
    }

    String eval(File file, Context context) throws FileNotFoundException {
        FileReader fileReader;
        fileReader = new FileReader(file);
        StringWriter w = new StringWriter();
        Velocity.evaluate(context, w, name+" Exporter: "+file.getName(), fileReader);
        return w.toString();
    }
    String eval(File file) throws FileNotFoundException {
        return eval(file, rootContext);
    }
    
    String eval(String templateString, Context context) {
        StringWriter w = new StringWriter();
        Velocity.evaluate(context, w, name+" Exporter", templateString);
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
    public Map<Integer, String> filterComponents(final String moduleFilter, final String portFilter, final int module, RobotComponent robot) {
        final Map<Integer, String> mapping = new HashMap<Integer, String>();
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                Map<String, Integer> modules = new HashMap<String, Integer>();
                Map<String, Integer> ports = new HashMap<String, Integer>();
                for (String property : self.getPropertyKeys()) {
                    if (property.endsWith(moduleFilter)) {
                        String key = property.replace(moduleFilter, "");
                        modules.put(key, Integer.parseInt(self.getProperty(property)));
                    } else if (property.endsWith(portFilter)) {
                        String key = property.replace(portFilter, "");
                        ports.put(key, Integer.parseInt(self.getProperty(property)));
                    }
                }
                for (Iterator i = ports.keySet().iterator(); i.hasNext();) {
                    String key = (String) i.next();
                    if (module == modules.get(key)) {
                        mapping.put(ports.get(key), self.getName()+" "+key);
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
    
    // UTILITIES
    String openFile(String path) throws IOException {
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
}
