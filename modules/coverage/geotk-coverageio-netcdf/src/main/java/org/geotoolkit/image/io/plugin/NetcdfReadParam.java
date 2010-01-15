/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import javax.imageio.ImageReader;

import ucar.nc2.dataset.Enhancements;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateSystem;

import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.referencing.adapters.NetcdfAxis;


/**
 * Default parameters for {@link NetcdfImageReader}. This class provides an iterator suitable
 * for calls to {@link #getDimensionForBands}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08 (derived from 2.4)
 * @module
 */
final class NetcdfReadParam extends SpatialImageReadParam {
    /**
     * The default source bands to read from the NetCDF file.
     * Also the default destination bands in the buffered image.
     */
    private static final int[] DEFAULT_BANDS = new int[1];

    /**
     * Creates a new set of parameters. The {@linkplain #sourceBands source bands}
     * and {@linkplain #destinationBands destination bands} are initialized to 0.
     *
     * @param reader The reader for which this parameter block is created
     */
    public NetcdfReadParam(final ImageReader reader) {
        super(reader);
        sourceBands = DEFAULT_BANDS;
        destinationBands = DEFAULT_BANDS;
    }

    /**
     * Returns an iterable over the <cite>name</cite> and <cite>axis direction</cite> of each
     * dimension. The map entry values are the dimension of the name of axis direction.
     *
     * @param  variable The variable from which to extract the dimension names and axis directions.
     * @return An iterable over the names and axis directions, or {@code null} if the variable was
     *         null or doesn't have coordinate system information.
     */
    static Iterable<Map.Entry<?, Integer>> getDimensionProperties(final Enhancements variable) {
        if (variable != null) {
            final List<CoordinateSystem> systems = variable.getCoordinateSystems();
            if (systems != null) {
                return new Iterable<Map.Entry<?, Integer>>() {
                    @Override public Iterator<Entry<?, Integer>> iterator() {
                        return new Iter(systems);
                    }
                };
            }
        }
        return null;
    }

    /**
     * An iterator over the <cite>name</cite> and <cite>axis direction</cite> of each dimension.
     * This iterator is created by {@link NetcdfReadParam#getDimensionProperties(Enhancements)}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.08
     *
     * @since 3.08
     * @module
     */
    private static final class Iter implements Iterator<Entry<?, Integer>> {
        /**
         * The iterator over all coordinate sytems defined by a variable.
         */
        private final Iterator<CoordinateSystem> systems;

        /**
         * The iterator over the axes in the current coordinate system.
         * <strong>NOTE</strong>: iteration shall be performed in reverse order.
         */
        private ListIterator<CoordinateAxis> axes;

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
         * Creates a new itertor for the given collection of coordinate systems.
         *
         * @param sys The collection coordinate systems.
         */
        Iter(final Iterable<CoordinateSystem> sys) {
            systems = sys.iterator();
            nextAxis();
        }

        /**
         * Sets {@link #axis} to the next axis, or to {@code null} if there is no more axis
         * on which to iterator.
         */
        private void nextAxis() {
            while (axes == null || !axes.hasPrevious()) {
                if (!systems.hasNext()) {
                    axis = null;
                    return;
                }
                final List<CoordinateAxis> list = systems.next().getCoordinateAxes();
                if (list != null) {
                    axes = list.listIterator(list.size());
                    index = 0;
                }
            }
            axis = axes.previous();
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
            final CoordinateAxis axis = this.axis;
            if (axis == null) {
                throw new NoSuchElementException();
            }
            final Object property;
            final int dimension = index / 2;
            switch (index % 2) {
                default: throw new AssertionError(index);
                case 0: property = axis.getName(); break;
                case 1: property = NetcdfAxis.getDirection(axis);
                        nextAxis(); break; // If we add new cases, this line must stay last.
            }
            index++;
            return new AbstractMap.SimpleImmutableEntry<Object, Integer>(property, dimension);
        }

        /**
         * Unsupported operation, since the iterable is unmodifiable.
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
