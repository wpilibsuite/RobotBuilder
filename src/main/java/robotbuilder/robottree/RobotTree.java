
package robotbuilder.robottree;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import lombok.Getter;

import org.yaml.snakeyaml.Yaml;

import robotbuilder.MainFrame;
import robotbuilder.palette.Palette;
import robotbuilder.PropertiesDisplay;
import robotbuilder.RobotBuilder;
import robotbuilder.SimpleHistory;
import robotbuilder.data.PaletteComponent;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.RobotVisitor;
import robotbuilder.data.RobotWalker;
import robotbuilder.data.Validator;
import robotbuilder.data.properties.Property;

/**
 * RobotTree is the tree representation of the robot map. It will contain nodes
 * that represent the various components that can be used to build the robot.
 * This is constructed by the user by dragging palette items to the tree.
 *
 * @author Alex Henning
 * @author Sam Carlberg
 */
public class RobotTree extends JPanel {

    // Names of the top level components that cannot be renamed. If a new section is introduced, put its name here
    private static final String[] topLevelComponentNames = {"Subsystems", "Operator Interface", "Commands"};
    private static final List<String> topLevelComponentNameList = Arrays.asList(topLevelComponentNames);

    @Getter
    JTree tree;
    DefaultTreeModel treeModel;
    PropertiesDisplay properties;
    /**
     * Stores whether or not the RobotTree has been saved
     */
    @Getter
    private boolean saved;
    /**
     * Names used by components during name auto-generation
     */
    private Set<String> usedNames = new HashSet<>();
    private Map<String, Validator> validators;
    /**
     * The currently selected node
     */
    private String filePath = null;

    @Getter
    private SimpleHistory<String> history = new SimpleHistory<>();

    private JFileChooser fileChooser = new JFileChooser();
    Palette palette;

    @Getter
    private RobotComponent dndData;
    private Mouse jtma;

    public RobotTree(PropertiesDisplay properties, Palette palette) {
        this.palette = palette;
        fileChooser.setFileFilter(new FileNameExtensionFilter("YAML save file", "yaml", "yml"));
        this.properties = properties;
        this.properties.setRobotTree(this);
        setLayout(new BorderLayout());
        RobotComponent root = makeTreeRoot();
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel) {
            @Override
            public String getToolTipText(MouseEvent e) {
                try {
                    TreePath path = getClosestPathForLocation(e.getX(), e.getY());
                    final RobotComponent component = (RobotComponent) path.getLastPathComponent();
                    if (component.isValid()) {
                        return component.getBase().getHelp();
                    } else {
                        // HTML to get multi-line text.
                        return "<html>" + component.getBase().getHelp() + "<br/>"
                                + component.getErrorMessage().replace("\n", "<br/>") + "</html>";
                    }
                } catch (ClassCastException ex) { // Ignore folders
                    return null;
                }
            }
        };
        tree.setName("Robot Tree");
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jtma = new Mouse();
        tree.addMouseListener(jtma);
        tree.addMouseMotionListener(jtma);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        add(new JScrollPane(tree), BorderLayout.CENTER);
        tree.setTransferHandler(new TreeTransferHandler(tree.getTransferHandler(), this));
        tree.setDragEnabled(true);
        ToolTipManager.sharedInstance().registerComponent(tree);

        validators = palette.getValidators();

        tree.setCellRenderer(new RobotTreeCellRenderer());

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        tree.addMouseListener(new RightClickMouseAdapter());
        tree.setFocusable(true);
        this.setFocusable(true);
        tree.addKeyListener(new KeyAdapter() {

            RobotComponent selected = null;
            TreePath path = null;

            @Override
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();
                path = tree.getSelectionPath();

                if (keyChar == KeyEvent.VK_DELETE) {
                    if (path != null) {
                        selected = (RobotComponent) path.getLastPathComponent();
                    } else {
                        return;
                    }

                    if (selected != getRoot() && selected.getParent() != getRoot()) {
                        delete(selected);
                        update();
                        takeSnapshot();
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                "Cannot delete \"" + selected + "\"!",
                                "Cannot Delete",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        for (String topLevelComponentName : topLevelComponentNames) {
            addName(topLevelComponentName);
        }
        SwingUtilities.invokeLater(() -> properties.setCurrentComponent(root));
        tree.setSelectionPath(new TreePath(getRoot()));
        history.addState(encode());
        saved = true;
    }

    /**
     * Creates an empty TreeRoot.
     *
     * @return An empty TreeRoot
     */
    private RobotComponent makeTreeRoot() {
        RobotComponent root = new RobotComponent("MyRobot", Palette.getInstance().getItem("Robot"), this);
        root.add(new RobotComponent("Subsystems", Palette.getInstance().getItem("Subsystems"), this));
        root.add(new RobotComponent("Operator Interface", Palette.getInstance().getItem("OI"), this));
        RobotComponent commands = new RobotComponent("Commands", Palette.getInstance().getItem("Commands"), this);
        root.add(commands);
        commands.add(new RobotComponent("Autonomous Command", Palette.getInstance().getItem("Command"), this));
        root.getProperty("Autonomous Command").setValue("Autonomous Command");
        return root;
    }

    /**
     * Gets the Validator of the given name.
     */
    public Validator getValidator(String name) {
        return validators.get(name);
    }

    /**
     * Gets the file path of the save file.
     *
     * @return
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the file path of the save file.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
        MainFrame.getInstance().setTitle("FRC RobotBuilder" + (filePath == null ? "" : " -- " + filePath));
    }

    /**
     * @return The icon used to display an open tree element.
     */
    public Icon getOpenIcon() {
        return ((DefaultTreeCellRenderer) tree.getCellRenderer()).getOpenIcon();
    }

    /**
     * Gets the default name of a given component in the specified subsystem.
     *
     * @param component The type of component to generate a default name for.
     * @return The default name.
     */
    String getDefaultComponentName(PaletteComponent componentType, String subsystem) {
        int i = 1;
        String name;
        while (true) {
            name = componentType.toString() + " " + i;
            if (!hasName(subsystem + name)) {
                addName((subsystem + name));
                return name;
            }
            i++;
        }
    }

    /**
     * Adds a name to the used names list.
     *
     * @param name The name being used
     */
    public void addName(String name) {
        usedNames.add(name);
    }

    /**
     * Removes the given name from the used names list.
     *
     * @param name The name being freed
     */
    public void removeName(String name) {
        if (!topLevelComponentNameList.contains(name)) {
            usedNames.remove(name);
        }
    }

    /**
     * Checks to see if the {@code RobotTree} already contains the given name.
     *
     * @param name The name being checked
     */
    public boolean hasName(String name) {
        return usedNames.contains(name);
    }

    /**
     * Save the RobotTree as a yaml file.
     *
     * @param path
     */
    public void save(String path) {
        setFilePath(path);
        try {
            Files.createDirectories(Path.of(path).getParent());
            FileWriter save = new FileWriter(path);
            save.write(this.encode());
            save.close();
        } catch (IOException ex) {
            Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        saved = true;
        MainFrame.getInstance().prefs.put("FileName", getFilePath());
    }

    public void save() {
        fileChooser.setSelectedFile(new File(treeModel.getRoot().toString()));
        if (getFilePath() == null) {
            int result = fileChooser.showSaveDialog(MainFrame.getInstance());
            if (result == JFileChooser.CANCEL_OPTION) {
                return;
            } else if (result == JFileChooser.ERROR_OPTION) {
                return;
            } else if (result == JFileChooser.APPROVE_OPTION) {
                setFilePath(fileChooser.getSelectedFile().getAbsolutePath());
                if (!filePath.endsWith(RobotBuilder.SAVE_FILE_TYPE)) {
                    filePath += "." + RobotBuilder.SAVE_FILE_TYPE;
                }
            }
        }
        save(filePath);
    }

    /**
     * Encodes the current state of the {@code RobotTree} as a String, which is
     * equivalent to the contents of a {@code YAML} file that contains the
     * current state of the {@code RobotTree}.
     *
     * @return The encoded state.
     */
    public String encode() {
        Object out = ((RobotComponent) treeModel.getRoot()).visit(new RobotVisitor() {
            @Override
            public Object visit(RobotComponent self, Object... extra) {
                Map<String, Object> me = new HashMap<>();
                me.put("Name", self.getName());
                me.put("Base", self.getBaseType());
                me.put("Properties", self.getProperties());
                List<Object> children = new ArrayList<>();
                for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
                    RobotComponent child = (RobotComponent) it.next();
                    children.add(child.visit(this));
                }
                me.put("Children", children);
                return me;
            }
        }, (Object[]) null);
        Yaml yaml = new Yaml();
        return yaml.dump("Version " + RobotBuilder.VERSION) + "\n---\n" + yaml.dump(out);
    }

    /**
     * Load the RobotTree from a yaml file.
     *
     * @param path
     */
    public void load(File path) {
        try {
            FileReader source = new FileReader(path);
            load(source);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        setFilePath(path.getAbsolutePath());
    }

    /**
     * Load the RobotTree from a yaml string.
     *
     * @param text
     */
    public void load(String text) {
        load(new StringReader(text));
    }

    /**
     * Load the RobotTree from a yaml string.
     *
     * @param in
     */
    public void load(Reader in) {
        resetTree();

        Iterator docs = new Yaml().loadAll(in).iterator();

        String version = (String) docs.next();
        if (!isVersionCompatible(version, RobotBuilder.VERSION)) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "File was made with RobotBuilder " + version.replace("V", "v")
                    + ", which is incompatible with version " + RobotBuilder.VERSION + ".",
                    "Wrong Version", JOptionPane.ERROR_MESSAGE);
            return; // Give up
        }

        try {
            Map<String, Object> details = (Map<String, Object>) docs.next();
            RobotComponent root = new RobotComponent();

            root.visit(new RobotVisitor() {
                @Override
                public Object visit(RobotComponent self, Object... extra) {
                    Map<String, Object> details = (Map<String, Object>) extra[0];
                    self.setRobotTree(RobotTree.this);
                    self.setName((String) details.get("Name"));
                    self.setBaseType((String) details.get("Base"));
                    self.setProperties((Map<String, Property>) details.get("Properties"));
                    for (String propertyName : self.getBase().getPropertiesKeys()) {
                        Object value = self.getProperties().get(propertyName);
                        if (value != null) {
                            value = ((Property) value).getValue();
                        }
                        Property property = self.getBase().getProperty(propertyName).copy();
                        property.setComponent(self);
                        if (value != null) {
                            property.setValue(value);
                        }
                        self.getProperties().put(propertyName, property);
                    }
                    for (Object childDescription : (List) details.get("Children")) {
                        RobotComponent child = new RobotComponent();
                        child.visit(this, childDescription);
                        self.add(child);
                    }
                    return null;
                }
            }, details);

            treeModel.setRoot(root);

            // Validate loaded ports
            ((RobotComponent) treeModel.getRoot()).walk(component -> component.getProperties().values().forEach(Property::update));

            properties.setCurrentComponent(root);
            update();

            // Add names to used names list
            walk(component -> addName(component.getFullName()));
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter writer = new StringWriter();
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "Failed to load the file.\nCause: " + e.getCause()
                    + "\nMessage: " + e.getMessage() + "\nStacktrace:\n" + writer.toString().substring(0, 500),
                    "Failed to Load File", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isVersionCompatible(String fileVersion, String robotBuilderVersion) {
        fileVersion = fileVersion.replaceAll("[^0-9.]", ""); // strip down to "1.5", "0.4", etc.
        if (fileVersion.isEmpty()) {
            return false;
        }
        if (fileVersion.equals(robotBuilderVersion)) { // shortcut
            return true;
        }
        Integer[] fileMajorMinor
                = Arrays.stream(fileVersion.split("\\."))
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
        Integer[] robotBuilderMajorMinor
                = Arrays.stream(robotBuilderVersion.split("\\."))
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
        if (fileMajorMinor.length < 2) {
            // Need at least a major and a minor version
            return false;
        }
        if (fileMajorMinor[0] != robotBuilderMajorMinor[0]) {
            // Major version does not match
            return false;
        }
        // Major version is good, and minor version is lower
        return fileMajorMinor[1] <= robotBuilderMajorMinor[1];
    }

    public void load() {
        if (OKToClose()) {
            int result = fileChooser.showOpenDialog(MainFrame.getInstance());
            if (result == JFileChooser.CANCEL_OPTION) {
                return;
            } else if (result == JFileChooser.ERROR_OPTION) {
                return;
            } else if (result == JFileChooser.APPROVE_OPTION) {
                setFilePath(fileChooser.getSelectedFile().getAbsolutePath());
            }
            load(new File(filePath));
        }
    }

    public void walk(RobotWalker walker) {
        ((RobotComponent) this.treeModel.getRoot()).walk(walker);
    }

    /**
     * Updates the UI display to adjust for changed names.
     */
    public void update() {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            treeModel.reload((TreeNode) path.getLastPathComponent());
        } else {
            treeModel.reload();
        }
        properties.update();

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public static final DataFlavor ROBOT_COMPONENT_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + RobotComponent.class.getName() + "\"", "Robot Component Flavor");

    /**
     * Gets if it's okay to close the application frame.<p>
     * A dialog appears if this hasn't been saved when the frame was told to
     * close and gives the user four options:
     * <ul>
     * <li>If the <i>Save</i> option is chosen, then the frame will save and
     * then quit.
     * <li>If the <i>Discard</i> option is chosen, then the frame will close
     * without saving.
     * <li>If the <i>Cancel</i> option is chosen, the application will
     * <u>not</u> close and will <u>not</u> save.
     * <li>If the dialog is closed in any way, the application will <u>not</u>
     * close and will <u>not</u> save.
     * </ul>
     *
     * @return True if it's okay to close the application frame, else false.
     */
    public boolean OKToClose() {
        String[] options = {"Save", "Discard", "Cancel"};
        if (saved) {
            return true;
        }
        int value = JOptionPane.showOptionDialog(MainFrame.getInstance(),
                "This RobotMap has changed since it was last saved. What \nshould happen to the file?",
                "Save file",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        switch (value) {
            case JOptionPane.YES_OPTION:
                save();
                return true;
            case JOptionPane.NO_OPTION:
                return true;
            case JOptionPane.CANCEL_OPTION:
                return false;
            default:
                return false;
        }
    }

    public void newFile() {
        newFile("MyRobot", "0");
    }

    public void newFile(String name, String team) {
        if (OKToClose()) {
            resetTree();
            getRoot().setName(name);
            getRoot().setProperty("Team Number", team);
            getRoot().setProperty("Java Package", "org.usfirst.frc" + team);
            saved = true;
            setFilePath(null);
            MainFrame.getInstance().prefs.put("FileName", "");
            properties.setCurrentComponent(getRoot());
        }
    }

    private void resetTree() {
        DefaultMutableTreeNode root = makeTreeRoot();
        treeModel.setRoot(root);
        tree.setSelectionPath(new TreePath(root));
        usedNames = new HashSet<>();
        validators = palette.getValidators();
    }

    /**
     * @return The root RobotComponent of the RobotTree
     */
    public RobotComponent getRoot() {
        return treeModel != null ? (RobotComponent) treeModel.getRoot() : null;
    }

    public List<RobotComponent> getSubsystems() {
        List<RobotComponent> subsystems = new LinkedList<>();
        walk(component -> {
            if (component.getBase().getType().equals("Subsystem")) {
                subsystems.add(component);
            }
        });

        return subsystems;
    }

    public List<RobotComponent> getCommands() {
        List<RobotComponent> commands = new LinkedList<>();
        walk(component -> {
            if (component.getBase().getType().equals("Command")) {
                commands.add(component);
            }
        });

        return commands;
    }

    public RobotComponent getComponentByName(final String name) {
        final RobotComponent[] component = new RobotComponent[1];
        walk((RobotComponent self) -> {
            if (self.getFullName().equals(name)) {
                component[0] = self;
            }
        });
        return component[0];
    }

    public boolean isRobotValid() {
        final boolean[] valid = {true};
        walk((RobotComponent self) -> {
            if (!self.isValid()) {
                valid[0] = false;
            }
        });

        return valid[0];
    }

    /**
     * Takes a snapshot of the current state and dirties the save flag.
     */
    public void takeSnapshot() {
        saved = false;
        history.addState(encode());
    }

    /**
     * Reverts to the previous snapshot if one exists.
     */
    public void undo() {
        load(history.undo());
    }

    /**
     * Changes to the next snapshot if one exists.
     */
    public void redo() {
        load(history.redo());
    }

    public void setSaved() {
        saved = true;
    }

    public void delete(final RobotComponent component) {
        component.walk(self -> {
            if (self != component) {
                delete(self);
            }
        });
        component.handleDelete();
        removeName(component.getFullName());
        properties.setCurrentComponent((RobotComponent) component.getParent());
        component.removeFromParent();
    }

    /**
     * Selects the component currently being edited.
     */
    public void selectEditingComponent() {
        selectRobotComponent(properties.getCurrentComponent());
    }

    /**
     * Selects the given robot component in the tree. Will do nothing if passed
     * null.
     *
     * @param component
     */
    public void selectRobotComponent(RobotComponent component) {
        if (component == null) {
            return;
        }
        tree.setSelectionPath(new TreePath(treeModel.getPathToRoot(component)));
    }

    private class Mouse extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            Object node = tree.getClosestPathForLocation(e.getPoint().x, e.getPoint().y).getLastPathComponent();
            if (node instanceof RobotComponent) {
                dndData = (RobotComponent) node;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            MainFrame.getInstance().updateStatus();
            Object node = tree.getClosestPathForLocation(e.getPoint().x, e.getPoint().y).getLastPathComponent();
            if (node instanceof RobotComponent) {
                RobotComponent comp = (RobotComponent) node;
                properties.setCurrentComponent(comp);
                MainFrame.getInstance().setHelp(comp.getBase());
            }
        }

    }

}
