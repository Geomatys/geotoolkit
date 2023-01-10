/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.data.shapefile.shp;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;

/**
 * Coordinates are ordered in an array like [x1,y1, ... xN, yN, z1, ... zN]
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
class ShapeCoordinateSequence3D extends ShapeCoordinateSequence2D {

    /**
     * Number of elements to skip in source coordinate array to position on first Z coordinate of this sequence.
     */
    final int zOffset;

    ShapeCoordinateSequence3D(final double[] coordinates){
        this(coordinates, 0, coordinates.length/3, coordinates.length / 3 * 2);
    }

    /**
     * Arguments have the same meaning as in {@link ShapeCoordinateSequence2D#ShapeCoordinateSequence2D(double[], int, int)},
     * except for additional z offset.
     *
     * @param zOffset Offset in the coordinate array where z coordinates for this sequence are stored.
     *                This is the count of numbers to skip from the start of the array to arrive to z ordinates of this sequence.
     *
     * @see ShapeCoordinateSequence2D#ShapeCoordinateSequence2D(double[], int, int) parent constructor.
     */
    ShapeCoordinateSequence3D(final double[] coordinates, int offset, int size, int zOffset) {
        super(coordinates, offset, size);
        this.zOffset = zOffset;
    }

    @Override
    public int getDimension() {
        return 3;
    }

    @Override
    public Coordinate getCoordinate(final int index) {
        final int i = offset + index * 2;
        return new Coordinate(coordinates[i], coordinates[i+1], coordinates[zOffset + index]);
    }

    @Override
    public void getCoordinate(final int index, final Coordinate coord) {
        final int i = (offset + index) * 2;
        coord.x = coordinates[i];
        coord.y = coordinates[i+1];
        coord.z = coordinates[zOffset + index];
    }

    @Override
    public double getOrdinate(final int index, final int ordinate) {
        switch(ordinate){
            case 0: return coordinates[offset + index * 2];
            case 1: return coordinates[offset + index * 2 + 1];
            case 2: return coordinates[zOffset + index];
            default:
                throw new IllegalArgumentException("Unvalid ordinate : " + ordinate);
        }
    }

    @Override
    public void setOrdinate(final int index, final int ordinate, final double value) {
        switch(ordinate){
            case 0: coordinates[offset + index * 2] = value; break;
            case 1: coordinates[offset + index * 2 + 1] = value; break;
            case 2: coordinates[zOffset + index] = value; break;
            default:
                throw new IllegalArgumentException("Unvalid ordinate : " + ordinate);
        }

    }

    @Override
    public CoordinateSequence clone() { return copy(); }

    @Override
    public CoordinateSequence copy() {
        // WARNING: if the coordinate array contains multiple/many geometries, copying it might cause important overhead.
        // To reduce the problem, we create an array copy containing only this geometry.
        final double[] coordinates = new double[Math.multiplyExact(size, 3)];
        System.arraycopy(this.coordinates, offset, coordinates, 0, size * 2);
        System.arraycopy(this.coordinates, zOffset, this.coordinates, size * 2, size);
        return new ShapeCoordinateSequence3D(coordinates, 0, size, size * 2);
    }
}
