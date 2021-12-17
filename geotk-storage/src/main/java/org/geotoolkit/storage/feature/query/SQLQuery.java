/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.feature.query;

import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SQLQuery extends org.apache.sis.storage.Query {

    private final String statement;
    private final String name;

    public SQLQuery(String statement, String name) {
        this.statement = statement;
        this.name = name;
    }

    public String getStatement() {
        return statement;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setSelection(Envelope envlp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setProjection(String... strings) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
