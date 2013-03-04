/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.math;

import java.io.Serializable;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.Numbers.*;
import static org.apache.sis.util.ArgumentChecks.ensureValidIndex;


/**
 * A vector which is a sequence of numbers.
 *
 * @author Martin Desruisseaux (MPO, Geomatys)
 * @version 3.00
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
     * The element type, or {@code null} if values are NaN.
     */
    private final Class<? extends Number> type;

    /**
     * The value at index 0.
     */
    private final double first;

    /**
     * The difference between the values at two adjacent indexes.
     */
    private final double increment;

    /**
     * The length of this vector.
     */
    private final int length;

    /**
     * Creates a sequence of numbers in a given range of values using the given increment.
     *
     * @param first     The first value, inclusive.
     * @param increment The difference between the values at two adjacent indexes.
     * @param length    The length of the vector.
     */
    public SequenceVector(final double first, final double increment, final int length) {
        if (length < 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$1, "length", length));
        }
        this.first     = first;
        this.increment = increment;
        this.length    = length;
        if (Double.isNaN(first) || Double.isNaN(increment)) {
            type = null;
        } else {
            Class<? extends Number> t = narrowestClass(first);
            t = widestClass(t,  narrowestClass(first + increment));
            t = widestClass(t,  narrowestClass(first + increment*(length-1)));
            type = t;
        }
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
        // Float is the smallest type capable to hold NaN.
        return (type != null) ? type : Float.class;
    }

    /**
     * Returns {@code true} if this vector returns {@code NaN} values.
     */
    @Override
    public boolean isNaN(final int index) {
        return type == null;
    }

    /**
     * Computes the value at the given index.
     */
    @Override
    public double doubleValue(final int index) throws IndexOutOfBoundsException {
        ensureValidIndex(length, index);
        return first + increment*index;
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
    Vector createSubList(final int first, final int step, final int length) {
        return new SequenceVector(doubleValue(first), increment*step, length);
    }
}
