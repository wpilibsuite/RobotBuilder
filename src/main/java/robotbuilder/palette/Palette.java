
package robotbuilder.palette;

import java.awt.CardLayout;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.util.*;
import java.util.regex.Matcher;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.yaml.snakeyaml.Yaml;

import robotbuilder.Utils;
import robotbuilder.utils.YamlUtils;
import robotbuilder.data.PaletteComponent;
import robotbuilder.data.Validator;
import robotbuilder.extensions.ExtensionComponent;
import robotbuilder.extensions.Extensions;

/**
 * The Palette is the set of components that can be used to create the robot
 * map. Each palette item represents a motor, sensor, or other component. These
 * are dragged to the robot tree.
 *
 * @author brad
 */
public class Palette extends JPanel {

    public static final int UNLIMITED = -1;

    private TreeModel model;
    static private Palette instance = null;
    private Map<String, PaletteComponent> paletteItems = new HashMap<>();
    private Map<String, Validator> validators;

    public enum Layouts {

        TREE, ICONS
    }

    private Palette() {
        this.validators = new HashMap<String, Validator>();
        InputStreamReader in;
        in = new InputStreamReader(Utils.getResourceAsStream("/PaletteDescription.yaml"));
        // Apply macros, if any
        StringWriter writer = new StringWriter();
        Velocity.setProperty("runtime.conversion.handler","none");
        Velocity.setProperty("parser.space_gobbling","structured");
        Velocity.setProperty("directive.if.empty_check","false");
        VelocityEngine ve = new VelocityEngine();
        Context context = new VelocityContext();

        context.put("home", System.getProperty("user.home").replace("\\", "\\\\") + Matcher.quoteReplacement(File.separator));
        ve.evaluate(context, writer, "RobotBuilder:PaletteDescription.yaml", in);
        Yaml yaml = YamlUtils.yaml;
        LinkedHashMap<String, Object> description = yaml.load(writer.toString());
        List<Map<String, List<PaletteComponent>>> sections = (List) description.get("Palette");
        List<Validator> validatorList = (List) description.get("Validators");
        List<ExtensionComponent> components = Extensions.getComponents();
        components.stream()
            .filter(ExtensionComponent::hasValidators)
            .map(ExtensionComponent::getValidators)
            .map(yaml::load)
            .map(Validator.class::cast)
            .peek(v -> System.out.println("Adding extension validator: " + v.getName()))
            .forEach(validatorList::add);
        sections.stream()
                .forEach(section -> {
                    String sectionName = section.keySet().toArray(new String[0])[0];
                    System.out.println("Adding extensions for section: " + sectionName);
                    components.stream()
                        .filter(c -> c.getPaletteSection().equals(sectionName))
                        .map(ExtensionComponent::getPaletteDescription)
                        .map(yaml::load)
                        .map(PaletteComponent.class::cast)
                        .forEach(p -> {
                            p.setIsExtension(true);
                            section.get(sectionName).add(p);
                        });
                });

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Palette");
        createPalette(root, sections);
        loadValidators(validatorList);
        model = new DefaultTreeModel(root);

        setLayout(new CardLayout());
        JScrollPane iconView = new JScrollPane(new IconView(this));
        iconView.getVerticalScrollBar().setUnitIncrement(32);
        add(iconView, Layouts.ICONS.toString());
        add(new JScrollPane(new TreeView(this)), Layouts.TREE.toString());
    }

    /**
     * Singleton getInstance method returns the single instance of the palette.
     *
     * @return Palette instance
     */
    public static Palette getInstance() {
        if (instance == null) {
            instance = new Palette();
        }
        return instance;
    }

    /**
     * Get the paletteItem that corresponds to a name. Each item on the palette
     * has a unique name and this method returns the PaletteItem object that
     * corresponds to the given name.
     *
     * @param name The name of the palette item
     * @return The PaletteItem for the given name
     */
    public PaletteComponent getItem(String name) {
        PaletteComponent item = paletteItems.get(name);
        if (item == null) {
            throw new IllegalArgumentException("No such palette item: " + name);
        }
        return item;
    }

    /**
     * Build the palette tree recursively by traversing the JSON data object
     *
     * @param root The parent tree node
     * @param sections Palette component data
     */
    private void createPalette(DefaultMutableTreeNode root, List<Map<String, List<PaletteComponent>>> sections) {
        // Allow order to be imposed on the palette
        sections.stream().forEach(section -> {
            String key = section.keySet().iterator().next();
            if (key.isEmpty()) {
                return;
            }
            List<PaletteComponent> items = section.get(key);
            DefaultMutableTreeNode node = null;
            if (!key.equals("Hidden")) {
                node = new DefaultMutableTreeNode(key);
                root.add(node);
            }
            for (PaletteComponent item : items) {
                createPaletteComponent(node, item);
            }
        });
    }

    private void createPaletteComponent(DefaultMutableTreeNode root, PaletteComponent component) {
        paletteItems.put(component.getName(), component);

        if (root != null) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(component);
            root.add(node);
        }
    }

    private void loadValidators(List<Validator> validatorsToAdd) {
        validatorsToAdd.stream().forEach(validator -> validators.put(validator.getName(), validator));
    }

    public Map<String, Validator> getValidators() {
        Map<String, Validator> copy = new HashMap<>();
        validators.keySet().stream().forEach((key) -> copy.put(key, validators.get(key).copy()));
        return copy;
    }

    public Collection<PaletteComponent> getPaletteComponents() {
        return paletteItems.values();
    }

    public TreeModel getPaletteModel() {
        return model;
    }

    public void setView(Layouts view) {
        ((CardLayout) getLayout()).show(this, view.toString());
    }
}
