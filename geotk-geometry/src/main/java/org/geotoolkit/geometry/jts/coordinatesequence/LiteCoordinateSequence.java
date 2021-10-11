/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
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
 */
package org.geotoolkit.geometry.jts.coordinatesequence;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

/**
 * @todo class description
 *
 * @author jeichar
 * @module
 * @since 2.1.x
 */
public class LiteCoordinateSequence extends PackedCoordinateSequence {

    /**
     * The packed coordinate array
     */
    private double[] coords;
    /**
     * Cached size, getSize() gets called an incredible number of times during rendering
     * (a profile shows 2 million calls when rendering 90.000 linear features)
     */
    private int size;

    /**
     * Builds a new packed coordinate sequence
     *
     * @param coords
     *
     */
    public LiteCoordinateSequence(final double[] coords) {
        super(2, 0);
        this.dimension = 2;
        if (coords.length % dimension != 0) {
            throw new IllegalArgumentException("Packed array does not contain " + "an integral number of coordinates");
        }
        this.coords = coords;
        this.size = coords.length / dimension;
    }

    /**
     * Builds a new packed coordinate sequence out of a float coordinate array
     *
     * @param coordinates
     */
    public LiteCoordinateSequence(final float[] coordinates) {
        super(2, 0);
        this.coords = new double[coordinates.length];
        this.dimension = 2;
        this.size = coords.length / dimension;
        System.arraycopy(coordinates, 0, this.coords, 0, coordinates.length);
    }

    /**
     * Builds a new packed coordinate sequence out of a coordinate array
     *
     * @param coordinates
     */
    public LiteCoordinateSequence(Coordinate[] coordinates) {
        super(2, 0);
        if (coordinates == null) {
            coordinates = new Coordinate[0];
        }
        this.dimension = 2;

        this.coords = new double[coordinates.length * this.dimension];
        for (int i = 0; i < coordinates.length; i++) {
            coords[i * this.dimension] = coordinates[i].x;
            coords[i * this.dimension + 1] = coordinates[i].y;
        }
        this.size = coordinates.length;
    }

    /**
     * Builds a new empty packed coordinate sequence of a given size and dimension
     *
     * @param size
     * @param dimension
     *
     */
    public LiteCoordinateSequence(final int size, final int dimension) {
        super(2, 0);
        if (dimension != 2) {
            throw new IllegalArgumentException("This type of sequence is always 2 dimensional");
        }
        this.dimension = 2;
        this.coords = new double[size * this.dimension];
        this.size = coords.length / dimension;
    }

    /**
     * Copy constructor
     * @param seq
     */
    public LiteCoordinateSequence(final LiteCoordinateSequence seq) {
        super(seq.dimension, 0);
        // a trivial benchmark can show that cloning arrays like this is actually faster
        // than calling clone on the array.
        this.dimension = seq.dimension;
        this.size = seq.size;
        final double[] orig = seq.getArray();
        this.coords = new double[orig.length];
        System.arraycopy(orig, 0, coords, 0, coords.length);
    }

    /**
     * @see org.locationtech.jts.geom.CoordinateSequence#getCoordinate(int)
     */
    @Override
    public Coordinate getCoordinateInternal(final int i) {
        final double x = coords[i * dimension];
        final double y = coords[i * dimension + 1];
        final double z = dimension == 2 ? java.lang.Double.NaN : coords[i * dimension + 2];
        return new Coordinate(x, y, z);
    }

    /**
     * @see org.locationtech.jts.geom.CoordinateSequence#size()
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object clone() {
        final double[] clone = new double[coords.length];
        System.arraycopy(coords, 0, clone, 0, coords.length);
        return new LiteCoordinateSequence(clone);
    }

    /**
     * @see org.locationtech.jts.geom.CoordinateSequence#getOrdinate(int, int)
     *      Beware, for performace reasons the ordinate index is not checked, if
     *      it's over dimensions you may not get an exception but a meaningless
     *      value.
     */
    @Override
    public double getOrdinate(final int index, final int ordinate) {
        return coords[index * dimension + ordinate];
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getX(final int index) {
        return coords[index * dimension];
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getY(final int index) {
        return coords[index * dimension + 1];
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOrdinate(final int index, final int ordinate, final double value) {
        coordRef = null;
        coords[index * dimension + ordinate] = value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope expandEnvelope(final Envelope env) {
        double minx = coords[0];
        double maxx = minx;
        double miny = coords[1];
        double maxy = miny;
        for (int i = 0; i < coords.length; i += dimension) {
            final double x = coords[i];
            final double y = coords[i + 1];

            if (x < minx) {
                minx = x;
            } else if (x > maxx) {
                maxx = x;
            }
            if (y < miny) {
                miny = y;
            } else if (y > maxy) {
                maxy = y;
            }
        }
        env.expandToInclude(minx, miny);
        env.expandToInclude(maxx, maxy);
        return env;
    }

    public double[] getArray() {
        return coords;
    }

    /**
     * @param coords2
     */
    public void setArray(final double[] coords2) {
        coords = coords2;
        size = coords.length / dimension;
        coordRef = null;
    }

    /**
     *  if this is a dimension=2 seq, then this is the same as getArray().
     *  If its >2 dims this will make a new array with dim=2
     */
    public double[] getXYArray() {
        if (dimension == 2){
            //this is always true
            return coords;
        }
        // this should never run, but its here for the future...
        final int n = size();
        final double[] result = new double[n * 2];
        for (int t = 0; t < n; t++) {
            result[t * 2] = getOrdinate(t, 0);
            result[t * 2 + 1] = getOrdinate(t, 1);
        }
        return result;
    }

    @Override
    public PackedCoordinateSequence copy() {
        return new LiteCoordinateSequence(coords.clone());
    }
}
