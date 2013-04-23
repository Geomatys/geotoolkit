/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.util.collection;

import java.io.Serializable;
import java.util.AbstractList;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.NullArgumentException;

import static org.apache.sis.util.ArgumentChecks.ensureValidIndex;


/**
 * An unmodifiable view of an array. Invoking
 *
 * {@preformat java
 *     UnmodifiableArrayList.wrap(array);
 * }
 *
 * is equivalent to
 *
 * {@preformat java
 *     Collections.unmodifiableList(Arrays.asList(array));
 * }
 *
 * But this class uses one less level of indirection.
 *
 * @param <E> The type of elements in the list.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 *
 * @deprecated Replaced by {@link org.apache.sis.util.collection.UnmodifiableArrayList}.
 */
@Deprecated
public class UnmodifiableArrayList<E> extends AbstractList<E>
        implements CheckedCollection<E>, org.apache.sis.util.collection.CheckedContainer<E>, Serializable
{
    /**
     * For compatibility with different versions.
     */
    private static final long serialVersionUID = -3605810209653785967L;

    /**
     * The wrapped array.
     */
    private final E[] array;

    /**
     * Creates a new instance of an array list. A direct reference to the given array is retained
     * (i.e. the array is <strong>not</strong> cloned). Consequently the given array should not
     * be modified after construction if this list is intended to be immutable.
     * <p>
     * This constructor is for subclassing only. Users should invoke the {@link #wrap} static
     * factory method, which provides more convenient handling of parameterized types.
     *
     * @param array The array to wrap.
     */
    protected UnmodifiableArrayList(final E... array) {
        if (array == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_1, "array"));
        }
        this.array = array;
    }

    /**
     * Creates a new instance of an array list. A direct reference to the given array is retained
     * (i.e. the array is <strong>not</strong> cloned). Consequently the given array should not
     * be modified after construction if this list is intended to be immutable.
     *
     * @param  <E> The type of elements in the list.
     * @param  array The array to wrap, or {@code null} if none.
     * @return The given array wrapped in an unmodifiable list, or {@code null} if the given
     *         array was null.
     *
     * @since 2.5
     */
    public static <E> UnmodifiableArrayList<E> wrap(final E... array) {
        return (array != null) ? new UnmodifiableArrayList<E>(array) : null;
    }

    /**
     * Creates a new instance of an array list over a subregion of the given array. A direct
     * reference to the given array is retained (i.e. the array is <strong>not</strong> cloned).
     * Consequently the given array should not be modified after construction if this list is
     * intended to be immutable.
     *
     * @param  <E> The type of elements in the list.
     * @param  array The array to wrap.
     * @param  lower low endpoint (inclusive) of the sublist.
     * @param  upper high endpoint (exclusive) of the sublist.
     * @return The given array wrapped in an unmodifiable list.
     * @throws IndexOutOfBoundsException If the lower or upper value are out of bounds.
     *
     * @since 3.00
     */
    public static <E> UnmodifiableArrayList<E> wrap(final E[] array, final int lower, final int upper)
            throws IndexOutOfBoundsException
    {
        if (lower < 0 || upper > array.length || lower > upper) {
            throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.ILLEGAL_RANGE_2, lower, upper));
        }
        if (lower == 0 && upper == array.length) {
            return new UnmodifiableArrayList<E>(array);
        }
        return new UnmodifiableArrayList.SubList<E>(array, lower, upper - lower);
    }

    /**
     * Returns the element type of the wrapped array.
     * The default implementation returns the value of {@link Class#getComponentType()}.
     *
     * @return The type of elements in the list.
     */
    @Override
    @SuppressWarnings("unchecked") // Safe if this instance was created safely with wrap(E[]).
    public Class<E> getElementType() {
        return (Class<E>) array.getClass().getComponentType();
    }

    /**
     * Returns the index of the first valid element.
     * To be overridden by {@link SubList} only.
     */
    int lower() {
        return 0;
    }

    /**
     * Returns the list size.
     */
    @Override
    public int size() {
        return array.length;
    }

    /**
     * Returns the size of the array backing this list. This is the length of the array
     * given to the constructor. It is equal to {@link #size} except if this instance
     * is a {@linkplain #subList sublist}, in which case the value returned by this method
     * is greater than {@code size()}.
     * <p>
     * This method is sometime used as a hint for choosing a {@code UnmodifiableArrayList}
     * instance to keep, given a choice. Note that a greater value is not necessarily more
     * memory consuming, since the backing array may be shared by many sublists.
     *
     * @return The length of the backing array.
     *
     * @since 3.00
     */
    public final int arraySize() {
        return array.length;
    }

    /**
     * Returns the element at the specified index.
     */
    @Override
    public E get(final int index) {
        return array[index];
    }

    /**
     * Returns the index in this list of the first occurrence of the specified
     * element, or -1 if the list does not contain this element.
     *
     * @param object The element to search for.
     * @return The index of the first occurrence of the given object, or {@code -1}.
     */
    @Override
    public int indexOf(final Object object) {
        final int lower = lower();
        final int upper = lower + size();
        if (object == null) {
            for (int i=lower; i<upper; i++) {
                if (array[i] == null) {
                    return i - lower;
                }
            }
        } else {
            for (int i=lower; i<upper; i++) {
                if (object.equals(array[i])) {
                    return i - lower;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index in this list of the last occurrence of the specified
     * element, or -1 if the list does not contain this element.
     *
     * @param object The element to search for.
     * @return The index of the last occurrence of the given object, or {@code -1}.
     */
    @Override
    public int lastIndexOf(final Object object) {
        final int lower = lower();
        int i = lower + size();
        if (object == null) {
            while (--i >= lower) {
                if (array[i] == null) {
                    break;
                }
            }
        } else {
            while (--i >= lower) {
                if (object.equals(array[i])) {
                    break;
                }
            }
        }
        return i - lower;
    }

    /**
     * Returns {@code true} if this collection contains the specified element.
     *
     * @param object The element to check for existence.
     * @return {@code true} if this collection contains the given element.
     */
    @Override
    public boolean contains(final Object object) {
        final int lower = lower();
        int i = lower + size();
        if (object == null) {
            while (--i >= lower) {
                if (array[i] == null) {
                    return true;
                }
            }
        } else {
            while (--i >= lower) {
                if (object.equals(array[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a view of the portion of this list between the specified
     * {@code lower}, inclusive, and {@code upper}, exclusive.
     *
     * @param  lower low endpoint (inclusive) of the sublist.
     * @param  upper high endpoint (exclusive) of the sublist.
     * @return A view of the specified range within this list.
     * @throws IndexOutOfBoundsException If the lower or upper value are out of bounds.
     */
    @Override
    public UnmodifiableArrayList<E> subList(final int lower, final int upper)
            throws IndexOutOfBoundsException
    {
        if (lower < 0 || upper > size() || lower > upper) {
            throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.ILLEGAL_RANGE_2, lower, upper));
        }
        return new SubList<E>(array, lower + lower(), upper - lower);
    }

    /**
     * A view over a portion of {@link UnmodifiableArrayList}.
     *
     * @param <E> The type of elements in the list.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private static final class SubList<E> extends UnmodifiableArrayList<E> {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -6297280390649627532L;

        /**
         * Index of the first element and size of this list.
         */
        private final int lower, size;

        /**
         * Creates a new sublist.
         */
        SubList(final E[] array, final int lower, final int size) {
            super(array);
            this.lower = lower;
            this.size  = size;
        }

        /**
         * Returns the index of the first element.
         */
        @Override
        int lower() {
            return lower;
        }

        /**
         * Returns the size of this list.
         */
        @Override
        public int size() {
            return size;
        }

        /**
         * Returns the element at the given index.
         */
        @Override
        public E get(final int index) {
            ensureValidIndex(size, index);
            return super.get(index + lower);
        }
    }

    /**
     * Compares this list with the given object for equality.
     *
     * @param  object The object to compare with this list.
     * @return {@code true} if the given object is equal to this list.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != this) {
            if (!(object instanceof UnmodifiableArrayList<?>)) {
                return super.equals(object);
            }
            final UnmodifiableArrayList<?> that = (UnmodifiableArrayList<?>) object;
            int size  = this.size();
            if (size != that.size()) {
                return false;
            }
            int i = this.lower();
            int j = that.lower();
            while (--size >= 0) {
                if (!Utilities.equals(this.array[i++], that.array[j++])) {
                    return false;
                }
            }
        }
        return true;
    }
}
