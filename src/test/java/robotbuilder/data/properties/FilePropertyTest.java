/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.MainFrame;

/**
 *
 * @author alex
 */
public class FilePropertyTest {
    
    public FilePropertyTest() {
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
        FileProperty fp = new FileProperty("Test", "", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "", "test", false);
        FileProperty copy = (FileProperty) fp.copy();
        FileProperty copy2 = (FileProperty) copy.copy();
        assertEquals("Copy should have the same name.", fp.name, copy.name);
        assertEquals("Copy should have the same default.", fp.defaultValue, copy.defaultValue);
        assertEquals("Copy should have the same value.", fp.value, copy.value);
        assertEquals("Copy should have the same extension.", fp.extension, copy.extension);
        assertEquals("Copy should have the same folder.", fp.folder, copy.folder);
        assertEquals("Copy should have the same validators.", fp.validators, copy.validators);
        assertEquals("Copy should have the same component.", fp.component, copy.component);
        
        assertEquals("Copy should have the same name.", fp.name, copy2.name);
        assertEquals("Copy should have the same default.", fp.defaultValue, copy2.defaultValue);
        assertEquals("Copy should have the same value.", fp.value, copy2.value);
        assertEquals("Copy should have the same extension.", fp.extension, copy2.extension);
        assertEquals("Copy should have the same folder.", fp.folder, copy2.folder);
        assertEquals("Copy should have the same validators.", fp.validators, copy2.validators);
        assertEquals("Copy should have the same component.", fp.component, copy2.component);
    }
    
    @Test public void testGetValue() {
        FileProperty fp = new FileProperty("Test", "", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "", "test", false);
        fp.value = null;
        assertEquals(fp.defaultValue, fp.getValue());
        fp.value = "test";
        assertEquals("test", fp.getValue());
        fp.value = "";
        assertEquals("", fp.getValue());
        fp.value = "/////";
        assertEquals("/////", fp.getValue());
        fp.value = "file.test";
        assertEquals("file.test", fp.getValue());
    }
    
    @Test public void testGetDisplayValue() throws IOException {
        FileProperty fp = new FileProperty("Test", "", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "", "test", false);
        fp.relative = false;
        fp.value = null;
        assertNull(((JFileChooser) fp.getDisplayValue()).getSelectedFile());
        MainFrame.getInstance().getCurrentRobotTree().setFilePath(new File("test.yml").getAbsolutePath());
        fp.value = "file.test";
        assertEquals(new File(new File(".").getCanonicalPath(), "file.test").getCanonicalPath(),
                ((JFileChooser) fp.getDisplayValue()).getSelectedFile().getCanonicalPath());
        
        MainFrame.getInstance().getCurrentRobotTree().setFilePath(new File("test.yml").getAbsolutePath());
        fp.value = "file";
        assertEquals(new File(new File(".").getCanonicalPath(), "file").getCanonicalPath(),
                ((JFileChooser) fp.getDisplayValue()).getSelectedFile().getCanonicalPath());
        
        FileProperty fp2 = new FileProperty("Test", "", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "", "test", true);
        fp2.value = null;
        assertNull(((JFileChooser) fp2.getDisplayValue()).getSelectedFile());
        fp2.value = "file.test";
        assertEquals(new File(new File(".").getCanonicalPath(), "file.test").getCanonicalPath(),
                ((JFileChooser) fp2.getDisplayValue()).getSelectedFile().getCanonicalPath());
        fp2.value = "file";
        assertEquals(new File(new File(".").getCanonicalPath(), "file").getCanonicalPath(),
                ((JFileChooser) fp2.getDisplayValue()).getSelectedFile().getCanonicalPath());
    }
    
    @Test public void testSetValue() {
        FileProperty fp = new FileProperty("Test", "", new String[0],
                MainFrame.getInstance().getCurrentRobotTree().getRoot(), "", "test", false);
        fp.setValue(null);
        assertNull(fp.value);
        fp.setValue("file");
        assertEquals("file.test", fp.value);
        fp.setValue("file.test");
        assertEquals("file.test", fp.value);
        fp.setValue("path/to/file");
        assertEquals("path/to/file.test", fp.value);
    }
}
