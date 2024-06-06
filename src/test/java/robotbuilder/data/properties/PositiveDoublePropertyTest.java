
package robotbuilder.data.properties;

import org.junit.*;
import robotbuilder.MainFrame;
import robotbuilder.extensions.Extensions;

import static org.junit.Assert.*;

/**
 *
 * @author alex
 */
public class PositiveDoublePropertyTest {

    public PositiveDoublePropertyTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Extensions.init();
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
        PositiveDoubleProperty dp = new PositiveDoubleProperty("Test", 0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "3.14159");
        PositiveDoubleProperty copy = (PositiveDoubleProperty) dp.copy();
        PositiveDoubleProperty copy2 = (PositiveDoubleProperty) copy.copy();
        assertEquals("Copy should have the same name.", dp.name, copy.name);
        assertEquals("Copy should have the same default.", dp.defaultValue, copy.defaultValue);
        assertEquals("Copy should have the same value.", dp.value, copy.value);
        assertEquals("Copy should have the same validators.", dp.validators, copy.validators);
        assertEquals("Copy should have the same component.", dp.component, copy.component);

        assertEquals("Copy should have the same name.", dp.name, copy2.name);
        assertEquals("Copy should have the same default.", dp.defaultValue, copy2.defaultValue);
        assertEquals("Copy should have the same value.", dp.value, copy2.value);
        assertEquals("Copy should have the same validators.", dp.validators, copy2.validators);
        assertEquals("Copy should have the same component.", dp.component, copy2.component);
    }

    @Test
    public void testGetValue() {
        PositiveDoubleProperty dp = new PositiveDoubleProperty("Test", 0.0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "3.14159");
        dp.value = null;
        assertEquals("Value should be the default value.", dp.getValue(), dp.defaultValue);
        dp.value = "1";
        assertEquals("Value should be 1.", (Double) dp.getValue(), 1.0, .00002);
        dp.value = "";
        assertEquals("Value should be \"\".", dp.getValue(), "");
        dp.value = "3.14159";
        assertEquals("Value should be 3.14159", (Double) dp.getValue(), 3.14159, .00002);
        dp.value = "-1";
        assertEquals("Value should be -1.", (Double) dp.getValue(), -1.0, .00002);
    }

    @Test
    public void testGetDisplayValue() {
        PositiveDoubleProperty dp = new PositiveDoubleProperty("Test", 0.0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "3.14159");
        dp.value = null;
        assertEquals("Display Value should be the default value.", dp.getDisplayValue(), dp.defaultValue.toString());
        dp.value = "1";
        assertEquals("Display Value should be 1.", dp.getDisplayValue(), "1.0");
        dp.value = "";
        assertEquals("Display Value should be \"\".", dp.getDisplayValue(), "");
        dp.value = "3.14159";
        assertEquals("Display Value should be 3.14159", dp.getDisplayValue(), "3.14159");
        dp.value = "-1.0";
        assertEquals("Display Value should be -1.", dp.getDisplayValue(), "-1.0");
    }

    @Test
    public void testSetValue() {
        PositiveDoubleProperty dp = new PositiveDoubleProperty("Test", 0.0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "3.14159");
        dp.setValueAndUpdate("1");
        assertEquals("Display Value should be 1.", dp.value, "1");
        dp.setValueAndUpdate("");
        assertEquals("Display Value should be \"\".", dp.value, "");
        dp.setValueAndUpdate("3.14159");
        assertEquals("Display Value should be 3.14159.", dp.value, "3.14159");
        dp.setValueAndUpdate(1.);
        assertEquals("Display Value should be 1.", dp.value, "1.0");
        dp.setValueAndUpdate(3.14159);
        assertEquals("Display Value should be 3.14159.", dp.value, "3.14159");
        dp.setValueAndUpdate("-1.0");
        assertEquals("Display Value should be 1.", dp.value, "-1.0");
    }

    @Test
    public void testIsValid() {
        PositiveDoubleProperty dp = new PositiveDoubleProperty("Test", 0.0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        dp.setValueAndUpdate("1");
        assertTrue(dp.isValid());
        dp.setValueAndUpdate("");
        assertFalse(dp.isValid());
        dp.setValueAndUpdate(false);
        assertFalse(dp.isValid());
        dp.setValueAndUpdate(3.14159);
        assertTrue(dp.isValid());
        dp.setValueAndUpdate(2);
        assertTrue(dp.isValid());
        dp.setValueAndUpdate(-1.0);
        assertFalse(dp.isValid());
        dp.setValueAndUpdate(Double.MAX_EXPONENT);
        assertTrue(dp.isValid());
        dp.setValueAndUpdate(Double.MIN_EXPONENT);
        assertFalse(dp.isValid());
        dp.setValueAndUpdate(Double.MAX_VALUE);
        assertTrue(dp.isValid());
        dp.setValueAndUpdate(Double.MIN_VALUE);
        assertTrue(dp.isValid());
    }

    @Test
    public void testGetError() {
        PositiveDoubleProperty dp = new PositiveDoubleProperty("Test", 0.0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        dp.setValueAndUpdate("1");
        assertNull(dp.getErrorMessage());
        dp.setValueAndUpdate("");
        assertNotNull(dp.getErrorMessage());
        dp.setValueAndUpdate(false);
        assertNotNull(dp.getErrorMessage());
        dp.setValueAndUpdate(3.14159);
        assertNull(dp.getErrorMessage());
        dp.setValueAndUpdate(2);
        assertNull(dp.getErrorMessage());
        dp.setValueAndUpdate(-1.0);
        assertNotNull(dp.getErrorMessage());
        dp.setValueAndUpdate(Double.MAX_EXPONENT);
        assertNull(dp.getErrorMessage());
        dp.setValueAndUpdate(Double.MIN_EXPONENT);
        assertNotNull(dp.getErrorMessage());
        dp.setValueAndUpdate(Double.MAX_VALUE);
        assertNull(dp.getErrorMessage());
        dp.setValueAndUpdate(Double.MIN_VALUE);
        assertNull(dp.getErrorMessage());

        String[] validators = {"DropdownSelected"};
        PositiveDoubleProperty dp2 = new PositiveDoubleProperty("Test", 0, validators,
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "null");
        assertNotNull(dp2.getErrorMessage());
    }

}
