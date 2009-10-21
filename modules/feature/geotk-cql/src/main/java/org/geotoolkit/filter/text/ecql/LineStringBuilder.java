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

import java.util.Stack;

import org.geotoolkit.filter.text.commons.BuildResultStack;
import org.geotoolkit.filter.text.cql2.CQLException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;


/**
 * Builds a LineString
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
final class LineStringBuilder extends GeometryBuilder {
    /**
     * @param statement
     * @param resultStack
     */
    public LineStringBuilder(final String statement, final BuildResultStack resultStack) {
        super(statement, resultStack);
    }

    @Override
    public Geometry build(final int pointNode) throws CQLException {
        // Retrieve the linestirng points
        final Stack<Coordinate> pointStack = popCoordinatesOf(pointNode);
        // now pointStack has the coordinate in the correct order
        // the next code creates the coordinate array used to create
        // the lineString
        final Coordinate[] coordinates = asCoordinate(pointStack);
        return getGeometryFactory().createLineString(coordinates);
    }
}
