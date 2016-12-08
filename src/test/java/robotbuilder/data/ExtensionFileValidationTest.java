
package robotbuilder.data;

import org.junit.Before;
import org.junit.Test;
import robotbuilder.extensions.Extensions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;

/**
 * @author phurley
 */
public class ExtensionFileValidationTest {
    Method isValidFile = null;

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

    @Test
    public void testExpectedFiles() throws InvocationTargetException, IllegalAccessException {
        Object result = null;
        result = isValidFile.invoke(Extensions.class, new File("help.html"));
        assertTrue((boolean) result);
    }

    @Test
    public void testExtraFile() throws InvocationTargetException, IllegalAccessException {
        // This test failed prior to fix for issue #16
        Object result = null;
        result = isValidFile.invoke(Extensions.class, new File("extra-help.html"));
        assertTrue((boolean) result);
    }
}
