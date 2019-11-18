/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
 *
 * Created on 31-dic-2004
 */
package org.geotoolkit.geometry.jts.coordinatesequence;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;

/**
 * An implementation of a JTS CSBuilder which uses a PackedCoordinateSequence.
 *
 * @author wolf
 * @module
 */
public abstract class PackedCSBuilder implements CSBuilder {

    int size = -1;
    int dimensions = -1;

    /**
     * {@inheritDoc }
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getDimension() {
        return dimensions;
    }

    public static class Double extends PackedCSBuilder {

        double[] coordinates;
        PackedCoordinateSequenceFactory factory = new PackedCoordinateSequenceFactory(PackedCoordinateSequenceFactory.DOUBLE);

        /**
         * {@inheritDoc }
         */
        @Override
        public void start(final int size, final int dimensions) {
            coordinates = new double[size * dimensions];
            this.size = size;
            this.dimensions = dimensions;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public CoordinateSequence end() {
            CoordinateSequence cs = factory.create(coordinates, dimensions);
            coordinates = null;
            size = -1;
            dimensions = -1;
            return cs;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setOrdinate(final double value, final int ordinateIndex,
                final int coordinateIndex) {
            coordinates[coordinateIndex * dimensions + ordinateIndex] = value;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public double getOrdinate(final int ordinateIndex, final int coordinateIndex) {
            return coordinates[coordinateIndex * dimensions + ordinateIndex];
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setOrdinate(final CoordinateSequence sequence, final double value, final int ordinateIndex, final int coordinateIndex) {
            PackedCoordinateSequence pcs = (PackedCoordinateSequence) sequence;
            pcs.setOrdinate(coordinateIndex, ordinateIndex, value);
        }
    }

    public static class Float extends PackedCSBuilder {

        float[] coordinates;
        PackedCoordinateSequenceFactory factory = new PackedCoordinateSequenceFactory(PackedCoordinateSequenceFactory.FLOAT);

        /**
         * {@inheritDoc }
         */
        @Override
        public void start(final int size, final int dimensions) {
            coordinates = new float[size * dimensions];
            this.size = size;
            this.dimensions = dimensions;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public CoordinateSequence end() {
            CoordinateSequence cs = factory.create(coordinates, dimensions);
            coordinates = null;
            size = -1;
            dimensions = -1;
            return cs;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setOrdinate(final double value, final int ordinateIndex,
                final int coordinateIndex) {
            coordinates[coordinateIndex * dimensions + ordinateIndex] = (float) value;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setOrdinate(final CoordinateSequence sequence, final double value, final int ordinateIndex, final int coordinateIndex) {
            PackedCoordinateSequence pcs = (PackedCoordinateSequence) sequence;
            pcs.setOrdinate(coordinateIndex, ordinateIndex, value);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public double getOrdinate(final int ordinateIndex, final int coordinateIndex) {
            return coordinates[coordinateIndex * dimensions + ordinateIndex];
        }
    }
}
