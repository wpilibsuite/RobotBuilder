package robotbuilder;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import robotbuilder.data.PaletteComponent;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.RobotWalker;

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
    private boolean saved;
    /** Names used by components during name auto-generation */
    private Set<String> usedNames = new HashSet<String>();
    /** The currently selected node */
    private DefaultMutableTreeNode currentNode;
    private String currentFile = null;
    private JFileChooser fileChooser = new JFileChooser();

    public RobotTree(PropertiesDisplay properties) {
	fileChooser.setFileFilter(new FileNameExtensionFilter("JSON save file", "json"));
	saved = true;
	this.properties = properties;
	this.properties.setRobotTree(this);
	setLayout(new BorderLayout());
	DefaultMutableTreeNode root = new RobotComponent("Team190Robot", Palette.getInstance().getItem("Robot"), this);
	treeModel = new DefaultTreeModel(root);
	tree = new JTree(treeModel);
	tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	tree.addTreeSelectionListener(this);
	tree.setDropMode(DropMode.ON_OR_INSERT);
	add(new JScrollPane(tree), BorderLayout.CENTER);
	tree.setTransferHandler(new TreeTransferHandler(tree.getTransferHandler()));
	tree.setDragEnabled(true);

	for (int i = 0; i < tree.getRowCount(); i++) {
	    tree.expandRow(i);
	}
    }

    /**
     * @param component The type of component to generate a default name for.
     * @return The default name.
     */
    private String getDefaultComponentName(PaletteComponent componentType) {
	int i = 0;
	String name;
	while (true) {
	    i++;
	    name = componentType.toString().replaceAll(" ", "_") + "_" + i;
	    if (!usedNames.contains(name)) {
		usedNames.add(name);
		return name;
	    }
	}
    }

    /**
     * Add a name to the used names list.
     * @param name The name being used
     */
    public void addName(String name) {
	usedNames.add(name);
    }

    /**
     * Remove a name from the used nomes list.
     * @param name The name being freed
     */
    public void removeName(String name) {
	usedNames.remove(name);
    }

    @Override
    public void valueChanged(TreeSelectionEvent tse) {
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

	if (node == null) {
	    return;
	}
	if (node instanceof DefaultMutableTreeNode) {
	    properties.setCurrentComponent(node);
	    currentNode = node;
	}
    }

    /**
     * Save the RobotTree as a json.
     * @param filePath 
     */
    public void save(String filePath) {
	if (currentFile == null) {
	    int result = fileChooser.showSaveDialog(MainFrame.getInstance().getFrame());
	    if (result == JFileChooser.CANCEL_OPTION) {
		return;
	    }
	    else if (result == JFileChooser.ERROR_OPTION) {
		return;
	    }
	    else if (result == JFileChooser.APPROVE_OPTION) {
		System.out.println("Saving to: " + fileChooser.getSelectedFile().getName());
	    }
	}
	try {
	    System.out.println("Saving to: " + filePath);
	    FileWriter save = new FileWriter(filePath);
	    JSONObject robot = ((RobotComponent) treeModel.getRoot()).encodeAsJSON();
	    System.out.println("Encoded to: " + robot);
	    robot.write(save);

	    System.out.println("Written");
	    save.close();
	} catch (JSONException ex) {
	    Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
	}
	saved = true;
    }

    /**
     * Load the RobotTree from a json.
     * @param filePath 
     */
    public void load(String filePath) {
	try {
	    System.out.println("Loading from: " + filePath);
	    FileReader source = new FileReader(filePath);
	    JSONTokener tokener;
	    tokener = new JSONTokener(source);
	    JSONObject json;
	    json = new JSONObject(tokener);
	    treeModel.setRoot(RobotComponent.decodeFromJSON(json, this));
	    update();
	    System.out.println("Loaded");
	    source.close();
	} catch (JSONException ex) {
	    Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
	}
	saved = true;
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

    boolean OKToClose() {
	String[] options = {"Discard", "Cancel", "Save"};
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
		options[2]);
	if (value == 2) {
	    System.out.println("Save");
	    save("save.json");
	} else if (value == 1) {
	    System.out.println("Cancel");
	} else {
	    System.out.println("Discard");
	    treeModel.setRoot(null);
	    return true;
	}
	return false;
    }

    public void newFile() {
	if (OKToClose()) {
	    DefaultMutableTreeNode root = new RobotComponent("Team190Robot", Palette.getInstance().getItem("Folder"), this);
	    treeModel.setRoot(root);
	    tree.setSelectionPath(new TreePath(root));
	    saved = true;
	}
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
		    System.out.println("Transfer: " + ((JTree) c).getSelectionPath().getLastPathComponent());
		    return ((JTree) c).getSelectionPath().getLastPathComponent();
		    //return currentNode;
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
//            try {
//                RobotComponent comp = ((RobotComponent) data.getTransferData(ROBOT_COMPONENT_FLAVOR));
//                System.out.println(comp.getPath());
//            } catch (UnsupportedFlavorException ex) {
//                Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                Logger.getLogger(RobotTree.class.getName()).log(Level.SEVERE, null, ex);
//            }
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
		newNode = new RobotComponent(getDefaultComponentName(base), base, robot);
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
	    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
	    System.out.println("parentNode=" + parentNode);
	    treeModel.insertNodeInto(newNode, parentNode, childIndex);
	    System.out.println("childIndex=" + childIndex);
	    tree.makeVisible(path.pathByAddingChild(newNode));
	    System.out.print("--");
	    tree.scrollRectToVisible(tree.getPathBounds(path.pathByAddingChild(newNode)));
	    System.out.println("--");
	    return true;
	    //return delegate.importData(support);
	}
    }
}
