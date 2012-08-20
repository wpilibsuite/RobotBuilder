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
    boolean saved;
    /** Names used by components during name auto-generation */
    private Set<String> usedNames = new HashSet<String>();
    private Map<String, Validator> validators;
    /** The currently selected node */
    private String filePath = null;
    
    private SimpleHistory<String> history = new SimpleHistory<String>();

    private JFileChooser fileChooser = new JFileChooser();

    RobotTree(PropertiesDisplay properties, Palette palette) {
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
        tree.addMouseListener(new MouseAdapterImpl());
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
     * Gets the default name of a given component in the specified subsystem.
     * @param component The type of component to generate a default name for.
     * @return The default name.
     */
    private String getDefaultComponentName(PaletteComponent componentType, String subsystem) {
	int i = 1;
	String name;
	while (true) {
	    name = componentType.toString() + (i == 1 ? "" : " " + i);
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

	if (node == null) {
	    return;
	}
	if (node instanceof RobotComponent) {
            properties.propTable.removeEditor();
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
	    FileWriter save = new FileWriter(path);
            save.write(this.encode());
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
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
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
        load(new StringReader(text));
    }
    
    /**
     * Load the RobotTree from a yaml string.
     * @param path 
     */
    public void load(Reader in) {
        resetTree(Palette.getInstance());
        
        Iterator docs = new Yaml().loadAll(in).iterator();
        
        String version = (String) docs.next();
        // FIXME: Asserts don't normally work...
        assert version.equals("Version "+RobotBuilder.VERSION); // TODO: handle more cleanly

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
    }
    
    public void load() {
        if (OKToClose()) {
            int result = fileChooser.showOpenDialog(MainFrame.getInstance().getFrame());
            if (result == JFileChooser.CANCEL_OPTION) {
                return;
            } else if (result == JFileChooser.ERROR_OPTION) {
                return;
            } else if (result == JFileChooser.APPROVE_OPTION) {
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
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
                save();
                return true;
            case JOptionPane.NO_OPTION:
                return true;
            case JOptionPane.CANCEL_OPTION:
                return false;
            default: return false;
        }
    }
    
    public void newFile(Palette palette) {
        if (OKToClose()) {
            resetTree(palette);
            saved = true;
            filePath = null;
            MainFrame.getInstance().prefs.put("FileName", "");
        }
    }

    private void resetTree(Palette palette) {
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

    void delete(final RobotComponent component) {
        component.walk(new RobotWalker() {
            @Override public void handleRobotComponent(RobotComponent self) {
                self.handleDelete();
                if (self != component) { delete(self); }
            }
        });
        removeName(component.getFullName());
        component.removeFromParent();
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
	    update();
	}

	@Override
	public int getSourceActions(JComponent c) {
	    //return COPY_OR_MOVE;
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
	    DefaultMutableTreeNode newNode;
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
                newNode = new RobotComponent(getDefaultComponentName(base, ((RobotComponent) parentNode).getSubsystem()), base, robot);
	    } else if (support.getTransferable().isDataFlavorSupported(ROBOT_COMPONENT_FLAVOR)) {
		try {
		    newNode = (RobotComponent) support.getTransferable().getTransferData(ROBOT_COMPONENT_FLAVOR);
		} catch (UnsupportedFlavorException e) {
		    return false;
		} catch (IOException e) {
		    return false;
		}
	    } else {
		return false;
	    }
	    treeModel.insertNodeInto(newNode, parentNode, childIndex);
            treeModel.reload(parentNode); // reloads the tree without reverting to the root
	    tree.makeVisible(path.pathByAddingChild(newNode));
	    tree.scrollRectToVisible(tree.getPathBounds(path.pathByAddingChild(newNode)));
            update();
            takeSnapshot();
	    return true;
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
         * <li>The {@code RobotComponent} that is to be added.
         * </ul>
         * @param name The name of the {@code AddItemAction}.
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

            /* 
            * Step one:   generate new name based off previous instances of this type of RobotComponent
            * Step two:   get the PaletteComponent of this type (e.g. "Gyro")
            */
            RobotComponent toAdd = new RobotComponent(
                    getDefaultComponentName(RobotComponent.getPaletteComponent(nameToAdd), selectedComponent.getSubsystem()), 
                    nameToAdd, 
                    robot);

            selectedComponent.addChild(toAdd);
            takeSnapshot();
            update();
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
            delete(target);
            update();
            takeSnapshot();
        }
    }

    private class MouseAdapterImpl extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if(SwingUtilities.isRightMouseButton(e)) { // Right click only
                
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                tree.setSelectionPath(path);
                Rectangle bounds = tree.getUI().getPathBounds(tree, path);
                
                if (bounds != null && bounds.contains(e.getX(), e.getY())) {
                    
                    final JPopupMenu mainMenu   = new JPopupMenu(); // main menu; encapsulates everything
                    final JMenu controllerMenu  = new JMenu("Add Controllers");
                    final JMenu sensorMenu      = new JMenu("Add Sensors");
                    final JMenu actuatorMenu    = new JMenu("Add Actuators");
                    final JMenu pneumaticMenu   = new JMenu("Add Pneumatics");
                    
                    final RobotComponent selected = (RobotComponent) path.getLastPathComponent(); // The component that's been clicked
                    RobotComponent componentToAdd = null;
                    
                    final String selectedType  = selected.getBase().getName(); // Subsystem -> Subsystem
                    String type                = selected.getBase().getType(); // Victor -> Actuator, Gyro -> PIDSource, etc.
                    
                    if(type.equals("PIDSource")) type = "Sensor"; // PIDSource -> Sensor
                    
                    final int numSupports = 25;
                    
                    
                    final JMenuItem delete = new JMenuItem("Delete");
                    boolean deleteable = true;
                    delete.setAction(new DeleteItemAction("Delete", selected));
                    
                    JMenuItem[] addActions    = new JMenuItem[3];
                    JMenuItem[] subsystemAdds = new JMenuItem[numSupports];
                    
                    if(selectedType.equals("Robot")){ // Can't do anything with the root.
                        return;
                    }
                    // Main folders: Subsystems, OI, and Commands
                    if(selectedType.equals("Subsystems")){
                        deleteable = false;
                        addActions[0] = new JMenuItem("Add Subsystem");
                        addActions[1] = new JMenuItem("Add PID Subsystem");
                        
                    } else if(selectedType.equals("OI")){
                        deleteable = false;
                        addActions[0] = new JMenuItem("Add Joystick");
                        addActions[1] = new JMenuItem("Add Joystick Button");
                        
                    } else if(selectedType.equals("Commands")){
                        deleteable = false;
                        addActions[0] = new JMenuItem("Add Command");
                        addActions[1] = new JMenuItem("Add Command Group");
                        addActions[2] = new JMenuItem("Add PID Command");
                    }
                    // Robot Drives
                    else if(selectedType.equals("Robot Drive 4")|| selectedType.equals("Robot Drive 2")) {
                        if(selected.supports(RobotComponent.getPaletteComponent("Victor"))) {
                            addActions[0] = new JMenuItem("Add Victor");
                            addActions[1] = new JMenuItem("Add Jaguar");
                        }
                    }
                    // Subsystem Menus and Choices
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
                    
                    if(selectedType.equals("Subsystem") || selectedType.equals("PID Subsystem")){
                        mainMenu.add(controllerMenu);
                        mainMenu.add(actuatorMenu);
                        mainMenu.add(sensorMenu);
                        mainMenu.add(pneumaticMenu);
                    } else if(selectedType.equals("PID Controller")) {
                        sensorMenu.remove(sensorMenu.getItem(7)); // Removes limit switch from the menu
                        
                        if(selected.supports(RobotComponent.getPaletteComponent("Victor"))){ // If no actuator, show the actuator menu
                            mainMenu.add(actuatorMenu);
                        }
                        if(selected.supports(RobotComponent.getPaletteComponent("Gyro"))){ // If no sensor, show the sensor menu
                            mainMenu.add(sensorMenu);
                        }
                    }
                    // Adds the 
                    for(int i = 0; i < subsystemAdds.length && subsystemAdds[i] != null; i++) {
//                            componentToAdd = new RobotComponent(subsystemAdds[i].getText().substring(4), selectedType, robot);
                        subsystemAdds[i].setAction(new AddItemAction(subsystemAdds[i].getText(), selected, componentToAdd));
                    }
                    
                    for(int i = 0; i < addActions.length && addActions[i] != null; i++) {
//                            componentToAdd = new RobotComponent(addActions[i].getText().substring(4), addActions[i].getText().substring(4), robot);
                        mainMenu.add(addActions[i]);
                        addActions[i].setAction(new AddItemAction(addActions[i].getText(), selected, componentToAdd));
                    }
                    
                    if(mainMenu.getSubElements().length > 0 && deleteable) mainMenu.addSeparator(); // Adds a separator above the "Delete" button
                    if(deleteable) mainMenu.add(delete);
                    mainMenu.show(tree, bounds.x, bounds.y + bounds.height);
                        
                }
            }
        }
    }
}
