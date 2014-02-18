/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.plugin;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import ucar.nc2.dataset.CoordinateAxis;

import org.geotoolkit.image.io.DimensionSlice;
import org.geotoolkit.referencing.adapters.NetcdfAxis;


/**
 * An iterator over the <cite>name</cite> and <cite>axis direction</cite> of each dimension.
 * This is suitable for calls to {@link DimensionSlice#findDimensionIndex(Iterable)}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
final class NetcdfAxesIterator implements Iterator<Map.Entry<?,Integer>> {
    /**
     * The iterator over the axes in the current coordinate system.
     * <strong>NOTE</strong>: iteration shall be performed in reverse order.
     */
    private final ListIterator<CoordinateAxis> axes;

    /**
     * The current coordinate axis in the iteration.
     */
    private CoordinateAxis axis;

    /**
     * An index which combine the current dimension with the next property (name or axis
     * direction) to return.
     *
     * {@preformat text
     *     index = dimension * NUM_PROPERTIES + nextProperty;
     * }
     */
    private int index;

    /**
     * Creates a new iterator for the given collection of coordinate systems.
     *
     * @param sys The collection coordinate systems.
     */
    NetcdfAxesIterator(final List<CoordinateAxis> axes) {
        this.axes = axes.listIterator(axes.size());
        if (this.axes.hasPrevious()) {
            axis = this.axes.previous();
        }
    }

    /**
     * Returns {@code true} if there is more element on which to iterate.
     */
    @Override
    public boolean hasNext() {
        return axis != null;
    }

    /**
     * Returns the next (<var>name or axis direction</var>, <var>dimension</var>) entry.
     */
    @Override
    public Entry<?, Integer> next() {
        if (axis == null) {
            throw new NoSuchElementException();
        }
        final Object property;
        final int dimension = index / 2;
        switch (index % 2) {
            default: throw new AssertionError(index);
            case 0: property = axis.getShortName(); break;
            case 1: property = NetcdfAxis.getDirection(axis);
                    axis = axes.hasPrevious() ? axes.previous() : null;
                    break; // If we add new cases, the above line must stay last.
        }
        index++;
        return new AbstractMap.SimpleImmutableEntry<>(property, dimension);
    }

    /**
     * Unsupported operation, since the iterable is unmodifiable.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
