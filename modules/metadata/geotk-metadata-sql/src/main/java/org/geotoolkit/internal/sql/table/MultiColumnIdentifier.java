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
package org.geotoolkit.internal.sql.table;

import java.util.Arrays;
import net.jcip.annotations.Immutable;

import org.apache.sis.util.Classes;


/**
 * An identifier which is built from more than one column. An instance of this class can be
 * given to the {@link SingletonTable} methods which expect a {@link Comparable} argument type.
 * Instances of this object are also used as keys for caching entries. Instances are required
 * to be immutable.
 *
 * @param <T> The type of this class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @see SingletonTable#createIdentifier
 *
 * @since 3.10
 * @module
 */
@Immutable
public abstract class MultiColumnIdentifier<T extends MultiColumnIdentifier<T>> implements Comparable<T> {
    /**
     * Creates a new instance.
     */
    protected MultiColumnIdentifier() {
    }

    /**
     * Returns the components of this identifier. The array returned by this method shall be
     * non-null and have a length greater than 1. Array elements can be either {@link String}
     * or {@link Integer} types (or the wrapper of some type that can be casted to {@code int}).
     * No other type is allowed.
     *
     * @return The components of this identifier.
     */
    public abstract Comparable<?>[] getIdentifiers();

    /**
     * Returns a hash code value for this identifier.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(getIdentifiers());
    }

    /**
     * Returns {@code true} if this object is equals to the given object.
     *
     * @param  other The other object to compare with this object.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other != null && other.getClass() == getClass()) {
            @SuppressWarnings("unchecked")
            final T that = (T) other;
            return Arrays.equals(getIdentifiers(), that.getIdentifiers());
        }
        return false;
    }

    /**
     * Compares this identifier with the given one for order.
     *
     * @param that The other object to compare with this object.
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public int compareTo(final T that) {
        final Comparable[] id1 = this.getIdentifiers();
        final Comparable[] id2 = that.getIdentifiers();
        int d = id1.length - id2.length;
        if (d == 0) {
            for (int i=0; i<id1.length; i++) {
                d = id1[i].compareTo(id2[i]);
                if (d != 0) {
                    break;
                }
            }
        }
        return d;
    }

    /**
     * Returns a string representation of this identifier for debugging purpose.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + Arrays.toString(getIdentifiers());
    }
}
