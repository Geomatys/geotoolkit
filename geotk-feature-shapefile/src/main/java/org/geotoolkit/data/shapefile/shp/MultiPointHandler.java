/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.geometry.jts.JTS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

/**
 *
 * @author aaime
 * @author Ian Schneider
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MultiPointHandler extends AbstractShapeHandler {

    /** Creates new MultiPointHandler */
    public MultiPointHandler(final boolean read3D) {
        super(ShapeType.POINT,read3D);
    }

    public MultiPointHandler(final ShapeType type, final boolean read3D) throws DataStoreException {
        super(type,read3D);
        if ((type != ShapeType.MULTIPOINT) && (type != ShapeType.MULTIPOINTM)
                && (type != ShapeType.MULTIPOINTZ)) {
            throw new DataStoreException(
                    "Multipointhandler constructor - expected type to be 8, 18, or 28");
        }
    }

    /**
     * Returns the shapefile shape type value for a point
     *
     * @return int Shapefile.POINT
     */
    @Override
    public ShapeType getShapeType() {
        return shapeType;
    }

    /**
     * Calcuates the record length of this object.
     *
     * @return int The length of the record that this shapepoint will take up in
     *         a shapefile
     */
    @Override
    public int getLength(final Object geometry) {
        final MultiPoint mp = (MultiPoint) geometry;

        final int numGeom = mp.getNumGeometries();
        final int length;

        if (shapeType == ShapeType.MULTIPOINT) {
            // two doubles per coord (16 * numgeoms) + 40 for header
            length = (numGeom * 16) + 40;
        } else if (shapeType == ShapeType.MULTIPOINTM) {
            // add the additional MMin, MMax for 16, then 8 per measure
            length = (numGeom * 16) + 40 + 16
                    + (8 * numGeom);
        } else if (shapeType == ShapeType.MULTIPOINTZ) {
            // add the additional ZMin,ZMax, plus 8 per Z
            length = (numGeom * 16) + 40 + 16
                    + (8 * numGeom) + 16
                    + (8 * numGeom);
        } else {
            throw new IllegalStateException("Expected ShapeType of Arc, got "
                    + shapeType);
        }

        return length;
    }

    protected Geometry createNull() {
        return GEOMETRY_FACTORY.createMultiPoint(new Point[0]);
    }

    @Override
    public Geometry estimated(final double minX, final double maxX, final double minY, final double maxY) {
        final Coordinate coord = new Coordinate((minX + maxX) / 2, (minY + maxY) / 2);
        return GEOMETRY_FACTORY.createMultiPoint(new Coordinate[] {coord});
    }

    @Override
    public Geometry read(final ByteBuffer buffer, final ShapeType type) {
        if (type == ShapeType.NULL) {
            return createNull();
        }

        final int dimensions = (read3D && shapeType == ShapeType.MULTIPOINTZ)? 3 : 2;

        // read bounding box (not needed)
        // HACK: noisy cast is needed for java 8 compatibility. Drop it once java 8 is not supported anymore
        ((Buffer)buffer).position(buffer.position() + 32);
        final int numpoints = buffer.getInt();

        final DoubleBuffer dbuffer = buffer.asDoubleBuffer();

        final double[] coords = new double[numpoints*dimensions];
        final int xySize = numpoints*2;
        dbuffer.get(coords,0,xySize);

        if(dimensions==2){
            return GEOMETRY_FACTORY.createMultiPoint(new ShapeCoordinateSequence2D(coords));
        } else {
            // z min, max
            // HACK: noisy cast is needed for java 8 compatibility. Drop it once java 8 is not supported anymore
            ((Buffer) dbuffer).position(dbuffer.position() + 2);
            dbuffer.get(coords,xySize,numpoints);
            return GEOMETRY_FACTORY.createMultiPoint(new ShapeCoordinateSequence3D(coords));
        }

    }

//    @Override
//    public Object read(ByteBuffer buffer, ShapeType type) {
//        if (type == ShapeType.NULL) {
//            return createNull();
//        }
//
//        // read bounding box (not needed)
//        buffer.position(buffer.position() + 4 * 8);
//
//        final int numpoints = buffer.getInt();
//        final Coordinate[] coords = new Coordinate[numpoints];
//
//        for (int t = 0; t < numpoints; t++) {
//            final double x = buffer.getDouble();
//            final double y = buffer.getDouble();
//            coords[t] = new Coordinate(x, y);
//        }
//
//        if (shapeType == ShapeType.MULTIPOINTZ) {
//            //skip zmin zmax
//            buffer.position(buffer.position() + 2 * 8);
//
//            for (int t = 0; t < numpoints; t++) {
//                coords[t].z = buffer.getDouble(); // z
//            }
//        }
//
//        return GEOMETRY_FACTORY.createMultiPoint(coords);
//    }

    @Override
    public void write(final ByteBuffer buffer, final Object geometry) {
        MultiPoint mp = (MultiPoint) geometry;

        Envelope box = mp.getEnvelopeInternal();
        buffer.putDouble(box.getMinX());
        buffer.putDouble(box.getMinY());
        buffer.putDouble(box.getMaxX());
        buffer.putDouble(box.getMaxY());

        buffer.putInt(mp.getNumGeometries());

        for (int t = 0, tt = mp.getNumGeometries(); t < tt; t++) {
            Coordinate c = (mp.getGeometryN(t)).getCoordinate();
            buffer.putDouble(c.x);
            buffer.putDouble(c.y);
        }

        if (shapeType == ShapeType.MULTIPOINTZ) {
            double[] zExtreame = {Double.NaN, Double.NaN};
            JTS.zMinMax(new CoordinateArraySequence(mp.getCoordinates()), zExtreame);

            if (Double.isNaN(zExtreame[0])) {
                buffer.putDouble(0.0);
                buffer.putDouble(0.0);
            } else {
                buffer.putDouble(zExtreame[0]);
                buffer.putDouble(zExtreame[1]);
            }

            for (int t = 0; t < mp.getNumGeometries(); t++) {
                Coordinate c = (mp.getGeometryN(t)).getCoordinate();
                double z = c.z;

                if (Double.isNaN(z)) {
                    buffer.putDouble(0.0);
                } else {
                    buffer.putDouble(z);
                }
            }
        }

        if (shapeType == ShapeType.MULTIPOINTM
                || shapeType == ShapeType.MULTIPOINTZ) {
            buffer.putDouble(-10E40);
            buffer.putDouble(-10E40);

            for (int t = 0; t < mp.getNumGeometries(); t++) {
                buffer.putDouble(-10E40);
            }
        }
    }

}
