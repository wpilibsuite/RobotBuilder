/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data.properties;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author alex
 */
public class PropertyTest {
    
    public PropertyTest() {
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

    @Test public void testEquals() {
        String[] v1 = {"test"};
        String[] v2 = {"test 2"};
        Property p = new StringProperty();
        p.setName("test");
        p.setValue("2");
        p.setValidators(v1);
        Property p2 = new IntegerProperty();
        Property p3 = new StringProperty();
        p3.setName("test");
        Property p4 = new StringProperty();
        p4.setName("test 2");
        Property p5 = new StringProperty();
        p5.setName("test");
        p5.setValue("3");
        Property p6 = new StringProperty();
        p6.setName("test");
        p6.setValue("2");
        Property p7 = new StringProperty();
        p7.setName("test");
        p7.setValue("2");
        p7.setValidators(v1);
        assertFalse(p.equals(null));
        assertFalse(p.equals(p2));
        assertFalse(p.equals(p3));
        assertFalse(p.equals(p4));
        assertFalse(p.equals(p5));
        assertFalse(p.equals(p6));
        assertTrue(p.equals(p7));
    }
    @Test public void testGetError() {
        Property p = new StringProperty();
        p.setName("test");
        p.setValue("2");
        p.setValidators(null);
        p.getErrorMessage();
    }
}
