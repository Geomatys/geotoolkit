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

package org.geotoolkit.data.osm.model;

import org.opengis.filter.identity.Identifier;

/**
 * Simple Identifier for osm features.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class SimpleId implements Identifier{

    private final Object id;

    public SimpleId(Object id){
        this.id = id;
    }

    @Override
    public Object getID() {
        return id;
    }

    @Override
    public boolean matches(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
