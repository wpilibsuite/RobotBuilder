
package robotbuilder.data;

import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.extensions.Extensions;
import robotbuilder.MainFrame;
import robotbuilder.robottree.RobotTree;
import robotbuilder.TestUtils;

/**
 *
 * @author alex
 */
public class ExistsValidatorTest {

    public ExistsValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Extensions.init();
        MainFrame.getInstance();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testValidSetup() {
        RobotTree tree = TestUtils.getNewRobotTree();
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);

        // Create a drive train subsystem
        RobotComponent driveTrain = new RobotComponent("Drive Train", "Subsystem", tree);
        subsystems.add(driveTrain);
        RobotComponent robotDrive = new RobotComponent("Robot Drive", "Differential Drive", tree);
        driveTrain.add(robotDrive);
        RobotComponent leftTalon = new RobotComponent("Left Talon", "Motor Controller", tree);
        robotDrive.add(leftTalon);
        RobotComponent rightTalon = new RobotComponent("Right Talon", "Motor Controller", tree);
        robotDrive.add(rightTalon);

        robotDrive.getProperty("Left Motor").setValueAndUpdate(leftTalon.getFullName());
        robotDrive.getProperty("Right Motor").setValueAndUpdate(rightTalon.getFullName());

        assertTrue("Left motor should be selected.", robotDrive.getProperty("Left Motor").isValid());
        assertTrue("Right motor should be selected.", robotDrive.getProperty("Right Motor").isValid());
    }

    @Test
    public void testInvalidSetup() {
        RobotTree tree = TestUtils.getNewRobotTree();
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);

        // Create a drive train subsystem
        RobotComponent driveTrain = new RobotComponent("Drive Train", "Subsystem", tree);
        subsystems.add(driveTrain);
        RobotComponent robotDrive = new RobotComponent("Robot Drive", "Differential Drive", tree);
        driveTrain.add(robotDrive);

        assertFalse("Left motor should not be selected.", robotDrive.getProperty("Left Motor").isValid());
        assertFalse("Right motor should not be selected.", robotDrive.getProperty("Right Motor").isValid());
    }

    @Test
    public void testInvalidToggle() {
        RobotTree tree = TestUtils.getNewRobotTree();
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);
        // Create an arm subsystem
        RobotComponent arm = new RobotComponent("Arm", "Subsystem", tree);
        subsystems.add(arm);
        RobotComponent pid = new RobotComponent("PID Controller", "PID Controller", tree);
        arm.add(pid);

        assertFalse("Input should not be selected.", pid.getProperty("Input").isValid());
        assertFalse("Output should not be selected.", pid.getProperty("Output").isValid());

        RobotComponent encoder = new RobotComponent("Encoder", "Quadrature Encoder", tree);
        pid.add(encoder);
        RobotComponent motor = new RobotComponent("Motor", "Motor Controller", tree);
        pid.add(motor);

        pid.getProperty("Input").setValueAndUpdate(encoder.getFullName());
        pid.getProperty("Output").setValueAndUpdate(motor.getFullName());

        assertTrue("Input should be selected.", pid.getProperty("Input").isValid());
        assertTrue("Output should be selected.", pid.getProperty("Output").isValid());

        System.out.println("Output: " + pid.getProperty("Output"));
        motor.removeFromParent();
        pid.getProperty("Output").update();

        System.out.println("Output: " + pid.getProperty("Output"));

        assertTrue("Input should be selected.", pid.getProperty("Input").isValid());
        assertFalse("Output should not be selected.", pid.getProperty("Output").isValid());
    }
}
