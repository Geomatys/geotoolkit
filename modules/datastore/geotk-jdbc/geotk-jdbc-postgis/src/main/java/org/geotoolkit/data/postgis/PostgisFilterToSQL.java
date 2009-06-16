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

import com.vividsolutions.jts.geom.Coordinate;
import java.io.IOException;

import org.geotoolkit.data.jdbc.FilterToSQL;
import org.geotools.filter.FilterCapabilities;
import org.geotoolkit.jdbc.JDBCDataStore;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BinarySpatialOperator;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.geotoolkit.factory.FactoryFinder;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.Envelope;

public class PostgisFilterToSQL extends FilterToSQL {

    final FilterToSqlHelper helper;

    Integer currentSRID;

    public PostgisFilterToSQL(final PostGISDialect dialect) {
        helper = new FilterToSqlHelper(this);
    }

    public boolean isLooseBBOXEnabled() {
        return helper.looseBBOXEnabled;
    }

    public void setLooseBBOXEnabled(final boolean looseBBOXEnabled) {
        helper.looseBBOXEnabled = looseBBOXEnabled;
    }

    @Override
    protected void visitLiteralGeometry(final Literal expression) throws IOException {
        // evaluate the literal and store it for later
        Geometry geom = (Geometry) evaluateLiteral(expression, Geometry.class);

        if ( geom instanceof LinearRing ) {
            //postgis does not handle linear rings, convert to just a line string
            geom = geom.getFactory().createLineString(((LinearRing) geom).getCoordinateSequence());
        }

        out.write("GeomFromText('");
        out.write(geom.toText());
        out.write("', " + currentSRID + ")");
    }

    @Override
    protected FilterCapabilities createFilterCapabilities() {
        return FilterToSqlHelper.createFilterCapabilities();
    }

    @Override
    protected Object visitBinarySpatialOperator(final BinarySpatialOperator filter,
            final Object extraData) {
        // basic checks
        if (filter == null)
            throw new NullPointerException(
                    "Filter to be encoded cannot be null");

        final PropertyName property = (PropertyName) filter.getExpression1();
        Literal geometry = (Literal) filter.getExpression2();

        final Object obj = geometry.getValue();
        if (obj instanceof Envelope) {
            final Envelope env = (Envelope) obj;
            final FilterFactory ff = FactoryFinder.getFilterFactory(null);
            final GeometryFactory gf = new GeometryFactory();
            final Coordinate[] coords = new Coordinate[5];
            coords[0] = new Coordinate(env.getMinimum(0), env.getMinimum(1));
            coords[1] = new Coordinate(env.getMinimum(0), env.getMaximum(1));
            coords[2] = new Coordinate(env.getMaximum(0), env.getMaximum(1));
            coords[3] = new Coordinate(env.getMaximum(0), env.getMinimum(1));
            coords[4] = new Coordinate(env.getMinimum(0), env.getMinimum(1));
            final LinearRing ring = gf.createLinearRing(coords);
            final Geometry geom = gf.createPolygon(ring, new LinearRing[0]);
            geometry = ff.literal(geom);
        }

//        if (!(filter instanceof BinaryComparisonOperator))
//            throw new IllegalArgumentException(
//                    "This filter is not a binary comparison, "
//                            + "can't do SDO relate against it: "
//                            + filter.getClass());
//
//        // extract the property name and the geometry literal
//        PropertyName property;
//        Literal geometry;
//        BinaryComparisonOperator op = (BinaryComparisonOperator) filter;
//        if (op.getExpression1() instanceof PropertyName
//                && op.getExpression2() instanceof Literal) {
//            property = (PropertyName) op.getExpression1();
//            geometry = (Literal) op.getExpression2();
//        } else if (op.getExpression2() instanceof PropertyName
//                && op.getExpression1() instanceof Literal) {
//            property = (PropertyName) op.getExpression2();
//            geometry = (Literal) op.getExpression1();
//        } else {
//            throw new IllegalArgumentException(
//                    "Can only encode spatial filters that do "
//                            + "compare a property name and a geometry");
//        }
//
        // handle native srid
        currentSRID = null;
        if (featureType != null) {
            // going thru evaluate ensures we get the proper result even if the
            // name has
            // not been specified (convention -> the default geometry)
            final AttributeDescriptor descriptor = (AttributeDescriptor) property
                    .evaluate(featureType);
            if (descriptor instanceof GeometryDescriptor) {
                currentSRID = (Integer) descriptor.getUserData().get(
                        JDBCDataStore.JDBC_NATIVE_SRID);
            }
        }

        return visitBinarySpatialOperator(filter, property, geometry, filter
                .getExpression1() instanceof Literal, extraData);
    }

    protected Object visitBinarySpatialOperator(final BinarySpatialOperator filter,
            final PropertyName property, final Literal geometry, final boolean swapped,
            final Object extraData) {
        helper.out = out;
        return helper.visitBinarySpatialOperator(filter, property, geometry,
                swapped, extraData);
    }

}
