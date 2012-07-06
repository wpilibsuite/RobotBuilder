/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.exporters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
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
    private static String[] DESCRIPTION_PROPERTIES = {"Import", "Declaration", "Construction", "Extra", "ClassName"};
    private static String ROBOT_MAP_TEMPLATE = "export/java/RobotMap.java";

    @Override
    public void export(RobotTree robot) throws IOException {
        System.out.println("Loading export description for java");
        loadExportDescription(DESCRIPTION_PATH, DESCRIPTION_PROPERTIES);
        
        System.out.println("Loading template "+ROBOT_MAP_TEMPLATE);
        String template = loadTemplate(ROBOT_MAP_TEMPLATE);
        
        getPath(robot.getRoot()); // TODO: Ugly hack
        System.out.println("Substituting real values into template");
        template = substitute(template, "package", robot.getRoot().getProperty("Java Package"));
        template = substitute(template, "imports", generateImports(robot));
        template = substitute(template, "declarations", generateDeclarations(robot));
        template = substitute(template, "constructions", generateConstructions(robot));
        
        System.out.println("Writing file");
        FileWriter out = new FileWriter(getPath(robot.getRoot())+"RobotMap.java"); // TODO: convert package to path
        out.write(template);
        out.close();
        System.out.println("Done");
    }
    
    /**
     * Generate the import statements for the exported robot map file.
     * @param robot
     * @return The String of import statements
     */
    private String generateImports(RobotTree robot) {
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
                if (!file.endsWith(".html"))
                    file += ".html";
            }
            robot.setProperty("Java Project", file);
        }
        System.out.println("Path: "+robot.getProperty("Java Project")+"/src/"+robot.getProperty("Java Package").replace(".", "/")+"/");
        return robot.getProperty("Java Project")+"/src/"+robot.getProperty("Java Package").replace(".", "/")+"/";
    }
}
