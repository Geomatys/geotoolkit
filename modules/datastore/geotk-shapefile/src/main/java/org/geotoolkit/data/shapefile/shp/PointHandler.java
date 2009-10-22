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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

/**
 * Wrapper for a Shapefile point.
 * 
 * @author aaime
 * @author Ian Schneider
 * 
 * @module pending
 */
public class PointHandler implements ShapeHandler {

    final ShapeType shapeType;

    public PointHandler(ShapeType type) throws ShapefileException {
        if ((type != ShapeType.POINT) && (type != ShapeType.POINTM)
                && (type != ShapeType.POINTZ)) { // 2d, 2d+m, 3d+m
            throw new ShapefileException(
                    "PointHandler constructor: expected a type of 1, 11 or 21");
        }

        shapeType = type;
    }

    public PointHandler() {
        shapeType = ShapeType.POINT; // 2d
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
    public int getLength(Object geometry) {
        int length;
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
    public Object read(ByteBuffer buffer, ShapeType type) {
        if (type == ShapeType.NULL) {
            return createNull();
        }

        double x = buffer.getDouble();
        double y = buffer.getDouble();
        double z = Double.NaN;

        if (shapeType == ShapeType.POINTM) {
            buffer.getDouble();
        }

        if (shapeType == ShapeType.POINTZ) {
            z = buffer.getDouble();
        }

        return GEOMETRY_FACTORY.createPoint(new Coordinate(x, y, z));
    }

    private Object createNull() {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(Double.NaN,
                Double.NaN, Double.NaN));
    }

    @Override
    public void write(ByteBuffer buffer, Object geometry) {
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
