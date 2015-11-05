
package robotbuilder.extensions;

import javax.swing.Icon;

import java.util.Arrays;
import java.util.List;

import lombok.NonNull;
import lombok.Value;

/**
 *
 * @author Sam Carlberg
 */
@Value
public class ExtensionComponent {

    /** The name of the palette description yaml file in the extension folder. */
    public static final String PALETTE_FILE_NAME = "PaletteDescription.yaml";
    /** The name of the palette icon file in the extension folder.*/
    public static final String ICON_FILE_NAME = "icon.png";
    /** The name of the validators yaml file in the extension folder. */
    public static final String VALIDATORS_FILE_NAME = "Validators.yaml";
    /** The name of the Java export file in the extension folder. */
    public static final String JAVA_EXPORT_FILE_NAME = "Java Export.yaml";
    /** The name of the C++ export file in the extension folder. */
    public static final String CPP_EXPORT_FILE_NAME = "C++ Export.yaml";
    /** The name of the HTML help file in the extension folder. */
    public static final String HTML_HELP_FILE_NAME = "help.html";
    /** The name of the config file in the extension folder. */
    public static final String CONFIG_FILE_NAME = "config.txt";

    /** List of all the names of files needed by an extension. */
    public static final List<String> EXTENSION_FILES = Arrays.asList(PALETTE_FILE_NAME, 
                                                                     ICON_FILE_NAME,
                                                                     VALIDATORS_FILE_NAME,
                                                                     JAVA_EXPORT_FILE_NAME,
                                                                     CPP_EXPORT_FILE_NAME,
                                                                     HTML_HELP_FILE_NAME,
                                                                     CONFIG_FILE_NAME);

    /**
     * The name of the component (e.g. "Double Motor")
     */
    @NonNull String name;

    /**
     * Palette description in the same format as
     * resources/export/PaletteDescription.yaml
     */
    @NonNull String paletteDescription;

    /**
     * The section of the palette this component belongs in.<p>
     * One of:
     * <ul>
     * <li>Subsystems
     * <li>Controllers
     * <li>Sensors
     * <li>Actuators
     * <li>Pneumatics
     * <li>OI
     * <li>Commands
     * </ul>
     *
     * This should be in the {@code config.txt} file, declared as
     * {@code section={Section Name}}
     */
    @NonNull String paletteSection;

    /**
     * The icon that shows up in the palette.
     */
    @NonNull Icon icon;

    /**
     * The validators for this component in the same format as
     * those in PaletteDescription.yaml
     */
    @NonNull String validators;

    /**
     * YAML-formatted export description in the same format as
     * ExportDescription.yaml in resources/export/java.
     */
    @NonNull String javaExport;

    /**
     * YAML-formatted export description in the same format as
     * ExportDescription.yaml in resources/export/cpp
     */
    @NonNull String cppExport;

    /**
     * HTML help text in the same format as the files in resources/export/html
     */
    @NonNull String htmlHelp;

    /**
     * Checks if this component has validators.
     */
    public boolean hasValidators() {
        return !validators.isEmpty();
    }

    /**
     * Checks if this component exports to C++
     */
    public boolean exportsToCpp() {
        return cppExport != null && !cppExport.isEmpty();
    }

    /**
     * Checks if this component exports to Java.
     */
    public boolean exportsToJava() {
        return javaExport != null && !javaExport.isEmpty();
    }

}
