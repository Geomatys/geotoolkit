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

import java.nio.ByteBuffer;
import org.apache.sis.storage.DataStoreException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

/**
 * Wrapper for a Shapefile point.
 *
 * @author aaime
 * @author Ian Schneider
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class PointHandler extends AbstractShapeHandler {

    public PointHandler(final boolean read3D) {
        super(ShapeType.POINT,read3D); //2d
    }

    public PointHandler(final ShapeType type, final boolean read3D) throws DataStoreException {
        super(type,read3D);
        if ((type != ShapeType.POINT) && (type != ShapeType.POINTM)
                && (type != ShapeType.POINTZ)) { // 2d, 2d+m, 3d+m
            throw new DataStoreException(
                    "PointHandler constructor: expected a type of 1, 11 or 21");
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

    @Override
    public int getLength(final Object geometry) {
        final int length;
        if (shapeType == ShapeType.POINT) {
            length = 20;
        } else if (shapeType == ShapeType.POINTM) {
            length = 28;
        } else if (shapeType == ShapeType.POINTZ) {
            length = 36;
        } else {
            throw new IllegalStateException("Expected ShapeType of Point, got"
                    + shapeType);
        }
        return length;
    }

    @Override
    public Geometry estimated(final double minX, final double maxX, final double minY, final double maxY) {
        final Coordinate coord = new Coordinate((minX + maxX) / 2, (minY + maxY) / 2);
        return GEOMETRY_FACTORY.createPoint(coord);
    }

    @Override
    public Geometry read(final ByteBuffer buffer, final ShapeType type) {
        if (type == ShapeType.NULL) {
            return createNull();
        }

        final double x = buffer.getDouble();
        final double y = buffer.getDouble();
        final double z;

        if (shapeType == ShapeType.POINTM) {
            buffer.getDouble();
        }

        if (shapeType == ShapeType.POINTZ) {
            z = buffer.getDouble();
        }else{
            z = Double.NaN;
        }

        return GEOMETRY_FACTORY.createPoint(new Coordinate(x, y, z));
    }

    private Geometry createNull() {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(Double.NaN,
                Double.NaN, Double.NaN));
    }

    @Override
    public void write(final ByteBuffer buffer, final Object geometry) {
        Coordinate c = ((Point) geometry).getCoordinate();

        buffer.putDouble(c.x);
        buffer.putDouble(c.y);

        if (shapeType == ShapeType.POINTZ) {
            if (Double.isNaN(c.z)) { // nan means not defined
                buffer.putDouble(0.0);
            } else {
                buffer.putDouble(c.z);
            }
        }

        if ((shapeType == ShapeType.POINTZ) || (shapeType == ShapeType.POINTM)) {
            buffer.putDouble(-10E40); // M
        }
    }

}
