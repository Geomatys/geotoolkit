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

/**
 * Coordinates are ordered in an array like [x1,y1, ... xN, yN, z1, ... zN]
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class ShapeCoordinateSequence3D extends ShapeCoordinateSequence2D {

    ShapeCoordinateSequence3D(double[] coordinates){
        super(coordinates,coordinates.length/3);
    }

    @Override
    public int getDimension() {
        return 3;
    }

    @Override
    public Coordinate getCoordinate(int index) {
        final int i = index*2;
        return new Coordinate(coordinates[i], coordinates[i+1], coordinates[size*2 + index]);
    }

    @Override
    public void getCoordinate(int index, Coordinate coord) {
        final int i = index*2;
        coord.x = coordinates[i];
        coord.y = coordinates[i+1];
        coord.z = coordinates[size*2 + index];
    }

    @Override
    public double getOrdinate(int index, int ordinate) {
        switch(ordinate){
            case 0: return coordinates[index*2];
            case 1: return coordinates[index*2 + 1];
            case 2: return coordinates[size*2 + index];
            default:
                throw new IllegalArgumentException("Unvalid ordinate : " + ordinate);
        }
    }

    @Override
    public void setOrdinate(int index, int ordinate, double value) {
        switch(ordinate){
            case 0: coordinates[index*2] = value; break;
            case 1: coordinates[index*2 + 1] = value; break;
            case 2: coordinates[size*2 + index] = value; break;
            default:
                throw new IllegalArgumentException("Unvalid ordinate : " + ordinate);
        }
        
    }

    @Override
    public CoordinateSequence clone(){
        return new ShapeCoordinateSequence3D(coordinates);
    }

}
