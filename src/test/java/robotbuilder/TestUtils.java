
package robotbuilder;

import robotbuilder.data.RobotWalker;
import robotbuilder.exporters.GenericExporter;
import robotbuilder.robottree.RobotTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

import robotbuilder.data.RobotComponent;

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
        tree.setSaved(); // Hack to keep from prompting for user input.
        tree.newFile();
        return tree;
    }

    /**
     * @return A known test tree that includes a large number of items from the
     * palette.
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
        robotDrive.getProperty("Left Motor Inverted").setValueAndUpdate(true);
        robotDrive.getProperty("Safety Enabled").setValueAndUpdate(false);
        robotDrive.getProperty("Sensitivity").setValueAndUpdate(0.25);
        driveTrain.add(robotDrive);
        RobotComponent leftVictor = new RobotComponent("Left Victor", "Speed Controller", tree);
        leftVictor.setProperty("Type", "Victor");
        robotDrive.add(leftVictor);
        RobotComponent rightVictor = new RobotComponent("Right Victor", "Speed Controller", tree);
        rightVictor.setProperty("Type", "Victor");
        robotDrive.add(rightVictor);
        robotDrive.getProperty("Right Motor").setValueAndUpdate(rightVictor);
        RobotComponent gyro = new RobotComponent("Gyro", "AnalogGyro", tree);
        driveTrain.add(gyro);
        gyro.getProperty("Sensitivity").setValueAndUpdate(2.33);

        // Create an arm subsystem
        RobotComponent arm = new RobotComponent("Arm", "Subsystem", tree);
        subsystems.add(arm);
        RobotComponent pid = new RobotComponent("PID Controller", "PID Controller", tree);
        arm.add(pid);
        pid.getProperty("P").setValueAndUpdate(2);
        pid.getProperty("I").setValueAndUpdate(1);
        pid.getProperty("D").setValueAndUpdate(-1);
//        pid.getProperty("Send to SmartDashboard").setValueAndUpdate(true);
        pid.getProperty("Limit Input").setValueAndUpdate(true);
        pid.getProperty("Continuous").setValueAndUpdate(true);
        RobotComponent motor = new RobotComponent("Motor", "Speed Controller", tree);
        motor.setProperty("Type", "Jaguar");
        pid.add(motor);
        RobotComponent encoder = new RobotComponent("Encoder", "Quadrature Encoder", tree);
        pid.add(encoder);
        encoder.getProperty("Distance Per Pulse").setValueAndUpdate(24);
        encoder.getProperty("PID Source").setValueAndUpdate("kDisplacement");
        RobotComponent limit = new RobotComponent("Limit", "Limit Switch", tree);
        arm.add(limit);

        // Create an wrist subsystem
        RobotComponent wrist = new RobotComponent("Wrist", "PID Subsystem", tree);
        subsystems.add(wrist);
        wrist.getProperty("P").setValueAndUpdate(2);
        wrist.getProperty("I").setValueAndUpdate(1);
        wrist.getProperty("D").setValueAndUpdate(-1);
        wrist.getProperty("Limit Input").setValueAndUpdate(true);
        wrist.getProperty("Continuous").setValueAndUpdate(true);
        RobotComponent wristMotor = new RobotComponent("Motor", "Speed Controller", tree);
        wristMotor.setProperty("Type", "Jaguar");
        wrist.add(wristMotor);
        RobotComponent pot = new RobotComponent("Pot", "Analog Potentiometer", tree);
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
        autoButton.getProperty("When to Run").setValueAndUpdate("whenPressed");

        // Create some commands
        RobotComponent tankDrive = new RobotComponent("Tank Drive", "Command", tree);
        commands.add(tankDrive);
        tankDrive.getProperty("Requires").setValueAndUpdate("Drive Train");
        RobotComponent armUp = new RobotComponent("Arm Up", "Command", tree);
        commands.add(armUp);
        RobotComponent auto = new RobotComponent("Autonomous", "Command Group", tree);
        commands.add(auto);

        // Deal with odd references
        driveTrain.getProperty("Default Command").setValueAndUpdate("Tank Drive");
        armUpButton.getProperty("Command").setValueAndUpdate("Arm Up");
        autoButton.getProperty("Command").setValueAndUpdate("Autonomous");

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

    public static int runBuild(String language) throws InterruptedException, IOException {
        System.out.println("====================================================");
        Process p;
        ProcessBuilder pb;

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
        if (isWindows) {
            System.out.println("Trying Windows compile...");
            pb = new ProcessBuilder("gradlew.bat", "build").directory(new File("test-resources/RobotBuilderTestProject" + language));
        } else {
            System.out.println("Trying *NIX compile...");
            pb = new ProcessBuilder("sh", "-c", "./gradlew", "build").directory(new File("test-resources/RobotBuilderTestProject"));
        }
        pb.redirectErrorStream(true);
        p = pb.start();
        //print the standard output from the build
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
        }

        System.out.println("====================================================");
        p.waitFor();
        System.out.println(p.exitValue());
        return p.exitValue();
    }
}
