/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.robottree;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import robotbuilder.MainFrame;
import robotbuilder.data.RobotComponent;

/**
 * Used for adding a {@link RobotComponent} to the
 * {@link RobotTree} via right-click menus.
 */
class AddItemAction extends AbstractAction {
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
    public AddItemAction(String name, RobotComponent selectedNode, RobotComponent childToAdd) {
        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, (new StringBuffer(name)).insert(4, "a ").toString());
        this.name = name;
        this.selectedComponent = selectedNode;
        this.childToAdd = childToAdd;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        RobotTree tree = MainFrame.getInstance().getCurrentRobotTree();
        JMenuItem source = (JMenuItem) e.getSource(); // The menu item that's been clicked
        // The menu item that's been clicked
        String nameToAdd = source.getText().substring(4); // Removes the "Add " in the beginning
        // Removes the "Add " in the beginning
        /*
         * Step one:   generate new name based off previous instances of this type of RobotComponent
         * Step two:   get the PaletteComponent of this type (e.g. "Gyro")
         */
        RobotComponent toAdd = new RobotComponent(tree.getDefaultComponentName(RobotComponent.getPaletteComponent(nameToAdd), selectedComponent.getSubsystem()), nameToAdd, tree);
        selectedComponent.addChild(toAdd);
        tree.takeSnapshot();
        tree.update();
    }
    
}
