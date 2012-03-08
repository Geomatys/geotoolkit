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
package org.geotoolkit.client.map;

import org.geotoolkit.client.Request;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class QueryTileReference {
    
    public final CoordinateReferenceSystem crs;
    public final MathTransform gridToCRS;
    public final String id;
    public final Request query;

    public QueryTileReference(final String id, final CoordinateReferenceSystem crs, 
            final MathTransform gridToCRS, final Request query) {
        this.crs = crs;
        this.gridToCRS = gridToCRS;
        this.id = id;
        this.query = query;
    }
    
}
