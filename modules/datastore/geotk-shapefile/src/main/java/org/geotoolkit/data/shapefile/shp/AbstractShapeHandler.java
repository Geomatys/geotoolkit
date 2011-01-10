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

import com.vividsolutions.jts.geom.GeometryFactory;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractShapeHandler implements ShapeHandler {

    protected static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    protected final ShapeType shapeType;
    protected final boolean read3D;
    
    public AbstractShapeHandler(final ShapeType shapeType,final boolean read3D){
        this.shapeType = shapeType;
        this.read3D = read3D;
    }

    @Override
    public final boolean read3D(){
        return read3D;
    }

}
