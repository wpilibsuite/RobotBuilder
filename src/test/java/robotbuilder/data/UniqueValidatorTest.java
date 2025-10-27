
package robotbuilder.data;

import org.junit.*;
import robotbuilder.MainFrame;
import robotbuilder.TestUtils;
import robotbuilder.extensions.Extensions;
import robotbuilder.robottree.RobotTree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author alex
 */
public class UniqueValidatorTest {

    public UniqueValidatorTest() {
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

    /**
     * Test that the default setup is valid.
     */
    @Test
    public void testSimpleValidSetup() {
        RobotTree tree = TestUtils.getNewRobotTree();
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);

        // Create a basic subsystem
        RobotComponent subsystem = new RobotComponent("Subsystem", "Subsystem", tree);
        subsystems.add(subsystem);
        RobotComponent talon1 = new RobotComponent("Talon 1", "Motor Controller", tree);
        subsystem.add(talon1);
        RobotComponent talon2 = new RobotComponent("Talon 2", "Motor Controller", tree);
        subsystem.add(talon2);

        // Test it
        assertTrue("Talon 1 Output Channel (PWM) is not valid.",
                talon1.getProperty("Output Channel (PWM)").isValid());
        assertTrue("Talon 2 Output Channel (PWM) is not valid.",
                talon2.getProperty("Output Channel (PWM)").isValid());
    }

    /**
     * Make an invalid set up and test that it's invalid in the right way
     */
    @Test
    public void testSimpleInvalidSetup() {
        RobotTree tree = TestUtils.getNewRobotTree();
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);

        // Create a basic subsystem
        RobotComponent subsystem = new RobotComponent("Subsystem", "Subsystem", tree);
        subsystems.add(subsystem);
        RobotComponent talon1 = new RobotComponent("Talon 1", "Motor Controller", tree);
        subsystem.add(talon1);
        RobotComponent talon2 = new RobotComponent("Talon 2", "Motor Controller", tree);
        subsystem.add(talon2);
        talon2.getProperty("Output Channel (PWM)").setValueAndUpdate("0");

        // Test it
        assertTrue("Talon 1 Output Channel (PWM) is not valid.",
                talon1.getProperty("Output Channel (PWM)").isValid());
        assertFalse("Talon 2 Output Channel (PWM) should not be valid.",
                talon2.getProperty("Output Channel (PWM)").isValid());
    }

    /**
     * Check that it starts out valid, gets modified to the right invalid state,
     * then becomes valid again.
     */
    @Test
    public void testSimpleValidToggle() {
        RobotTree tree = TestUtils.getNewRobotTree();
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);

        // Create a basic subsystem
        RobotComponent subsystem = new RobotComponent("Subsystem", "Subsystem", tree);
        subsystems.add(subsystem);
        RobotComponent talon1 = new RobotComponent("Talon 1", "Motor Controller", tree);
        subsystem.add(talon1);
        RobotComponent talon2 = new RobotComponent("Talon 2", "Motor Controller", tree);
        subsystem.add(talon2);

        // Test it
        assertTrue("Talon 1 Output Channel (PWM) is not valid.",
                talon1.getProperty("Output Channel (PWM)").isValid());
        assertTrue("Talon 2 Output Channel (PWM) is not valid.",
                talon2.getProperty("Output Channel (PWM)").isValid());

        // Make it invalid
        talon2.getProperty("Output Channel (PWM)").setValueAndUpdate("0");

        // Test it
        assertTrue("Talon 1 Output Channel (PWM) is not valid.",
                talon1.getProperty("Output Channel (PWM)").isValid());
        assertFalse("Talon 2 Output Channel (PWM) should not be valid.",
                talon2.getProperty("Output Channel (PWM)").isValid());

        // Make it valid agoin
        talon2.getProperty("Output Channel (PWM)").setValueAndUpdate("1");

        // Test it
        assertTrue("Talon 1 Output Channel (PWM) is not valid.",
                talon1.getProperty("Output Channel (PWM)").isValid());
        assertTrue("Talon 2 Output Channel (PWM) is not valid.",
                talon2.getProperty("Output Channel (PWM)").isValid());
    }
}
