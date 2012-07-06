/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.exporters;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import robotbuilder.MainFrame;
import robotbuilder.RobotTree;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.RobotWalker;

/**
 *
 * @author alex
 */
public class WiringExporter extends AbstractExporter {
    private static String HTML_TEMPLATE = "export/wiring/wiring.html";

    @Override
    public void export(RobotTree robot) throws IOException {
        System.out.println("Loading template "+HTML_TEMPLATE);
        String template = loadTemplate(HTML_TEMPLATE);
        
        System.out.println("Substituting real values into template");
        template = substitute(template, "DIO 1", generateDIO(1, robot));
        template = substitute(template, "DIO 2", generateDIO(2, robot));
        template = substitute(template, "PWM 1", generatePWM(1, robot));
        template = substitute(template, "PWM 2", generatePWM(2, robot));
        template = substitute(template, "Analog 1", generateAnalog(1, robot));
        template = substitute(template, "Analog 2", generateAnalog(2, robot));
        
        System.out.println("Writing file");
        FileWriter out = new FileWriter(getPath(robot));
        out.write(template);
        out.close();
        System.out.println("Opening file");
        launchBrowser(new File(getPath(robot)).toURI());
        System.out.println("Done");
    }

    private String generateDIO(final int module, RobotTree robot) {
        Map<Integer, String> mapping = filterComponents("Module (Digital)", "Channel (Digital)", module, robot);
        
        String out = "";
        
        for (Iterator i = mapping.keySet().iterator(); i.hasNext();) {
            Integer key =  (Integer) i.next();
            out += "<tr><td>"+key+"</td><td>"+mapping.get(key) +"</td></tr>";
        }
        
        return out;
    }

    
    private String generatePWM(int module, RobotTree robot) {
        Map<Integer, String> mapping = filterComponents("Module (Digital)", "Channel (PWM)", module, robot);
        
        String out = "";
        
        for (Iterator i = mapping.keySet().iterator(); i.hasNext();) {
            Integer key =  (Integer) i.next();
            out += "<tr><td>"+key+"</td><td>"+mapping.get(key) +"</td></tr>";
        }
        
        return out;
    }
    
    private String generateAnalog(int module, RobotTree robot) {
        Map<Integer, String> mapping = filterComponents("Module (Analog)", "Channel (Analog)", module, robot);
        
        String out = "";
        
        for (Iterator i = mapping.keySet().iterator(); i.hasNext();) {
            Integer key =  (Integer) i.next();
            out += "<tr><td>"+key+"</td><td>"+mapping.get(key) +"</td></tr>";
        }
        
        return out;
    }
    
    private Map<Integer, String> filterComponents(final String moduleFilter, final String portFilter, final int module, RobotTree robot) {
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
    
    private String getPath(RobotTree robot) throws IOException {
        if ((robot.getRoot().getProperty("Wiring File")).equals("")) {
            String file = null;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getAbsolutePath().endsWith(".html") || file.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "HTML Files";
                }
            });
            int result = fileChooser.showSaveDialog(MainFrame.getInstance().getFrame());
            if (result == JFileChooser.CANCEL_OPTION) {
                throw new IOException("No file selected.");
            } else if (result == JFileChooser.ERROR_OPTION) {
                throw new IOException("Error selecting file.");
            } else if (result == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile().getAbsolutePath();
                if (!file.endsWith(".html"))
                    file += ".html";
            }
            robot.getRoot().setProperty("Wiring File", file);
        }
        return robot.getRoot().getProperty("Wiring File");
    }

    private void launchBrowser(final URI toURI) throws IOException {
        try {
            Desktop.getDesktop().browse(toURI);
        } catch (UnsupportedOperationException e)  {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process p;
                    try {
                        System.out.println("firefox -new-tab "+toURI.toString());
                        p = Runtime.getRuntime().exec("firefox -new-tab "+toURI.toString());
                        p.waitFor();
                        // Do something after the forked process terminates
                        System.out.println("The browser is closed");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        }
    }

    @Override
    public String getFullName(RobotComponent comp) {
        return comp.getFullName();
    }

    @Override
    public String getFullName(String s) {
        return s;
    }

    @Override
    public String getShortName(RobotComponent comp) {
        return comp.getName();
    }

    @Override
    public String getShortName(String s) {
        return s;
    }
}
