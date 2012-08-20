/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.robottree;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import robotbuilder.MainFrame;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author alex
 */
class MouseAdapterImpl extends MouseAdapter {

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            // Right click only
            JTree tree = MainFrame.getInstance().getCurrentRobotTree().tree;
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            tree.setSelectionPath(path);
            Rectangle bounds = tree.getUI().getPathBounds(tree, path);
            if (bounds != null && bounds.contains(e.getX(), e.getY())) {
                final JPopupMenu mainMenu = new JPopupMenu(); // main menu; encapsulates everything
                // main menu; encapsulates everything
                final JMenu controllerMenu = new JMenu("Add Controllers");
                final JMenu sensorMenu = new JMenu("Add Sensors");
                final JMenu actuatorMenu = new JMenu("Add Actuators");
                final JMenu pneumaticMenu = new JMenu("Add Pneumatics");
                final RobotComponent selected = (RobotComponent) path.getLastPathComponent(); // The component that's been clicked
                // The component that's been clicked
                RobotComponent componentToAdd = null;
                final String selectedType = selected.getBase().getName(); // Subsystem -> Subsystem
                // Subsystem -> Subsystem
                String type = selected.getBase().getType(); // Victor -> Actuator, Gyro -> PIDSource, etc.
                // Victor -> Actuator, Gyro -> PIDSource, etc.
                if (type.equals("PIDSource")) {
                    type = "Sensor"; // PIDSource -> Sensor
                    // PIDSource -> Sensor
                }
                final int numSupports = 25;
                final JMenuItem delete = new JMenuItem("Delete");
                boolean deleteable = true;
                delete.setAction(new DeleteItemAction("Delete", selected));
                JMenuItem[] addActions = new JMenuItem[3];
                JMenuItem[] subsystemAdds = new JMenuItem[numSupports];
                if (selectedType.equals("Robot")) {
                    // Can't do anything with the root.
                    return;
                }
                // Main folders: Subsystems, OI, and Commands
                if (selectedType.equals("Subsystems")) {
                    deleteable = false;
                    addActions[0] = new JMenuItem("Add Subsystem");
                    addActions[1] = new JMenuItem("Add PID Subsystem");
                } else if (selectedType.equals("OI")) {
                    deleteable = false;
                    addActions[0] = new JMenuItem("Add Joystick");
                    addActions[1] = new JMenuItem("Add Joystick Button");
                } else if (selectedType.equals("Commands")) {
                    deleteable = false;
                    addActions[0] = new JMenuItem("Add Command");
                    addActions[1] = new JMenuItem("Add Command Group");
                    addActions[2] = new JMenuItem("Add PID Command");
                } else if (selectedType.equals("Robot Drive 4") || selectedType.equals("Robot Drive 2")) {
                    if (selected.supports(RobotComponent.getPaletteComponent("Victor"))) {
                        addActions[0] = new JMenuItem("Add Victor");
                        addActions[1] = new JMenuItem("Add Jaguar");
                    }
                }
                // Subsystem Menus and Choices
                if (selectedType.equals("Subsystem") || selectedType.equals("PID Subsystem") || selectedType.equals("PID Controller")) {
                    subsystemAdds[0] = new JMenuItem("Add Robot Drive 4");
                    subsystemAdds[1] = new JMenuItem("Add Robot Drive 2");
                    subsystemAdds[2] = new JMenuItem("Add PID Controller");
                    for (int i = 0; i < 3; i++) {
                        controllerMenu.add(subsystemAdds[i]);
                    }
                    subsystemAdds[3] = new JMenuItem("Add Gyro");
                    subsystemAdds[4] = new JMenuItem("Add Accelerometer");
                    subsystemAdds[5] = new JMenuItem("Add Quadrature Encoder");
                    subsystemAdds[6] = new JMenuItem("Add Indexed Encoder");
                    subsystemAdds[7] = new JMenuItem("Add Gear Tooth Sensor");
                    subsystemAdds[8] = new JMenuItem("Add Potentiometer");
                    subsystemAdds[9] = new JMenuItem("Add Analog Input");
                    subsystemAdds[10] = new JMenuItem("Add Limit Switch");
                    subsystemAdds[11] = new JMenuItem("Add Digital Input");
                    subsystemAdds[12] = new JMenuItem("Add Ultrasonic");
                    for (int i = 3; i < 12; i++) {
                        sensorMenu.add(subsystemAdds[i]);
                    }
                    subsystemAdds[13] = new JMenuItem("Add Victor");
                    subsystemAdds[14] = new JMenuItem("Add Jaguar");
                    subsystemAdds[15] = new JMenuItem("Add Servo");
                    subsystemAdds[16] = new JMenuItem("Add Digital Output");
                    subsystemAdds[17] = new JMenuItem("Add Spike");
                    for (int i = 13; i < 17; i++) {
                        actuatorMenu.add(subsystemAdds[i]);
                    }
                    subsystemAdds[18] = new JMenuItem("Add Compressor");
                    subsystemAdds[19] = new JMenuItem("Add Solenoid");
                    subsystemAdds[20] = new JMenuItem("Add Relay Solenoid");
                    subsystemAdds[21] = new JMenuItem("Add Double Solenoid");
                    subsystemAdds[22] = new JMenuItem("Add Relay Double Solenoid");
                    for (int i = 18; i < 22; i++) {
                        pneumaticMenu.add(subsystemAdds[i]);
                    }
                }
                if (selectedType.equals("Subsystem") || selectedType.equals("PID Subsystem")) {
                    mainMenu.add(controllerMenu);
                    mainMenu.add(actuatorMenu);
                    mainMenu.add(sensorMenu);
                    mainMenu.add(pneumaticMenu);
                } else if (selectedType.equals("PID Controller")) {
                    sensorMenu.remove(sensorMenu.getItem(7)); // Removes limit switch from the menu
                    // Removes limit switch from the menu
                    if (selected.supports(RobotComponent.getPaletteComponent("Victor"))) {
                        // If no actuator, show the actuator menu
                        mainMenu.add(actuatorMenu);
                    }
                    if (selected.supports(RobotComponent.getPaletteComponent("Gyro"))) {
                        // If no sensor, show the sensor menu
                        mainMenu.add(sensorMenu);
                    }
                }
                // Adds the
                for (int i = 0; i < subsystemAdds.length && subsystemAdds[i] != null; i++) {
                    //                            componentToAdd = new RobotComponent(subsystemAdds[i].getText().substring(4), selectedType, robot);
                    subsystemAdds[i].setAction(new AddItemAction(subsystemAdds[i].getText(), selected, componentToAdd));
                }
                for (int i = 0; i < addActions.length && addActions[i] != null; i++) {
                    //                            componentToAdd = new RobotComponent(addActions[i].getText().substring(4), addActions[i].getText().substring(4), robot);
                    mainMenu.add(addActions[i]);
                    addActions[i].setAction(new AddItemAction(addActions[i].getText(), selected, componentToAdd));
                }
                if (mainMenu.getSubElements().length > 0 && deleteable) {
                    mainMenu.addSeparator(); // Adds a separator above the "Delete" button
                    // Adds a separator above the "Delete" button
                }
                if (deleteable) {
                    mainMenu.add(delete);
                }
                mainMenu.show(tree, bounds.x, bounds.y + bounds.height);
            }
        }
    }
    
}
