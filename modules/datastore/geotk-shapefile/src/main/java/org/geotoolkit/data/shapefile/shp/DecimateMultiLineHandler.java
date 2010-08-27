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

import java.nio.ByteBuffer;

import com.vividsolutions.jts.geom.LineString;
import java.nio.DoubleBuffer;
import org.geotoolkit.storage.DataStoreException;


/**
 * Decimation while reading
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DecimateMultiLineHandler extends MultiLineHandler {

    private final double[] res;

    public DecimateMultiLineHandler(boolean read3D, double[] res){
        super(read3D);
        this.res = res;
    }

    public DecimateMultiLineHandler(ShapeType type, boolean read3D, double[] res) throws DataStoreException{
        super(type,read3D);
        this.res = res;
    }

    @Override
    public Object read(ByteBuffer buffer, ShapeType type) {

        if (type == ShapeType.NULL) {
            return createNull();
        }
        final int dimensions = (read3D && shapeType == ShapeType.ARCZ) ? 3 : 2;
        // skip bounding box (not needed)
        buffer.position(buffer.position() + 4 * 8);

        final int numParts = buffer.getInt();
        final int numPoints = buffer.getInt(); // total number of points

        //store each line string buffer start position
        final int[] partOffsets = new int[numParts];
        for (int i = 0; i < numParts; i++) {
            partOffsets[i] = buffer.getInt();
        }

        // read the first two coordinates and start building the coordinate sequences
        final double[][] lines = new double[numParts][0];
        //use a double buffer to increase bulk reading
        final DoubleBuffer dbuffer = buffer.asDoubleBuffer();

        for (int part = 0; part < numParts; part++) {

            final int finish;
            if (part == (numParts - 1)) {
                finish = numPoints;
            } else {
                finish = partOffsets[part + 1];
            }

            final int length = finish - partOffsets[part];
            final double[] coords;
            if (length == 1) {
                //only one point for a line, JTS do not like that, so we make two points at same place.
                coords = new double[2*dimensions];
                dbuffer.get(coords, 0, 2);
                coords[2] = coords[0];
                coords[3] = coords[1];
            } else {
                coords = new double[length*dimensions];
                dbuffer.get(coords, 0, length*2);
            }
            lines[part] = coords;
        }

        // if we have another coordinate, read and add to the coordinate
        // sequences
        if (dimensions == 3) {
            // z min, max
            dbuffer.position(dbuffer.position() + 2);
            for (int part = 0; part < numParts; part++) {

                final int finish;
                if (part == (numParts - 1)) {
                    finish = numPoints;
                } else {
                    finish = partOffsets[part + 1];
                }

                final double[] coords = lines[part];
                final int length = finish - partOffsets[part];
                if (length == 1) {
                    //only one point for a line, JTS do not like that, so we make two points at same place.
                    coords[4] = coords[5] = dbuffer.get();
                } else {
                    dbuffer.get(coords, length*2, length);
                }
            }
        }

        // Prepare line strings and return the multilinestring
        final LineString[] lineStrings = new LineString[numParts];
        for (int part = 0; part < numParts; part++) {
            if(dimensions == 2){
                lineStrings[part] = GEOMETRY_FACTORY.createLineString(
                        new ShapeCoordinateSequence2D(lines[part],decimateLine2D(lines[part])));
            }else{
                lineStrings[part] = GEOMETRY_FACTORY.createLineString(
                        new ShapeCoordinateSequence3D(lines[part]));
            }
        }

        return GEOMETRY_FACTORY.createMultiLineString(lineStrings);
    }


    private int decimateLine2D(double[] array){
        final int size = array.length/2;
        if(size < 3){
            return size;
        }

        //maximium distance from the line
        double toleranceSq = Math.min(res[0], res[1]);
        toleranceSq *= toleranceSq;


        int length = 2;
        int afterP1 = 2;
        double x1 = array[0];
        double y1 = array[1];

search: for (int j=4; j<array.length; j+=2) {
            final double x2 = array[j  ] - x1;
            final double y2 = array[j+1] - y1;
            final double lineLengthSq = (x2 * x2) + (y2 * y2);
            int k = afterP1;
            do {
                final double px = array[k++] - x1;
                final double py = array[k++] - y1;
                /*
                 * Definition of term:
                 *    - "the line" is the line from (x1,y1) to (x2,y2).
                 *
                 * Compute the squared distance from (x1,y1) to the projection of (xp,yp) on
                 * the line. We do that by computing the dot product of the (x1,y1)-(xp,yp)
                 * vector by the unitary vector colinear with the line.
                 */
                double distanceSq = px * x2 + py * y2;
                distanceSq *= distanceSq / lineLengthSq;
                /*
                 * Squared distance to line is the length of the (x1,y1)-(xp,yp)
                 * vector minor the length we computed above (Phytagore relation).
                 */
                distanceSq = px * px + py * py - distanceSq;
                if (distanceSq > toleranceSq) {
                    array[length++] = x1 = array[j - 2];
                    array[length++] = y1 = array[j - 1];
                    afterP1 = j;
                    continue search;
                }
            } while (k != j);
        }

        if(afterP1 != array.length){
            array[length++] = array[array.length - 2];
            array[length++] = array[array.length - 1];
        }

        return length/2;

        //we have at least two points when we are here
//        if(array.length == length){
//            //no points where removed
//            return array;
//        }else{
//            final double[] decimated = new double[length];
//            System.arraycopy(array, 0, decimated, 0, length);
//            return decimated;
//        }
    }


}
