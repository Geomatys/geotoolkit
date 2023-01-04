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

import java.util.Arrays;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

/**
 * Coordinates are ordered in an array like [x1,y1, ... xN, yN]
 *
 * Differs from {@link PackedCoordinateSequence}:
 * <ul>
 *     <li>
 *         An offset and size allow to use only part of the source coordinate array.
 *         This allow to densely pack multiple geometries in a single double array.
 *         This allow to avoid copies of array as much as possible.
 *     </li>
 *     <li>
 *         {@link #expandEnvelope(Envelope)} is <em>heavily</em> optimized, to avoid repeating calls to {@link Envelope#expandToInclude(double, double)} )} calls.
 *     </li>
 *     <li>Base for {@link ShapeCoordinateSequence3D Shape 3D sequence}, that packs all z coordinates at the end of the array.</li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 * @module
 */
class ShapeCoordinateSequence2D implements CoordinateSequence {

    /**
     * Offset as number of elements in {@link #coordinates} array.
     * This is the number of numbers to skip, not the number of points/coordinates.
     */
    protected final int offset;
    protected final double[] coordinates;
    protected final int size;

    ShapeCoordinateSequence2D(final double[] coordinates) {
        this(coordinates, coordinates.length/2);
    }

    ShapeCoordinateSequence2D(final double[] coordinates, final int size) {
        this(coordinates, 0, size);
    }

    /**
     *
     * @param coordinates The array containing points of this sequence.
     * @param offset Offset where the coordinate sequence starts. This is the number of elements to skip from array start.
     * @param size Number of <em>points</em> in this coordinate sequences.
     */
    ShapeCoordinateSequence2D(final double[] coordinates, final int offset, final int size) {
        this.coordinates = coordinates;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public Coordinate getCoordinate(final int index) {
        final int i = offset + index * 2;
        return new Coordinate(coordinates[i], coordinates[i+1], Coordinate.NULL_ORDINATE);
    }

    @Override
    public Coordinate getCoordinateCopy(final int i) {
        return getCoordinate(i);
    }

    @Override
    public void getCoordinate(final int index, final Coordinate coord) {
        final int i = offset + index * 2;
        coord.x = coordinates[i];
        coord.y = coordinates[i+1];
    }

    @Override
    public double getX(final int index) {
        return coordinates[offset + index * 2];
    }

    @Override
    public double getY(final int index) {
        return coordinates[offset + index * 2 + 1];
    }

    @Override
    public double getOrdinate(final int index, final int ordinate) {
        return coordinates[offset + index * 2 + ordinate];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void setOrdinate(final int index, final int ordinate, final double value) {
        coordinates[offset + index * 2 + ordinate] = value;
    }

    @Override
    public Coordinate[] toCoordinateArray() {
        final Coordinate[] array = new Coordinate[size];
        for(int i=0;i<size;i++){
            array[i] = getCoordinate(i);
        }
        return array;
    }

    @Override
    public Envelope expandEnvelope(final Envelope envlp) {
        if(size == 0) {
            return envlp;
        }

        final int startIdx = offset;
        double minX = coordinates[startIdx];
        double minY = coordinates[startIdx + 1];
        double maxX = minX;
        double maxY = minY;

        for (int i=startIdx + 2, n=startIdx + size * 2; i<n; i++) {
            final double x = coordinates[i];
            final double y = coordinates[++i];
            if(x < minX) minX = x;
            else if(x > maxX) maxX = x;

            if(y < minY) minY = y;
            else if(y > maxY) maxY = y;
        }
        envlp.expandToInclude(minX, minY);
        envlp.expandToInclude(maxX, maxY);
        return envlp;
    }

    @Override
    public CoordinateSequence clone(){
        return copy();
    }

    @Override
    public CoordinateSequence copy() {
        // WARNING: if the coordinate array contains multiple/many geometries, copying it might cause important overhead.
        // To reduce the problem, we create an array copy containing only this geometry.
        final double[] coordinates = Arrays.copyOfRange(this.coordinates, offset, offset + size * 2);
        return new ShapeCoordinateSequence2D(coordinates, 0, size);
    }
}
