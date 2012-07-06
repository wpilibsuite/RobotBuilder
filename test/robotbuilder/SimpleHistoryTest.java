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
        history = new SimpleHistory<Integer>(1000);
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
    
    @Test
    public void undoRedoAll() {
        for(Integer i = 0; i <= 20; i++) {
            history.addState(i);
            assertEquals("Adding failed. Value ", history.getCurrentState(), i);
        }
        
        System.out.println("Current state after adding: "+history.getCurrentState());
        System.out.println("------------------------------\n"+
                           "             UNDO             \n"+
                           "------------------------------");
        
        for(Integer i = history.getLast(); i != history.getFirst() && history.getFirst() != null; i = history.undo()) {
            System.out.print("Last: "+history.getLast());
            System.out.print("\tCurrent state: "+history.getCurrentState());
            System.out.println("\tFirst: "+history.getFirst());
            
            assertEquals("Undo failed. Value ", i, history.getCurrentState());
        }
        
        System.out.println("Current state after undoing: "+history.getCurrentState()+"\n");
        
        System.out.println("------------------------------\n"+
                           "             REDO             \n"+
                           "------------------------------");
        
        for(Integer i = history.getCurrentState(); i != history.getLast() && history.getLast() != null; i = history.redo()) {
            System.out.print("Last: "+history.getLast());
            System.out.print("\tCurrent state: "+history.getCurrentState());
            System.out.println("\tFirst: "+history.getFirst());
            
            assertEquals("Redo failed. Value ", i, history.getCurrentState());
        }
        
        assertEquals("Unequal values after undoing and redoing. Value ", (Integer)20, history.getCurrentState());
        System.out.println("Current state: "+history.getCurrentState()+"\n\n");
        
    }
    
    
}
