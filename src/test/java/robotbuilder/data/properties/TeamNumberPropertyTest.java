
package robotbuilder.data.properties;

import org.junit.*;
import robotbuilder.MainFrame;

import static org.junit.Assert.*;

/**
 *
 * @author alex
 */
public class TeamNumberPropertyTest {

    public TeamNumberPropertyTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
    public void testCopy() {
        TeamNumberProperty ip = new TeamNumberProperty("Test", 330, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        TeamNumberProperty copy = (TeamNumberProperty) ip.copy();
        TeamNumberProperty copy2 = (TeamNumberProperty) copy.copy();
        assertEquals("Copy should have the same name.", ip.name, copy.name);
        assertEquals("Copy should have the same default.", ip.defaultValue, copy.defaultValue);
        assertEquals("Copy should have the same value.", ip.value, copy.value);
        assertEquals("Copy should have the same validators.", ip.validators, copy.validators);
        assertEquals("Copy should have the same component.", ip.component, copy.component);

        assertEquals("Copy should have the same name.", ip.name, copy2.name);
        assertEquals("Copy should have the same default.", ip.defaultValue, copy2.defaultValue);
        assertEquals("Copy should have the same value.", ip.value, copy2.value);
        assertEquals("Copy should have the same validators.", ip.validators, copy2.validators);
        assertEquals("Copy should have the same component.", ip.component, copy2.component);
    }

    @Test
    public void testGetValue() {
        TeamNumberProperty ip = new TeamNumberProperty("Test", 330, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        ip.value = null;
        assertEquals("Value should be the default value.", ip.getValue(), ip.defaultValue);
        ip.value = "1";
        assertEquals("Value should be 1.", ip.getValue(), 1);
        ip.value = "";
        assertEquals("Value should be \"\".", ip.getValue(), "");
        ip.value = "2";
        assertEquals("Value should be 2.", ip.getValue(), 2);
    }

    @Test
    public void testGetDisplayValue() {
        TeamNumberProperty ip = new TeamNumberProperty("Test", 330, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        ip.value = null;
        assertEquals("Display value should be the default value.", ip.getDisplayValue(), ip.defaultValue.toString());
        ip.value = "1";
        assertEquals("Display value should be \"1\".", ip.getDisplayValue(), "1");
        ip.value = "";
        assertEquals("Display value should be \"\".", ip.getDisplayValue(), "");
        ip.value = "2";
        assertEquals("Display value should be \"2\".", ip.getDisplayValue(), "2");
    }

    @Test
    public void testSetValue() {
        TeamNumberProperty ip = new TeamNumberProperty("Test", 330, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        ip.setValueAndUpdate("1");
        assertEquals("Value should be \"1\".", ip.value, "1");
        ip.setValueAndUpdate("");
        assertEquals("Value should be \"\".", ip.value, "");
        ip.setValueAndUpdate(2);
        assertEquals("Value should be \"2\".", ip.value, "2");
    }

    @Test
    public void testIsValid() {
        TeamNumberProperty ip = new TeamNumberProperty("Test", 330, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        ip.setValueAndUpdate("1");
        assertTrue(ip.isValid());
        ip.setValueAndUpdate("");
        assertFalse(ip.isValid());
        ip.setValueAndUpdate(false);
        assertFalse(ip.isValid());
        ip.setValueAndUpdate(3.14159);
        assertFalse(ip.isValid());
        ip.setValueAndUpdate(2);
        assertTrue(ip.isValid());
        ip.setValueAndUpdate(Integer.MAX_VALUE);
        assertTrue(ip.isValid());
        ip.setValueAndUpdate(Integer.MIN_VALUE);
        assertFalse(ip.isValid());
        ip.setValueAndUpdate(0);
        assertFalse(ip.isValid());
    }

    @Test
    public void testGetError() {
        TeamNumberProperty ip = new TeamNumberProperty("Test", 330, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        ip.setValueAndUpdate("1");
        assertNull(ip.getErrorMessage());
        ip.setValueAndUpdate("");
        assertNotNull(ip.getErrorMessage());
        ip.setValueAndUpdate(false);
        assertNotNull(ip.getErrorMessage());
        ip.setValueAndUpdate(3.14159);
        assertNotNull(ip.getErrorMessage());
        ip.setValueAndUpdate(2);
        assertNull(ip.getErrorMessage());
        ip.setValueAndUpdate(Integer.MAX_VALUE);
        assertNull(ip.getErrorMessage());
        ip.setValueAndUpdate(Integer.MIN_VALUE);
        assertNotNull(ip.getErrorMessage());
        ip.setValueAndUpdate(0);
        assertNotNull(ip.getErrorMessage());
    }
}
