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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.geotoolkit.geometry.jts.coordinatesequence.LiteCoordinateSequence;
import org.opengis.referencing.operation.TransformException;

/**
 * Decimate points at the given resolution.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GeometryScaleTransformer extends AbstractGeometryTransformer{

    private final double resX;
    private final double resY;

    public GeometryScaleTransformer(final double resX, final double resY){
        this.resX = resX;
        this.resY = resY;
    }

    @Override
    public CoordinateSequence transform(final CoordinateSequence cs, final int minpoints) {
        final Coordinate[] coords = cs.toCoordinateArray();
        final Coordinate[] deci = decimate(coords,minpoints);
        if(deci.length == coords.length){
            //nothing to decimate
            return cs;
        }else{
            return csf.create(deci);
        }
    }

    private Coordinate[] decimate(final Coordinate[] coords, final int minpoint) {
        if(coords.length==0) return coords;

        int length = 1;

        final boolean closed = coords[0].equals2D(coords[coords.length-1]);

        for(int i=1,j=0; i<coords.length; i++){
            final double distX = Math.abs(coords[j].x - coords[i].x);
            if (distX > resX) {
                length++;
                j++;
                coords[j] = coords[i];
                continue;
            }

            final double distY = Math.abs(coords[j].y - coords[i].y);
            if (distY > resY) {
                length++;
                j++;
                coords[j] = coords[i];
                continue;
            }
        }

        if (length == coords.length) {
            //nothing to decimate
            return coords;
        } else {
            //ensure we have the minimum number of points
            if (length < minpoint) {
                final Coordinate lastCoord = coords[coords.length-1];
                for(int i=length; i<minpoint; i++){
                    coords[i] = lastCoord;
                }
                length = minpoint;
            }

            //ensure it forms a closed line string if asked for
            if (closed && !coords[0].equals2D(coords[length-1])) {
                coords[length] = new Coordinate(coords[0]);
                length++;
            }

            final Coordinate[] cs = new Coordinate[length];
            System.arraycopy(coords, 0, cs, 0, length);
            return cs;
        }
    }

    @Override
    public Point transform(final Point geom) throws TransformException{
        //nothing to decimate
        return geom;
    }

    @Override
    protected MultiPoint transform(final MultiPoint geom) throws TransformException{
        final int nbGeom = geom.getNumGeometries();

        if(nbGeom == 1){
            //nothing to decimate
            return geom;
        }else{
            final LiteCoordinateSequence cs = new LiteCoordinateSequence(nbGeom, 2);
            for(int i=0;i<nbGeom;i++){
                final Coordinate coord = geom.getGeometryN(i).getCoordinate();
                cs.setX(i, coord.x);
                cs.setY(i, coord.y);
            }
            return gf.createMultiPoint(transform(cs,1));
        }
    }
}
