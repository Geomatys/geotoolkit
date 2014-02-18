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


package org.geotoolkit.data.query;

import org.geotoolkit.data.session.Session;
import org.opengis.feature.type.Name;

/**
 *
 * @author Johann Sorel (Geoamtys)
 * @module pending
 */
public class DefaultTextStatement implements TextStatement {

    private final Name name;
    private final String statement;
    private final Session session;

    public DefaultTextStatement(final String statement, final Session session, final Name name) {
        this.statement = statement;
        this.session = session;
        this.name = name;
    }

    @Override
    public String getStatement() {
        return statement;
    }
    
    @Override
    public Session getSession(){
        return session;
    }

    @Override
    public Name getName() {
        return name;
    }
    
}
