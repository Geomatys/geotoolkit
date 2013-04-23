/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.db.dialect;

import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractSQLDialect implements SQLDialect {

    @Override
    public boolean ignoreTable(String name) {
        return false;
    }

    /**
     * Default implementation handles no filter.
     * Everything will be added in the post filter.
     */
    @Override
    public Filter[] splitFilter(Filter filter) {
        final Filter[] divided = new Filter[2];
        divided[0] = Filter.INCLUDE;
        divided[1] = filter;
        return divided;
    }

    @Override
    public void encodeColumnName(StringBuilder sql, String name) {
        sql.append(getTableEscape()).append(name).append(getTableEscape());
    }

    @Override
    public void encodeColumnAlias(StringBuilder sql, String name) {
        sql.append(" as ");
        encodeColumnName(sql, name);
    }
    
    @Override
    public void encodeSchemaName(StringBuilder sql, String name) {
        sql.append(getTableEscape()).append(name).append(getTableEscape());
    }

    @Override
    public void encodeTableName(StringBuilder sql, String name) {
        sql.append(getTableEscape()).append(name).append(getTableEscape());
    }
    
}
