/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.MainFrame;
import robotbuilder.palette.Palette;
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
    
    @Test public void testValidSetup() {
        RobotTree tree = TestUtils.getNewRobotTree();
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);
        
        // Create a drive train subsystem
        RobotComponent driveTrain = new RobotComponent("Drive Train", "Subsystem", tree);
        subsystems.add(driveTrain);
        RobotComponent robotDrive = new RobotComponent("Robot Drive", "Robot Drive 2", tree);
        driveTrain.add(robotDrive);
        RobotComponent leftVictor = new RobotComponent("Left Victor", "Speed Controller", tree);
        robotDrive.add(leftVictor);
        RobotComponent rightVictor = new RobotComponent("Right Victor", "Speed Controller", tree);
        robotDrive.add(rightVictor);
        
        robotDrive.getProperty("Left Motor").setValue(leftVictor.getFullName());
        robotDrive.getProperty("Right Motor").setValue(rightVictor.getFullName());
        
        assertTrue("Left motor should be selected.", robotDrive.getProperty("Left Motor").isValid());
        assertTrue("Right motor should be selected.", robotDrive.getProperty("Right Motor").isValid());
    }
    
    @Test public void testInvalidSetup() {
        RobotTree tree = TestUtils.getNewRobotTree();
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);
        
        // Create a drive train subsystem
        RobotComponent driveTrain = new RobotComponent("Drive Train", "Subsystem", tree);
        subsystems.add(driveTrain);
        RobotComponent robotDrive = new RobotComponent("Robot Drive", "Robot Drive 2", tree);
        driveTrain.add(robotDrive);
        
        assertFalse("Left motor should not be selected.", robotDrive.getProperty("Left Motor").isValid());
        assertFalse("Right motor should not be selected.", robotDrive.getProperty("Right Motor").isValid());
    }
    
    @Test public void testInvalidToggle() {
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
        RobotComponent motor = new RobotComponent("Motor", "Speed Controller", tree);
        pid.add(motor);
        
        pid.getProperty("Input").setValue(encoder.getFullName());
        pid.getProperty("Output").setValue(motor.getFullName());
        
        assertTrue("Input should be selected.", pid.getProperty("Input").isValid());
        assertTrue("Output should be selected.", pid.getProperty("Output").isValid());
        
        System.out.println("Output: "+pid.getProperty("Output"));
        motor.removeFromParent();
        pid.getProperty("Output").update();
        
        System.out.println("Output: "+pid.getProperty("Output"));
        
        assertTrue("Input should be selected.", pid.getProperty("Input").isValid());
        assertFalse("Output should not be selected.", pid.getProperty("Output").isValid());
    }
}
