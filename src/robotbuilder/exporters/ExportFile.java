/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.exporters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

/**
 *
 * @author Alex Henning
 */
public class ExportFile {
    private File export;
    private String source, update;
    private Map<String, String> modifications = new HashMap<String, String>();
    private Map<String, String> vars = new HashMap<String, String>();

    public void export(GenericExporter exporter) throws IOException {
        // Build the context
        Context fileContext = new VelocityContext(exporter.rootContext);
        if (vars != null) {
            for (String key : vars.keySet()) {
                fileContext.put(key, exporter.eval(vars.get(key), fileContext));
            }
        }
        
        if (export.exists()) backup(exporter); // Create a backup for the user!
        
        mkdir(export.getParentFile());
        
        // Export
        if (!export.exists() || update.equals("Overwrite")) {
            FileWriter out = new FileWriter(export);
            out.write(exporter.evalResource(source, fileContext));
            out.close();
        } else if (update.equals("Modify")) {
            String file = exporter.openFile(export.getAbsolutePath());
            for (String id : modifications.keySet()) {
                Context idContext = new VelocityContext(fileContext);
                idContext.put("id", id);
                String beginning = exporter.eval(exporter.begin_modification, idContext);
                String end = exporter.eval(exporter.end_modification, idContext);
                file = file.replaceAll("(" + beginning + ")([\\s\\S]*?)(" + end + ")",
                        "$1\r\n" + exporter.evalResource(modifications.get(id), idContext) + "\r\n    $3");
            }
            FileWriter out = new FileWriter(export);
            file = file.replaceAll("[\r\n]+", "\r\n");
            out.write(file);
            out.close();
        }
    }
    
    void backup(GenericExporter exporter) throws IOException {
        File backup = new File(export.getAbsoluteFile()+"~");
        FileWriter out = new FileWriter(backup);
        out.write(exporter.openFile(export.getAbsolutePath()));
        out.close();
    }
    
    
    // Getters and Setters for YAML
    public String getExport() {
        return export.getAbsolutePath();
    }
    public void setExport(String path) {
        export = new File(path);
    }
    public String getSource() {
        return source;
    }
    public void setSource(String path) {
        source = path;
    }
    public String getUpdate() {
        return update;
    }
    public void setUpdate(String update) {
        this.update = update;
    }
    public Map<String, String> getModifications() {
        return modifications;
    }
    public void setModifications(Map<String, String> modifications) {
        this.modifications = modifications;
    }
    public Map<String, String> getVariables() {
        return vars;
    }
    public void setVariables(Map<String, String> vars) {
        this.vars = vars;
    }

    /**
     * Make a directory and any directory above it if necessary.
     * @param dir The directory to make
     */
    private void mkdir(File dir) {
        if (dir.exists()) return;
        mkdir(dir.getParentFile());
        dir.mkdir();
    }
}
