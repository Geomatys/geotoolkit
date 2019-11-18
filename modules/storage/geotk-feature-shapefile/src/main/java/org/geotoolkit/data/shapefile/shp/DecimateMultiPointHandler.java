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
import org.apache.sis.storage.DataStoreException;
import org.locationtech.jts.geom.Geometry;

/**
 * Decimation while reading
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DecimateMultiPointHandler extends MultiPointHandler {

    private final double resX;
    private final double resY;

    public DecimateMultiPointHandler(final boolean read3D, final double[] res){
        super(read3D);
        this.resX = res[0];
        this.resY = res[1];
    }

    public DecimateMultiPointHandler(final ShapeType type, final boolean read3D, final double[] res) throws DataStoreException{
        super(type,read3D);
        this.resX = res[0];
        this.resY = res[1];
    }

    @Override
    public Geometry read(final ByteBuffer buffer, final ShapeType type) {
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

    private int decimatePoint2D(final double[] coords){
        int length = 1;

        for (int i=2,j=0; i<coords.length; i+=2) {
            final double distX = Math.abs(coords[j] - coords[i]);
            if(distX > resX){
                length++;
                j+=2;
                coords[j] = coords[i];
                coords[j+1] = coords[i+1];
                continue;
            }

            final double distY = Math.abs(coords[j+1] - coords[i+1]);
            if (distY > resY) {
                length++;
                j+=2;
                coords[j] = coords[i];
                coords[j+1] = coords[i+1];
                continue;
            }
        }
        return length;
    }
}
