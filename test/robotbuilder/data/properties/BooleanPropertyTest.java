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
public class BooleanPropertyTest {
    
    public BooleanPropertyTest() {
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
        BooleanProperty bp = new BooleanProperty("Test", false, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), true);
        BooleanProperty copy = (BooleanProperty) bp.copy();
        BooleanProperty copy2 = (BooleanProperty) copy.copy();
        assertEquals("Copy should have the same name.", bp.name, copy.name);
        assertEquals("Copy should have the same default.", bp.defaultValue, copy.defaultValue);
        assertEquals("Copy should have the same value.", bp.value, copy.value);
        assertEquals("Copy should have the same validators.", bp.validators, copy.validators);
        assertEquals("Copy should have the same component.", bp.component, copy.component);
        
        assertEquals("Copy should have the same name.", bp.name, copy2.name);
        assertEquals("Copy should have the same default.", bp.defaultValue, copy2.defaultValue);
        assertEquals("Copy should have the same value.", bp.value, copy2.value);
        assertEquals("Copy should have the same validators.", bp.validators, copy2.validators);
        assertEquals("Copy should have the same component.", bp.component, copy2.component);
    }
    
    @Test public void testGetValue() {
        BooleanProperty bp = new BooleanProperty("Test", false, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), true);
        bp.value = null;
        assertEquals("Value should be the default value.", bp.getValue(), bp.defaultValue);
        bp.value = true;
        assertEquals("Value should be true.", bp.getValue(), true);
        bp.value = false;
        assertEquals("Value should be false.", bp.getValue(), false);
    }
    
    @Test public void testGetDisplayValue() {
        BooleanProperty bp = new BooleanProperty("Test", false, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), true);
        bp.value = null;
        assertEquals("Display value should be the default value.", bp.getDisplayValue(), bp.defaultValue);
        bp.value = true;
        assertEquals("Display value should be true.", bp.getDisplayValue(), true);
        bp.value = false;
        assertEquals("Display value should be false.", bp.getDisplayValue(), false);
    }
    
    @Test public void testSetValue() {
        BooleanProperty bp = new BooleanProperty("Test", false, new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), true);
        bp.setValue(true);
        assertEquals("Value should be true.", bp.value, true);
        bp.setValue(false);
        assertEquals("Value should be false.", bp.value, false);
    }
}
