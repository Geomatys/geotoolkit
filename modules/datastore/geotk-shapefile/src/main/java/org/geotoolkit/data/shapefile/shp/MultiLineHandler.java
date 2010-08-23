/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
import java.nio.ByteBuffer;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import java.nio.DoubleBuffer;

import org.geotoolkit.storage.DataStoreException;

/**
 * The default JTS handler for shapefile. Currently uses the default JTS
 * GeometryFactory, since it doesn't seem to matter.
 *
 * @author Ian Schneider
 * @author aaime
 * @module pending
 */
public class MultiLineHandler implements ShapeHandler {

    private final ShapeType shapeType;


    /** Create a MultiLineHandler for ShapeType.ARC */
    public MultiLineHandler() {
        shapeType = ShapeType.ARC;
    }

    /**
     * Create a MultiLineHandler for one of: <br>
     * ShapeType.ARC,ShapeType.ARCM,ShapeType.ARCZ
     * 
     * @param type The ShapeType to use.
     * @throws DataStoreException If the ShapeType is not correct (see constructor).
     */
    public MultiLineHandler(ShapeType type) throws DataStoreException {
        if ((type != ShapeType.ARC) && (type != ShapeType.ARCM) && (type != ShapeType.ARCZ)) {
            throw new DataStoreException("MultiLineHandler constructor - expected type to be 3, 13 or 23");
        }

        shapeType = type;
    }

    /**
     * Get the type of shape stored
     * (ShapeType.ARC,ShapeType.ARCM,ShapeType.ARCZ)
     */
    @Override
    public ShapeType getShapeType() {
        return shapeType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getLength(final Object geometry) {
        final MultiLineString multi = (MultiLineString) geometry;
        final int numlines = multi.getNumGeometries();
        final int numpoints = multi.getNumPoints();

        if (shapeType == ShapeType.ARC) {
            return 44 + (4*numlines) + (16*numpoints);
        } else if (shapeType == ShapeType.ARCM) {
            return 44 + (4*numlines) + (16*numpoints) + 8 + 8 + (8*numpoints);
        } else if (shapeType == ShapeType.ARCZ) {
            return 44 + (4*numlines) + (16*numpoints) + 8 + 8 + (8*numpoints) + 8 + 8 + (8*numpoints);
        } else {
            throw new IllegalStateException("Expected ShapeType of Arc, got " + shapeType);
        }
    }

    private Object createNull() {
        return GEOMETRY_FACTORY.createMultiLineString((LineString[]) null);
    }

//    @Override
//    public Object read(ByteBuffer buffer, ShapeType type) {
//
//        if (type == ShapeType.NULL) {
//            return createNull();
//        }
//        final int dimensions = (shapeType == ShapeType.ARCZ) ? 3 : 2;
//        // skip bounding box (not needed)
//        buffer.position(buffer.position() + 4 * 8);
//
//        final int numParts = buffer.getInt();
//        final int numPoints = buffer.getInt(); // total number of points
//
//        //store each line string buffer start position
//        final int[] partOffsets = new int[numParts];
//        for (int i = 0; i < numParts; i++) {
//            partOffsets[i] = buffer.getInt();
//        }
//
//        // read the first two coordinates and start building the coordinate sequences
//        final double[][] lines = new double[numParts][0];
//        //use a double buffer to increase bulk reading
//        final DoubleBuffer dbuffer = buffer.asDoubleBuffer();
//
//        for (int part = 0; part < numParts; part++) {
//
//            final int finish;
//            if (part == (numParts - 1)) {
//                finish = numPoints;
//            } else {
//                finish = partOffsets[part + 1];
//            }
//
//            final int length = finish - partOffsets[part];
//            final double[] coords;
//            if (length == 1) {
//                //only one point for a line, JTS do not like that, so we make two points at same place.
//                coords = new double[2*dimensions];
//                dbuffer.get(coords, 0, 2);
//                coords[2] = coords[0];
//                coords[3] = coords[1];
//            } else {
//                coords = new double[length*dimensions];
//                dbuffer.get(coords, 0, length*2);
//            }
//            lines[part] = coords;
//        }
//
//        // if we have another coordinate, read and add to the coordinate
//        // sequences
//        if (dimensions == 3) {
//            // z min, max
//            dbuffer.position(dbuffer.position() + 2);
//            for (int part = 0; part < numParts; part++) {
//
//                final int finish;
//                if (part == (numParts - 1)) {
//                    finish = numPoints;
//                } else {
//                    finish = partOffsets[part + 1];
//                }
//
//                final double[] coords = lines[part];
//                final int length = finish - partOffsets[part];
//                if (length == 1) {
//                    //only one point for a line, JTS do not like that, so we make two points at same place.
//                    coords[4] = coords[5] = dbuffer.get();
//                } else {
//                    dbuffer.get(coords, length*2, length);
//                }
//            }
//        }
//
//        // Prepare line strings and return the multilinestring
//        final LineString[] lineStrings = new LineString[numParts];
//        for (int part = 0; part < numParts; part++) {
//            if(dimensions == 2){
//                lineStrings[part] = GEOMETRY_FACTORY.createLineString(new ShapeCoordinateSequence2D(lines[part]));
//            }else{
//                lineStrings[part] = GEOMETRY_FACTORY.createLineString(new ShapeCoordinateSequence3D(lines[part]));
//            }
//        }
//
//        return GEOMETRY_FACTORY.createMultiLineString(lineStrings);
//    }

    @Override
    public Object read(ByteBuffer buffer, ShapeType type) {

        if (type == ShapeType.NULL) {
            return createNull();
        }
        final int dimensions = (shapeType == ShapeType.ARCZ) ? 3 : 2;
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
        final Coordinate[][] lines = new Coordinate[numParts][0];
        //use a double buffer to increase bulk reading
        final DoubleBuffer dbuffer = buffer.asDoubleBuffer();

        for (int part = 0; part < numParts; part++) {

            final int finish;
            if (part == (numParts - 1)) {
                finish = numPoints;
            } else {
                finish = partOffsets[part + 1];
            }

            final Coordinate[] coords;
            int length = finish - partOffsets[part];
            if (length == 1) {
                coords = new Coordinate[2];
                coords[0] = new Coordinate(dbuffer.get(), dbuffer.get());
                coords[1] = new Coordinate(coords[0]);
            } else {
                coords = new Coordinate[length];
                for(int i=0,n=coords.length; i<n; i++){
                    coords[i] = new Coordinate(dbuffer.get(), dbuffer.get());
                }
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

                final Coordinate[] coords = lines[part];
                int length = finish - partOffsets[part];
                if (length == 1) {
                    coords[0].z = dbuffer.get();
                    coords[1].z = coords[0].z;
                } else {
                    for(Coordinate coord : coords){
                        coord.z = dbuffer.get();
                    }
                }
            }
        }

        // Prepare line strings and return the multilinestring
        final LineString[] lineStrings = new LineString[numParts];
        for (int part = 0; part < numParts; part++) {
            lineStrings[part] = GEOMETRY_FACTORY.createLineString(new CoordinateArraySequence(lines[part]));
        }

        return GEOMETRY_FACTORY.createMultiLineString(lineStrings);
    }

    @Override
    public void write(ByteBuffer buffer, Object geometry) {
        final MultiLineString multi = (MultiLineString) geometry;

        final Envelope box = multi.getEnvelopeInternal();
        buffer.putDouble(box.getMinX());
        buffer.putDouble(box.getMinY());
        buffer.putDouble(box.getMaxX());
        buffer.putDouble(box.getMaxY());

        final int numParts = multi.getNumGeometries();
        final CoordinateSequence[] lines = new CoordinateSequence[numParts];
        final double[] zExtreame = {Double.NaN, Double.NaN};
        final int npoints = multi.getNumPoints();

        buffer.putInt(numParts);
        buffer.putInt(npoints);

        for (int i=0, idx=0; i<numParts; i++) {
            lines[i] = ((LineString) multi.getGeometryN(i)).getCoordinateSequence();
            buffer.putInt(idx);
            idx += lines[i].size();
        }
        
        for(int lineN = 0; lineN < lines.length; lineN++){
            final CoordinateSequence coords = lines[lineN];
            if (shapeType == ShapeType.ARCZ) {
                JTSUtilities.zMinMax(coords, zExtreame);
            }
            final int ncoords = coords.size();
            
            for (int t=0; t<ncoords; t++) {
                buffer.putDouble(coords.getX(t));
                buffer.putDouble(coords.getY(t));
            }
        }

        if (shapeType == ShapeType.ARCZ) {
            if (Double.isNaN(zExtreame[0])) {
                buffer.putDouble(0.0);
                buffer.putDouble(0.0);
            } else {
                buffer.putDouble(zExtreame[0]);
                buffer.putDouble(zExtreame[1]);
            }

            for(int lineN = 0; lineN < lines.length; lineN++){
                final CoordinateSequence coords = lines[lineN];
                final int ncoords = coords.size();
                
                for (int t = 0; t < ncoords; t++) {
                    final double z = coords.getOrdinate(t, 2);
                    if (Double.isNaN(z)) {
                        buffer.putDouble(0.0);
                    } else {
                        buffer.putDouble(z);
                    }
                }
            }

            buffer.putDouble(-10E40);
            buffer.putDouble(-10E40);

            for (int t = 0; t < npoints; t++) {
                buffer.putDouble(-10E40);
            }
        }
    }

}
