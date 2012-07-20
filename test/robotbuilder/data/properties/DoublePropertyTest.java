/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.MainFrame;

/**
 *
 * @author alex
 */
public class DoublePropertyTest {
    
    public DoublePropertyTest() {
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
    
    @Test public void testCopy() {
        DoubleProperty dp = new DoubleProperty("Test", 0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "3.14159");
        DoubleProperty copy = (DoubleProperty) dp.copy();
        DoubleProperty copy2 = (DoubleProperty) copy.copy();
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
    
    @Test public void testGetValue() {
        DoubleProperty dp = new DoubleProperty("Test", 0.0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "3.14159");
        dp.value = null;
        assertEquals("Value should be the default value.", dp.getValue(), dp.defaultValue);
        dp.value = "1";
        assertEquals("Value should be 1.", (Double) dp.getValue(), 1.0, .02);
        dp.value = "";
        assertEquals("Value should be \"\".", dp.getValue(), "");
        dp.value = "3.14159";
        assertEquals("Value should be 1.", (Double) dp.getValue(), 3.14159, .00002);
    }
    
    @Test public void testGetDisplayValue() {
        DoubleProperty dp = new DoubleProperty("Test", 0.0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "3.14159");
        dp.value = null;
        assertEquals("Display Value should be the default value.", dp.getDisplayValue(), dp.defaultValue.toString());
        dp.value = "1";
        assertEquals("Display Value should be 1.", dp.getDisplayValue(), "1.0");
        dp.value = "";
        assertEquals("Display Value should be \"\".", dp.getDisplayValue(), "");
        dp.value = "3.14159";
        assertEquals("Display Value should be 1.", dp.getDisplayValue(), "3.14159");
    }
    
    @Test public void testSetValue() {
        DoubleProperty dp = new DoubleProperty("Test", 0.0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "3.14159");
        dp.setValue("1");
        assertEquals("Display Value should be 1.", dp.value, "1");
        dp.setValue("");
        assertEquals("Display Value should be \"\".", dp.value, "");
        dp.setValue("3.14159");
        assertEquals("Display Value should be 1.", dp.value, "3.14159");
        dp.setValue(1.);
        assertEquals("Display Value should be 1.", dp.value, "1.0");
        dp.setValue(3.14159);
        assertEquals("Display Value should be 1.", dp.value, "3.14159");
    }
    
    @Test public void testIsValid() {
        DoubleProperty dp = new DoubleProperty("Test", 0.0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        dp.setValue("1");
        assertTrue(dp.isValid());
        dp.setValue("");
        assertFalse(dp.isValid());
        dp.setValue(false);
        assertFalse(dp.isValid());
        dp.setValue(3.14159);
        assertTrue(dp.isValid());
        dp.setValue(2);
        assertTrue(dp.isValid());
        dp.setValue(Double.MAX_EXPONENT);
        assertTrue(dp.isValid());
        dp.setValue(Double.MIN_EXPONENT);
        assertTrue(dp.isValid());
        dp.setValue(Double.MAX_VALUE);
        assertTrue(dp.isValid());
        dp.setValue(Double.MIN_VALUE);
        assertTrue(dp.isValid());
    }
    
    @Test public void testGetError() {
        DoubleProperty dp = new DoubleProperty("Test", 0.0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        dp.setValue("1");
        assertNull(dp.getErrorMessage());
        dp.setValue("");
        assertNotNull(dp.getErrorMessage());
        dp.setValue(false);
        assertNotNull(dp.getErrorMessage());
        dp.setValue(3.14159);
        assertNull(dp.getErrorMessage());
        dp.setValue(2);
        assertNull(dp.getErrorMessage());
        dp.setValue(Double.MAX_EXPONENT);
        assertNull(dp.getErrorMessage());
        dp.setValue(Double.MIN_EXPONENT);
        assertNull(dp.getErrorMessage());
        dp.setValue(Double.MAX_VALUE);
        assertNull(dp.getErrorMessage());
        dp.setValue(Double.MIN_VALUE);
        assertNull(dp.getErrorMessage());

        String[] validators = {"DropdownSelected"};
        DoubleProperty dp2 = new DoubleProperty("Test", 0, validators,
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "null");
        assertNotNull(dp2.getErrorMessage());
    }

}
