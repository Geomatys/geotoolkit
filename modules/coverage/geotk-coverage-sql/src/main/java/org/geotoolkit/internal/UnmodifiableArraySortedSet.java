/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;


/**
 * An unmodifiable sorted set backed by an array of comparable objects.
 * This class is not public since it assumes that the given array is already
 * sorted and does not contains duplicated values (this is not verified).
 *
 * @param <E> The type of elements in the set.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
public abstract class UnmodifiableArraySortedSet<E> extends AbstractSet<E> implements SortedSet<E>, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6259677334686182111L;

    /**
     * The lower and upper bounds of the array.
     */
    protected final int lower, upper;

    /**
     * Creates a new instance.
     *
     * @param lower The index of the first valid element in the backing array.
     * @param upper The index after the last valid element in the backing array.
     */
    protected UnmodifiableArraySortedSet(final int lower, final int upper) {
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * Returns a new instance backed by the same array but using differnet lower and upper
     * array bounds.
     *
     * @param  lower The index of the first valid element in the backing array.
     * @param  upper The index after the last valid element in the backing array.
     * @return A new instance backed by the same array and using the given bounds.
     */
    protected abstract UnmodifiableArraySortedSet<E> create(final int lower, final int upper);

    /**
     * Returns the element at the given index.
     *
     * @param  i The index where to fetch the element.
     * @return The element at the given index.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    protected abstract E elementAt(int i) throws IndexOutOfBoundsException;

    /**
     * Returns the index of the given element using the appropriate {@code Arrays.binarySearch}
     * method.
     *
     * @param  e The object to search.
     * @return The index of the element, or a value derived from the insertion point as
     *         documented in {@code Arrays.binarySearch}.
     */
    protected abstract int indexOf(Object e);

    /**
     * Returns {@code null} since this implementation assumes that we use the natural ordering.
     */
    @Override
    public Comparator<E> comparator() {
        return null;
    }

    /**
     * Returns the number of elements in this set.
     */
    @Override
    public int size() {
        return upper - lower;
    }

    /**
     * Returns {@code true} if this set contains the given element.
     */
    @Override
    public boolean contains(Object o) {
        final int i = indexOf(o);
        return i >= lower && i < upper;
    }

    /**
     * Returns the first element in this set.
     */
    @Override
    public E first() {
        if (lower == upper) {
            throw new NoSuchElementException();
        }
        return elementAt(lower);
    }

    /**
     * Returns the last element in this set.
     */
    @Override
    public E last() {
        if (lower == upper) {
            throw new NoSuchElementException();
        }
        return elementAt(upper - 1);
    }

    /**
     * Returns an iterator over all elements in the set.
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int index = lower;

            @Override public boolean hasNext() {
                return index < upper;
            }

            @Override public E next() {
                if (index < upper) {
                    return elementAt(index++);
                }
                throw new NoSuchElementException();
            }

            @Override public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns a sub-set from the beginning of this set to the given element (exclusive).
     */
    @Override
    public SortedSet<E> headSet(final Object toElement) {
        int i = indexOf(toElement);
        if (i < 0) i = ~i;
        return (i == upper) ? this : create(0, i);
    }

    /**
     * Returns a sub-set from the given element (inclusive) to the end of this set.
     */
    @Override
    public SortedSet<E> tailSet(final Object fromElement) {
        int i = indexOf(fromElement);
        if (i < 0) i = ~i;
        return (i == lower) ? this : create(i, upper);
    }

    /**
     * Returns a sub-set from the given element (inclusive) to the given one (exclusive).
     */
    @Override
    public SortedSet<E> subSet(final Object fromElement, final Object toElement) {
        int lo = indexOf(fromElement);
        int hi = indexOf(  toElement);
        if (lo < 0) lo = ~lo;
        if (hi < 0) hi = ~hi;
        return (lo == lower && hi == upper) ? this : create(lo, hi);
    }

    /**
     * An unmodifiable sorted set of numbers backed by an array of type {@code double[]}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.10
     *
     * @since 3.10
     * @module
     */
    public static final class Number extends UnmodifiableArraySortedSet<java.lang.Number> {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -8037878367513806431L;

        /**
         * The sorted array of values.
         */
        private final double[] values;

        /**
         * Creates a new instance from the given set.
         *
         * @param numbers The set to copy.
         */
        public Number(final Set<? extends java.lang.Number> numbers) {
            super(0, numbers.size());
            values = new double[upper];
            int i = 0;
            for (final java.lang.Number e : numbers) {
                values[i++] = e.doubleValue();
            }
            Arrays.sort(values);
        }

        /**
         * Creates a new instance wrapping the given array.
         */
        private Number(final double[] values, final int lower, final int upper) {
            super(lower, upper);
            this.values = values;
        }

        /**
         * Creates a new instance wrapping the same array in the given range.
         */
        @Override
        protected UnmodifiableArraySortedSet<java.lang.Number> create(int lower, int upper) {
            return new Number(values, lower, upper);
        }

        /**
         * Returns the element at the given index.
         */
        @Override
        protected java.lang.Number elementAt(int i) throws IndexOutOfBoundsException {
            return java.lang.Double.valueOf(values[i]);
        }

        /**
         * Returns the index of the given element.
         */
        @Override
        protected int indexOf(final Object e) {
            return Arrays.binarySearch(values, lower, upper, ((java.lang.Number) e).doubleValue());
        }
    }

    /**
     * An unmodifiable sorted set of dates backed by an array of type {@code long[]}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.10
     *
     * @since 3.10
     * @module
     */
    public static final class Date extends UnmodifiableArraySortedSet<java.util.Date> {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 6763321681710208337L;

        /**
         * The sorted array of times, in milliseconds since January 1st 1970.
         */
        private final long[] times;

        /**
         * Creates a new instance from the given set.
         *
         * @param dates The set to copy.
         */
        public Date(final Set<? extends java.lang.Number> dates) {
            super(0, dates.size());
            times = new long[upper];
            int i = 0;
            for (final java.lang.Number e : dates) {
                times[i++] = e.longValue();
            }
            Arrays.sort(times);
        }

        /**
         * Creates a new instance wrapping the given array.
         */
        private Date(final long[] times, final int lower, final int upper) {
            super(lower, upper);
            this.times = times;
        }

        /**
         * Creates a new instance wrapping the same array in the given range.
         */
        @Override
        protected UnmodifiableArraySortedSet<java.util.Date> create(int lower, int upper) {
            return new Date(times, lower, upper);
        }

        /**
         * Returns the element at the given index.
         */
        @Override
        protected java.util.Date elementAt(int i) throws IndexOutOfBoundsException {
            return new java.util.Date(times[i]);
        }

        /**
         * Returns the index of the given element.
         */
        @Override
        protected int indexOf(final Object e) {
            return Arrays.binarySearch(times, lower, upper, ((java.util.Date) e).getTime());
        }
    }
}
