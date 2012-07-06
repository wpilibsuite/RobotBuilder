/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.exporters;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import robotbuilder.MainFrame;
import robotbuilder.RobotTree;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.RobotWalker;

/**
 *
 * @author Alex Henning
 */
public class JavaExporter extends AbstractExporter {
    private static String DESCRIPTION_PATH = "export/java/ExportDescription.json";
    private static String[] DESCRIPTION_PROPERTIES = {"Import", "Declaration", "Construction", "Extra", "ClassName", "Subsystem Export"};
    private static String ROBOT_MAP_TEMPLATE = "export/java/RobotMap.java";
    private static String SUBSYSTEM_TEMPLATE = "export/java/Subsystem.java";

    @Override
    public void export(RobotTree robot) throws IOException {
        System.out.println("Loading export description for java");
        loadExportDescription(DESCRIPTION_PATH, DESCRIPTION_PROPERTIES);
        
        System.out.println("Loading template "+ROBOT_MAP_TEMPLATE);
        
        getPath(robot.getRoot()); // TODO: Ugly hack
        System.out.println("Exporting RobotMap");
        String template = loadTemplate(ROBOT_MAP_TEMPLATE);
        template = substitute(template, "package", robot.getRoot().getProperty("Java Package"));
        template = substitute(template, "imports", generateImports(robot.getRoot()));
        template = substitute(template, "declarations", generateDeclarations(robot));
        template = substitute(template, "constructions", generateConstructions(robot));
        
        System.out.println("Writing RobotMap file");
        FileWriter out = new FileWriter(getPath(robot.getRoot())+"RobotMap.java");
        out.write(template);
        out.close();
        
        System.err.println("Exporting Subsystems");
        for (RobotComponent subsystem : robot.getSubsystems()) {
            if (subsystemExists(subsystem)) {
                
            } else {
                template = loadTemplate(SUBSYSTEM_TEMPLATE);
                template = substitute(template, "package", robot.getRoot().getProperty("Java Package"));
                template = substitute(template, "imports", generateImports(subsystem));
                template = substitute(template, "Subsystem Name", getFullName(subsystem));
                template = substitute(template, "Subsystem Export", generateSubsystemExport(subsystem));
                
                System.out.println("Writing "+getShortName(subsystem)+".java file");
                FileWriter subsystemOut = new FileWriter(getPath(robot.getRoot())+"/subsystems/"+getFullName(subsystem)+".java");
                subsystemOut.write(template);
                subsystemOut.close();
            }
        }
        
        System.out.println("Done");
    }
    
    /**
     * Generate the import statements for the exported robot map file.
     * @param robot
     * @return The String of import statements
     */
    private String generateImports(RobotComponent robot) {
        final Set<String> imports = new TreeSet<String>();
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                String instruction = componentInstructions.get(self.getBase().getName()).get("Import");
                String className = componentInstructions.get(self.getBase().getName()).get("ClassName");
                imports.add(substitute(instruction, self, className));
            }
        });
        
        String out = "";
        for (String imp : imports) {
            if (!"".equals(imp)) out += imp + "\n";
        }
        return out;
    }
    
    private String generateDeclarations(RobotTree robot) {
        final LinkedList<String> declarations = new LinkedList<String>();
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                String instruction = componentInstructions.get(self.getBase().getName()).get("Declaration");
                String className = componentInstructions.get(self.getBase().getName()).get("ClassName");
                declarations.add(substitute(instruction, self, className));
            }
        });
        
        String out = "";
        for (String dec : declarations) {
            if (!"".equals(dec)) out += "    " + dec + "\n";
        }
        return out;
    }
    
    private String generateConstructions(RobotTree robot) {
        final LinkedList<String> constructions = new LinkedList<String>();
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                String instruction = componentInstructions.get(self.getBase().getName()).get("Construction");
                String extraInstruction = componentInstructions.get(self.getBase().getName()).get("Extra");
                String className = componentInstructions.get(self.getBase().getName()).get("ClassName");
                System.out.println(self.getBase().getName()+": "+className+" -- "+instruction+" -- "+extraInstruction);
                constructions.add(substitute(instruction, self, className));
                constructions.add(substitute(extraInstruction, self, className));
            }
        });

        String out = "";
        for (String cons : constructions) {
            if (!"".equals(cons)) out += "        " + cons + "\n";
        }
        return out;
    }
    
    private boolean subsystemExists(RobotComponent subsystem) {
        return false; // TODO: Implement
    }
    
    private String generateSubsystemExport(RobotComponent subsystem) {
        final LinkedList<String> components = new LinkedList<String>();
        subsystem.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                String instruction = componentInstructions.get(self.getBase().getName()).get("Subsystem Export");
                String className = componentInstructions.get(self.getBase().getName()).get("ClassName");
                System.out.println(self.getBase().getName()+": "+className+" -- "+instruction);
                components.add(substitute(instruction, self, className));
            }
        });

        String out = "";
        for (String comp : components) {
            if (!"".equals(comp)) out += "    " + comp + "\n";
        }
        return out;
    }
    
    private String getPath(RobotComponent robot) throws IOException {
        if ((robot.getProperty("Java Package")).equals("")) {
            String packageName = (String) JOptionPane.showInputDialog(MainFrame.getInstance().getFrame(), "Java Package", "Java Package", JOptionPane.PLAIN_MESSAGE, null, null, null);
            robot.setProperty("Java Package", packageName);
        }
        if ((robot.getProperty("Java Project")).equals("")) {
            String file = null;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showDialog(MainFrame.getInstance().getFrame(), "Export");
            if (result == JFileChooser.CANCEL_OPTION) {
                throw new IOException("No file selected.");
            } else if (result == JFileChooser.ERROR_OPTION) {
                throw new IOException("Error selecting file.");
            } else if (result == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile().getAbsolutePath();
            }
            robot.setProperty("Java Project", file);
        }
        System.out.println("Path: "+robot.getProperty("Java Project")+"/src/"+robot.getProperty("Java Package").replace(".", "/")+"/");
        return robot.getProperty("Java Project")+"/src/"+robot.getProperty("Java Package").replace(".", "/")+"/";
    }

    @Override
    public String getFullName(RobotComponent comp) {
        if (comp.getBase().getType().equals("Subsystem")) {
            return comp.getFullName().replace(" ", "_");
        } else {
            return getFullName(comp.getFullName());
        }
    }
    
    @Override
    public String getFullName(String s) {
        return s.toUpperCase().replace(" ", "_");
    }

    @Override
    public String getShortName(RobotComponent comp) {
        return getShortName(comp.getName());
    }

    @Override
    public String getShortName(String s) {
        return s.substring(0, 1).toLowerCase()+s.replace(" ", "").substring(1);
    }
}
