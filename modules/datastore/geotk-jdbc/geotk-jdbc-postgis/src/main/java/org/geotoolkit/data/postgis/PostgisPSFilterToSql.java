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
package org.geotoolkit.data.postgis;

import org.geotools.filter.FilterCapabilities;
import org.geotoolkit.jdbc.PreparedFilterToSQL;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BinarySpatialOperator;

public class PostgisPSFilterToSql extends PreparedFilterToSQL {

    final FilterToSqlHelper helper;

    public PostgisPSFilterToSql(final PostGISPSDialect dialect) {
        super(dialect);
        helper = new FilterToSqlHelper(this);
    }

    public boolean isLooseBBOXEnabled() {
        return helper.looseBBOXEnabled;
    }

    public void setLooseBBOXEnabled(final boolean looseBBOXEnabled) {
        helper.looseBBOXEnabled = looseBBOXEnabled;
    }

    @Override
    protected FilterCapabilities createFilterCapabilities() {
        return FilterToSqlHelper.createFilterCapabilities();
    }

    @Override
    protected Object visitBinarySpatialOperator(final BinarySpatialOperator filter,
            final PropertyName property, final Literal geometry, final boolean swapped,
            final Object extraData)
    {
        helper.out = out;
        return helper.visitBinarySpatialOperator(filter, property, geometry, swapped, extraData);
    }

}
