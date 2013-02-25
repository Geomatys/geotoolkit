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
import org.apache.sis.util.Numbers;


/**
 * A vector which is the concatenation of two other vectors.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class ConcatenatedVector extends Vector implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 4639375525939012394L;

    /**
     * The vectors to concatenate.
     */
    private final Vector first, second;

    /**
     * The length of the first vector.
     */
    private final int limit;

    /**
     * Creates a concatenated vector.
     *
     * @param first The vector for the lower indices.
     * @param second The vector for the higher indices.
     */
    public ConcatenatedVector(final Vector first, final Vector second) {
        this.first  = first;
        this.second = second;
        this.limit  = first.size();
    }

    /**
     * Returns widest type of the two vectors.
     */
    @Override
    public Class<? extends Number> getElementType() {
        return Numbers.widestClass(first.getElementType(), second.getElementType());
    }

    /**
     * Returns the length of this vector.
     */
    @Override
    public int size() {
        return limit + second.size();
    }

    /**
     * Returns {@code true} if the value at the given index is {@code NaN}.
     */
    @Override
    public boolean isNaN(int index) throws IndexOutOfBoundsException {
        final Vector v;
        if (index < limit) {
            v = first;
        } else {
            v = second;
            index -= limit;
        }
        return v.isNaN(index);
    }

    /**
     * Returns the value at the given index.
     */
    @Override
    public double doubleValue(int index) {
        final Vector v;
        if (index < limit) {
            v = first;
        } else {
            v = second;
            index -= limit;
        }
        return v.doubleValue(index);
    }

    /**
     * Returns the value at the given index.
     */
    @Override
    public float floatValue(int index) {
        final Vector v;
        if (index < limit) {
            v = first;
        } else {
            v = second;
            index -= limit;
        }
        return v.floatValue(index);
    }

    /**
     * Returns the value at the given index.
     */
    @Override
    public long longValue(int index) {
        final Vector v;
        if (index < limit) {
            v = first;
        } else {
            v = second;
            index -= limit;
        }
        return v.longValue(index);
    }

    /**
     * Returns the value at the given index.
     */
    @Override
    public int intValue(int index) {
        final Vector v;
        if (index < limit) {
            v = first;
        } else {
            v = second;
            index -= limit;
        }
        return v.intValue(index);
    }

    /**
     * Returns the value at the given index.
     */
    @Override
    public short shortValue(int index) {
        final Vector v;
        if (index < limit) {
            v = first;
        } else {
            v = second;
            index -= limit;
        }
        return v.shortValue(index);
    }

    /**
     * Returns the value at the given index.
     */
    @Override
    public byte byteValue(int index) {
        final Vector v;
        if (index < limit) {
            v = first;
        } else {
            v = second;
            index -= limit;
        }
        return v.byteValue(index);
    }

    /**
     * Returns the value at the given index.
     *
     * @throws ArrayIndexOutOfBoundsException if the given index is out of bounds.
     */
    @Override
    public Number get(int index) {
        final Vector v;
        if (index < limit) {
            v = first;
        } else {
            v = second;
            index -= limit;
        }
        return v.get(index);
    }

    /**
     * Sets the value at the given index.
     */
    @Override
    public Number set(int index, final Number value) {
        final Vector v;
        if (index < limit) {
            v = first;
        } else {
            v = second;
            index -= limit;
        }
        return v.set(index, value);
    }

    /**
     * Delegates to the backing vectors if possible.
     */
    @Override
    Vector createSubList(final int first, final int step, final int length) {
        if (first >= limit) {
            return second.subList(first - limit, step, length);
        }
        if (first + step*length <= limit) {
            return this.first.subList(first, step, length);
        }
        return super.createSubList(first, step, length);
    }

    /**
     * Delegates to the backing vectors since there is a chance that they overloaded
     * their {@code concatenate} method with a more effective implementation.
     */
    @Override
    Vector createConcatenate(final Vector toAppend) {
        return first.concatenate(second.concatenate(toAppend));
    }
}
