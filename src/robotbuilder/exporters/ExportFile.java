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
 * @author alex
 */
class ExportFile extends File {
    File template;
    String update;
    Map<String, String> modifications = new HashMap<String, String>();
    Map<String, String> vars = new HashMap<String, String>();

    ExportFile(Map<String, Object> map, String path) {
        super((String) map.get("Export"));
        System.out.println(this);
        template = new File(path + ((String) map.get("Source")));
        update = (String) map.get("Update");
        modifications = (Map<String, String>) map.get("Modifications");
        vars = (Map<String, String>) map.get("Variables");
    }

    public void export(GenericExporter exporter) throws IOException {
        // Build the context
        Context fileContext = new VelocityContext(exporter.rootContext);
        if (vars != null) {
            for (String key : vars.keySet()) {
                fileContext.put(key, exporter.eval(vars.get(key), fileContext));
            }
        }
        
        if (this.exists()) backup(exporter); // Create a backup for the user!
        
        // Export
        if (!this.exists() || update.equals("Overwrite")) {
            FileWriter out = new FileWriter(this);
            out.write(exporter.eval(template, fileContext));
            out.close();
        } else if (update.equals("Modify")) {
            String file = exporter.openFile(this.getAbsolutePath());
            for (String id : modifications.keySet()) {
                Context idContext = new VelocityContext(fileContext);
                idContext.put("id", id);
                String beginning = exporter.eval(exporter.begin_modification, idContext);
                String end = exporter.eval(exporter.end_modification, idContext);
                file = file.replaceAll("(" + beginning + ")([\\s\\S]*?)(" + end + ")",
                        "$1\n" + exporter.eval(new File(exporter.path + modifications.get(id)), idContext) + "\n    $3");
            }
            FileWriter out = new FileWriter(this);
            out.write(file);
            out.close();
        }
    }
    
    void backup(GenericExporter exporter) throws IOException {
        File backup = new File(this.getAbsoluteFile()+"~");
        FileWriter out = new FileWriter(backup);
        out.write(exporter.openFile(this.getAbsolutePath()));
        out.close();
    }
}
