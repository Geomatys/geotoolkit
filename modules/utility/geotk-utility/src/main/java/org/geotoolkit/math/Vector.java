/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.math;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.RandomAccess;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.collection.CheckedCollection;


/**
 * A vector of real numbers. An instance of {@code Vector} can be a wrapper around an
 * array of Java primitive type (typically {@code float[]} or {@code double[]}), or it
 * may be a function calculating values on the fly. Often the two above-cited cases are
 * used together, for example in a time series where:
 * <p>
 * <ul>
 *   <li><var>x</var>[<var>i</var>] is a linear function of <var>i</var>
 *       (e.g. the sampling time of measurements performed at a fixed time interval)</li>
 *   <li><var>y</var>[<var>i</var>] is the measurement of a phenomenon at time
 *       <var>x</var>[<var>i</var>].</li>
 * </ul>
 * <p>
 * Instances of {@code Vector} are created by calls to the {@link #create(Object)} static method.
 * The supplied array is not cloned - changes to the primitive array are reflected in the vector,
 * and vis-versa.
 * <p>
 * Vectors can be created over a subrange of an array, provides a view of the elements in reverse
 * order, <cite>etc.</cite>. The example below creates a view over a subrange:
 *
 * {@preformat java
 *     Vector v = Vector.create(array).subList(lower, upper)
 * }
 *
 * @author Martin Desruisseaux (MPO, Geomatys)
 * @version 3.0
 *
 * @since 1.0
 * @module
 */
public abstract class Vector extends AbstractList<Number> implements CheckedCollection<Number>, RandomAccess {
    /**
     * Wraps the given object in a vector. The argument should be one of the following:
     * <p>
     * <ul>
     *   <li>An array of a primitive type, like {@code float[]}.</li>
     *   <li>An array of a wrapper type, like <code>{@linkplain Float}[]</code>.</li>
     *   <li>A {@code Vector}, in which case it is returned unchanged.</li>
     *   <li>The {@code null} value, in which case {@code null} is returned.</li>
     * </ul>
     * <p>
     * The argument is not cloned. Consequently changes in the underlying array are reflected
     * in this vector, and vis-versa.
     *
     * @param  array The object to wrap in a vector, or {@code null}.
     * @return The given object wrapped in a vector, or {@code null} if the argument was {@code null}.
     * @throws IllegalArgumentException If the type of the given object is not recognized by the method.
     */
    public static Vector create(final Object array) throws IllegalArgumentException {
        if (array == null || array instanceof Vector) {
            return (Vector) array;
        }
        final Class<?> type = array.getClass();
        Class<?> component = type.getComponentType();
        if (component != null) {
            component = Classes.primitiveToWrapper(component);
            if (Number.class.isAssignableFrom(component)) {
                return new ArrayVector(array);
            }
        }
        throw new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOW_TYPE_$1,
                (component != null) ? component : type));
    }

    /**
     * Creates a sequence of numbers in the given range of values using the given increment.
     * The {@linkplain #getElementType() element type} will be the smallest type that can be
     * used for storing every values. For example it will be {@code Byte.class} for the range
     * [100:1:120] but will be {@code Double.class} for the range [0:0.1:1].
     *
     * @param  first     The first value, inclusive.
     * @param  increment The difference between the values at two adjacent indexes.
     * @param  limit     The last value, <strong>exclusive</strong>.
     * @return The given sequence as a vector.
     */
    public static Vector createSequence(final double first, final double increment, final double limit) {
        return new SequenceVector(first, increment, limit);
    }

    /**
     * For subclasses constructor.
     */
    protected Vector() {
    }

    /**
     * Returns the number of elements in this vector.
     *
     * @return The number of elements in this vector.
     */
    @Override
    public abstract int size();

    /**
     * Returns the type of elements in this vector. If this vector is backed by an array of a
     * primitive type, then this method returns the <em>wrapper</em> class, not the primitive
     * type. For example if this vector is backed by an array of type {@code float[]}, then
     * this method returns {@code Float.class}, not {@link Float#TYPE}.
     */
    @Override
    public abstract Class<? extends Number> getElementType();

    /**
     * Returns {@code true} if the value at the given index is {@code NaN}.
     *
     * @param  index The index in the [0 &hellip; {@linkplain #size size}-1] range.
     * @return {@code true} if the value at the given index is {@code NaN}.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     */
    public abstract boolean isNaN(final int index) throws IndexOutOfBoundsException;

    /**
     * Returns the value at the given index as a {@code double}.
     *
     * @param  index The index in the [0 &hellip; {@linkplain #size size}-1] range.
     * @return The value at the given index.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     * @throws ClassCastException if the component type can not be converted to a
     *         {@code double} by an identity or widening conversion.
     */
    public abstract double doubleValue(final int index) throws IndexOutOfBoundsException, ClassCastException;

    /**
     * Returns the value at the given index as a {@code float}.
     * If this vector uses internally a wider type like {@code double}, then this method may
     * cast the value or throw an exception at implementation choice. The general guidelines
     * are:
     * <p>
     * <ul>
     *   <li>If the value is read from a primitive array of a wider type (typically through a
     *       vector created by {@link #create(Object)}, throw an exception because we assume
     *       that the data provider thinks that the extra precision is needed.</li>
     *   <li>If the value is the result of a computation (typically through a vector created
     *       by {@link #createSequence}), cast the value because the calculation accuracy is
     *       often unknown to the vector - and not necessarly its job.</li>
     * </ul>
     *
     * @param  index The index in the [0 &hellip; {@linkplain #size size}-1] range.
     * @return The value at the given index.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     * @throws ClassCastException if the component type can not be converted to a
     *         {@code float} by an identity or widening conversion.
     */
    public abstract float floatValue(final int index) throws IndexOutOfBoundsException, ClassCastException;

    /**
     * Returns the value at the given index as a {@code long}.
     * If this vector uses internally a wider type, then this method may cast the value
     * or throw an exception according the same guidlines than {@link #floatValue}.
     *
     * @param  index The index in the [0 &hellip; {@linkplain #size size}-1] range.
     * @return The value at the given index.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     * @throws ClassCastException if the component type can not be converted to a
     *         {@code long} by an identity or widening conversion.
     */
    public abstract long longValue(final int index) throws IndexOutOfBoundsException, ClassCastException;

    /**
     * Returns the value at the given index as an {@code int}.
     * If this vector uses internally a wider type, then this method may cast the value
     * or throw an exception according the same guidlines than {@link #floatValue}.
     *
     * @param  index The index in the [0 &hellip; {@linkplain #size size}-1] range.
     * @return The value at the given index.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     * @throws ClassCastException if the component type can not be converted to an
     *         {@code int} by an identity or widening conversion.
     */
    public abstract int intValue(final int index) throws IndexOutOfBoundsException, ClassCastException;

    /**
     * Returns the value at the given index as a {@code short}.
     * If this vector uses internally a wider type, then this method may cast the value
     * or throw an exception according the same guidlines than {@link #floatValue}.
     *
     * @param  index The index in the [0 &hellip; {@linkplain #size size}-1] range.
     * @return The value at the given index.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     * @throws ClassCastException if the component type can not be converted to a
     *         {@code short} by an identity or widening conversion.
     */
    public abstract short shortValue(final int index) throws IndexOutOfBoundsException, ClassCastException;

    /**
     * Returns the value at the given index as a {@code byte}.
     * If this vector uses internally a wider type, then this method may cast the value
     * or throw an exception according the same guidlines than {@link #floatValue}.
     *
     * @param  index The index in the [0 &hellip; {@linkplain #size size}-1] range.
     * @return The value at the given index.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     * @throws ClassCastException if the component type can not be converted to a
     *         {@code byte} by an identity or widening conversion.
     */
    public abstract byte byteValue(final int index) throws IndexOutOfBoundsException, ClassCastException;

    /**
     * Returns the number at the given index, or {@code null} if none.
     *
     * @param  index The index in the [0 &hellip; {@linkplain #size size}-1] range.
     * @return The value at the given index.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     */
    @Override
    public abstract Number get(final int index) throws IndexOutOfBoundsException;

    /**
     * Sets the number at the given index.
     *
     * @param  index The index in the [0 &hellip; {@linkplain #size size}-1] range.
     * @param  value The value to set at the given index.
     * @return The value previously stored at the given index.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     * @throws ArrayStoreException if the given value can not be stored in this vector.
     */
    @Override
    public abstract Number set(final int index, final Number value)
            throws IndexOutOfBoundsException, ArrayStoreException;

    /**
     * Converts an array of indexes used by this vector to the indexes used by the
     * backing vector. If there is no such backing vector, then returns a clone of
     * the given array. This method must also check index validity.
     * <p>
     * Only subclasses that are views of this vector will override this method.
     *
     * @param  index The indexes given by the user.
     * @return The indexes to use. Must be a new array in order to protect it from user changes.
     * @throws IndexOutOfBoundsException if at least one index is out of bounds.
     */
    int[] toBacking(int[] index) throws IndexOutOfBoundsException {
        index = index.clone();
        final int length = size();
        for (int i : index) {
            if (i < 0 || i >= length) {
                throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, i));
            }
        }
        return index;
    }

    /**
     * Returns a view which contains the values of this vector at the given indexes.
     * This method does not copy the values, consequently any modification to the
     * values of this vector will be reflected in the returned view and vis-versa.
     *
     * @param  index Index of the values to be returned.
     * @return A view of this vector containing values at the given indexes.
     * @throws IndexOutOfBoundsException if at least one index is out of bounds.
     */
    public Vector subvector(int... index) throws IndexOutOfBoundsException {
        index = toBacking(index);
        int first, limit, step;
        switch (index.length) {
            case 0: {
                first = 0;
                limit = 0;
                step  = 1;
                break;
            }
            case 1: {
                first = index[0];
                limit = first + 1;
                step  = 1;
                break;
            }
            default: {
                first = index[0];
                limit = index[1];
                step  = limit - first;
                for (int i=2; i<index.length; i++) {
                    final int current = index[i];
                    if (current - limit != step) {
                        return createSub(index);
                    }
                    limit = current;
                }
                break;
            }
        }
        return subList(first, step, limit);
    }

    /**
     * Returns a view which contain the values of this vector in the given index range.
     * The returned view will contain the values from index {@code lower} inclusive to
     * {@code upper} exclusive.
     * <p>
     * This method delegates its work to <code>{@linkplain #subList(int,int,int) subList}(lower,
     * 1, upper)</code>. It is declared final in order to force every subclasses to override the
     * later method instead than this one.
     *
     * @param  lower Index of the first value to be included in the returned view.
     * @param  upper Index after the last value to be included in the returned view.
     * @return A view of this vector containing values in the given index range.
     * @throws IndexOutOfBoundsException If an index is outside the
     *         [0 &hellip; {@linkplain #size size}-1] range.
     */
    @Override
    public final Vector subList(final int lower, final int upper) throws IndexOutOfBoundsException {
        return subList(lower, 1, upper);
    }

    /**
     * Returns a view which contain the values of this vector in the given index range.
     * The returned view will contain the values from index {@code first} inclusive to
     * {@code limit} exclusive, with index incremented by the given {@code step} value,
     * which can be negative. More specifically the index <var>i</var> in the returned
     * vector will maps the element at index {@code i*step + first} in this vector.
     * <p>
     * This method does not copy the values. Consequently any modification to the
     * values of this vector will be reflected in the returned view and vis-versa.
     *
     * @param  first Index of the first value to be included in the returned view.
     * @param  step	 The index increment between each values to be returned. Can be positive or
     *               negative, but not zero. If negative, then the values will be returned in
     *               reverse order. In such case {@code first} must be greater than {@code limit}.
     * @param  limit Index after the last value to be included in the returned view.
     * @return A view of this vector containing values in the given index range.
     * @throws ArithmeticException if {@code step} if zero.
     * @throws IndexOutOfBoundsException If an index is outside the
     *         [0 &hellip; {@linkplain #size size}-1] range.
     */
    public Vector subList(final int first, final int step, final int limit)
            throws ArithmeticException, IndexOutOfBoundsException
    {
        if (step == 1 && first == 0 && limit == size()) {
            return this;
        }
        return createSub(first, step, limit);
    }

    /**
     * Implementation of {@link #subList(int[])} to be overriden by subclasses.
     */
    Vector createSub(final int[] index) {
        return new SubVector(index);
    }

    /**
     * Implementation of {@link #subList(int,int,int)} to be overriden by subclasses.
     */
    Vector createSub(final int first, final int step, final int limit) {
        return new SubList(first, step, limit);
    }

    /**
     * A view over an other vector at pre-selected indexes.
     *
     * @author Martin Desruisseaux (MPO, Geomatys)
     * @version 3.0
     *
     * @since 1.0
     * @module
     */
    private final class SubVector extends Vector implements Serializable {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 6574040261355090760L;

        /**
         * The pre-selected indexes.
         */
        private final int[] index;

        /**
         * Creates a new view over the values at the given indexes. This constructor
         * does not clone the array; it is caller responsability to clone it if needed.
         */
        public SubVector(int[] index) {
            this.index = index;
        }

        /** Returns the indexes where to look for the value in the enclosing vector. */
        @Override int[] toBacking(final int[] i) throws IndexOutOfBoundsException {
            final int[] ni = new int[i.length];
            for (int j=0; j<ni.length; j++) {
                ni[j] = index[i[j]];
            }
            return ni;
        }

        /** Returns the type of elements in this vector. */
        @Override public Class<? extends Number> getElementType() {
            return Vector.this.getElementType();
        }

        /** Returns the length of this view. */
        @Override public int size() {
            return index.length;
        }

        /** Delegates to the enclosing vector. */
        @Override public boolean isNaN(final int i) {
            return Vector.this.isNaN(index[i]);
        }

        /** Delegates to the enclosing vector. */
        @Override public double doubleValue(final int i) {
            return Vector.this.doubleValue(index[i]);
        }

        /** Delegates to the enclosing vector. */
        @Override public float floatValue(final int i) {
            return Vector.this.floatValue(index[i]);
        }

        /** Delegates to the enclosing vector. */
        @Override public long longValue(final int i) {
            return Vector.this.longValue(index[i]);
        }

        /** Delegates to the enclosing vector. */
        @Override public int intValue(final int i) {
            return Vector.this.intValue(index[i]);
        }

        /** Delegates to the enclosing vector. */
        @Override public short shortValue(final int i) {
            return Vector.this.shortValue(index[i]);
        }

        /** Delegates to the enclosing vector. */
        @Override public byte byteValue(final int i) {
            return Vector.this.byteValue(index[i]);
        }

        /** Delegates to the enclosing vector. */
        @Override public Number get(final int i) {
            return Vector.this.get(index[i]);
        }

        /** Delegates to the enclosing vector. */
        @Override public Number set(final int i, final Number value) {
            return Vector.this.set(index[i], value);
        }

        /** Delegates to the enclosing vector. */
        @Override Vector createSub(int[] index) {
            return Vector.this.createSub(index);
        }

        /** Delegates to the enclosing vector. */
        @Override Vector createSub(int first, final int step, final int limit) {
            final int ni[] = new int[length(first, step, limit)];
            for (int j=0; j<ni.length; j++) {
                ni[j] = index[first];
                first += step;
            }
            return Vector.this.subvector(ni);
        }
    }

    /**
     * Return the length of a subvector in the given range with the given step.
     * If the result is an invalid length, format an error message assuming that
     * the given range in invalid.
     */
    static int length(final int first, final int step, final int limit) {
        final int length = (limit - first) / step;
        if (length < 0) {
            final int key;
            final Object arg1, arg2;
            if (step == 1) {
                key  = Errors.Keys.BAD_RANGE_$2;
                arg1 = first;
                arg2 = limit;
            } else {
                key  = Errors.Keys.ILLEGAL_ARGUMENT_$2;
                arg1 = "range";
                arg2 = "[" + first + ':' + step + ':' + limit + ']';
            }
            throw new IllegalArgumentException(Errors.format(key, arg1, arg2));
        }
        return length;
    }

    /**
     * A view over an other vector in a range of index.
     *
     * @author Martin Desruisseaux (MPO, Geomatys)
     * @version 3.0
     *
     * @since 1.0
     * @module
     */
    private final class SubList extends Vector implements Serializable {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 7641036842053528486L;

        /**
         * Index of the first element in the enclosing vector.
         */
        private final int first;

        /**
         * The index increment. May be negative but not zero.
         */
        private final int step;

        /**
         * The length of this vector.
         */
        private final int length;

        /**
         * Creates a new view over the given range.
         */
        protected SubList(final int first, final int step, final int limit) {
            this.first  = first;
            this.step   = step;
            this.length = length(first, step, limit);
        }

        /** Returns the index where to look for the value in the enclosing vector. */
        private int toBacking(final int index) throws IndexOutOfBoundsException {
            if (index < 0 || index >= length) {
                throw new IndexOutOfBoundsException(Errors.format(
                        Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, index));
            }
            return index*step + first;
        }

        /** Returns the index where to look for the value in the enclosing vector. */
        @Override int[] toBacking(final int[] index) throws IndexOutOfBoundsException {
            final int[] ni = new int[index.length];
            for (int j=0; j<ni.length; j++) {
                ni[j] = toBacking(index[j]);
            }
            return ni;
        }

        /** Returns the type of elements in this vector. */
        @Override public Class<? extends Number> getElementType() {
            return Vector.this.getElementType();
        }

        /** Returns the length of this subvector. */
        @Override public int size() {
            return length;
        }

        /** Delegates to the enclosing vector. */
        @Override public boolean isNaN(final int index) {
            return Vector.this.isNaN(toBacking(index));
        }

        /** Delegates to the enclosing vector. */
        @Override public double doubleValue(final int index) {
            return Vector.this.doubleValue(toBacking(index));
        }

        /** Delegates to the enclosing vector. */
        @Override public float floatValue(final int index) {
            return Vector.this.floatValue(toBacking(index));
        }

        /** Delegates to the enclosing vector. */
        @Override public long longValue(final int index) {
            return Vector.this.longValue(toBacking(index));
        }

        /** Delegates to the enclosing vector. */
        @Override public int intValue(final int index) {
            return Vector.this.intValue(toBacking(index));
        }

        /** Delegates to the enclosing vector. */
        @Override public short shortValue(final int index) {
            return Vector.this.shortValue(toBacking(index));
        }

        /** Delegates to the enclosing vector. */
        @Override public byte byteValue(final int index) {
            return Vector.this.byteValue(toBacking(index));
        }

        /** Delegates to the enclosing vector. */
        @Override public Number get(final int index) {
            return Vector.this.get(toBacking(index));
        }

        /** Delegates to the enclosing vector. */
        @Override public Number set(final int index, final Number value) {
            return Vector.this.set(toBacking(index), value);
        }

        /** Delegates to the enclosing vector. */
        @Override Vector createSub(int[] index) {
            return Vector.this.createSub(index);
        }

        /** Delegates to the enclosing vector. */
        @Override Vector createSub(int first, int step, int limit) {
            first = toBacking(first);
            step *= this.step;
            limit = toBacking(limit);
            return Vector.this.subList(first, step, limit);
        }
    }
}
