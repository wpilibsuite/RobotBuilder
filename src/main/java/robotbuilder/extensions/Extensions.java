
package robotbuilder.extensions;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import lombok.experimental.UtilityClass;

import org.yaml.snakeyaml.Yaml;

import robotbuilder.utils.YamlUtils;
import robotbuilder.Utils;
import static robotbuilder.extensions.ExtensionComponent.CONFIG_FILE_NAME;
import static robotbuilder.extensions.ExtensionComponent.CPP_EXPORT_FILE_NAME;
import static robotbuilder.extensions.ExtensionComponent.HTML_HELP_FILE_NAME;
import static robotbuilder.extensions.ExtensionComponent.ICON_FILE_NAME;
import static robotbuilder.extensions.ExtensionComponent.JAVA_EXPORT_FILE_NAME;
import static robotbuilder.extensions.ExtensionComponent.PALETTE_FILE_NAME;
import static robotbuilder.extensions.ExtensionComponent.VALIDATORS_FILE_NAME;
import static robotbuilder.RobotBuilder.FRC_HOME;

/**
 *
 * @author Sam Carlberg
 */
@UtilityClass
public class Extensions {

    /**
     * The path to the extensions folder. By default, it should be in {@code FRC_HOME/Robotbuilder/extensions/}
     * where FRC_HOME is {@code ~/wpilib/YEAR} on *nix and macOS and {@code c:\Users\Public\wpilib\YEAR} on Windows.
     */
    public final String EXTENSIONS_FOLDER_PATH = FRC_HOME + "/Robotbuilder/extensions/";
    private File extensionsFolder;

    private List<ExtensionComponent> components;

    private boolean scannedComponents = false;
    private boolean initialized = false;

    public void init() {
        if (!initialized) {
            extensionsFolder = new File(EXTENSIONS_FOLDER_PATH);
            extensionsFolder.mkdirs();
            initialized = true;
        }
    }

    /**
     * Gets the components in the extension folder. If the folder hasn't been
     * scanned, this will scan the folder and generate the relevant
     * {@link ExtensionComponent ExtensionComponents}. If you want to rescan,
     * call {@link #scanForComponents()}.
     */
    public List<ExtensionComponent> getComponents() {
        if (!scannedComponents) {
            components = scanForComponents();
        }
        System.out.println("Extension components: " + components.stream().map(ExtensionComponent::getName).collect(Collectors.toList()));
        return components;
    }

    public List<ExtensionComponent> scanForComponents() {
        List<ExtensionComponent> l = new ArrayList<>();
        Stream.of(extensionsFolder.listFiles())
                .filter(f -> f.isDirectory())
                .filter(d -> hasRequiredFiles(d))
                .filter(d -> Stream.of(d.listFiles()).
                                    allMatch(f -> isValidFile(f)))
                .map(d -> makeExtensionComponent(d))
                .forEach(l::add);
        scannedComponents = true;
        return l;
    }

    private ExtensionComponent makeExtensionComponent(File extensionDir) {
        String name = extensionDir.getName();

        String pdFile = extensionDir.getAbsolutePath() + "/" + PALETTE_FILE_NAME;
        String paletteDescription = Utils.getFileText(pdFile);

        String configFile = extensionDir.getAbsolutePath() + "/" + CONFIG_FILE_NAME;
        String configText = Utils.getFileText(configFile);

        String section = getPaletteSection(configText);

        String iconFile = extensionDir.getAbsolutePath() + "/" + ICON_FILE_NAME;
        Icon icon = new ImageIcon(iconFile);

        String vFile = extensionDir.getAbsolutePath() + "/" + VALIDATORS_FILE_NAME;
        String validators = Utils.getFileText(vFile);

        String jeFile = extensionDir.getAbsolutePath() + "/" + JAVA_EXPORT_FILE_NAME;
        String javaExport = Utils.getFileText(jeFile);

        String ceFile = extensionDir.getAbsolutePath() + "/" + CPP_EXPORT_FILE_NAME;
        String cppExport = Utils.getFileText(ceFile);

        String hhFile = extensionDir.getAbsolutePath() + "/" + HTML_HELP_FILE_NAME;
        String htmlHelp = Utils.getFileText(hhFile);

        return new ExtensionComponent(name, paletteDescription, section, icon, validators, javaExport, cppExport, htmlHelp);
    }

    private boolean hasRequiredFiles(File possibleExtensionDir) {
        return Stream.of(possibleExtensionDir.listFiles())
                .filter(File::isFile)
                .map(File::getName)
                .filter(ExtensionComponent.EXTENSION_FILES::contains)
                .count() == ExtensionComponent.EXTENSION_FILES.size();
    }

    private boolean isValidFile(File file) {
        switch (file.getName()) {
            case HTML_HELP_FILE_NAME:
            case ICON_FILE_NAME:
            case JAVA_EXPORT_FILE_NAME:
            case CPP_EXPORT_FILE_NAME:
                // These are non-critical files; return true
                return true;
            case CONFIG_FILE_NAME:
                // TODO maybe validate this?
                return true;
            case PALETTE_FILE_NAME:
            case VALIDATORS_FILE_NAME:
                // Make sure these are valid yaml files
                return Utils.doesNotError(() -> YamlUtils.load(Utils.getFileText(file)));
            default:
                System.out.println("Unexpected file in extensions directory: " + file + ", ignoring.");
                return true;
        }
    }

    private String getPaletteSection(String configFileText) {
        Stream<String> lines = Stream.of(configFileText.split("\n"));
        Optional<String> s = lines.filter(line -> line.startsWith("section=")).findFirst();
        return s.map(line -> line.split("=")[1]).orElse("[None]");
    }

}
