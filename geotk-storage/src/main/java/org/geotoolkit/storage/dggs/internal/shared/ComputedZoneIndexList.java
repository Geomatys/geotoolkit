/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.storage.dggs.internal.shared;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.LongFunction;

/**
 * A compressed zone identifer list.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ComputedZoneIndexList extends AbstractList<Object> {

    private final long start;
    private final long step;
    private final int count;
    private final LongFunction<Object> toId;

    public ComputedZoneIndexList(long start, long step, int count, LongFunction<Object> toId) {
        this.start = start;
        this.step = step;
        this.count = count;
        this.toId = toId;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<Object> iterator() {
        return listIterator();
    }

    @Override
    public Object get(int index) {
        if (index < 0 || index >= count) throw new IndexOutOfBoundsException();
        return toId.apply(start + index * step);
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof Integer i) {
            long s = ((long) i - start) / step;
            if (s < 0 || s >= count) return -1;
            return (int) s;
        } else if (o instanceof Long i) {
            long s = (i - start) / step;
            if (s < 0 || s >= count) return -1;
            return (int) s;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return indexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<Object> listIterator(int i) {
        final Ite ite = new Ite();
        ite.idx = i-1;
        return ite;
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        final int nb = toIndex - fromIndex;
        return new ComputedZoneIndexList(start + step*fromIndex, step, nb, toId);
    }

    private class Ite implements ListIterator<Object> {

        private int idx = -1;

        @Override
        public boolean hasNext() {
            return idx < count;
        }

        @Override
        public Object next() {
            if (idx == count) throw new NoSuchElementException();
            idx++;
            return toId.apply(start + idx*step);
        }

        @Override
        public boolean hasPrevious() {
            return idx > 0;
        }

        @Override
        public Object previous() {
            if (idx == 0) throw new NoSuchElementException();
            idx--;
            return toId.apply(start + idx*step);
        }

        @Override
        public int nextIndex() {
            return idx + 1;
        }

        @Override
        public int previousIndex() {
            return idx - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void set(Object e) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void add(Object e) {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

}
