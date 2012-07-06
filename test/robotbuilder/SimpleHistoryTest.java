/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Sam
 */
public class SimpleHistoryTest {
    
    SimpleHistory<Integer> history;
    
    public SimpleHistoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        history = new SimpleHistory<Integer>();
    }
    
    @After
    public void tearDown() {
    }
    @Test
    public void add() {
        for(Integer i = 0; i < 20; i++) {
            history.addState(i);
            assertEquals("Unexpected value "+i, history.getCurrentState(), i);
        }
    }
    
    @Test
    public void undoRedo() {
        history.addState(1);
        history.addState(2);
        Integer curr = history.getCurrentState();
        history.undo();
        assertEquals("Undo didn't work.  ", curr, history.redo());
    }
    
    @Test
    public void undo() {
        history.addState(1);
        history.addState(2);
        history.undo();
        assertEquals("Undo failed. ", history.getCurrentState(), (Integer) 1);
    }
    
    @Test
    public void undoThenAdd() {
        System.out.println("UNDO THEN ADD");
        for(Integer i = 0; i <= 20; i++) {
            history.addState(i);
            assertEquals("Adding failed. Value ", history.getCurrentState(), i);
        }
        history.undo();
        history.addState(65535);
        assertEquals("Conflict in the future. Value ", (Integer)65535, history.redo());
        System.out.println("Current state: "+history.getCurrentState());
    }
}
