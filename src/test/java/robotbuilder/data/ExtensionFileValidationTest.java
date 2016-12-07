
package robotbuilder.data;

import org.junit.*;
import robotbuilder.MainFrame;
import robotbuilder.extensions.Extensions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;

/**
 * @author phurleyk
 */
public class ExtensionFileValidationTest {
    Method isValidFile = null;

    public ExtensionFileValidationTest() {
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
        Method method = null;
        try {
            isValidFile = Extensions.class.getDeclaredMethod("isValidFile", File.class);
            isValidFile.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testExpectedFiles() {
        try {
            Object result = null;
            result = isValidFile.invoke(Extensions.class, new File("help.html"));
            assertTrue((boolean) result);
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void testExtraFile() {
        try {
            // This test failed prior to fix for issue #16
            Object result = null;
            result = isValidFile.invoke(Extensions.class, new File("extra-help.html"));
            assertTrue((boolean) result);
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }
}
