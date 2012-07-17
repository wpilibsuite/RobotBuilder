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
public class StringPropertyTest {
    
    public StringPropertyTest() {
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
        StringProperty sp = new StringProperty("Test", "Default", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Other");
        StringProperty copy = (StringProperty) sp.copy();
        StringProperty copy2 = (StringProperty) copy.copy();
        assertEquals("Copy should have the same name.", sp.name, copy.name);
        assertEquals("Copy should have the same default.", sp.defaultValue, copy.defaultValue);
        assertEquals("Copy should have the same value.", sp.value, copy.value);
        assertEquals("Copy should have the same validators.", sp.validators, copy.validators);
        assertEquals("Copy should have the same component.", sp.component, copy.component);
        
        assertEquals("Copy should have the same name.", sp.name, copy2.name);
        assertEquals("Copy should have the same default.", sp.defaultValue, copy2.defaultValue);
        assertEquals("Copy should have the same value.", sp.value, copy2.value);
        assertEquals("Copy should have the same validators.", sp.validators, copy2.validators);
        assertEquals("Copy should have the same component.", sp.component, copy2.component);
    }
    
    @Test public void testGetValue() {
        StringProperty sp = new StringProperty("Test", "Default", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Other");
        sp.value = null;
        assertEquals("Value should be the default value.", sp.getValue(), sp.defaultValue);
        sp.value = "Test";
        assertEquals("Value should be \"Test\".", sp.getValue(), "Test");
        sp.value = "";
        assertEquals("Value should be \"\".", sp.getValue(), "");
        sp.value = "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTest";
        assertEquals("Value should be \"Test\".", sp.getValue(), "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTest");
    }
    
    @Test public void testGetDisplayValue() {
        StringProperty sp = new StringProperty("Test", "Default", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Other");
        sp.value = null;
        assertEquals("Display value should be the default value.", sp.getDisplayValue(), sp.defaultValue);
        sp.value = "Test";
        assertEquals("Display value should be \"Test\".", sp.getDisplayValue(), "Test");
        sp.value = "";
        assertEquals("Display value should be \"\".", sp.getDisplayValue(), "");
        sp.value = "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTest";
        assertEquals("Display value should be \"Test\".", sp.getDisplayValue(), "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTest");
    }
    
    @Test public void testSetValue() {
        StringProperty sp = new StringProperty("Test", "Default", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Other");
        sp.setValue("Test");
        assertEquals("Value should be \"Test\".", sp.value, "Test");
        sp.setValue("");
        assertEquals("Value should be \"\".", sp.value, "");
        sp.setValue("TestTestTestTestTestTestTestTestTestTestTestTestTestTestTest");
        assertEquals("Value should be \"Test\".", sp.value, "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTest");
    }
}
