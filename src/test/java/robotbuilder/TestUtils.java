
package robotbuilder;

import robotbuilder.data.RobotWalker;
import robotbuilder.exporters.GenericExporter;
import robotbuilder.robottree.RobotTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        RobotComponent oi = (RobotComponent) robot.getChildren().elementAt(1);
        RobotComponent commands = (RobotComponent) robot.getChildren().elementAt(2);

        // Create a drive train subsystem
        RobotComponent driveTrain = new RobotComponent("Drive Train", "Subsystem", tree);
        subsystems.add(driveTrain);
        RobotComponent robotDrive = new RobotComponent("Robot Drive", "Differential Drive", tree);
        robotDrive.getProperty("Safety Enabled").setValueAndUpdate(false);
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
        pid.getProperty("Send to SmartDashboard").setValueAndUpdate(true);
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

        // Create a wrist subsystem
        RobotComponent wrist = new RobotComponent("Wrist", "PID Subsystem", tree);
        subsystems.add(wrist);
        wrist.getProperty("P").setValueAndUpdate(2);
        wrist.getProperty("I").setValueAndUpdate(1);
        wrist.getProperty("D").setValueAndUpdate(-1);
        wrist.getProperty("Continuous").setValueAndUpdate(true);
        RobotComponent wristMotor = new RobotComponent("Motor", "Speed Controller", tree);
        wristMotor.setProperty("Type", "Jaguar");
        wrist.add(wristMotor);
        RobotComponent pot = new RobotComponent("Pot", "Analog Potentiometer", tree);
        wrist.add(pot);

        // Create a misc subsystem
        RobotComponent misc = new RobotComponent("Misc", "Subsystem", tree);
        subsystems.add(misc);
        //Speed Controller Group causes save and load file test false failures
        //RobotComponent scg1 = new RobotComponent("SCG1", "Speed Controller", tree);
        //scg1.setProperty("Type", "PWMVictorSPX");
        //RobotComponent scg2 = new RobotComponent("SCG2", "Speed Controller", tree);
        //scg2.setProperty("Type", "Spark");
        //RobotComponent scg3 = new RobotComponent("SCG3", "Speed Controller", tree);
        //scg3.setProperty("Type", "SD540");
        //RobotComponent scg4 = new RobotComponent("SCG4", "Speed Controller", tree);
        //scg4.setProperty("Type", "PWMVenom");
        //RobotComponent scg = new RobotComponent("SCG", "Speed Controller Group", tree);
        //scg.add(scg1);
        //scg.add(scg2);
        //scg.add(scg3);
        //scg.add(scg4);
        //scg.getProperty("SpeedController3").setValueAndUpdate(scg3);
        //scg.getProperty("SpeedController4").setValueAndUpdate(scg4);
        //misc.add(scg);
        RobotComponent dd1 = new RobotComponent("DD1", "Speed Controller", tree);
        dd1.setProperty("Type", "VictorSP");
        RobotComponent dd2 = new RobotComponent("DD2", "Speed Controller", tree);
        dd2.setProperty("Type", "VictorSP");
        RobotComponent diffDrive = new RobotComponent("Diff Drive", "Differential Drive", tree);
        diffDrive.add(dd1);
        diffDrive.add(dd2);
        diffDrive.getProperty("Left Motor").setValueAndUpdate(dd1);
        diffDrive.getProperty("Right Motor").setValueAndUpdate(dd2);
        misc.add(diffDrive);
        RobotComponent indexEncoder = new RobotComponent("Indexed Encoder", "Indexed Encoder", tree);
        misc.add(indexEncoder);
        RobotComponent analogAccel = new RobotComponent("Analog Accelerometer", "AnalogAccelerometer", tree);
        misc.add(analogAccel);
        RobotComponent dI = new RobotComponent("DI", "Digital Input", tree);
        misc.add(dI);
        RobotComponent ultrasonic = new RobotComponent("Ultrasonic", "Ultrasonic", tree);
        misc.add(ultrasonic);
        RobotComponent PDP = new RobotComponent("Power Distribution Panel", "PowerDistribution", tree);
        misc.add(PDP);
        RobotComponent nidec = new RobotComponent("Nidec", "Nidec Brushless", tree);
        misc.add(nidec);
        RobotComponent servo = new RobotComponent("Servo", "Servo", tree);
        misc.add(servo);
        RobotComponent dO = new RobotComponent("DO", "Digital Output", tree);
        misc.add(dO);
        RobotComponent relay = new RobotComponent("Relay", "Spike", tree);
        misc.add(relay);
        RobotComponent aO = new RobotComponent("Analog Output", "Analog Output", tree);
        misc.add(aO);
        RobotComponent compressor = new RobotComponent("Compressor", "Compressor", tree);
        misc.add(compressor);
        RobotComponent solenoid = new RobotComponent("Solenoid", "Solenoid", tree);
        misc.add(solenoid);
        RobotComponent relaySolenoid = new RobotComponent("Relay Solenoid", "Relay Solenoid", tree);
        misc.add(relaySolenoid);
        RobotComponent doubleSolenoid = new RobotComponent("Double Solenoid", "Double Solenoid", tree);
        misc.add(doubleSolenoid);

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
        RobotComponent xbox = new RobotComponent("Xbox", "Xbox Controller", tree);
        oi.add(xbox);
        RobotComponent xboxButton = new RobotComponent("Arm Up Xbox Button", "Xbox Button", tree);
        xboxButton.getProperty("When to Run").setValueAndUpdate("whenPressed");
        xboxButton.getProperty("Button").setValueAndUpdate("A");
        xbox.add(xboxButton);



        // Create some commands
        RobotComponent tankDrive = new RobotComponent("Tank Drive", "Command", tree);
        commands.add(tankDrive);
        tankDrive.getProperty("Requires").setValueAndUpdate("Drive Train");
        RobotComponent armUp = new RobotComponent("Arm Up", "Command", tree);
        commands.add(armUp);
        RobotComponent armDown = new RobotComponent("Arm Down", "Command", tree);
        commands.add(armDown);
        RobotComponent auto = new RobotComponent("Autonomous", "Sequential Command Group", tree);
        commands.add(auto);

        //need to set timeout parameter for Wait Command
        //RobotComponent wait = new RobotComponent("Wait", "Wait Command", tree);
        //commands.add(wait);

        //Conditional Command is broken on C++
        //RobotComponent conditionalCommand = new RobotComponent("CC", "Conditional Command", tree);
        //conditionalCommand.getProperty("On True Command").setValueAndUpdate(armUp);
        //conditionalCommand.getProperty("On False Command").setValueAndUpdate(armDown);
        //commands.add(conditionalCommand);

        // Create setpoint command
        RobotComponent setpointCommand = new RobotComponent("Wrist Setpoint", "Setpoint Command", tree);
        commands.add(setpointCommand);
        setpointCommand.getProperty("Requires").setValueAndUpdate("Wrist");
        setpointCommand.getProperty("Button on SmartDashboard").setValueAndUpdate(false);

        // Create instant command
        RobotComponent instantCommand = new RobotComponent("Instant Command 1", "Instant Command", tree);
        commands.add(instantCommand);

        // Create instant command with requires
        RobotComponent instantCommand2 = new RobotComponent("Instant Command 2", "Instant Command", tree);
        commands.add(instantCommand2);
        instantCommand2.getProperty("Requires").setValueAndUpdate("Wrist");

        // Deal with odd references
        driveTrain.getProperty("Default Command").setValueAndUpdate("Tank Drive");
        armUpButton.getProperty("Command").setValueAndUpdate("Arm Up");
        autoButton.getProperty("Command").setValueAndUpdate("Autonomous");
        xboxButton.getProperty("Command").setValueAndUpdate("Instant Command 1");

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

    public static int runBuild(String projectDirectory) throws InterruptedException, IOException {
        System.out.println("====================================================");
        Process p;
        ProcessBuilder pb;

        //enable desktop builds and disable roboRIO builds to allow compile test with desktop compiler.
        try {
            Path path = Paths.get("build/test-resources", projectDirectory, "build.gradle");
            Stream<String> lines = Files.lines(path);
            List<String> replaced = lines
                    .map(line -> line.replaceAll("def includeDesktopSupport = false", "def includeDesktopSupport = true"))
                    .collect(Collectors.toList());
            Files.write(path, replaced);
            lines.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
        if (isWindows) {
            System.out.println("Trying Windows compile...");
            pb = new ProcessBuilder("gradlew.bat", "build", "-Ptoolchain-optional-roboRio").directory(new File("build/test-resources/" + projectDirectory));
        } else {
            System.out.println("Trying *NIX compile...");
            //string array necessary to pass build as a parameter to gradle and not sh. https://stackoverflow.com/a/55164823
            pb = new ProcessBuilder(new String[]{"sh", "-c", "./gradlew build -Ptoolchain-optional-roboRio"}).directory(new File("build/test-resources/" + projectDirectory));
        }
        pb.redirectErrorStream(true);
        System.out.println("Running command: " + Arrays.toString(pb.command().toArray()));
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
