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

import java.nio.ByteBuffer;

import org.geotoolkit.geometry.jts.coordinatesequence.CSBuilder;
import org.geotoolkit.geometry.jts.coordinatesequence.CSBuilderFactory;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import org.geotoolkit.data.DataStoreException;

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
    private final CSBuilder BUILDER = CSBuilderFactory.getDefaultBuilder();


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

    @Override
    public Object read(ByteBuffer buffer, ShapeType type) {
        if (type == ShapeType.NULL) {
            return createNull();
        }
        final int dimensions = (shapeType == ShapeType.ARCZ) ? 3 : 2;
        // read bounding box (not needed)
        buffer.position(buffer.position() + 4 * 8);

        final int numParts = buffer.getInt();
        final int numPoints = buffer.getInt(); // total number of points

        final int[] partOffsets = new int[numParts];

        // points = new Coordinate[numPoints];
        for (int i = 0; i < numParts; i++) {
            partOffsets[i] = buffer.getInt();
        }
        // read the first two coordinates and start building the coordinate
        // sequences
        final CoordinateSequence[] lines = new CoordinateSequence[numParts];
        int finish = 0;
        int start = 0;
        int length = 0;
        boolean clonePoint = false;
        for (int part = 0; part < numParts; part++) {
            start = partOffsets[part];

            if (part == (numParts - 1)) {
                finish = numPoints;
            } else {
                finish = partOffsets[part + 1];
            }

            length = finish - start;
            if (length == 1) {
                length = 2;
                clonePoint = true;
            } else {
                clonePoint = false;
            }

            BUILDER.start(length, dimensions);
            for (int i = 0; i < length; i++) {
                BUILDER.setOrdinate(buffer.getDouble(), 0, i);
                BUILDER.setOrdinate(buffer.getDouble(), 1, i);
            }

            if (clonePoint) {
                BUILDER.setOrdinate(BUILDER.getOrdinate(0, 0), 0, 1);
                BUILDER.setOrdinate(BUILDER.getOrdinate(1, 0), 1, 1);
            }

            lines[part] = BUILDER.end();
        }

        // if we have another coordinate, read and add to the coordinate
        // sequences
        if (dimensions == 3) {
            // z min, max
            buffer.position(buffer.position() + 2 * 8);
            for (int part = 0; part < numParts; part++) {
                start = partOffsets[part];

                if (part == (numParts - 1)) {
                    finish = numPoints;
                } else {
                    finish = partOffsets[part + 1];
                }

                length = finish - start;
                if (length == 1) {
                    length = 2;
                    clonePoint = true;
                } else {
                    clonePoint = false;
                }

                for (int i = 0; i < length; i++) {
                    BUILDER.setOrdinate(lines[part], buffer.getDouble(), 2, i);
                }

            }
        }

        // Prepare line strings and return the multilinestring
        final LineString[] lineStrings = new LineString[numParts];
        for (int part = 0; part < numParts; part++) {
            lineStrings[part] = GEOMETRY_FACTORY.createLineString(lines[part]);
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
