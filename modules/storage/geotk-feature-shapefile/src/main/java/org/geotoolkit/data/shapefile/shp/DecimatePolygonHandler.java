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
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.geometry.jts.JTS;
import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;


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
    public Geometry read(final ByteBuffer buffer, final ShapeType type) {
        if (type == ShapeType.NULL) {
            return createNull();
        }

        //clear from previous read
        shells.clear();
        holes.clear();

        // skip the bounds
        buffer.position(buffer.position() + 32);

        final int numParts = buffer.getInt();
        final int numPoints = buffer.getInt();
        final int[] partOffsets = new int[numParts];

        for (int i = 0; i < numParts; i++) {
            partOffsets[i] = buffer.getInt();
        }

        final DoubleBuffer dbuffer = buffer.asDoubleBuffer();
        final int dimensions = (read3D && shapeType == ShapeType.POLYGONZ)? 3:2;

        //read everything in one round : +2 for minZ/maxZ
        final double[] coords = new double[numPoints*dimensions + ((dimensions==2)?0:2)];
        final int xySize = numPoints*2;
        dbuffer.get(coords);

        int coordIndex = 0;
        for (int part = 0; part < numParts; part++) {

            final int finish;
            if (part == (numParts - 1)) {
                finish = numPoints;
            } else {
                finish = partOffsets[part + 1];
            }
            final int length = finish - partOffsets[part];

            // REVISIT: polyons with only 1 to 3 points are not polygons -
            // geometryFactory will bomb so we skip if we find one.
            if(length > 0 && length < 4){
                coordIndex += length;
                continue;
            }

            final Coordinate[] points = new Coordinate[length];
            for (int i = 0; i < length; i++) {
                if(dimensions==2){
                    points[i] = new Coordinate(coords[coordIndex*2],coords[coordIndex*2+1]);
                }else{
                    points[i] = new Coordinate(coords[coordIndex*2],coords[coordIndex*2+1],coords[xySize+coordIndex+2]);
                }
                coordIndex++;
            }

            JTS.ensureClosed(points);

            final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(decimateRing(points));
            if (CGAlgorithms.isCCW(points)) {
                // counter-clockwise
                holes.add(ring);
            } else {
                // clockwise
                shells.add(ring);
            }
        }

        // quick optimization: if there's only one shell no need to check
        // for holes inclusion
        if (shells.size() == 1) {
            return createMulti(shells.get(0), holes);
        }
        // if for some reason, there is only one hole, we just reverse it and
        // carry on.
        else if (holes.size() == 1 && shells.isEmpty()) {
            //LOGGER.warning("only one hole in this polygon record");
            return createMulti(JTS.reverseRing(holes.get(0)));
        } else {

            // build an association between shells and holes
            final List<List<LinearRing>> holesForShells = assignHolesToShells(shells, holes);
            return buildGeometries(shells, holes, holesForShells);
        }
    }

    private Coordinate[] decimateRing(final Coordinate[] coords) {
        int length = 1;

        int i=1,j=0;
        for(; i<coords.length-1; i++){
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
            return coords;
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
            return cs;
        }
    }
}
