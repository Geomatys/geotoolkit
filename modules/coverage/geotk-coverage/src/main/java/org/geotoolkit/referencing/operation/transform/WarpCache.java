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
package org.geotoolkit.referencing.operation.transform;

import javax.media.jai.Warp;
import javax.media.jai.WarpGrid;

import org.apache.sis.util.collection.Cache;


/**
 * A cache of {@link Warp} objects created by {@link WarpFactory}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
final class WarpCache extends Cache<WarpKey, Warp> {
    /**
     * Creates a new cache. We will allocate approximatively 8 kilobytes for storing grids by
     * strong references. We don't need lot of space because grids are typically not that big.
     * If more space is required, the additional grids will be retained by weak references.
     */
    WarpCache() {
        super(16, 2000, false);
    }

    /**
     * Returns an estimation of an entry cost. In this implementation, the
     * cost of an entry is its grid size, plus an arbitrary constant value.
     * This cost computation is expressed in units of {@code float} elements.
     */
    @Override
    protected int cost(final Warp value) {
        int cost = 4;
        if (value instanceof WarpGrid) {
            final WarpGrid grid = (WarpGrid) value;
            cost += grid.getXNumCells() * grid.getYNumCells();
        }
        return cost;
    }
}
