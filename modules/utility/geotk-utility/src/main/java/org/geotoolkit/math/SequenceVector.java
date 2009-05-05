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
import org.geotoolkit.resources.Errors;


/**
 * A vector which is a sequence of numbers.
 *
 * @author Martin Desruisseaux (MPO, Geomatys)
 * @version 3.0
 *
 * @since 1.0
 * @module
 */
final class SequenceVector extends Vector implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7980737287789566091L;

    /**
     * The value at index 0.
     */
    private final double first;

    /**
     * The difference between the values at two adjacent indexes.
     */
    private final double step;

    /**
     * The length of this vector.
     */
    private final int length;

    /**
     * Creates a sequence for the given values.
     *
     * @param first The first value, inclusive.
     * @param step  The difference between the values at two adjacent indexes.
     * @param last  The last value, inclusive.
     */
    public SequenceVector(final double first, final double step, final double last) {
        this.first = first;
        this.step  = step;
        length = Math.max(0, (int) ((last - first) / step + (1 + 1E-9)));
    }

    /**
     * Returns the vector size.
     */
    @Override
    public int size() {
        return length;
    }

    /**
     * Returns the type of elements.
     */
    @Override
    public Class<? extends Number> getElementType() {
        return Double.class;
    }

    /**
     * Returns {@code true} if this vector returns {@code NaN} values.
     */
    @Override
    public boolean isNaN(final int index) throws IndexOutOfBoundsException {
        return Double.isNaN(doubleValue(index));
    }

    /**
     * Computes the value at the given index.
     */
    @Override
    public double doubleValue(final int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, index));
        }
        return first + step*index;
    }

    /**
     * Computes the value at the given index.
     */
    @Override
    public float floatValue(final int index) throws IndexOutOfBoundsException {
        return (float) doubleValue(index);
    }

    /**
     * Computes the value at the given index.
     */
    @Override
    public long longValue(final int index) throws IndexOutOfBoundsException {
        return Math.round(doubleValue(index));
    }

    /**
     * Computes the value at the given index.
     */
    @Override
    public int intValue(final int index) throws IndexOutOfBoundsException {
        return (int) longValue(index);
    }

    /**
     * Computes the value at the given index.
     */
    @Override
    public short shortValue(final int index) throws IndexOutOfBoundsException {
        return (short) longValue(index);
    }

    /**
     * Computes the value at the given index.
     */
    @Override
    public byte byteValue(final int index) throws IndexOutOfBoundsException {
        return (byte) longValue(index);
    }

    /**
     * Computes the value at the given index.
     */
    @Override
    public Number get(final int index) throws IndexOutOfBoundsException {
        return doubleValue(index);
    }

    /**
     * Unsupported operation since this vector is not modifiable.
     */
    @Override
    public Number set(final int index, final Number value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new sequence.
     */
    @Override
    Vector createSub(final int first, final int step, final int last) {
        return new SequenceVector(doubleValue(first), this.step*step, doubleValue(last));
    }
}
