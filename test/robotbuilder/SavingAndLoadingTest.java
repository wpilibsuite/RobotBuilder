/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.io.File;
import static org.junit.Assert.assertEquals;
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
        RobotTree tree = MainFrame.getInstance().getCurrentRobotTree();
        tree.newFile(Palette.getInstance());
        tree.isRobotValid();
        RobotComponent before = tree.getRoot();
        tree.save("test/save.yaml");
        tree.load(new File("test/save.yaml"));
        RobotComponent after = tree.getRoot();
        assertEquals("Loaded file should be identical to the saved file.",
                before, after);
    }
    
    @Test
    public void saveAndLoadFullRobot() {
        RobotTree tree = TestUtils.generateTestTree();
        tree.isRobotValid();
        RobotComponent before = tree.getRoot();
        tree.save("test/save.yaml");
        tree.load(new File("test/save.yaml"));
        RobotComponent after = tree.getRoot();
        assertEquals("Loaded file should be identical to the saved file.",
                before, after);
    }
}
