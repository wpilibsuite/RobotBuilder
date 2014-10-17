/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import javax.swing.JComboBox;
import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.MainFrame;

/**
 *
 * @author alex
 */
public class ChoicesPropertyTest {
    
    public ChoicesPropertyTest() {
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
    
    String[] choices = {"1", "2", "3"};
    
    @Test public void testCopy() {
        ChoicesProperty cp = new ChoicesProperty("Test", "1", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), choices, "2");
        ChoicesProperty copy = (ChoicesProperty) cp.copy();
        ChoicesProperty copy2 = (ChoicesProperty) copy.copy();
        assertEquals("Copy should have the same name.", cp.name, copy.name);
        assertEquals("Copy should have the same default.", cp.defaultValue, copy.defaultValue);
        assertEquals("Copy should have the same value.", cp.value, copy.value);
        assertEquals("Copy should have the same choices.", cp.choices, copy.choices);
        assertEquals("Copy should have the same validators.", cp.validators, copy.validators);
        assertEquals("Copy should have the same component.", cp.component, copy.component);
        
        assertEquals("Copy should have the same name.", cp.name, copy2.name);
        assertEquals("Copy should have the same default.", cp.defaultValue, copy2.defaultValue);
        assertEquals("Copy should have the same value.", cp.value, copy2.value);
        assertEquals("Copy should have the same choices.", cp.choices, copy2.choices);
        assertEquals("Copy should have the same validators.", cp.validators, copy2.validators);
        assertEquals("Copy should have the same component.", cp.component, copy2.component);
    }
    
    @Test public void testGetValue() {
        ChoicesProperty cp = new ChoicesProperty("Test", "1", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), choices, "2");
        cp.value = null;
        assertEquals("Value should be the default value.", cp.getValue(), cp.defaultValue);
        cp.value = "1";
        assertEquals("Value should be 1.", cp.getValue(), "1");
        cp.value = "";
        assertEquals("Value should be \"\".", cp.getValue(), "");
        cp.value = "3.14159";
        assertEquals("Value should be 1.", cp.getValue(), "3.14159");
        cp.value = "3";
        assertEquals("Value should be 3.", cp.getValue(), "3");
    }
    
    @Test public void testGetDisplayValue() {
        ChoicesProperty cp = new ChoicesProperty("Test", "1", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), choices, "2");
        cp.value = null;
        assertEquals("Display Value should be the default value.", ((JComboBox) cp.getDisplayValue()).getSelectedItem(), cp.defaultValue);
        cp.value = "1";
        assertEquals("Display Value should be 1.", ((JComboBox) cp.getDisplayValue()).getSelectedItem(), "1");
        System.out.println("Getting 3....");
        cp.value = "3";
        assertEquals("Display Value should be 3.", ((JComboBox) cp.getDisplayValue()).getSelectedItem(), "3");
        cp.value = "";
        assertEquals("Display Value should be the previous value.", ((JComboBox) cp.getDisplayValue()).getSelectedItem(), "3");
        cp.value = "3.14159";
        assertEquals("Display Value should be the previous value.", ((JComboBox) cp.getDisplayValue()).getSelectedItem(), "3");
    }
    
    @Test public void testSetValue() {
        ChoicesProperty cp = new ChoicesProperty("Test", "1", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), choices, "2");
        cp.setValue("1");
        assertEquals("Value should be 1.", cp.value, "1");
        cp.setValue("3");
        assertEquals("Value should be 3.", cp.value, "3");
        cp.setValue("");
        assertEquals("Value should be \"\".", cp.value, "");
        cp.setValue("3.14159");
        assertEquals("Value should be \"3.14159\".", cp.value, "3.14159");
        
        assertNotNull(cp.getDisplayValue()); // Add a combo box to the mix.
        cp.setValue("1");
        assertEquals("Value should be 1.", cp.value, "1");
        cp.setValue("3");
        assertEquals("Value should be 3.", cp.value, "3");
        cp.setValue("");
        assertEquals("Value should be \"\".", cp.value, "3");
        cp.setValue("3.14159");
        assertEquals("Value should be \"3.14159\".", cp.value, "3");
    }

}
