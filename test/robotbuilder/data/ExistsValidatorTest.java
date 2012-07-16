/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.MainFrame;
import robotbuilder.Palette;
import robotbuilder.RobotTree;

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
        RobotTree tree = MainFrame.getInstance().getCurrentRobotTree();
        tree.newFile(Palette.getInstance());
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);
        
        // Create a drive train subsystem
        RobotComponent driveTrain = new RobotComponent("Drive Train", "Subsystem", tree);
        subsystems.add(driveTrain);
        RobotComponent robotDrive = new RobotComponent("Robot Drive", "Robot Drive 2", tree);
        driveTrain.add(robotDrive);
        RobotComponent leftVictor = new RobotComponent("Left Victor", "Victor", tree);
        robotDrive.add(leftVictor);
        RobotComponent rightVictor = new RobotComponent("Right Victor", "Victor", tree);
        robotDrive.add(rightVictor);
        
        robotDrive.getProperty("Left Motor").setValue(leftVictor.getFullName());
        robotDrive.getProperty("Right Motor").setValue(rightVictor.getFullName());
        
        assertTrue("Left motor should be selected.", robotDrive.getProperty("Left Motor").isValid());
        assertTrue("Right motor should be selected.", robotDrive.getProperty("Right Motor").isValid());
    }
    
    @Test public void testInvalidSetup() {
        RobotTree tree = MainFrame.getInstance().getCurrentRobotTree();
        tree.newFile(Palette.getInstance());
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);
        
        // Create a drive train subsystem
        RobotComponent driveTrain = new RobotComponent("Drive Train", "Subsystem", tree);
        subsystems.add(driveTrain);
        RobotComponent robotDrive = new RobotComponent("Robot Drive", "Robot Drive 2", tree);
        driveTrain.add(robotDrive);
        
        System.out.println("Left Motor: "+robotDrive.getProperty("Left Motor"));
        System.out.println("Right Motor: "+robotDrive.getProperty("Right Motor"));
        
        assertFalse("Left motor should not be selected.", robotDrive.getProperty("Left Motor").isValid());
        assertFalse("Right motor should not be selected.", robotDrive.getProperty("Right Motor").isValid());

    }
}
