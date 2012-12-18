/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import robotbuilder.palette.Palette;
import robotbuilder.robottree.RobotTree;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.*;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author alex
 */
public class SavingAndLoadingTest {
    
    public SavingAndLoadingTest() {
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
    
    @Test
    public void saveAndLoadANewFile() {
        RobotTree tree = TestUtils.getNewRobotTree();
        tree.isRobotValid();
        RobotComponent before = tree.getRoot();
        tree.save("test/save.yml");
        tree.load(new File("test/save.yml"));
        RobotComponent after = tree.getRoot();
        assertEquals("Loaded file should be identical to the saved file.",
                before, after);
    }
    
    @Test
    public void saveAndLoadFullRobot() {
        RobotTree tree = TestUtils.generateTestTree();
        tree.isRobotValid();
        RobotComponent before = tree.getRoot();
        tree.save("test/save.yml");
        tree.load(new File("test/save.yml"));
        RobotComponent after = tree.getRoot();
        assertEquals("Loaded file should be identical to the saved file.",
                before, after);
    }
    
    @Test
    public void saveAndLoadFromAnOddLocation() throws IOException {
        RobotTree tree = MainFrame.getInstance().getCurrentRobotTree();
        tree.newFile();
        tree.isRobotValid();
        RobotComponent before = tree.getRoot();
        File tmpFile = File.createTempFile("robotbuilder-test-save", "-"+Long.toString(System.nanoTime())+".yml");
        tree.save(tmpFile.getAbsolutePath());
        assertTrue("Didn't save in the correct location.", tmpFile.exists());
        tree.load(tmpFile);
        RobotComponent after = tree.getRoot();
        assertEquals("Loaded file should be identical to the saved file.",
                before, after);
    }
}
