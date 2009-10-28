/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
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

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequenceFactory;

/**
 * An implementation of a JTS CSBuilder which uses a PackedCoordinateSequence.
 * 
 * @author wolf
 * @module pending
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

        double[] ordinates;
        PackedCoordinateSequenceFactory factory = new PackedCoordinateSequenceFactory(PackedCoordinateSequenceFactory.DOUBLE);

        /**
         * {@inheritDoc }
         */
        @Override
        public void start(int size, int dimensions) {
            ordinates = new double[size * dimensions];
            this.size = size;
            this.dimensions = dimensions;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public CoordinateSequence end() {
            CoordinateSequence cs = factory.create(ordinates, dimensions);
            ordinates = null;
            size = -1;
            dimensions = -1;
            return cs;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setOrdinate(double value, int ordinateIndex,
                int coordinateIndex) {
            ordinates[coordinateIndex * dimensions + ordinateIndex] = value;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public double getOrdinate(int ordinateIndex, int coordinateIndex) {
            return ordinates[coordinateIndex * dimensions + ordinateIndex];
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setOrdinate(CoordinateSequence sequence, double value, int ordinateIndex, int coordinateIndex) {
            PackedCoordinateSequence pcs = (PackedCoordinateSequence) sequence;
            pcs.setOrdinate(coordinateIndex, ordinateIndex, value);
        }
    }

    public static class Float extends PackedCSBuilder {

        float[] ordinates;
        PackedCoordinateSequenceFactory factory = new PackedCoordinateSequenceFactory(PackedCoordinateSequenceFactory.FLOAT);

        /**
         * {@inheritDoc }
         */
        @Override
        public void start(int size, int dimensions) {
            ordinates = new float[size * dimensions];
            this.size = size;
            this.dimensions = dimensions;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public CoordinateSequence end() {
            CoordinateSequence cs = factory.create(ordinates, dimensions);
            ordinates = null;
            size = -1;
            dimensions = -1;
            return cs;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setOrdinate(double value, int ordinateIndex,
                int coordinateIndex) {
            ordinates[coordinateIndex * dimensions + ordinateIndex] = (float) value;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setOrdinate(CoordinateSequence sequence, double value, int ordinateIndex, int coordinateIndex) {
            PackedCoordinateSequence pcs = (PackedCoordinateSequence) sequence;
            pcs.setOrdinate(coordinateIndex, ordinateIndex, value);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public double getOrdinate(int ordinateIndex, int coordinateIndex) {
            return ordinates[coordinateIndex * dimensions + ordinateIndex];
        }
    }
}
