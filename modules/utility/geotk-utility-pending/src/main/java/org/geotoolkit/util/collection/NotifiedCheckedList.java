/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import org.geotoolkit.util.NumberRange;

/**
 * Abstract synchronized list that define notify methods called when
 * objects are added or removed.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class NotifiedCheckedList<E> extends CheckedArrayList<E>{
    
    public NotifiedCheckedList(Class<E> type) {
        super(type);
    }
    
    public NotifiedCheckedList(Class<E> type, int capacity) {
        super(type,capacity);
    }

    protected abstract void notifyAdd(final E item, int index);
    
    protected abstract void notifyAdd(final Collection<? extends E> items, NumberRange<Integer> range);
    
    protected abstract void notifyRemove(final E item, int index);
    
    protected abstract void notifyRemove(final Collection<? extends E> items, NumberRange<Integer> range);
        
    @Override
    public boolean add(final E element) throws IllegalArgumentException, UnsupportedOperationException {
        if(element == null) return false;
        final boolean added = super.add(element);
        if (added) {
            final int index = super.size() - 1;
            notifyAdd(element, index);
        }
        return added;
    }

    @Override
    public void add(final int index, final E element) throws IllegalArgumentException, UnsupportedOperationException {
        super.add(index, element);
        notifyAdd(element, index);
    }

    @Override
    public boolean addAll(final Collection<? extends E> collection) throws IllegalArgumentException, UnsupportedOperationException {
        final int startIndex = super.size();
        final boolean added = super.addAll(collection);
        if (added) {
            notifyAdd(collection, NumberRange.create(startIndex, super.size()-1) );
        }
        return added;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> collection) throws IllegalArgumentException, UnsupportedOperationException {
        final boolean added = super.addAll(index, collection);
        if (added) {
            notifyAdd(collection, NumberRange.create(index, index + collection.size()) );
        }
        return added;
    }

    @Override
    public boolean remove(final Object o) throws UnsupportedOperationException {
        final int index = super.indexOf(o);
        if (index >= 0) {
            super.remove(index);
            notifyRemove(super.getElementType().cast(o), index );
            return true;
        }
        return false;
    }

    @Override
    public E remove(final int index) throws UnsupportedOperationException {
        final E removed = super.remove(index);
        notifyRemove(removed, index );
        return removed;
    }

    @Override
    public boolean removeAll(final Collection<?> c) throws UnsupportedOperationException {
        //TODO handle remove by collection events if possible
        // to avoid several calls to remove
        boolean valid = false;
        for(final Object i : c){
            final boolean val = remove(i);
            if(val) valid = val;
        }
        return valid;
    }

    @Override
    public void clear() throws UnsupportedOperationException {
        if(!isEmpty()){
            final Collection<E> copy = new ArrayList<E>(this);
            final NumberRange<Integer> range = NumberRange.create(0, copy.size()-1);
            super.clear();
            notifyRemove(copy, range);
        }
    }
    
    
    
        
}
