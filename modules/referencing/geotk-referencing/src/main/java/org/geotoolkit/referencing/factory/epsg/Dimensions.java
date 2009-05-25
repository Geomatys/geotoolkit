/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;


/**
 * A counter for source and target dimensions (to be kept together).
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
final class Dimensions {
    /**
     * The dimensions as an encoded value.
     */
    int encoded;

    /**
     * The occurences of this dimensions.
     */
    int occurences;

    Dimensions(final int e) {
        encoded = e;
    }

    /**
     * Returns a hash code for this object.
     */
    @Override
    public int hashCode() {
        // MUST ignore 'occurences'.
        return encoded;
    }

    /**
     * Compares this object wirh the given one for equality.
     */
    @Override
    public boolean equals(final Object object) {
        // MUST ignore 'occurences'.
        return (object instanceof Dimensions) && ((Dimensions) object).encoded == encoded;
    }

    /**
     * For debugging purpose only.
     */
    @Override
    public String toString() {
        return "[(" + (encoded >>> 16) + ',' + (encoded & 0xFFFF) + ")\u00D7" + occurences + ']';
    }
}
