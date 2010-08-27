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

import org.geotoolkit.storage.DataStoreException;


/**
 * Decimation while reading
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DecimatePolygonHandler extends PolygonHandler {

    private final double[] res;

    public DecimatePolygonHandler(boolean read3D, double[] res){
        super(read3D);
        this.res = res;
    }

    public DecimatePolygonHandler(ShapeType type, boolean read3D, double[] res) throws DataStoreException{
        super(type,read3D);
        this.res = res;
    }

}
