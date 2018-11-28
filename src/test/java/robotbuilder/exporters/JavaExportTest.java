
package robotbuilder.exporters;

import org.junit.*;
import robotbuilder.TestUtils;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.RobotWalker;
import robotbuilder.robottree.RobotTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 *
 * @author alex
 */
public class JavaExportTest {

    public JavaExportTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        File project = new File("test-resources/RobotBuilderTestProject/");
        TestUtils.delete(project);
        assertFalse(project.exists());
        project.mkdir();
    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore("This is dependent upon hardware and the system")
    public void testJavaExport() throws IOException, InterruptedException {
        RobotTree tree = TestUtils.generateTestTree();
        tree.getRoot().setName("RobotBuilderTestProject");
        tree.getRoot().getProperty("Export Directory").setValueAndUpdate(new File("test-resources/").getAbsolutePath());
        tree.getRoot().getProperty("Java Package").setValueAndUpdate("robotcode");
        tree.walk(new RobotWalker() {
            @Override
            public void handleRobotComponent(RobotComponent self) { // Gives us better diagnostics when the robot tree isn't valid.
                assertTrue("Component not valid: " + self + ": " + self.getErrorMessage(), self.isValid());
            }
        });
        assertTrue("Robot tree is not valid.", tree.isRobotValid()); // Fails early instead of opening up a window to report failure.
        GenericExporter exporter = new GenericExporter("/export/java/");
        exporter.post_export_action = null;
        exporter.export(tree);

        System.out.println("====================================================");
        Process p;
        try {
            System.out.println("Trying *NIX compile...");
            p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ant compile", "2>&1"}, null, new File("test-resources/RobotBuilderTestProject"));
        } catch (IOException ex) { // Catch for windows
            System.out.println("Trying Windows compile...");
            p = Runtime.getRuntime().exec("ant.bat compile", null, new File("test-resources/RobotBuilderTestProject"));
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
        }
        System.out.println("====================================================");
        p.waitFor();
        System.out.println(p.exitValue());
        assertEquals("Exit value should be 0, compilation failed.", p.exitValue(), 0);
    }
}
