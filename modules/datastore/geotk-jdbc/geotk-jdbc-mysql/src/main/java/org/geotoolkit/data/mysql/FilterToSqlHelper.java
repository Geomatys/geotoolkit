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

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.data.jdbc.FilterToSQL;
import org.geotoolkit.filter.capability.DefaultFilterCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialOperators;
import org.geotoolkit.jdbc.dialect.AbstractSQLDialect;

import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.filter.capability.SpatialOperator;
import org.opengis.filter.capability.SpatialOperators;

class FilterToSqlHelper {

    protected static final String IO_ERROR = "io problem writing filter";

    final FilterToSQL delegate;
    Writer out;
    boolean looseBBOXEnabled;

    public FilterToSqlHelper(final FilterToSQL delegate) {
        this.delegate = delegate;
    }

    public static DefaultFilterCapabilities createFilterCapabilities() {

        final List<SpatialOperator> spatialOptsList = new ArrayList<SpatialOperator>();
        spatialOptsList.addAll(AbstractSQLDialect.BASE_DBMS_CAPABILITIES.getSpatialCapabilities().getSpatialOperators().getOperators());

        final SpatialOperators spatialOpts = new DefaultSpatialOperators(spatialOptsList.toArray(new SpatialOperator[spatialOptsList.size()]));
        final SpatialCapabilities spatialCaps = new DefaultSpatialCapabilities(
                AbstractSQLDialect.BASE_DBMS_CAPABILITIES.getSpatialCapabilities().getGeometryOperands().toArray(new GeometryOperand[]{}),
                spatialOpts);
        return new DefaultFilterCapabilities(AbstractSQLDialect.BASE_DBMS_CAPABILITIES.getVersion(),
                                             AbstractSQLDialect.BASE_DBMS_CAPABILITIES.getIdCapabilities(),
                                             spatialCaps,
                                             AbstractSQLDialect.BASE_DBMS_CAPABILITIES.getScalarCapabilities());
    }

}
