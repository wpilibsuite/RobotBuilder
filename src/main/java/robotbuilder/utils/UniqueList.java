
package robotbuilder.utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A variation on {@link ArrayList} where every element is unique.
 *
 * @author Sam Carlberg
 */
public class UniqueList<E> extends ArrayList<E> {

    /**
     * Constructs an empty ArraySet with the default capacity.
     */
    public UniqueList() {
        super();
    }

    /**
     * Constructs an empty ArraySet with the given capacity.
     *
     * @param capacity the starting capacity of the ArraySet.
     */
    public UniqueList(int capacity) {
        super(capacity);
    }

    @Override
    public E[] toArray() {
        E[] clone = (E[]) new Object[size()];
        System.arraycopy(super.toArray(), 0, clone, 0, size());
        return clone;
    }

    @Override
    public boolean add(E e) {
        if (contains(e)) {
            return false;
        }
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        if (contains(element)) {
            return;
        }
        super.add(index, element);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (!contains(e)) {
                add(index, e);
                index++;
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            return false;
        }
        int oldSize = size();
        c.forEach(this::add);
        return size() > oldSize;
    }

}
