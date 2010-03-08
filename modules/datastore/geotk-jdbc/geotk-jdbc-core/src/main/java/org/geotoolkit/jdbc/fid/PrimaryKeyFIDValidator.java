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

import org.geotoolkit.filter.visitor.SimplifyingFilterVisitor.FIDValidator;
import org.geotoolkit.jdbc.DefaultJDBCDataStore;


/**
 * Fid validator which validates with respect to a primary key.
 *
 * @author Justin Deoliveira, OpenGeo
 * @module pending
 */
public class PrimaryKeyFIDValidator implements FIDValidator {

    private final PrimaryKey key;
    private final DefaultJDBCDataStore store;

    public PrimaryKeyFIDValidator(DefaultJDBCDataStore store, PrimaryKey key) {
        this.store = store;
        this.key = key;
    }

    @Override
    public boolean isValid(final String fid) {
        try {
            PrimaryKey.decodeFID(key, fid, true);
            return true;
        }
        catch(IllegalArgumentException e) {
            return false;
        }
    }
}
