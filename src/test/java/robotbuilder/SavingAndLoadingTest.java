
package robotbuilder;

import org.junit.*;
import robotbuilder.data.RobotComponent;
import robotbuilder.extensions.Extensions;
import robotbuilder.robottree.RobotTree;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author alex
 */
public class SavingAndLoadingTest {

    private static final String SAVE_FILE = "build/test-resources/save.yaml";

    public SavingAndLoadingTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Extensions.init();
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
        tree.save(SAVE_FILE);
        tree.save("build/test-resources/savefull.yaml");
        tree.load(new File(SAVE_FILE));
        tree.save("build/test-resources/savefull2.yaml");
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
        File tmpFile = File.createTempFile("robotbuilder-test-save", "-" + Long.toString(System.nanoTime()) + ".yaml");
        tree.save(tmpFile.getAbsolutePath());
        assertTrue("Didn't save in the correct location.", tmpFile.exists());
        tree.load(tmpFile);
        RobotComponent after = tree.getRoot();
        assertEquals("Loaded file should be identical to the saved file.",
                before, after);
    }
}
