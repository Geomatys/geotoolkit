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

import org.apache.sis.storage.DataStoreException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;


/**
 * Decimation while reading
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DecimatePolygonHandler extends PolygonHandler {

    private final double resX;
    private final double resY;

    public DecimatePolygonHandler(final boolean read3D, final double[] res){
        super(read3D);
        this.resX = res[0];
        this.resY = res[1];
    }

    public DecimatePolygonHandler(final ShapeType type, final boolean read3D, final double[] res) throws DataStoreException{
        super(type,read3D);
        this.resX = res[0];
        this.resY = res[1];
    }

    @Override
    protected CoordinateSequence decimateRing(final CoordinateSequence source) {
        int length = 1;

        // TODO: try to optimize this to use coordinate sequence directly.
        Coordinate[] coords = source.toCoordinateArray();

        int i=1,j=0;
        for(; i<coords.length - 1 ; i++) {
            final double distX = Math.abs(coords[j].x - coords[i].x);
            if(distX > resX){
                length++;
                j++;
                coords[j] = coords[i];
                continue;
            }

            final double distY = Math.abs(coords[j].y - coords[i].y);
            if(distY > resY){
                length++;
                j++;
                coords[j] = coords[i];
                continue;
            }
        }

        //always include the last point, to preserve the ring
        length++; j++;
        coords[j] = coords[i];

        if (length == coords.length) {
            //nothing to decimate
            return new CoordinateArraySequence(coords, source.getDimension(), source.getMeasures());
        } else {
            //ensure we have the minimum number of points
            if (length < 4) {
                final Coordinate lastCoord = coords[coords.length-1];
                for(i=length-1;i<4;i++){
                    coords[i] = lastCoord;
                }
                length = 4;
            }

            //ensure it forms a closed line string if asked for
            if (!coords[0].equals2D(coords[length-1])) {
                coords[length-1] = new Coordinate(coords[0]);
            }
            final Coordinate[] cs = new Coordinate[length];
            System.arraycopy(coords, 0, cs, 0, length);
            return new CoordinateArraySequence(cs, source.getDimension(), source.getMeasures());
        }
    }
}
