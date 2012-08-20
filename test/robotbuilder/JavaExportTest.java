/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import robotbuilder.robottree.RobotTree;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.*;
import robotbuilder.exporters.GenericExporter;

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
    public void testJavaExport() throws IOException, InterruptedException {
        RobotTree tree = TestUtils.generateTestTree();
        tree.getRoot().setName("RobotBuilderTestProject");
        tree.getRoot().getProperty("Java Project").setValue("test-resources/RobotBuilderTestProject/");
        tree.getRoot().getProperty("Java Package").setValue("robotcode");
        GenericExporter exporter = new GenericExporter("/export/java/");
        exporter.export(tree);

        Process p;
        try {
            p = Runtime.getRuntime().exec("ant compile", null, new File("test-resources/RobotBuilderTestProject/"));
        } catch (IOException ex) { // Catch for windows
            p = Runtime.getRuntime().exec("ant.bat compile", null, new File("test-resources/RobotBuilderTestProject/"));
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
        }
        p.waitFor();
        System.out.println(p.exitValue());
        assertEquals("Exit value should be 0, compilation failed.", p.exitValue(), 0);
    }
}
