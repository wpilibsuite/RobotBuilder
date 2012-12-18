/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.MainFrame;
import robotbuilder.palette.Palette;
import robotbuilder.robottree.RobotTree;
import robotbuilder.actions.GettingStartedAction;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author alex
 */
public class TypeSelectionPropertyTest {
    
    public TypeSelectionPropertyTest() {
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
        TypeSelectionProperty tp = new TypeSelectionProperty("Test", "None", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Subsystem", "");
        TypeSelectionProperty copy = (TypeSelectionProperty) tp.copy();
        TypeSelectionProperty copy2 = (TypeSelectionProperty) copy.copy();
        assertEquals("Copy should have the same name.", tp.name, copy.name);
        assertEquals("Copy should have the same default.", tp.defaultValue, copy.defaultValue);
        assertEquals("Copy should have the same value.", tp.value, copy.value);
        assertEquals("Copy should have the same type.", tp.type, copy.type);
        assertEquals("Copy should have the same validators.", tp.validators, copy.validators);
        assertEquals("Copy should have the same component.", tp.component, copy.component);
        
        assertEquals("Copy should have the same name.", tp.name, copy2.name);
        assertEquals("Copy should have the same default.", tp.defaultValue, copy2.defaultValue);
        assertEquals("Copy should have the same value.", tp.value, copy2.value);
        assertEquals("Copy should have the same type.", tp.type, copy2.type);
        assertEquals("Copy should have the same validators.", tp.validators, copy2.validators);
        assertEquals("Copy should have the same component.", tp.component, copy2.component);
    }
    
    @Test public void testGetValue() {
        TypeSelectionProperty tp = new TypeSelectionProperty("Test", "None", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Subsystem", "");
        tp.value = null;
        assertEquals(tp.defaultValue, tp.getValue());
        tp.value = "Arm";
        assertEquals("Arm", tp.getValue());
        tp.value = "";
        assertEquals("", tp.getValue());
        tp.value = false;
        assertEquals(false, tp.getValue());
        tp.value = "Wrist";
        assertEquals("Wrist", tp.getValue());
    }
    
    @Test public void testGetDisplayValue() {
        TypeSelectionProperty tp = new TypeSelectionProperty("Test", "None", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Subsystem", "");
        tp.value = null;
        assertEquals(tp.defaultValue, ((JComboBox) tp.getDisplayValue()).getSelectedItem());
        tp.value = "Wrist"; tp.valueComponent = wrist;
        assertEquals("Wrist", ((JComboBox) tp.getDisplayValue()).getSelectedItem());
        System.out.println("Getting 3....");
        tp.value = "Arm"; tp.valueComponent = arm;
        assertEquals("Arm", ((JComboBox) tp.getDisplayValue()).getSelectedItem());
        tp.value = "";
        assertEquals("Arm", ((JComboBox) tp.getDisplayValue()).getSelectedItem());
        tp.value = "Drive Train"; tp.valueComponent = driveTrain;
        assertEquals("Drive Train", ((JComboBox) tp.getDisplayValue()).getSelectedItem());
    }
    
    @Test public void testSetValue() {
        TypeSelectionProperty tp = new TypeSelectionProperty("Test", "None", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "Subsystem", "");
        tp.setValue("Wrist");
        assertEquals("Wrist", tp.value);
        tp.setValue("Arm");
        assertEquals("Arm", tp.value);
        tp.setValue("");
        assertEquals("None", tp.value);
        tp.setValue(null);
        assertEquals("None", tp.value);
        tp.setValue("Drive Train");
        assertEquals("Drive Train", tp.value);
    }
}
