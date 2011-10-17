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
package org.geotoolkit.data.mysql;


import java.io.IOException;

import org.opengis.filter.expression.Literal;
import org.opengis.filter.spatial.BinarySpatialOperator;

import org.geotoolkit.filter.capability.DefaultFilterCapabilities;
import org.geotoolkit.data.jdbc.FilterToSQL;


public class MySQLFilterToSQL extends FilterToSQL {

    final FilterToSqlHelper helper;

    Integer currentSRID;

    public MySQLFilterToSQL(final MySQLDialect dialect) {
        helper = new FilterToSqlHelper(this);
    }

    @Override
    protected void visitLiteralGeometry(final Literal expression) throws IOException {
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    protected DefaultFilterCapabilities createFilterCapabilities() {
        return FilterToSqlHelper.createFilterCapabilities();
    }

    @Override
    protected Object visitBinarySpatialOperator(final BinarySpatialOperator filter,
            final Object extraData) {
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

}
