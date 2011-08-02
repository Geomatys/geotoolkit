/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import org.geotoolkit.storage.DataStoreException;

/**
 * Wrapper for a Shapefile Null geometry.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class NullHandler extends AbstractShapeHandler {

    public NullHandler(final boolean read3D) {
        super(ShapeType.NULL,read3D);
    }

    public NullHandler(final ShapeType type, final boolean read3D) throws DataStoreException {
        super(type,read3D);
        if (type != ShapeType.NULL) {
            throw new DataStoreException(
                    "NullHandler constructor: expected a type of 0");
        }
    }

    /**
     * Returns the shapefile shape type value for a null
     * 
     * @return int Shapefile.Null
     */
    @Override
    public ShapeType getShapeType() {
        return shapeType;
    }

    @Override
    public int getLength(final Object geometry) {
        return 4;
    }

    @Override
    public Object estimated(final double minX, final double maxX, final double minY, final double maxY) {
        return null;
    }
    
    @Override
    public Object read(final ByteBuffer buffer, final ShapeType type) {
        return createNull();
    }

    private Object createNull() {
        return null;
    }

    @Override
    public void write(final ByteBuffer buffer, final Object geometry) {
    }

}
