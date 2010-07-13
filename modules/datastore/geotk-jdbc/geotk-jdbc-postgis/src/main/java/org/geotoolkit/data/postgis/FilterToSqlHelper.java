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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.data.jdbc.FilterToSQL;
import org.geotoolkit.filter.capability.DefaultFilterCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialOperator;
import org.geotoolkit.filter.capability.DefaultSpatialOperators;
import org.geotoolkit.jdbc.dialect.AbstractSQLDialect;

import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.filter.capability.SpatialOperator;
import org.opengis.filter.capability.SpatialOperators;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.DistanceBufferOperator;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;

class FilterToSqlHelper {

    protected static final String IO_ERROR = "io problem writing filter";

    final FilterToSQL delegate;
    Writer out;
    boolean looseBBOXEnabled;

    public FilterToSqlHelper(final FilterToSQL delegate) {
        this.delegate = delegate;
    }

    public static DefaultFilterCapabilities createFilterCapabilities() {
        final GeometryOperand[] operandEnvelope = new GeometryOperand[]{GeometryOperand.Envelope};
        final SpatialOperator operatorBBOX       = new DefaultSpatialOperator(BBOX.NAME      , operandEnvelope);
        final SpatialOperator operatorBeyond     = new DefaultSpatialOperator(Beyond.NAME    , operandEnvelope);
        final SpatialOperator operatorContains   = new DefaultSpatialOperator(Contains.NAME  , operandEnvelope);
        final SpatialOperator operatorCrosses    = new DefaultSpatialOperator(Crosses.NAME   , operandEnvelope);
        final SpatialOperator operatorDisjoint   = new DefaultSpatialOperator(Disjoint.NAME  , operandEnvelope);
        final SpatialOperator operatorDWithin    = new DefaultSpatialOperator(DWithin.NAME   , operandEnvelope);
        final SpatialOperator operatorEquals     = new DefaultSpatialOperator(Equals.NAME    , operandEnvelope);
        final SpatialOperator operatorIntersects = new DefaultSpatialOperator(Intersects.NAME, operandEnvelope);
        final SpatialOperator operatorOverlaps   = new DefaultSpatialOperator(Overlaps.NAME  , operandEnvelope);
        final SpatialOperator operatorTouches    = new DefaultSpatialOperator(Touches.NAME   , operandEnvelope);
        final SpatialOperator operatorWithin     = new DefaultSpatialOperator(Within.NAME    , operandEnvelope);

        final List<SpatialOperator> spatialOptsList = new ArrayList<SpatialOperator>();
        spatialOptsList.addAll(AbstractSQLDialect.BASE_DBMS_CAPABILITIES.getSpatialCapabilities().getSpatialOperators().getOperators());
        spatialOptsList.add(operatorBBOX);
        spatialOptsList.add(operatorBeyond);
        spatialOptsList.add(operatorContains);
        spatialOptsList.add(operatorCrosses);
        spatialOptsList.add(operatorDisjoint);
        spatialOptsList.add(operatorDWithin);
        spatialOptsList.add(operatorEquals);
        spatialOptsList.add(operatorIntersects);
        spatialOptsList.add(operatorOverlaps);
        spatialOptsList.add(operatorTouches);
        spatialOptsList.add(operatorWithin);

        final SpatialOperators spatialOpts = new DefaultSpatialOperators(spatialOptsList.toArray(new SpatialOperator[spatialOptsList.size()]));
        final SpatialCapabilities spatialCaps = new DefaultSpatialCapabilities(
                AbstractSQLDialect.BASE_DBMS_CAPABILITIES.getSpatialCapabilities().getGeometryOperands().toArray(new GeometryOperand[]{}),
                spatialOpts);
        return new DefaultFilterCapabilities(AbstractSQLDialect.BASE_DBMS_CAPABILITIES.getVersion(),
                                             AbstractSQLDialect.BASE_DBMS_CAPABILITIES.getIdCapabilities(),
                                             spatialCaps,
                                             AbstractSQLDialect.BASE_DBMS_CAPABILITIES.getScalarCapabilities());
    }

    protected Object visitBinarySpatialOperator(final BinarySpatialOperator filter,
            final PropertyName property, final Literal geometry, final boolean swapped,
            final Object extraData)
    {
        try {
            if (filter instanceof DistanceBufferOperator) {
                visitDistanceSpatialOperator((DistanceBufferOperator) filter,
                        property, geometry, swapped, extraData);
            } else {
                visitComparisonSpatialOperator(filter, property, geometry,
                        swapped, extraData);
            }
        } catch (IOException e) {
            throw new RuntimeException(IO_ERROR, e);
        }
        return extraData;
    }

    void visitDistanceSpatialOperator(final DistanceBufferOperator filter, final PropertyName property,
            final Literal geometry, final boolean swapped, final Object extraData) throws IOException
    {
        if ((filter instanceof DWithin && !swapped)
                || (filter instanceof Beyond && swapped)) {
            out.write("ST_DWITHIN(");
            property.accept(delegate, extraData);
            out.write(",");
            geometry.accept(delegate, extraData);
            out.write(",");
            out.write(Double.toString(filter.getDistance()));
            out.write(")");
        }
        if ((filter instanceof DWithin && swapped)
                || (filter instanceof Beyond && !swapped)) {
            out.write("ST_DISTANCE(");
            property.accept(delegate, extraData);
            out.write(",");
            geometry.accept(delegate, extraData);
            out.write(") > ");
            out.write(Double.toString(filter.getDistance()));
        }
    }

    void visitComparisonSpatialOperator(final BinarySpatialOperator filter, final PropertyName property,
            final Literal geometry, final boolean swapped, final Object extraData) throws IOException
    {

        if(!(filter instanceof Disjoint)) {
            property.accept(delegate, extraData);
            out.write(" && ");
            geometry.accept(delegate, extraData);

            // if we're just encoding a bbox in loose mode, we're done
            if(filter instanceof BBOX && looseBBOXEnabled)
                return;

            out.write(" AND ");
        }

        String closingParenthesis = ")";
        if (filter instanceof Equals) {
            out.write("equals");
        } else if (filter instanceof Disjoint) {
            out.write("NOT (intersects");
            closingParenthesis += ")";
        } else if (filter instanceof Intersects || filter instanceof BBOX) {
            out.write("intersects");
        } else if (filter instanceof Crosses) {
            out.write("crosses");
        } else if (filter instanceof Within) {
            if(swapped)
                out.write("contains");
            else
                out.write("within");
        } else if (filter instanceof Contains) {
            if(swapped)
                out.write("within");
            else
                out.write("contains");
        } else if (filter instanceof Overlaps) {
            out.write("overlaps");
        } else if (filter instanceof Touches) {
            out.write("touches");
        } else {
            throw new IOException("Unsupported filter type " + filter.getClass());
        }
        out.write("(");

        property.accept(delegate, extraData);
        out.write(", ");
        geometry.accept(delegate, extraData);

        out.write(closingParenthesis);

    }
}
