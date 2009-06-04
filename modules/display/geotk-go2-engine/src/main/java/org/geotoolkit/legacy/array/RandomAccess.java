/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.legacy.array;

import java.awt.geom.Point2D;


/**
 * Marker interface used by {@link PointArray} implementations to indicate that they support fast
 * (generally constant time) random access. Most default (flat) arrays implement this interface,
 * while compressed arrays generally do not implement it.
 *
 * @version $Id: RandomAccess.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 *
 * @deprecated No replacement. See <A HREF="http://jira.codehaus.org/browse/GEOT-776">GEOT-776</A>.
 */
public interface RandomAccess {
    /**
     * Returns the number of points in this array.
     */
    public abstract int count();

    /**
     * Returns the point at the specified index.
     *
     * @param  index The index from 0 inclusive to {@link #count} exclusive.
     * @return The point at the given index.
     * @throws IndexOutOfBoundsException if <code>index</code> is out of bounds.
     */
    public abstract Point2D getValue(int index) throws IndexOutOfBoundsException;
}
