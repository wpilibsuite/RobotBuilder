package robotbuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.*;
import org.yaml.snakeyaml.Yaml;
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

    private JTree tree;
    private DefaultTreeModel treeModel;
    private PropertiesDisplay properties;
    /** Stores whether or not the RobotTree has been saved */
    private boolean saved;
    /** Names used by components during name auto-generation */
    private Set<String> usedNames = new HashSet<String>();
    private Map<String, Validator> validators;
    /** The currently selected node */
    private String filePath = null;
    
    private SimpleHistory<String> history = new SimpleHistory<String>(100);
    private int snapshots = 0;
    
    private Preferences prefs;

    private JFileChooser fileChooser = new JFileChooser();

    RobotTree(PropertiesDisplay properties, Palette palette) {
	fileChooser.setFileFilter(new FileNameExtensionFilter("YAML save file", "yaml"));
	saved = true;
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
                    return ((RobotComponent) path.getLastPathComponent()).getBase().getHelp();
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
        
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component renderer = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                RobotComponent comp = (RobotComponent) value;
                
                if (comp.isValid()) {
                    renderer.setForeground(Color.black);
                } else {
                    renderer.setForeground(Color.red);
                }
                
                return renderer;
            }
        });
        
	for (int i = 0; i < tree.getRowCount(); i++) {
	    tree.expandRow(i);
	}
        //<editor-fold desc="Right click stuff" defaultstate="collapsed">
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)) { // Right click only
                    
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    Rectangle bounds = tree.getUI().getPathBounds(tree, path);
                    
                    if (bounds != null && bounds.contains(e.getX(), e.getY())) {
                        
                        final JPopupMenu mainMenu   = new JPopupMenu(); // main menu; encapsulates everything
                        final JMenu controllerMenu  = new JMenu("Add Controllers");
                        final JMenu sensorMenu      = new JMenu("Add Sensors");
                        final JMenu actuatorMenu    = new JMenu("Add Actuators");
                        final JMenu pneumaticMenu   = new JMenu("Add Pneumatics");
                        
                        final RobotComponent selected = (RobotComponent) path.getLastPathComponent(); // The component that's been clicked
                        RobotComponent componentToAdd = null;
                        
                        final String name          = selected.toString();
                        final String selectedType  = selected.getBase().getName(); // Subsystem -> Subsystem, 
                        String gottenType          = selected.getBase().getType(); // Victor -> Actuator, Gyro -> PIDSource, etc.
                        
                        if(gottenType.equals("PIDSource")) gottenType = "Sensor"; // PIDSource -> Sensor
                        final String componentType = gottenType;
                        
                        final int numSupports = 25;
                        
                        
                        System.out.println("\nComponent name: \""+name+"\"");
                        System.out.println("Type of selected component: \""+selectedType+"\"");
                        System.out.println("Given type of selected component: \""+gottenType+"\"");
                        System.out.println("Base type of component: \""+componentType+"\"\n");
                        
                        final JMenuItem delete = new JMenuItem("Delete");
                        delete.setAction(new DeleteItemAction("Delete", selected));
//                        final JMenuItem wipe = new JMenuItem("Clear");
//                        wipe.setAction(new ClearAction("Clear", selected));
                        final JMenuItem cancel = new JMenuItem("Cancel");
                        
                        
                        JMenuItem[] addActions    = new JMenuItem[3];
                        JMenuItem[] subsystemAdds = new JMenuItem[numSupports];
                        
                        
                        //<editor-fold defaultstate="collapsed" desc="Main folders: Subsystems, OI, and Commands">
                        if(selectedType.equals("Subsystems")){
                            delete.setEnabled(false);
                            addActions[0] = new JMenuItem("Add Subsystem");
                            addActions[1] = new JMenuItem("Add PID Subsystem");
                            
                        } else if(selectedType.equals("OI")){
                            delete.setEnabled(false);
                            addActions[0] = new JMenuItem("Add Joystick");
                            addActions[1] = new JMenuItem("Add Joystick Button");
                            
                        } else if(selectedType.equals("Commands")){
                            delete.setEnabled(false);
                            addActions[0] = new JMenuItem("Add Command");
                            addActions[1] = new JMenuItem("Add Command Group");
                            addActions[2] = new JMenuItem("Add PID Command");
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Robot Drives">
                        else if(selectedType.equals("Robot Drive 4")|| selectedType.equals("Robot Drive 2")) {
                            addActions[0] = new JMenuItem("Add Victor");
                            addActions[1] = new JMenuItem("Add Jaguar");
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Subsystem Menus and Choices">
                        if(selectedType.equals("Subsystem") || selectedType.equals("PID Subsystem") || selectedType.equals("PID Controller")){
                            subsystemAdds[0]  = new JMenuItem("Add Robot Drive 4");
                            subsystemAdds[1]  = new JMenuItem("Add Robot Drive 2");
                            subsystemAdds[2]  = new JMenuItem("Add PID Controller");
                            for(int i = 0; i < 3; i++)
                                controllerMenu.add(subsystemAdds[i]);
                            
                            subsystemAdds[3]  = new JMenuItem("Add Gyro");
                            subsystemAdds[4]  = new JMenuItem("Add Accelerometer");
                            subsystemAdds[5]  = new JMenuItem("Add Quadrature Encoder");
                            subsystemAdds[6]  = new JMenuItem("Add Indexed Encoder");
                            subsystemAdds[7]  = new JMenuItem("Add Gear Tooth Sensor");
                            subsystemAdds[8]  = new JMenuItem("Add Potentiometer");
                            subsystemAdds[9]  = new JMenuItem("Add Analog Input");
                            subsystemAdds[10] = new JMenuItem("Add Limit Switch");
                            subsystemAdds[11] = new JMenuItem("Add Digital Input");
                            subsystemAdds[12] = new JMenuItem("Add Ultrasonic");
                            for(int i = 3; i < 12; i++)
                                sensorMenu.add(subsystemAdds[i]);

                            subsystemAdds[13] = new JMenuItem("Add Victor");
                            subsystemAdds[14] = new JMenuItem("Add Jaguar");
                            subsystemAdds[15] = new JMenuItem("Add Servo");
                            subsystemAdds[16] = new JMenuItem("Add Digital Output");
                            subsystemAdds[17] = new JMenuItem("Add Spike");
                            for(int i = 13; i < 17; i++)
                                actuatorMenu.add(subsystemAdds[i]);

                            subsystemAdds[18] = new JMenuItem("Add Compressor");
                            subsystemAdds[19] = new JMenuItem("Add Solenoid");
                            subsystemAdds[20] = new JMenuItem("Add Relay Solenoid");
                            subsystemAdds[21] = new JMenuItem("Add Double Solenoid");
                            subsystemAdds[22] = new JMenuItem("Add Relay Double Solenoid");
                            for(int i = 18; i < 22; i++)
                                pneumaticMenu.add(subsystemAdds[i]);
                        }
                        //</editor-fold>
                        
                        if(selectedType.equals("Subsystem") || selectedType.equals("PID Subsystem")){
                            mainMenu.add(controllerMenu);
                            mainMenu.add(actuatorMenu);
                            mainMenu.add(sensorMenu);
                            mainMenu.add(pneumaticMenu);
                        } else if(selectedType.equals("PID Controller")) {
                            mainMenu.add(actuatorMenu);
                            mainMenu.add(sensorMenu);
                        }
                        
                        for(int i = 0; i < subsystemAdds.length && subsystemAdds[i] != null; i++) {
//                            componentToAdd = new RobotComponent(subsystemAdds[i].getText().substring(4), selectedType, robot);
                            subsystemAdds[i].setAction(new AddItemAction(subsystemAdds[i].getText(), selected, componentToAdd));
                        }
                        
                        for(int i = 0; i < addActions.length && addActions[i] != null; i++) {
//                            componentToAdd = new RobotComponent(addActions[i].getText().substring(4), addActions[i].getText().substring(4), robot);
                            mainMenu.add(addActions[i]);
                            addActions[i].setAction(new AddItemAction(addActions[i].getText(), selected, componentToAdd));
                        }
                        
//                        mainMenu.add(wipe);
                        mainMenu.addSeparator();
                        mainMenu.add(delete);
                        mainMenu.show(tree, bounds.x, bounds.y + bounds.height);
                            
                    }
                }
            }
            
        });
        //</editor-fold>
        tree.setFocusable(true);
        this.setFocusable(true);
        tree.addKeyListener(new KeyAdapter(){
            
            boolean ctrlDown = false;
            @Override
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();
                System.out.println("Modifiers: \""+e.getModifiersEx()+"\"");
                ctrlDown = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0;
                System.out.println("Control down = "+ctrlDown);

                TreePath path = tree.getSelectionPath();
                RobotComponent selected = (RobotComponent) path.getLastPathComponent();
                System.out.println("Key char \""+keyChar+"\"");

                if(keyChar == KeyEvent.VK_BACK_SPACE || keyChar == KeyEvent.VK_DELETE) {

                    if(!selected.getBase().toString().equals("Subsystems") &&
                    !selected.getBase().toString().equals("OI") &&
                    !selected.getBase().toString().equals("Commands") &&
                    !selected.getBase().toString().equals("Robot")) {

                        selected.removeFromParent();
                        selected.walk(new RobotWalker() {
                            @Override
                            public void handleRobotComponent(RobotComponent self) {
                                MainFrame.getInstance().getCurrentRobotTree().removeName(self.getFullName());
                            }
                        });
                        update();
                        takeSnapshot();
                        System.out.println("\""+selected.getName()+"\" removed");
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), 
                                                      "Cannot delete \""+selected+"\"", 
                                                      "Cannot Delete", 
                                                      JOptionPane.ERROR_MESSAGE);
                    }
                } else if((e.getKeyCode() == KeyEvent.VK_C) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    System.out.println("CTRL+C");
                } else if((e.getKeyCode() == KeyEvent.VK_X) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    System.out.println("CTRL+X");
                } else if((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    System.out.println("CTRL+V");
                } else if((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    System.out.println("CTRL+Z");
                } else if((e.getKeyCode() == KeyEvent.VK_Y) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    System.out.println("CTRL+Y");
                }
            }
        });
    }

    /**
     * Creates an empty TreeRoot.
     * @return An empty TreeRoot
     */
    private RobotComponent makeTreeRoot() {
	RobotComponent root = new RobotComponent("Team190Robot", Palette.getInstance().getItem("Robot"), this);
        root.add(new RobotComponent("Subsystems", Palette.getInstance().getItem("Subsystems"), this));
        root.add(new RobotComponent("Operator Interface", Palette.getInstance().getItem("OI"), this));
        root.add(new RobotComponent("Commands", Palette.getInstance().getItem("Commands"), this));
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
     * Gets the default name of a given component in the specified subsystem.
     * @param component The type of component to generate a default name for.
     * @return The default name.
     */
    private String getDefaultComponentName(PaletteComponent componentType, String subsystem) {
	int i = 0;
	String name;
	while (true) {
	    i++;
	    name = componentType.toString() + " " + i;
	    if (!usedNames.contains(subsystem+name)) {
		usedNames.add(subsystem+name);
		return name;
	    }
	}
    }

    /**
     * Adds a name to the used names list.
     * @param name The name being used
     */
    public void addName(String name) {
	usedNames.add(name);
    }

    /**
     * Removes the given name from the used names list.
     * @param name The name being freed
     */
    public void removeName(String name) {
        System.out.println("Removing name: "+name);
	usedNames.remove(name);
        System.out.println("Used names: "+usedNames);
    }

    /**
     * Checks to see if the {@code RobotTree} already contains
     * the given name.
     * @param name The name being checked
     */
    public boolean hasName(String name) {
	return usedNames.contains(name);
    }

    @Override
    public void valueChanged(TreeSelectionEvent tse) {
	RobotComponent node = (RobotComponent) tree.getLastSelectedPathComponent();

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
        filePath = path;
	try {
	    System.out.println("Saving to: " + path);
	    FileWriter save = new FileWriter(path);
            save.write(this.encode());
	    System.out.println("Written");
	    save.close();
	} catch (IOException ex) {
	    Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
	}
	saved = true;
        MainFrame.getInstance().prefs.put("FileName", filePath);
    }
    
    public void save() {
	if (filePath == null) {
	    int result = fileChooser.showSaveDialog(MainFrame.getInstance().getFrame());
	    if (result == JFileChooser.CANCEL_OPTION) {
		return;
	    }
	    else if (result == JFileChooser.ERROR_OPTION) {
		return;
	    }
	    else if (result == JFileChooser.APPROVE_OPTION) {
                filePath = fileChooser.getSelectedFile().getName();
                if (!filePath.endsWith(".yaml"))
                        filePath += ".yaml";
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
//        System.out.println(yaml.dump(out));
        return yaml.dump("Version "+RobotBuilder.VERSION)+"\n---\n"+yaml.dump(out);
    }

    /**
     * Load the RobotTree from a yaml file.
     * @param path 
     */
    public void load(File path) {
	try {
	    System.out.println("Loading from: " + path);
	    FileReader source = new FileReader(path);
            load(source);
	} catch (IOException ex) {
	    Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
	}
        filePath = path.getAbsolutePath();
    }
    
    /**
     * Load the RobotTree from a yaml string.
     * @param path 
     */
    public void load(String text) {
        System.out.println("Loading from a String");
        load(new StringReader(text));
    }
    
    /**
     * Load the RobotTree from a yaml string.
     * @param path 
     */
    public void load(Reader in) {
        System.out.println("Loading");
        newFile(Palette.getInstance());
        
        Iterator docs = new Yaml().loadAll(in).iterator();
        
        String version = (String) docs.next();
        System.out.println("Version: '"+version+"' == '"+RobotBuilder.VERSION+"'");
        assert version.equals("Version "+RobotBuilder.VERSION); // TODO: handle more cleanly
        System.out.println("Version: '"+version+"' == '"+RobotBuilder.VERSION+"'");

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
                for (Property property : self.getProperties().values()) {
                    property.setComponent(self);
                    if (self.getBase().getProperty(property.getName()) != null) {
                        property.update(self.getBase().getProperty(property.getName()));
                    }
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
        System.out.println("Loaded");
        
        // Add names to used names list
        walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                addName(self.getFullName());
            }
        });
    }
    
    public void load() {
        int result = fileChooser.showOpenDialog(MainFrame.getInstance().getFrame());
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        else if (result == JFileChooser.ERROR_OPTION) {
            return;
        }
        else if (result == JFileChooser.APPROVE_OPTION) {
            filePath = fileChooser.getSelectedFile().getName();
        }
        load(new File(filePath));
    }

    public void walk(RobotWalker walker) {
	((RobotComponent) this.treeModel.getRoot()).walk(walker);
    }

    /**
     * Updates the UI display to adjust for changed names.
     */
    public void update() {
	treeModel.reload();

	for (int i = 0; i < tree.getRowCount(); i++) {
	    tree.expandRow(i);
	}
	saved = false;
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
    boolean OKToClose() {
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
                System.out.println("Save");
                save();
                return true;
            case JOptionPane.NO_OPTION:
                System.out.println("Discarded changes");
                return true;
            case JOptionPane.CANCEL_OPTION:
                System.out.println("Cancelled closure");
                return false;
            default: return false;
        }
    }

    public void newFile(Palette palette) {
        DefaultMutableTreeNode root = makeTreeRoot();
        treeModel.setRoot(root);
        tree.setSelectionPath(new TreePath(root));
        usedNames = new HashSet<String>();
        validators = palette.getValidators();
        saved = true;
        MainFrame.getInstance().prefs.put("FileName", "");
    }

    /**
     * @return The root RobotComponent of the RobotTree
     */
    public RobotComponent getRoot() {
        return (RobotComponent) treeModel.getRoot();
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

    public Vector<String> getNamesOfType(final String type) {
        final Vector<String> names = new Vector<String>();
        walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) {
                if (self.getBase().getType().equals(type)) {
                    names.add(self.getName());
                }
            }
        });
        
        return names;
    }
    
    public void takeSnapshot(){
        System.out.println("Snapshot number "+ ++snapshots +" taken.");
        history.addState(encode());
    }
    
    public void undo() {
        System.out.println("Undo button pressed");
        load(history.undo());
    }
    
    public void redo(){
        System.out.println("Redo button pressed");
        load(history.redo());
    }
    
    public void setSaved() {
        saved = true;
    }

    /**
     * A transfer handler for that wraps the default transfer handler of RobotTree.
     * 
     * @author Alex Henning
     */
    private class TreeTransferHandler extends TransferHandler {

	private TransferHandler delegate;

	public TreeTransferHandler(TransferHandler delegate) {
	    this.delegate = delegate;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
	    return delegate.canImport(comp, transferFlavors);
	}

	@Override
	public boolean canImport(TransferSupport support) {
	    if (!support.isDrop()) {
		return false;
	    }
	    support.setShowDropLocation(true);
	    JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
	    TreePath path = dl.getPath();
	    if (path == null) {
		return false;
	    }
	    RobotComponent target = ((RobotComponent) path.getLastPathComponent());
	    if (support.getTransferable().isDataFlavorSupported(DataFlavor.stringFlavor)) {
		String data;
		try {
		    data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
		    System.out.println("UFE");
		    return false;
		} catch (IOException e) {
		    System.out.println("IOE");
		    return false;
		}
		PaletteComponent base = Palette.getInstance().getItem(data);
		assert base != null; // TODO: Handle more gracefully
		return target.supports(base);
	    } else if (support.getTransferable().isDataFlavorSupported(ROBOT_COMPONENT_FLAVOR)) {
		RobotComponent data;
		try {
		    data = (RobotComponent) support.getTransferable().getTransferData(ROBOT_COMPONENT_FLAVOR);
		} catch (UnsupportedFlavorException e) {
		    System.out.println("UnsupportedFlavor");
		    return false;
		} catch (IOException e) {
		    e.printStackTrace();
		    System.out.println("IOException");
		    return false;
		}
		System.out.println(data);
                Set<String> invalid = new HashSet();
                invalid.add("Robot"); invalid.add("Subsystems");
                invalid.add("OI"); invalid.add("Commands");
                if (data == null) return false;
                if (invalid.contains(data.getBase().getType())) return false;
		return target.supports(data);
	    } else {
		System.out.println("Unsupported flavor. The flavor you have chosen is no sufficiently delicious.");
		return false;
	    }
	}

	@Override
	protected Transferable createTransferable(final JComponent c) {
	    return new Transferable() {

		DataFlavor[] flavors = {ROBOT_COMPONENT_FLAVOR};
                
                Object data = ((JTree) c).getSelectionPath().getLastPathComponent();
                
		@Override
		public DataFlavor[] getTransferDataFlavors() {
		    return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor df) {
		    for (DataFlavor flavor : flavors) {
			if (flavor.equals(df)) {
			    return true;
			}
		    }
		    return false;
		}

		@Override
		public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
//                    System.out.print("Transfer data: "+data);
                    return data;
		}
	    };
	}

	@Override
	public void exportAsDrag(JComponent comp, InputEvent event, int action) {
	    delegate.exportAsDrag(comp, event, action);
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
	    System.out.println("Export ended for action: " + action);
	    update();
	}

	@Override
	public int getSourceActions(JComponent c) {
	    //return COPY_OR_MOVE;
            System.out.println("Supports: "+delegate.getSourceActions(c));
	    return delegate.getSourceActions(c);
	}

	@Override
	public Icon getVisualRepresentation(Transferable t) {
	    return delegate.getVisualRepresentation(t);
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
	    return delegate.importData(comp, t);
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
	    if (!canImport(support)) {
		return false;
	    }
	    JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
	    TreePath path = dl.getPath();
	    int childIndex = dl.getChildIndex();
	    if (childIndex == -1) {
		childIndex = tree.getModel().getChildCount(path.getLastPathComponent());
	    }
	    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
	    System.out.println("parentNode=" + parentNode);
	    DefaultMutableTreeNode newNode;
	    if (support.getTransferable().isDataFlavorSupported(DataFlavor.stringFlavor)) {
		System.out.println("Importing from palette");
		String data;
		try {
		    data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
		    System.out.println("UFE");
		    return false;
		} catch (IOException e) {
		    System.out.println("IOE");
		    return false;
		}
		System.out.println("Data: " + data);
		PaletteComponent base = Palette.getInstance().getItem(data);
		assert base != null; // TODO: Handle more gracefully
                System.out.println("Creating Component...");
                newNode = new RobotComponent(getDefaultComponentName(base, ((RobotComponent) parentNode).getSubsystem()), base, robot);
                System.out.println("...Component Created");
	    } else if (support.getTransferable().isDataFlavorSupported(ROBOT_COMPONENT_FLAVOR)) {
		System.out.println("Moving a robot component");
		try {
		    newNode = (RobotComponent) support.getTransferable().getTransferData(ROBOT_COMPONENT_FLAVOR);
		} catch (UnsupportedFlavorException e) {
		    System.out.println("UnsupportedFlavor");
		    return false;
		} catch (IOException e) {
		    System.out.println("IOException");
		    return false;
		}
		System.out.println("Imported a robot component: " + newNode.toString());
	    } else {
		return false;
	    }
	    System.out.println("newNode=" + newNode);
	    saved = false;
	    treeModel.insertNodeInto(newNode, parentNode, childIndex);
            treeModel.reload(parentNode); // reloads the tree without reverting to the root
	    System.out.println("childIndex=" + childIndex);
	    tree.makeVisible(path.pathByAddingChild(newNode));
	    System.out.print("--");
	    tree.scrollRectToVisible(tree.getPathBounds(path.pathByAddingChild(newNode)));
	    System.out.println("--");
            update();
            takeSnapshot();
	    return true;
	    //return delegate.importData(support);
	}
    }
    
    /**
     * Used for adding a {@link RobotComponent} to the 
     * {@link RobotTree} via right-click menus.
     */
    private class AddItemAction extends AbstractAction {
        /** The currently selected node */
        RobotComponent selectedComponent;
        /** The node to add */
        RobotComponent childToAdd;
        String name;
        
        /**
         * Creates a new {@code AddItemAction} based on
         * <ul>
         * <li>The {@link JMenuItem} that has been right clicked.
         * <li>The name of said {@code RobotComponent}.
         * <li>The {@code JTree} that contains this {@code RobotComponent}.
         * <li>The {@code RobotComponent} that is to be added.
         * </ul>
         * @param name The name of the {@code AddItemAction}.
         * @param tree The {@code JTree} that contains the selected {@code RobotComponent}
         * @param selectedNode The {@code RobotComponent} that has been right clicked ("selected").
         * @param childToAdd The {@code RobotComponent} to add when this is clicked.
         */
        public AddItemAction(String name, 
                            RobotComponent selectedNode, 
                            RobotComponent childToAdd) {
            
            putValue(Action.NAME, name);
            putValue(Action.SHORT_DESCRIPTION, (new StringBuffer(name)).insert(4, "a ").toString());
            
            this.name = name;
            this.selectedComponent = selectedNode;
            this.childToAdd = childToAdd;
        }

        @Override
        /**
         * @inheritdoc
         */
        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem) e.getSource();     // The menu item that's been clicked
            String nameToAdd = source.getText().substring(4); // Removes the "Add " in the beginning

            System.out.println("\nCreating component...");
            /* 
            * Step one:   generate new name based off previous instances of this type of RobotComponent
            * Step two:   get the PaletteComponent of this type (e.g. "Gyro")
            */
            RobotComponent toAdd = new RobotComponent(
                    getDefaultComponentName(RobotComponent.getPaletteComponent(nameToAdd), selectedComponent.getSubsystem()), 
                    nameToAdd, 
                    robot);
            System.out.println("Component created\n");

            selectedComponent.addChild(toAdd);
            update();
            takeSnapshot();
            System.out.println("Item \""+toAdd.getFullName()+"\" added.");
        }
    }
    
    private class DeleteItemAction extends AbstractAction {
        private RobotComponent target;
        
        public DeleteItemAction(String name, RobotComponent target) {
            putValue(Action.NAME, name);
            putValue(Action.SHORT_DESCRIPTION, "Delete this component.");
            
            this.target = target;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Removing \""+target.getName()+"\"...");
            target.removeFromParent();
            target.walk(new RobotWalker() {
                @Override
                public void handleRobotComponent(RobotComponent self) {
                    MainFrame.getInstance().getCurrentRobotTree().removeName(self.getFullName());
                }
            });
            update();
            takeSnapshot();
            System.out.println("\""+target.getName()+"\" removed");
            
        }
    }
    
    private class ClearAction extends AbstractAction {
        
        private String name;
        private RobotComponent selected;
        
        public ClearAction(String name, RobotComponent selected) {
            putValue(Action.NAME, name);
            putValue(Action.SHORT_DESCRIPTION, "Clear the components within this.");
            this.name = name;
            this.selected = selected;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            selected.removeAllChildren();
            update();
            takeSnapshot();
        }
        
    }
}
