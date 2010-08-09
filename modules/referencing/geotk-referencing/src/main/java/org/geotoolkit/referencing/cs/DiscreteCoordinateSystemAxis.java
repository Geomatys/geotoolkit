/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.referencing.cs;


/**
 * Interface for coordinate systems axis having discrete ordinate values.
 * Such axis is typically an axis in a coverage grid. They appears for
 * example in data read from a NetCDF file.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
public interface DiscreteCoordinateSystemAxis {
    /**
     * Returns the number of ordinate values.
     *
     * @return The number of ordinate values.
     */
    int length();

    /**
     * Returns the ordinate value at the given index. The returned value is typically
     * an instance of {@link Number} or {@link java.util.Date}.
     *
     * @param  index The index at which to return the ordinate value.
     * @return The ordinate value at the given index.
     * @throws IndexOutOfBoundsException If the given index is outside the
     *         [0 &hellip; {@linkplain #length() length}-1] range.
     */
    Comparable<?> getOrdinateAt(int index) throws IndexOutOfBoundsException;
}
