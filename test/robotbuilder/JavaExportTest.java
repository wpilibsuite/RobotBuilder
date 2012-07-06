/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

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
        File commands = new File("test-resources/RobotBuilderTestProject/src/robotcode/commands");
        File subsystems = new File("test-resources/RobotBuilderTestProject/src/robotcode/subsystems");
        File robotmap = new File("test-resources/RobotBuilderTestProject/src/robotcode/RobotMap.java");
        File oi = new File("test-resources/RobotBuilderTestProject/src/robotcode/OI.java");
        
        if (commands.listFiles() != null)
            for (File child : commands.listFiles()) { child.delete(); }
        commands.delete();
        assertFalse(commands.exists());
        commands.mkdir();

        if (subsystems.listFiles() != null)
            for (File child : subsystems.listFiles()) { child.delete(); }
        subsystems.delete();
        assertFalse(subsystems.exists());
        subsystems.mkdir();

        robotmap.delete();
        assertFalse(robotmap.exists());
        oi.delete();
        assertFalse(oi.exists());
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testJavaExport() throws IOException, InterruptedException {
        RobotTree tree = TestUtils.generateTestTree();
        tree.getRoot().getProperty("Java Project").setValue("test-resources/RobotBuilderTestProject/");
        tree.getRoot().getProperty("Java Package").setValue("robotcode");
        GenericExporter exporter = new GenericExporter("/export/java/");
        exporter.export(tree);

        Process p = Runtime.getRuntime().exec("ant compile", null, new File("test-resources/RobotBuilderTestProject/"));
        p.waitFor();
        String output = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = reader.readLine();
        while (line != null) {
            output += line +"\n";
            line = reader.readLine();
        }
        System.out.println(output);
        System.out.println(p.exitValue());
        assertEquals("Exit value should be 0, compilation failed.", p.exitValue(), 0);
    }
}
