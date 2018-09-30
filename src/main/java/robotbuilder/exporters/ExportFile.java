
package robotbuilder.exporters;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import robotbuilder.Utils;
import robotbuilder.utils.CodeFileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Alex Henning
 */
public class ExportFile {

    private File export;
    private String source, update;
    private Map<String, String> modifications = new HashMap<>();
    private Map<String, String> vars = new HashMap<>();
    private boolean executable = false;

    public boolean export(GenericExporter exporter) throws IOException {
        System.out.println("Exporting:  " + source + " to " + export);
        boolean newProject = false;
        // Build the context
        Context fileContext = new VelocityContext(exporter.rootContext);
        if (vars != null) {
            for (String key : vars.keySet()) {
                fileContext.put(key, exporter.eval(vars.get(key), fileContext));
            }
        }

        if (export.exists()) {
            backup(exporter); // Create a backup for the user!
        } else {
            newProject = mkdir(export.getParentFile());
        }

        String oldType = CodeFileUtils.getSavedSuperclass(export);
        String newType = ("Exact".equals(update)) ? "" : CodeFileUtils.getSavedSuperclass(exporter.evalResource(source, fileContext));
        System.out.println("Saved type: " + oldType);
        System.out.println("  New type: " + newType);
        // Export
        if (!export.exists() && "Exact".equals(update)) {
            try (InputStream in = Utils.getResourceAsStream(source)) {
                Files.copy(in, export.toPath());
            }
        } else if (!export.exists() || "Overwrite".equals(update) || !equals(newType, oldType)) {
            System.out.println("Overwriting " + export);
            try (FileWriter out = new FileWriter(export)) {
                out.write(exporter.evalResource(source, fileContext));
            }
        } else if ("Modify".equals(update)) {
            System.out.println("Modifying " + export);
            String file = exporter.openFile(export.getAbsolutePath());
            for (String id : modifications.keySet()) {
                Context idContext = new VelocityContext(fileContext);
                idContext.put("id", id);
                String beginning = exporter.eval(exporter.begin_modification, idContext);
                String end = exporter.eval(exporter.end_modification, idContext);
                file = file.replaceAll("(" + beginning + ")([\\s\\S]*?)(" + end + ")",
                        "$1\r\n" + exporter.evalResource(modifications.get(id), idContext) + "\r\n    $3");
            }
            try (FileWriter out = new FileWriter(export)) {
                file = file.replaceAll("\r\n?|\n", "\r\n");
                out.write(file);
            }
        }
        if (executable) {
            export.setExecutable(true);
        }
        return newProject;
    }

    void backup(GenericExporter exporter) throws IOException {
        File backup = new File(export.getAbsoluteFile() + "~");
        try (FileWriter out = new FileWriter(backup)) {
            out.write(exporter.openFile(export.getAbsolutePath()));
        }
    }

    private boolean equals(String s1, String s2) {
        return (s1 == null) ? (s2 == null) : (s1.equals(s2));
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

    public void setExecutable(boolean b) {
        executable = b;
    }

    public boolean isExecutable() {
        return executable;
    }

    /**
     * Make a directory and any directory above it if necessary.
     *
     * @param dir The directory to make
     * @return true if a directory was created
     */
    private boolean mkdir(File dir) {
        if (dir.exists()) {
            return false;
        }
        mkdir(dir.getParentFile());
        return dir.mkdir();
    }
}
