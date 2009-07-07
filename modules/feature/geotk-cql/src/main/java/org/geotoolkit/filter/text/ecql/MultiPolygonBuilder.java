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

import java.util.List;

import org.geotoolkit.filter.text.commons.BuildResultStack;
import org.geotoolkit.filter.text.cql2.CQLException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;


/**
 * Builds the a {@link MultiPolygon} using the {@link Polygon} made in previous steps of
 * parsing process.
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @since
 */
final class MultiPolygonBuilder extends GeometryBuilder {
    /**
     * @param statement
     * @param resultStack
     */
    public MultiPolygonBuilder(final String statement, final BuildResultStack resultStack) {
        super(statement, resultStack);
    }

    /**
     * Build a {@link MultiPolygon} using the polygon node made in previous steps of the
     * parsing process.
     */
    @Override
    public Geometry build(final int polygonNode) throws CQLException {
        final List<Geometry> polygonList = popGeometry(polygonNode);
        final Polygon[] polygons = polygonList.toArray(new Polygon[polygonList.size()]) ;
        return getGeometryFactory().createMultiPolygon(polygons);
    }
}
