/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.MainFrame;
import robotbuilder.data.ExistsValidator;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.Validator;

/**
 *
 * @author alex
 */
public class IntegerPropertyTest {
    
    public IntegerPropertyTest() {
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
        IntegerProperty ip = new IntegerProperty("Test", 0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        IntegerProperty copy = (IntegerProperty) ip.copy();
        IntegerProperty copy2 = (IntegerProperty) copy.copy();
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
    
    @Test public void testGetValue() {
        IntegerProperty ip = new IntegerProperty("Test", 0, new String[0],
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
    
    @Test public void testGetDisplayValue() {
        IntegerProperty ip = new IntegerProperty("Test", 0, new String[0],
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
    
    @Test public void testSetValue() {
        IntegerProperty ip = new IntegerProperty("Test", 0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        ip.setValue("1");
        assertEquals("Value should be \"1\".", ip.value, "1");
        ip.setValue("");
        assertEquals("Value should be \"\".", ip.value, "");
        ip.setValue(2);
        assertEquals("Value should be \"2\".", ip.value, "2");
    }
    
    @Test public void testIsValid() {
        IntegerProperty ip = new IntegerProperty("Test", 0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        ip.setValue("1");
        assertTrue(ip.isValid());
        ip.setValue("");
        assertFalse(ip.isValid());
        ip.setValue(false);
        assertFalse(ip.isValid());
        ip.setValue(3.14159);
        assertFalse(ip.isValid());
        ip.setValue(2);
        assertTrue(ip.isValid());
        ip.setValue(Integer.MAX_VALUE);
        assertTrue(ip.isValid());
        ip.setValue(Integer.MIN_VALUE);
        assertTrue(ip.isValid());
    }
    
    @Test public void testGetError() {
        IntegerProperty ip = new IntegerProperty("Test", 0, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "1");
        ip.setValue("1");
        assertNull(ip.getErrorMessage());
        ip.setValue("");
        assertNotNull(ip.getErrorMessage());
        ip.setValue(false);
        assertNotNull(ip.getErrorMessage());
        ip.setValue(3.14159);
        assertNotNull(ip.getErrorMessage());
        ip.setValue(2);
        assertNull(ip.getErrorMessage());
        ip.setValue(Integer.MAX_VALUE);
        assertNull(ip.getErrorMessage());
        ip.setValue(Integer.MIN_VALUE);
        assertNull(ip.getErrorMessage());

        String[] validators = {"DropdownSelected"};
        IntegerProperty ip2 = new IntegerProperty("Test", 0, validators,
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "null");
        assertNotNull(ip2.getErrorMessage());
    }
}
