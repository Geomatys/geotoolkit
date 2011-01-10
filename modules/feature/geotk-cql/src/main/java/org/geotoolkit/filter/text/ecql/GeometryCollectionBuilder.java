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
import com.vividsolutions.jts.geom.GeometryCollection;


/**
 * Builds a {@link GeometryCollection} using the
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
final class GeometryCollectionBuilder extends GeometryBuilder {

    /**
     * @param statement
     * @param resultStack
     */
    public GeometryCollectionBuilder(final String statement, final BuildResultStack resultStack) {
        super(statement, resultStack);
    }

    @Override
    public Geometry build(final int jjtgeometryliteral) throws CQLException {
        final List<Geometry> geometryList = popGeometryLiteral(jjtgeometryliteral);
        final Geometry[] geometries = geometryList.toArray(new Geometry[geometryList.size()]) ;
        final GeometryCollection geometryCollection= getGeometryFactory().createGeometryCollection(geometries);

        return geometryCollection;
    }
}
