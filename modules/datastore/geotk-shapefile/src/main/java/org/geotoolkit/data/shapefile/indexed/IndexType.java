/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile.indexed;

import org.geotoolkit.data.shapefile.ShpFileType;

/**
 * Enumerates the different types of Shapefile geometry indices there are.
 * 
 * @author jesse
 */
public enum IndexType {
    /**
     * Don't use indexing
     */
    NONE(null),
    /**
     * The same index as mapserver. Its the most reliable and is the default
     */
    QIX(ShpFileType.QIX);

    public final ShpFileType shpFileType;

    private IndexType(final ShpFileType shpFileType) {
        this.shpFileType = shpFileType;
    }
}
