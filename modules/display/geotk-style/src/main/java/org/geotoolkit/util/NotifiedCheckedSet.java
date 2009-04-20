
package org.geotoolkit.util;

import java.util.Collection;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.collection.CheckedHashSet;

/**
 * Abstract synchronized set that define notify methods called when
 * objects are added or removed.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class NotifiedCheckedSet<E> extends CheckedHashSet<E>{
    
    public NotifiedCheckedSet(Class<E> type) {
        super(type);
    }
    
    public NotifiedCheckedSet(Class<E> type, int capacity) {
        super(type,capacity);
    }

    protected abstract void notifyAdd(final E item, NumberRange<Integer> range);
    
    protected abstract void notifyAdd(final Collection<? extends E> item, NumberRange<Integer> range);
    
    protected abstract void notifyRemove(final E item, NumberRange<Integer> range);
    
    @Override
    public boolean add(final E element) throws IllegalArgumentException, UnsupportedOperationException {
        if(element == null) return false;
        final boolean added = super.add(element);
        if (added) {
            notifyAdd(element, null);
        }
        return added;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) throws IllegalArgumentException, UnsupportedOperationException {
        final boolean added = super.addAll(collection);
        if (added) {
            notifyAdd(collection, null );
        }
        return added;
    }

    @Override
    public boolean remove(Object o) throws UnsupportedOperationException {
        final boolean removed = super.remove(o);
        if (removed) {
            notifyRemove(super.getElementType().cast(o), null );
        }
        return removed;
    }

    @Override
    public boolean removeAll(Collection<?> c) throws UnsupportedOperationException {
        //TODO handle remove by collection events if possible
        // to avoid several calls to remove
        boolean valid = false;
        for(Object i : c){
            final boolean val = remove(i);
            if(val) valid = val;
        }
        return valid;
    }   
        
        
}
