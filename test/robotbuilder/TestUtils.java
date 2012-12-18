package robotbuilder;


import robotbuilder.palette.Palette;
import robotbuilder.robottree.RobotTree;
import java.io.File;
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
     * @return A new robot tree.
     */
    public static RobotTree getNewRobotTree() {
        RobotTree tree = MainFrame.getInstance().getCurrentRobotTree();
        tree.saved = true; // Hack to keep from prompting for user input.
        tree.newFile();
        return tree;
    }
    
    /**
     * @return A known test tree that includes a large number of items from the
     *         palette.
     */
    public static RobotTree generateTestTree() {
        RobotTree tree = getNewRobotTree();
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);
        RobotComponent oi = (RobotComponent) robot.getChildren().elementAt(0);
        RobotComponent commands = (RobotComponent) robot.getChildren().elementAt(0);
        
        // Create a drive train subsystem
        RobotComponent driveTrain = new RobotComponent("Drive Train", "Subsystem", tree);
        subsystems.add(driveTrain);
        RobotComponent robotDrive = new RobotComponent("Robot Drive", "Robot Drive 2", tree);
        robotDrive.getProperty("Left Motor Inverted").setValue(true);
        robotDrive.getProperty("Safety Enabled").setValue(false);
        robotDrive.getProperty("Sensitivity").setValue(0.25);
        driveTrain.add(robotDrive);
        RobotComponent leftVictor = new RobotComponent("Left Victor", "Speed Controller", tree);
        leftVictor.setProperty("Type", "Victor");
        robotDrive.add(leftVictor);
        RobotComponent rightVictor = new RobotComponent("Right Victor", "Speed Controller", tree);
        rightVictor.setProperty("Type", "Victor");
        robotDrive.add(rightVictor);
        RobotComponent gyro = new RobotComponent("Gyro", "Gyro", tree);
        driveTrain.add(gyro);
        gyro.getProperty("Sensitivity").setValue(2.33);
        
        // Create an arm subsystem
        RobotComponent arm = new RobotComponent("Arm", "Subsystem", tree);
        subsystems.add(arm);
        RobotComponent pid = new RobotComponent("PID Controller", "PID Controller", tree);
        arm.add(pid);
        pid.getProperty("P").setValue(2);
        pid.getProperty("I").setValue(1);
        pid.getProperty("D").setValue(-1);
//        pid.getProperty("Send to SmartDashboard").setValue(true);
        pid.getProperty("Limit Input").setValue(true);
        pid.getProperty("Continuous").setValue(true);
        RobotComponent motor = new RobotComponent("Motor", "Speed Controller", tree);
        motor.setProperty("Type", "Jaguar");
        pid.add(motor);
        RobotComponent encoder = new RobotComponent("Encoder", "Quadrature Encoder", tree);
        pid.add(encoder);
        encoder.getProperty("Distance Per Pulse").setValue(24);
        encoder.getProperty("PID Source").setValue("kDistance");
        RobotComponent limit = new RobotComponent("Limit", "Limit Switch", tree);
        arm.add(limit);
        
        // Create an wrist subsystem
        RobotComponent wrist = new RobotComponent("Wrist", "PID Subsystem", tree);
        subsystems.add(wrist);
        wrist.getProperty("P").setValue(2);
        wrist.getProperty("I").setValue(1);
        wrist.getProperty("D").setValue(-1);
        wrist.getProperty("Limit Input").setValue(true);
        wrist.getProperty("Continuous").setValue(true);
        RobotComponent wristMotor = new RobotComponent("Motor", "Speed Controller", tree);
        wristMotor.setProperty("Type", "Jaguar");
        wrist.add(wristMotor);
        RobotComponent pot = new RobotComponent("Pot", "Potentiometer", tree);
        wrist.add(pot);
        
        // Create a simple OI
        RobotComponent leftstick = new RobotComponent("Left Joystick", "Joystick", tree);
        oi.add(leftstick);
        RobotComponent rightstick = new RobotComponent("Right Joystick", "Joystick", tree);
        oi.add(rightstick);
        RobotComponent armUpButton = new RobotComponent("Arm Up Button", "Joystick Button", tree);
        leftstick.add(armUpButton);
        RobotComponent autoButton = new RobotComponent("Autonomous Button", "Joystick Button", tree);
        rightstick.add(autoButton);
        autoButton.getProperty("When to Run").setValue("whenPressed");
        
        // Create some commands
        RobotComponent tankDrive = new RobotComponent("Tank Drive", "Command", tree);
        commands.add(tankDrive);
        tankDrive.getProperty("Requires").setValue("Drive Train");
        RobotComponent armUp = new RobotComponent("Arm Up", "Command", tree);
        commands.add(armUp);
        RobotComponent auto = new RobotComponent("Autonomous", "Command Group", tree);
        commands.add(auto);

        // Deal with odd references
        driveTrain.getProperty("Default Command").setValue("Tank Drive");
        armUpButton.getProperty("Command").setValue("Arm Up");
        autoButton.getProperty("Command").setValue("Autonomous");
        
        return tree;
    }

    public static void delete(File dir) {
        if (dir.listFiles() != null) {
            for (File f : dir.listFiles()) {
                delete(f);
            }
        }
        dir.delete();
    }
}
