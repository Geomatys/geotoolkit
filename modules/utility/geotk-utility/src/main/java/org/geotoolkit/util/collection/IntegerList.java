/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.util.Cloneable;


/**
 * A list of unsigned integer values. This class packs the values in the minimal amount of bits
 * required for storing unsigned integers of the given {@linkplain #maximalValue maximal value}.
 * <p>
 * This class is <strong>not</strong> thread-safe. Synchronizations (if wanted) are user's
 * responsibility.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 2.5
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.collection.IntegerList}.
 */
@Deprecated
public class IntegerList extends org.apache.sis.util.collection.IntegerList implements Cloneable {
    /**
     * Creates an initially empty list with the given initial capacity.
     *
     * @param initialCapacity The initial capacity.
     * @param maximalValue The maximal value to be allowed, inclusive.
     */
    public IntegerList(int initialCapacity, int maximalValue) {
        super(initialCapacity, maximalValue);
    }

    /**
     * Creates a new list with the given initial size.
     * The value of all elements are initialized to 0.
     *
     * @param initialCapacity The initial capacity.
     * @param maximalValue The maximal value to be allowed, inclusive.
     * @param fill If {@code true}, the initial {@linkplain #size} is set to the initial
     *        capacity with all values set to 0.
     */
    public IntegerList(final int initialCapacity, int maximalValue, final boolean fill) {
        super(initialCapacity, maximalValue, fill);
    }

    /**
     * Adds the given element as the {@code int} primitive type.
     *
     * @param value The value to add.
     * @throws IllegalArgumentException if the given value is out of bounds.
     *
     * @see #removeLast
     */
    public void addInteger(final int value) throws IllegalArgumentException {
        addInt(value);
    }

    /**
     * Returns the element at the given index as the {@code int} primitive type.
     *
     * @param index The element index.
     * @return The value at the given index.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     */
    public int getInteger(final int index) throws IndexOutOfBoundsException {
        return getInt(index);
    }

    /**
     * Sets the element at the given index as the {@code int} primitive type.
     *
     * @param index The element index.
     * @param value The value at the given index.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     * @throws IllegalArgumentException if the given value is out of bounds.
     */
    public void setInteger(int index, int value) throws IndexOutOfBoundsException {
        setInt(index, value);
    }

    /**
     * Returns a clone of this list.
     *
     * @return A clone of this list.
     */
    @Override
    public IntegerList clone() {
        return (IntegerList) super.clone();
    }
}
