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
package org.geotoolkit.jdbc.fid;

import java.util.ArrayList;


/**
 * Primary key for tables which do not have a primary key.
 * <p>
 * New key values are generated "from thin air" and are not persistent.
 * </p>
 * @author Justin Deoliveira, The Open Planning Project
 *
 * @module pending
 */
public class NullPrimaryKey extends PrimaryKey {

    public NullPrimaryKey(final String tableName) {
        super(tableName, new ArrayList());
    }
}
