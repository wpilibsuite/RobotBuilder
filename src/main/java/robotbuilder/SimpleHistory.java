
package robotbuilder;

import java.util.Deque;
import java.util.LinkedList;

/**
 * A helper class used to keep track of changes to the
 * {@link robotbuilder.robottree.RobotTree} and undo or redo them as necessary.
 *
 * @author Sam Carlberg
 */
public class SimpleHistory<E> {

    private boolean frozen = false;

    private Deque<E> past = new LinkedList<>();
    private E present = null;
    private Deque<E> future = new LinkedList<>();

    /**
     * Adds the given state to the history.
     *
     * @param state The state to add to the history.
     */
    public void addState(E state) {
        if (!frozen && state != null && state.getClass() != null) {
            if (present != null) {
                past.addLast(present);
            }
            present = state;
            forgetFuture();
        }
    }

    /**
     * Gets the current state of the history.
     *
     * @return
     */
    public E getCurrentState() {
        return present;
    }

    /**
     * Undoes an action, if possible.
     */
    public E undo() {
        if (canUndo()) {
            future.addLast(present);
            present = past.pollLast();
        }
        return getCurrentState();
    }

    /**
     * Redoes an undo, if possible.
     */
    public E redo() {
        if (canRedo()) {
            past.addLast(present);
            present = future.pollLast();
        }
        return getCurrentState();
    }

    public int getUndoSize() {
        return past.size();
    }

    public int getRedoSize() {
        return future.size();
    }

    /**
     * Marks if it is possible to undo an action. Essentially, if the undo list
     * is empty, this will return false.
     *
     * @return True if it is possible to undo, else false.
     */
    private boolean canUndo() {
        return !frozen && !past.isEmpty();
    }

    /**
     * Marks if it is possible to redo an action. Essentially, if the redo list
     * is empty, this will return false.
     *
     * @return True if it is possible to redo, else false.
     */
    private boolean canRedo() {
        return !frozen && !future.isEmpty();
    }

    /**
     * Clears the redo list.
     */
    public void forgetFuture() {
        if (!frozen) {
            future.clear();
        }
    }

    /**
     * Freezes undo and redo.
     */
    public void freeze() {
        frozen = true;
    }

    /**
     * Unfreezes undo and redo.
     */
    public void unfreeze() {
        frozen = false;
    }
}
