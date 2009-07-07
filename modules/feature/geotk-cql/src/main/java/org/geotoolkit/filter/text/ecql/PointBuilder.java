/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.text.ecql;

import org.geotoolkit.filter.text.commons.BuildResultStack;
import org.geotoolkit.filter.text.commons.IToken;
import org.geotoolkit.filter.text.commons.Result;
import org.geotoolkit.filter.text.cql2.CQLException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;


/**
 * Builds a point using the coordinates of stack that maintain the coordinates made
 * in the parsing process.
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @since 2.6
 */
final class PointBuilder extends GeometryBuilder {
    public PointBuilder(final String stmt, final BuildResultStack resultStack) {
        super (stmt, resultStack);
    }

    /**
     * Builds a Point geometry
     */
    @Override
    public Geometry build() throws CQLException {
        final Result result = getResultStack().popResult();
        final IToken token = result.getToken();
        try {
            final Coordinate coordinate = (Coordinate) result.getBuilt();
            return getGeometryFactory().createPoint(coordinate);
        } catch (ClassCastException e) {
            throw new CQLException(e.getMessage(), token, getStatemet());
        }
    }
}
