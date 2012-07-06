package robotbuilder;


import robotbuilder.data.RobotComponent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alex Henning
 */
public class TestUtils {
    
    /**
     * @return A known test tree that includes a large number of items from the
     *         palette.
     */
    public static RobotTree generateTestTree() {
        RobotTree tree = MainFrame.getInstance().getCurrentRobotTree();
        tree.newFile(Palette.getInstance());
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);
        RobotComponent oi = (RobotComponent) robot.getChildren().elementAt(0);
        RobotComponent commands = (RobotComponent) robot.getChildren().elementAt(0);
        
        // Create a drive train subsystem
        RobotComponent driveTrain = new RobotComponent("Drive Train", "Subsystem", tree);
        subsystems.add(driveTrain);
        // TODO: Add default command
        RobotComponent robotDrive = new RobotComponent("Robot Drive", "Robot Drive 2", tree);
        driveTrain.add(robotDrive);
        RobotComponent leftVictor = new RobotComponent("Left Victor", "Victor", tree);
        robotDrive.add(leftVictor);
        RobotComponent rightVictor = new RobotComponent("Right Victor", "Victor", tree);
        robotDrive.add(rightVictor);
        RobotComponent gyro = new RobotComponent("Gyro", "Gyro", tree);
        gyro.getProperty("Sensitivity").setValue(2.33);
        driveTrain.add(gyro);
        
        // Create an arm subsystem
        RobotComponent arm = new RobotComponent("Arm", "Subsystem", tree);
        subsystems.add(arm);
        RobotComponent pid = new RobotComponent("PID Controller", "PID Controller", tree);
        arm.add(pid);
        RobotComponent motor = new RobotComponent("Motor", "Jaguar", tree);
        pid.add(motor);
        RobotComponent encoder = new RobotComponent("Encoder", "Quadrature Encoder", tree);
        pid.add(encoder);
        RobotComponent limit = new RobotComponent("Limit", "Limit Switch", tree);
        arm.add(limit);
        // TODO: change properties
        
        // TODO: Add OI
        
        // TODO: Add Commands

        return tree;
    }
}
