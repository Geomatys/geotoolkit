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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Coordinates are ordered in an array like [x1,y1, ... xN, yN]
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class ShapeCoordinateSequence2D implements CoordinateSequence {

    protected final double[] coordinates;
    protected final int size;

    ShapeCoordinateSequence2D(final double[] coordinates){
        this(coordinates, coordinates.length/2);
    }

    ShapeCoordinateSequence2D(final double[] coordinates, final int size){
        this.coordinates = coordinates;
        this.size = size;
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public Coordinate getCoordinate(final int index) {
        final int i = index*2;
        return new Coordinate(coordinates[i], coordinates[i+1], Coordinate.NULL_ORDINATE);
    }

    @Override
    public Coordinate getCoordinateCopy(final int i) {
        return getCoordinate(i);
    }

    @Override
    public void getCoordinate(final int index, final Coordinate coord) {
        final int i = index*2;
        coord.x = coordinates[i];
        coord.y = coordinates[i+1];
    }

    @Override
    public double getX(final int index) {
        return coordinates[index*2];
    }

    @Override
    public double getY(final int index) {
        return coordinates[index*2 + 1];
    }

    @Override
    public double getOrdinate(final int index, final int ordinate) {
        return coordinates[index*2 + ordinate];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void setOrdinate(final int index, final int ordinate, final double value) {
        coordinates[index*2 + ordinate] = value;
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
        if(size == 0){
            return envlp;
        }
        
        double minX = coordinates[0];
        double minY = coordinates[1];
        double maxX = minX;
        double maxY = minY;
        
        for(int i=2,n=size*2; i<n; i++){
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
        return new ShapeCoordinateSequence2D(coordinates);
    }

}
