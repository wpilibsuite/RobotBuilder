package robotbuilder.robottree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.*;
import org.yaml.snakeyaml.Yaml;
import robotbuilder.MainFrame;
import robotbuilder.palette.Palette;
import robotbuilder.PropertiesDisplay;
import robotbuilder.RobotBuilder;
import robotbuilder.SimpleHistory;
import robotbuilder.data.*;
import robotbuilder.data.properties.Property;

/**
 *
 * @author brad
 * RobotTree is the tree representation of the robot map. It will contain
 * nodes that represent the various components that can be used to build
 * the robot. This is constructed by the user by dragging palette items to the tree.
 */
public class RobotTree extends JPanel implements TreeSelectionListener {

    JTree tree;
    DefaultTreeModel treeModel;
    PropertiesDisplay properties;
    /** Stores whether or not the RobotTree has been saved */
    public boolean saved;
    /** Names used by components during name auto-generation */
    private Set<String> usedNames = new HashSet<String>();
    private Map<String, Validator> validators;
    /** The currently selected node */
    private String filePath = null;
    
    private SimpleHistory<String> history = new SimpleHistory<String>();

    private JFileChooser fileChooser = new JFileChooser();
    Palette palette;

    public RobotTree(PropertiesDisplay properties, Palette palette) {
        this.palette = palette;
	fileChooser.setFileFilter(new FileNameExtensionFilter("YAML save file", "yml"));
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
                        return "<html>"+component.getBase().getHelp()+"<br/>"+
                                component.getErrorMessage().replace("\n", "<br/>")+"</html>";
                    }
                } catch (ClassCastException ex) { // Ignore folders
                    return null;
                }
            }
        };
        tree.setName("Robot Tree");
	tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	tree.addTreeSelectionListener(this);
	tree.setDropMode(DropMode.ON_OR_INSERT);
	add(new JScrollPane(tree), BorderLayout.CENTER);
	tree.setTransferHandler(new TreeTransferHandler(tree.getTransferHandler()));
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
        tree.addKeyListener(new KeyAdapter(){
            
            RobotComponent selected = null;
            TreePath path = null;
            
            @Override
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();
                path = tree.getSelectionPath();

                if(keyChar == KeyEvent.VK_DELETE) {
                    if(path != null) {
                        selected = (RobotComponent) path.getLastPathComponent();
                    } else {
                        return;
                    }

                    if(!selected.getBase().toString().equals("Subsystems") &&
                            !selected.getBase().toString().equals("OI") &&
                            !selected.getBase().toString().equals("Commands") &&
                            !selected.getBase().toString().equals("Robot")) {
                        delete(selected);
                        update();
                        takeSnapshot();
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), 
                                                      "Cannot delete \""+selected+"\"!", 
                                                      "Cannot Delete", 
                                                      JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        saved = true;
    }

    /**
     * Creates an empty TreeRoot.
     * @return An empty TreeRoot
     */
    private RobotComponent makeTreeRoot() {
	RobotComponent root = new RobotComponent("MyRobot", Palette.getInstance().getItem("Robot"), this);
        root.add(new RobotComponent("Subsystems", Palette.getInstance().getItem("Subsystems"), this));
        root.add(new RobotComponent("Operator Interface", Palette.getInstance().getItem("OI"), this));
        RobotComponent commands = new RobotComponent("Commands", Palette.getInstance().getItem("Commands"), this);
        root.add(commands);
        commands.add(new RobotComponent("Autonomous Command", Palette.getInstance().getItem("Command"), this));
        root.getProperty("Autonomous Command")._setValue("Autonomous Command");
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
     * @return 
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Gets the file path of the save file.
     * @return 
     */
    protected void setFilePath(String filePath) {
        this.filePath = filePath;
        MainFrame.getInstance().setTitle("FRC RobotBuilder"+(filePath==null?"":" -- "+filePath));
    }
    
    /**
     * @return The icon used to display an open tree element.
     */
    public Icon getOpenIcon() {
        return ((DefaultTreeCellRenderer) tree.getCellRenderer()).getOpenIcon(); 
    }

    /**
     * Gets the default name of a given component in the specified subsystem.
     * @param component The type of component to generate a default name for.
     * @return The default name.
     */
    String getDefaultComponentName(PaletteComponent componentType, String subsystem) {
	int i = 1;
	String name;
	while (true) {
	    name = componentType.toString() + " " + i;
	    if (!usedNames.contains((subsystem+name).toLowerCase())) {
		usedNames.add((subsystem+name).toLowerCase());
		return name;
	    }
            i++;
	}
    }

    /**
     * Adds a name to the used names list.
     * @param name The name being used
     */
    public void addName(String name) {
	usedNames.add(name.toLowerCase());
    }

    /**
     * Removes the given name from the used names list.
     * @param name The name being freed
     */
    public void removeName(String name) {
	usedNames.remove(name.toLowerCase());
    }

    /**
     * Checks to see if the {@code RobotTree} already contains
     * the given name.
     * @param name The name being checked
     */
    public boolean hasName(String name) {
	return usedNames.contains(name.toLowerCase());
    }

    @Override
    public void valueChanged(TreeSelectionEvent tse) {
	RobotComponent node = (RobotComponent) tree.getLastSelectedPathComponent();
        MainFrame.getInstance().updateStatus();

	if (node == null) {
	    return;
	}
	if (node instanceof RobotComponent) {
	    properties.setCurrentComponent(node);
            MainFrame.getInstance().setHelp(node.getBase().getHelpFile());
	}
    }

    /**
     * Save the RobotTree as a yaml file.
     * @param path 
     */
    public void save(String path) {
        setFilePath(path);
	try {
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
	if (getFilePath() == null) {
	    int result = fileChooser.showSaveDialog(MainFrame.getInstance().getFrame());
	    if (result == JFileChooser.CANCEL_OPTION) {
		return;
	    }
	    else if (result == JFileChooser.ERROR_OPTION) {
		return;
	    }
	    else if (result == JFileChooser.APPROVE_OPTION) {
                setFilePath(fileChooser.getSelectedFile().getAbsolutePath());
                if (!filePath.endsWith(".yml"))
                        filePath += ".yml";
	    }
	}
        save(filePath);
    }
    
    /**
     * Encodes the current state of the {@code RobotTree} as a String, 
     * which is equivalent to the contents of a {@code YAML} file that 
     * contains the current state of the {@code RobotTree}. 
     * @return The encoded state.
     */
    public String encode() {
        Object out = ((RobotComponent) treeModel.getRoot()).visit(new RobotVisitor() {
            @Override
            public Object visit(RobotComponent self, Object...extra) {
                Map<String, Object> me = new HashMap<String, Object>();
                me.put("Name", self.getName());
                me.put("Base", self.getBaseType());
                me.put("Properties", self.getProperties());
                List<Object> children = new ArrayList<Object>();
                for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
                    RobotComponent child = (RobotComponent) it.next();
                    children.add(child.visit(this));
                }
                me.put("Children", children);
                return me;
            }
        }, (Object[])null);
        Yaml yaml = new Yaml();
        return yaml.dump("Version "+RobotBuilder.VERSION)+"\n---\n"+yaml.dump(out);
    }

    /**
     * Load the RobotTree from a yaml file.
     * @param path 
     */
    public void load(File path) {
	try {
	    FileReader source = new FileReader(path);
            load(source);
	} catch (FileNotFoundException ex) {
            Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
	    Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
	}
        setFilePath(path.getAbsolutePath());
    }
    
    /**
     * Load the RobotTree from a yaml string.
     * @param path 
     */
    public void load(String text) {
        load(new StringReader(text));
    }
    
    /**
     * Load the RobotTree from a yaml string.
     * @param path 
     */
    public void load(Reader in) {
        resetTree();
        
        Iterator docs = new Yaml().loadAll(in).iterator();
        
        String version = (String) docs.next();
        if (!version.equals("Version "+RobotBuilder.VERSION)) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "File was made with RobotBuilder "+version.replace("V", "v")
                    +" which is incompatable with version "+RobotBuilder.VERSION+".",
                    "Wrong Version", JOptionPane.ERROR_MESSAGE);
            return; // Give up
        }

        try {
            Map<String, Object> details = (Map<String, Object>) docs.next();
            RobotComponent root = new RobotComponent();
        
            root.visit(new RobotVisitor() {
                @Override
                public Object visit(RobotComponent self, Object...extra) {
                    Map<String, Object> details = (Map<String, Object>) extra[0];
                    self.setRobotTree(robot);
                    self.setName((String) details.get("Name"));
                    self.setBaseType((String) details.get("Base"));
                    self.setProperties((Map<String, Property>) details.get("Properties"));
                    for (String propertyName : self.getBase().getPropertiesKeys()) {
                        Object value = self.getProperties().get(propertyName);
                        if (value != null) value = ((Property) value).getValue();
                        Property property = self.getBase().getProperty(propertyName).copy();
                        property.setComponent(self);
                        if (value != null) property._setValue(value);
                        self.getProperties().put(propertyName, property);
                    }
                    for (Object childDescription : (List) details.get("Children")) {
                        RobotComponent child = new RobotComponent();
                        child.visit(this, (Map<String, Object>) childDescription);
                        self.add(child);
                    }
                    return null;
                }
            }, details);
            
            treeModel.setRoot(root);
            
            // Validate loaded ports
            ((RobotComponent) treeModel.getRoot()).walk(new RobotWalker() {
                @Override
                public void handleRobotComponent(RobotComponent self) {
                    for (Property property : self.getProperties().values()) {
                        property.update();
                    }
                }
            });
            
            properties.setCurrentComponent(root);
            update();
            
            // Add names to used names list
            walk(new RobotWalker() {
                @Override
                public void handleRobotComponent(RobotComponent self) {
                    addName(self.getFullName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter writer = new StringWriter();
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "Failed to load the file.\nCause: "+e.getCause()
                    +"\nMessage: "+e.getMessage()+"\nStacktrace:\n"+writer.toString().substring(0, 500),
                    "Failed to Load File", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void load() {
        if (OKToClose()) {
            int result = fileChooser.showOpenDialog(MainFrame.getInstance().getFrame());
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
	treeModel.reload();
        properties.update();

	for (int i = 0; i < tree.getRowCount(); i++) {
	    tree.expandRow(i);
	}
    }
//    public static DataFlavor ROBOT_COMPONENT_FLAVOR = new DataFlavor(RobotComponent.class, "Robot Component Flavor");
    public static DataFlavor ROBOT_COMPONENT_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + RobotComponent.class.getName() + "\"", "Robot Component Flavor");
    private RobotTree robot = this;

    /**
     * Gets if it's okay to close the application frame.<p>
     * A dialog appears if this hasn't been saved when the frame was told to
     * close and gives the user four options:
     * <ul>
     * <li>If the <i>Save</i> option is chosen, then the frame will save
     * and then quit.
     * <li>If the <i>Discard</i> option is chosen, then the frame
     * will close without saving.
     * <li>If the <i>Cancel</i> option is chosen, the application will
     * <u>not</u> close and will <u>not</u> save.
     * <li>If the dialog is closed in any way, the application will <u>not</u>
     * close and will <u>not</u> save.
     * </ul>
     * @return True if it's okay to close the application frame, else false.
     */
    public boolean OKToClose() {
	String[] options = {"Save", "Discard", "Cancel"};
	if (saved) {
	    return true;
	}
	int value = JOptionPane.showOptionDialog(MainFrame.getInstance().getFrame(),
		"This RobotMap has changed since it was last saved. What \nshould happen to the file?",
		"Save file",
		JOptionPane.YES_NO_CANCEL_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[0]);
        switch(value){
            case JOptionPane.YES_OPTION:
                save();
                return true;
            case JOptionPane.NO_OPTION:
                return true;
            case JOptionPane.CANCEL_OPTION:
                return false;
            default: return false;
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
            saved = true;
            setFilePath(null);
            MainFrame.getInstance().prefs.put("FileName", "");
        }
    }

    private void resetTree() {
        DefaultMutableTreeNode root = makeTreeRoot();
        treeModel.setRoot(root);
        tree.setSelectionPath(new TreePath(root));
        usedNames = new HashSet<String>();
        validators = palette.getValidators();
    }

    /**
     * @return The root RobotComponent of the RobotTree
     */
    public RobotComponent getRoot() {
        return treeModel != null ? (RobotComponent) treeModel.getRoot() : null;
    }

    public Iterable<RobotComponent> getSubsystems() {
        final LinkedList<RobotComponent> subsystems = new LinkedList<RobotComponent>();
        walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                if (self.getBase().getType().equals("Subsystem")) {
                    subsystems.add(self);
                }
            }
        });
        
        return subsystems;
    }

    public Iterable<RobotComponent> getCommands() {
        final LinkedList<RobotComponent> commands = new LinkedList<RobotComponent>();
        walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                if (self.getBase().getType().equals("Command")) {
                    commands.add(self);
                }
            }
        });
        
        return commands;
    }

    public RobotComponent getComponentByName(final String name) {
        final RobotComponent[] component = new RobotComponent[1];
        robot.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                if (self.getFullName().equals(name)) {
                    component[0] = self;
                }
            }
        });
        return component[0];
    }

    public boolean isRobotValid() {
        final boolean valid[] = {true};
        walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                if (!self.isValid()) valid[0] = false;
            }
        });
        
        return valid[0];
    }
    
    /**
     * Takes a snapshot of the current state and dirties the save flag.
     */
    public void takeSnapshot(){
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
    public void redo(){
        load(history.redo());
    }
    
    public void setSaved() {
        saved = true;
    }

    public void delete(final RobotComponent component) {
        component.walk(new RobotWalker() {
            @Override public void handleRobotComponent(RobotComponent self) {
                if (self != component) { delete(self); }
            }
        });
        component.handleDelete();
        removeName(component.getFullName());
        properties.setCurrentComponent((RobotComponent) component.getParent());
        component.removeFromParent();
    }
}
