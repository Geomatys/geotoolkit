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

package org.geotoolkit.geometry.jts.transform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

/**
 * Decimate points at the given resolution.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GeometryScaleTransformer extends AbstractGeometryTransformer{

    private final double resX;
    private final double resY;

    public GeometryScaleTransformer(double resX, double resY){
        this.resX = resX;
        this.resY = resY;
    }

    @Override
    public CoordinateSequence transform(CoordinateSequence cs, int minpoints) {
        final Coordinate[] coords = cs.toCoordinateArray();
        final Coordinate[] deci = decimate(coords,minpoints);
        if(deci.length == coords.length){
            //nothing to decimate
            return cs;
        }else{
            return csf.create(deci);
        }
    }

    private Coordinate[] decimate(Coordinate[] coords, int minpoint) {
        int lenght = 1;

        final boolean closed = coords[0].equals2D(coords[coords.length-1]);

        for(int i=1,j=0; i<coords.length; i++){
            final double distX = Math.abs(coords[j].x - coords[i].x);
            if(distX > resX){
                lenght++;
                j++;
                coords[j] = coords[i];
                continue;
            }

            final double distY = Math.abs(coords[j].y - coords[i].y);
            if(distY > resY){
                lenght++;
                j++;
                coords[j] = coords[i];
                continue;
            }
        }

        if(lenght == coords.length){
            //nothing to decimate
            return coords;
        }else{
            //ensure we have the minimum number of points
            if(lenght < minpoint){
                final Coordinate lastCoord = coords[coords.length-1];
                for(int i=lenght-1;i<minpoint;i++){
                    coords[i] = lastCoord;
                }
                lenght = minpoint;
            }
            
            //ensure it forms a closed line string if asked for
            if(closed && !coords[0].equals2D(coords[lenght-1])){
                coords[lenght] = new Coordinate(coords[0]);
                lenght++;
            }

            final Coordinate[] cs = new Coordinate[lenght];
            System.arraycopy(coords, 0, cs, 0, lenght);
            return cs;
        }
    }

}
