
package robotbuilder;

import java.util.Deque;
import java.util.LinkedList;

/**
 * A helper class used to keep track of changes to the {@link RobotTree}
 * and undo or redo them as necessary.
 * @author Sam
 */
public class SimpleHistory<E> {
    
    private Deque<E> pastAndPresent = new LinkedList<E>();
    private Deque<E> future = new LinkedList<E>();
    private int timesSaved = 0;
    
    public SimpleHistory(){}    
        
    /**
     * Adds the given state to the history.
     * @param state The state to add to the history.
     */
    public void addState(E state){
        if(state != null && state.getClass() != null){
            pastAndPresent.addLast(state);
            future.clear();
            timesSaved++;
        }
    }

    /**
     * Gets the current state of the history.
     * @return 
     */
    public E getCurrentState(){
        return pastAndPresent.peekLast();
    }
    
    /**
     * Undoes an action, if possible.
     */
    public E undo(){
        if(canUndo()){
            future.addLast(pastAndPresent.pollLast());
        }
        return getCurrentState();
    }

    /**
     * Redoes an undo, if possible.
     */
    public E redo(){
        if(canRedo()){
            pastAndPresent.addLast(future.pollLast());
        }
        return getCurrentState();
    }
    
    public int getUndoSize(){
        return pastAndPresent.size();
    }
    
    public int getRedoSize(){
        return future.size();
    }
    
    /**
     * Marks if it is possible to undo an action. Essentially,
     * if the undo list is empty, this will return false.
     * @return True if it is possible to undo, else false.
     */
    private boolean canUndo(){
        return !pastAndPresent.isEmpty() && pastAndPresent.size() > 0;
    }

    /**
     * Marks if it is possible to redo an action. Essentially,
     * if the redo list is empty, this will return false.
     * @return True if it is possible to redo, else false.
     */
    private boolean canRedo(){
        return !future.isEmpty() && future.size() >= 0;
    }
    
    /**
     * Clears the redo list.
     */
    public void forgetFuture(){
        future.clear();
    }
}
