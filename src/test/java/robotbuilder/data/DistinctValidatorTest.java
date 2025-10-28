
package robotbuilder.data;

import java.util.LinkedList;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.MainFrame;
import robotbuilder.extensions.Extensions;
import robotbuilder.robottree.RobotTree;
import robotbuilder.TestUtils;
import robotbuilder.data.properties.IntegerProperty;
import robotbuilder.data.properties.Property;

/**
 *
 * @author alex
 */
public class DistinctValidatorTest {

    public DistinctValidatorTest() {
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

        robotDrive.getProperty("Left Motor").setValueAndUpdate(leftTalon.getName());
        robotDrive.getProperty("Right Motor").setValueAndUpdate(rightTalon.getName());

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
        RobotComponent leftTalon = new RobotComponent("Left Talon", "Motor Controller", tree);
        robotDrive.add(leftTalon);
        RobotComponent rightTalon = new RobotComponent("Right Talon", "Motor Controller", tree);
        robotDrive.add(rightTalon);

        robotDrive.getProperty("Left Motor").setValueAndUpdate(leftTalon.getName());
        robotDrive.getProperty("Right Motor").setValueAndUpdate(leftTalon.getName());

        assertFalse("Left motor should be selected.", robotDrive.getProperty("Left Motor").isValid());
        assertFalse("Right motor should be selected.", robotDrive.getProperty("Right Motor").isValid());
    }

    @Test
    public void testToggle() {
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

        robotDrive.getProperty("Left Motor").setValueAndUpdate(leftTalon.getName());
        robotDrive.getProperty("Right Motor").setValueAndUpdate(rightTalon.getName());

        assertTrue("Left motor should be selected.", robotDrive.getProperty("Left Motor").isValid());
        assertTrue("Right motor should be selected.", robotDrive.getProperty("Right Motor").isValid());

        robotDrive.getProperty("Left Motor").setValueAndUpdate(leftTalon.getName());
        robotDrive.getProperty("Right Motor").setValueAndUpdate(leftTalon.getName());

        assertFalse("Left motor should be selected.", robotDrive.getProperty("Left Motor").isValid());
        assertFalse("Right motor should be selected.", robotDrive.getProperty("Right Motor").isValid());

        robotDrive.getProperty("Left Motor").setValueAndUpdate(rightTalon.getName());
        robotDrive.getProperty("Right Motor").setValueAndUpdate(leftTalon.getName());

        assertTrue("Left motor should be selected.", robotDrive.getProperty("Left Motor").isValid());
        assertTrue("Right motor should be selected.", robotDrive.getProperty("Right Motor").isValid());
    }

    @Test
    public void testGetError() {
        List<String> fs = new LinkedList<String>();
        fs.add("Field 1");
        fs.add("Field 2");
        DistinctValidator dv = new DistinctValidator("Test", fs);

        Property f1 = new IntegerProperty(), f2 = new IntegerProperty();
        f1.setName("Field 1");
        f2.setName("Field 2");
        Object val1 = new Object(), val2 = new Object();

        dv.update(null, "Field 1", val1);
        dv.update(null, "Field 2", val2);
        assertNull(dv.getError(null, f1));
        assertNull(dv.getError(null, f2));
        dv.update(null, "Field 2", val1);
        assertNotNull(dv.getError(null, f1));
        assertNotNull(dv.getError(null, f2));
    }

    @Test
    public void testInvalidField() {
        List<String> fs = new LinkedList<String>();
        fs.add("Field 1");
        fs.add("Field 2");
        DistinctValidator dv = new DistinctValidator("Test", fs);

        try {
            dv.update(null, "Field 3", new Object());
            assertTrue("Distinct Validator should throw an AssertionError for invalid fields.", false);
        } catch (AssertionError e) {
            // Successful
        }
    }

    @Test
    public void testGetAndSetFields() {
        List<String> fs = new LinkedList<String>();
        fs.add("Field 1");
        fs.add("Field 2");
        DistinctValidator dv = new DistinctValidator();
        dv.setFields(fs);
        assertEquals(fs, dv.getFields());
        assertEquals(dv.getFields(), dv.fields);
    }
}
