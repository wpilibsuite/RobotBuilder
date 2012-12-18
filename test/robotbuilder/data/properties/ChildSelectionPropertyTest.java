/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import javax.swing.JComboBox;
import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.MainFrame;
import robotbuilder.palette.Palette;
import robotbuilder.robottree.RobotTree;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author alex
 */
public class ChildSelectionPropertyTest {
    
    public ChildSelectionPropertyTest() {
    }
    
    RobotComponent driveTrain, arm, wrist;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        RobotTree tree = MainFrame.getInstance().getCurrentRobotTree();
        tree.newFile();
        RobotComponent robot = tree.getRoot();
        RobotComponent subsystems = (RobotComponent) robot.getChildren().elementAt(0);
        
        // Create a drive train subsystem
        driveTrain = new RobotComponent("Drive Train", "Subsystem", tree);
        subsystems.add(driveTrain);
        // Create an arm subsystem
        arm = new RobotComponent("Arm", "Subsystem", tree);
        subsystems.add(arm);
        // Create an wrist subsystem
        wrist = new RobotComponent("Wrist", "PID Subsystem", tree);
        subsystems.add(wrist);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test public void testCopy() {
        ChildSelectionProperty cp = new ChildSelectionProperty("Test", "0", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Subsystem", "");
        ChildSelectionProperty copy = (ChildSelectionProperty) cp.copy();
        ChildSelectionProperty copy2 = (ChildSelectionProperty) copy.copy();
        assertEquals("Copy should have the same name.", cp.name, copy.name);
        assertEquals("Copy should have the same default.", cp.defaultValue, copy.defaultValue);
        assertEquals("Copy should have the same value.", cp.value, copy.value);
        assertEquals("Copy should have the same type.", cp.type, copy.type);
        assertEquals("Copy should have the same validators.", cp.validators, copy.validators);
        assertEquals("Copy should have the same component.", cp.component, copy.component);
        
        assertEquals("Copy should have the same name.", cp.name, copy2.name);
        assertEquals("Copy should have the same default.", cp.defaultValue, copy2.defaultValue);
        assertEquals("Copy should have the same value.", cp.value, copy2.value);
        assertEquals("Copy should have the same type.", cp.type, copy2.type);
        assertEquals("Copy should have the same validators.", cp.validators, copy2.validators);
        assertEquals("Copy should have the same component.", cp.component, copy2.component);
    }
    
    @Test public void testGetValue() {
        ChildSelectionProperty cp = new ChildSelectionProperty("Test", "0", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Subsystem", "");
        cp.value = null;
        assertEquals(cp.defaultValue, cp.getValue());
        cp.value = "Arm";
        assertEquals("Arm", cp.getValue());
        cp.value = "";
        assertEquals("", cp.getValue());
        cp.value = false;
        assertEquals(false, cp.getValue());
        cp.value = "Wrist";
        assertEquals("Wrist", cp.getValue());
    }
    
    @Test public void testGetDisplayValue() {
        ChildSelectionProperty cp = new ChildSelectionProperty("Test", "0", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Subsystem", "");
        cp.value = null;
        assertEquals("Drive Train", ((JComboBox) cp.getDisplayValue()).getSelectedItem());
        cp.value = "Wrist"; cp.valueComponent = wrist;
        assertEquals("Wrist", ((JComboBox) cp.getDisplayValue()).getSelectedItem());
        System.out.println("Getting 3....");
        cp.value = "Arm"; cp.valueComponent = arm;
        assertEquals("Arm", ((JComboBox) cp.getDisplayValue()).getSelectedItem());
        cp.value = "";
        assertEquals("Arm", ((JComboBox) cp.getDisplayValue()).getSelectedItem());
        cp.value = "Drive Train"; cp.valueComponent = driveTrain;
        assertEquals("Drive Train", ((JComboBox) cp.getDisplayValue()).getSelectedItem());
    }
    
    @Test public void testSetValue() {
        ChildSelectionProperty cp = new ChildSelectionProperty("Test", "0", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Subsystem", "");
        cp.setValue("Wrist");
        assertEquals("Wrist", cp.value);
        cp.setValue("Arm");
        assertEquals("Arm", cp.value);
        cp.setValue("");
        assertEquals("Drive Train", cp.value);
        cp.setValue(null);
        assertEquals("Drive Train", cp.value);
        cp.setValue("Drive Train");
        assertEquals("Drive Train", cp.value);
    }

}
