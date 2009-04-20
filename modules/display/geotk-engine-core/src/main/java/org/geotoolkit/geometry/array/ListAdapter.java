/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.array;

// J2SE dependencies
import java.io.Serializable;
import java.util.AbstractList;
import java.util.RandomAccess;

// Geotools dependencies
import org.geotoolkit.geometry.DirectPosition2D;


/**
 * Exposes a {@link PointArray2D} as a list of positions with efficient random access.
 *
 * @source $URL$
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 *
 * @see PointArray2D#positions
 */
final class ListAdapter extends AbstractList implements RandomAccess, Serializable {
    /**
     * Serial version for compatibility with previous version.
     */
    private static final long serialVersionUID = -8691631393465315424L;

    /**
     * The array of (<var>x</var>,<var>y</var>) coordinates.
     *
     * @param array The array of (<var>x</var>,<var>y</var>) coordinates
     */
    private final PointArray2D array;

    /**
     * Wraps the specified array in a list.
     */
    public ListAdapter(final PointArray2D array) {
        this.array = array;
    }

    /**
     * Returns this list size.
     */
    public int size() {
        return array.length();
    }

    /**
     * Returns the position at the specified index.
     */
    public Object get(final int index) {
        return array.get(index, null);
    }
}
