
package robotbuilder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.*;
import robotbuilder.extensions.Extensions;
import robotbuilder.robottree.RobotTree;

/**
 *
 * @author
 */
public class VersionTest {

    public VersionTest() {
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
    public void versionCompatibleTest() {
        RobotTree tree = TestUtils.getNewRobotTree();
        tree.isRobotValid();
        assertTrue(tree.isVersionCompatible(RobotBuilder.VERSION, RobotBuilder.VERSION));
        assertFalse(tree.isVersionCompatible("2.0", "3.5"));
        assertFalse(tree.isVersionCompatible("4.0", "3.5"));
        assertFalse(tree.isVersionCompatible("2.5", "3.5"));
        assertFalse(tree.isVersionCompatible("4.5", "3.5"));
        assertFalse(tree.isVersionCompatible("2.9", "3.5"));
        assertFalse(tree.isVersionCompatible("4.9", "3.5"));
        assertTrue(tree.isVersionCompatible("3.4", "3.5"));
        assertFalse(tree.isVersionCompatible("3.6", "3.5"));
        assertTrue(tree.isVersionCompatible("3.4", "3.11"));
        assertFalse(tree.isVersionCompatible("3.11", "3.5"));
    }

}
