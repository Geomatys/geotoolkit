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

package org.geotoolkit.data.query;

import org.geotoolkit.data.session.Session;
import org.opengis.feature.type.Name;

/**
 * Default selector implementation.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class DefaultSelector implements Selector{

    private final Session session;
    private final Name typeName;
    private final String name;

    public DefaultSelector(Session session, Name typeName, String selectorName) {

        if(typeName == null){
            throw new NullPointerException("Selector feature type name must not be null.");
        }
        if(selectorName == null){
            throw new NullPointerException("Selector name must not be null.");
        }

        this.session = session;
        this.typeName = typeName;
        this.name = selectorName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Session getSession() {
        return session;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Name getFeatureTypeName() {
        return typeName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getSelectorName() {
        return name;
    }

}
