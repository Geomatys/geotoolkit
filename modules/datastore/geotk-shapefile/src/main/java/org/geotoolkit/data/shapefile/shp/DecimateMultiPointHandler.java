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
import java.nio.DoubleBuffer;
import org.geotoolkit.storage.DataStoreException;

/**
 * Decimation while reading
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DecimateMultiPointHandler extends MultiPointHandler {

    private final double resX;
    private final double resY;

    public DecimateMultiPointHandler(boolean read3D, double[] res){
        super(read3D);
        this.resX = res[0];
        this.resY = res[1];
    }

    public DecimateMultiPointHandler(ShapeType type, boolean read3D, double[] res) throws DataStoreException{
        super(type,read3D);
        this.resX = res[0];
        this.resY = res[1];
    }

    @Override
    public Object read(ByteBuffer buffer, ShapeType type) {
        if (type == ShapeType.NULL) {
            return createNull();
        }

        final int dimensions = (read3D && shapeType == ShapeType.MULTIPOINTZ)? 3 : 2;

        // read bounding box (not needed)
        buffer.position(buffer.position() + 32);
        final int numpoints = buffer.getInt();

        final DoubleBuffer dbuffer = buffer.asDoubleBuffer();

        final double[] coords = new double[numpoints*dimensions];
        final int xySize = numpoints*2;
        dbuffer.get(coords,0,xySize);

        if(dimensions==2){
            return GEOMETRY_FACTORY.createMultiPoint(
                    new ShapeCoordinateSequence2D(coords,decimatePoint2D(coords)));
        } else {
            // z min, max
            dbuffer.position(dbuffer.position() + 2);
            dbuffer.get(coords,xySize,numpoints);
            return GEOMETRY_FACTORY.createMultiPoint(
                    new ShapeCoordinateSequence3D(coords));
        }

    }

    private int decimatePoint2D(double[] coords){
        int lenght = 1;

        for(int i=2,j=0; i<coords.length; i+=2){
            final double distX = Math.abs(coords[j] - coords[i]);
            if(distX > resX){
                lenght++;
                j+=2;
                coords[j] = coords[i];
                coords[j+1] = coords[i+1];
                continue;
            }

            final double distY = Math.abs(coords[j+1] - coords[i+1]);
            if(distY > resY){
                lenght++;
                j+=2;
                coords[j] = coords[i];
                coords[j+1] = coords[i+1];
                continue;
            }
        }

        return lenght;
    }


}
