/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.filter;

import java.util.Set;
import org.apache.sis.filter.DefaultFilterFactory;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.filter.binaryspatial.DefaultBBox;
import org.geotoolkit.filter.binaryspatial.DefaultBeyond;
import org.geotoolkit.filter.binaryspatial.DefaultContains;
import org.geotoolkit.filter.binaryspatial.DefaultCrosses;
import org.geotoolkit.filter.binaryspatial.DefaultDWithin;
import org.geotoolkit.filter.binaryspatial.DefaultDisjoint;
import org.geotoolkit.filter.binaryspatial.DefaultEquals;
import org.geotoolkit.filter.binaryspatial.DefaultIntersect;
import org.geotoolkit.filter.binaryspatial.DefaultOverlaps;
import org.geotoolkit.filter.binaryspatial.DefaultTouches;
import org.geotoolkit.filter.binaryspatial.DefaultWithin;
import org.geotoolkit.filter.capability.DefaultArithmeticOperators;
import org.geotoolkit.filter.capability.DefaultComparisonOperators;
import org.geotoolkit.filter.capability.DefaultFilterCapabilities;
import org.geotoolkit.filter.capability.DefaultFunctionName;
import org.geotoolkit.filter.capability.DefaultFunctions;
import org.geotoolkit.filter.capability.DefaultIdCapabilities;
import org.geotoolkit.filter.capability.DefaultOperator;
import org.geotoolkit.filter.capability.DefaultScalarCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialOperator;
import org.geotoolkit.filter.capability.DefaultSpatialOperators;
import org.geotoolkit.geometry.BoundingBox;
import org.opengis.filter.Id;
import org.opengis.filter.PropertyIsNil;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.capability.ArithmeticOperators;
import org.opengis.filter.capability.ComparisonOperators;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.capability.Functions;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.IdCapabilities;
import org.opengis.filter.capability.Operator;
import org.opengis.filter.capability.ScalarCapabilities;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.filter.capability.SpatialOperator;
import org.opengis.filter.capability.SpatialOperators;
import org.opengis.filter.capability.TemporalCapabilities;
import org.opengis.filter.capability.TemporalOperand;
import org.opengis.filter.capability.TemporalOperators;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.Identifier;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Default implementation of a Types filterFactory.
 * All objects created by this factory are immutable.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultFilterFactory2 extends DefaultFilterFactory {

////////////////////////////////////////////////////////////////////////////////
//
//  SPATIAL FILTERS
//
////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public BBOX bbox(final String propertyName, final double minx,
            final double miny, final double maxx, final double maxy, final String srs) {
        final PropertyName name = property(propertyName);
        return bbox(name,minx,miny,maxx,maxy,srs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BBOX bbox(final Expression e, final double minx, final double miny,
            final double maxx, final double maxy, final String srs) {

        final BoundingBox env;

        if (srs == null || srs.trim().isEmpty()) {
            env = new BoundingBox(new double[]{minx, miny}, new double[]{maxx, maxy});
            return bbox(e, env);
        }

        CoordinateReferenceSystem crs = null;
        FactoryException firstException = null;
        try {
            crs = CRS.forCode(srs);
        } catch (FactoryException ex) {
            firstException = ex;
        }

        //TODO : featurestore from geotools sucks, they dont even provide the authority name sometimes !!!
        // we are forced add the two next tests

        if(crs == null && !srs.startsWith("CRS:") && !srs.startsWith("EPSG:")){
            //we presume all epsg given are using the epsg authority
            //this is a necessity since the last geotools modules aren't correctly providing the authority
            final String test = "EPSG:"+srs;

            try {
                crs = CRS.forCode(test);
            } catch (FactoryException ex) {
                if(firstException == null){
                    firstException = ex;
                }
            }
        }

        if(crs == null && !srs.startsWith("CRS:") && !srs.startsWith("EPSG:")){
            //we presume all epsg given are using the epsg authority
            //this is a necessity since the last geotools modules aren't correctly providing the authority
            final String test = "CRS:"+srs;

            try {
                crs = CRS.forCode(test);
            } catch (FactoryException ex) {
                if(firstException == null){
                    firstException = ex;
                }
            }
        }


        if(crs == null){
            throw new IllegalArgumentException("Invalid srs : " +srs +" , check that you have the corresponding authority registered." +
                    "\n primary exception : "+firstException.getMessage(), firstException);
        }

        env = new BoundingBox(crs);
        env.setRange(0, minx, maxx);
        env.setRange(1, miny, maxy);

        return bbox(e,env);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BBOX bbox(final Expression e, final Envelope bounds) {
        if(e != null && !(e instanceof PropertyName)){
            throw new IllegalArgumentException("Expression expected to be a PropertyName, instead found a " + e.getClass());
        }
        return new DefaultBBox((PropertyName)e, new DefaultLiteral<>(BoundingBox.castOrCopy(bounds)));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Beyond beyond(final String propertyName, final Geometry geometry,
            final double distance, final String units) {
        final PropertyName name = property(propertyName);
        final Literal geom = literal(geometry);
        return beyond(name, geom,distance,units);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Beyond beyond(final Expression left, final Expression right,
            final double distance, final String units) {
        return new DefaultBeyond(left, right, distance, units);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Contains contains(final String propertyName, final Geometry geometry) {
        final PropertyName name = property(propertyName);
        final Literal geom = literal(geometry);
        return contains(name, geom);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Contains contains(final Expression left, final Expression right) {
        return new DefaultContains(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Crosses crosses(final String propertyName, final Geometry geometry) {
        final PropertyName name = property(propertyName);
        final Literal geom = literal(geometry);
        return crosses(name, geom);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Crosses crosses(final Expression left, final Expression right) {
        return new DefaultCrosses(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Disjoint disjoint(final String propertyName, final Geometry geometry) {
        final PropertyName name = property(propertyName);
        final Literal geom = literal(geometry);
        return disjoint(name, geom);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Disjoint disjoint(final Expression left, final Expression right) {
        return new DefaultDisjoint(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DWithin dwithin(final String propertyName, final Geometry geometry,
            final double distance, final String units) {
        final PropertyName name = property(propertyName);
        final Literal geom = literal(geometry);
        return dwithin(name, geom,distance,units);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DWithin dwithin(final Expression left, final Expression right,
            final double distance, final String units) {
        return new DefaultDWithin(left, right, distance, units);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Equals equals(final String propertyName, final Geometry geometry) {
        final PropertyName name = property(propertyName);
        final Literal geom = literal(geometry);
        return equal(name, geom);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Equals equal(final Expression left, final Expression right) {
        return new DefaultEquals(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Intersects intersects(final String propertyName, final Geometry geometry) {
        final PropertyName name = property(propertyName);
        final Literal geom = literal(geometry);
        return intersects(name, geom);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Intersects intersects(final Expression left, final Expression right) {
        return new DefaultIntersect(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Overlaps overlaps(final String propertyName, final Geometry geometry) {
        final PropertyName name = property(propertyName);
        final Literal geom = literal(geometry);
        return overlaps(name, geom);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Overlaps overlaps(final Expression left, final Expression right) {
        return new DefaultOverlaps(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Touches touches(final String propertyName, final Geometry geometry) {
        final PropertyName name = property(propertyName);
        final Literal geom = literal(geometry);
        return touches(name, geom);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Touches touches(final Expression left, final Expression right) {
        return new DefaultTouches(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Within within(final String propertyName, final Geometry geometry) {
        final PropertyName name = property(propertyName);
        final Literal geom = literal(geometry);
        return within(name, geom);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Within within(final Expression left, final Expression right) {
        return new DefaultWithin(left, right);
    }

////////////////////////////////////////////////////////////////////////////////
//
//  FILTERS
//
////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public Id id(final Set<? extends Identifier> ids) {
        return new DefaultId(ids);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyName property(final GenericName name) {
        return property(name.toString());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyName property(final String name) {
        return new DefaultPropertyName(name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsNull isNull(final Expression expr) {
        return new DefaultPropertyIsNull(expr);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsNil isNil(Expression expr) {
        return new DefaultPropertyIsNil(expr);
    }

////////////////////////////////////////////////////////////////////////////////
//
//  EXPRESSIONS
//
////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final Object obj) {
        if(obj instanceof Envelope && !(obj instanceof BoundingBox) ){
            //special case for envelopes to change them in JTS geometries
            return new DefaultEnvelopeLiteral((Envelope) obj);
        }else{
            return new DefaultLiteral<>(obj);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final byte b) {
        return new DefaultLiteral<>(b);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final short s) {
        return new DefaultLiteral<>(s);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final int i) {
        return new DefaultLiteral<>(i);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final long l) {
        return new DefaultLiteral<>(l);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final float f) {
        return new DefaultLiteral<>(f);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final double d) {
        return new DefaultLiteral<>(d);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final char c) {
        return new DefaultLiteral<>(c);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final boolean b) {
        return new DefaultLiteral<>(b);
    }

////////////////////////////////////////////////////////////////////////////////
//
//  CAPABILITIES
//
////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public Operator operator(final String name) {
        return new DefaultOperator(name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SpatialOperator spatialOperator(final String name,
            final GeometryOperand[] geometryOperands) {
        return new DefaultSpatialOperator(name, geometryOperands);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FunctionName functionName(final String name, final int nargs) {
        return new DefaultFunctionName(name, null, nargs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Functions functions(final FunctionName[] functionNames) {
        return new DefaultFunctions(functionNames);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SpatialOperators spatialOperators(final SpatialOperator[] spatialOperators) {
        return new DefaultSpatialOperators(spatialOperators);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ComparisonOperators comparisonOperators(final Operator[] comparisonOperators) {
        return new DefaultComparisonOperators(comparisonOperators);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ArithmeticOperators arithmeticOperators(final boolean simple,
            final Functions functions) {
        return new DefaultArithmeticOperators(simple, functions);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ScalarCapabilities scalarCapabilities(final ComparisonOperators comparison,
            final ArithmeticOperators arithmetic, final boolean logical) {
        return new DefaultScalarCapabilities(logical, comparison, arithmetic);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SpatialCapabilities spatialCapabilities(
            final GeometryOperand[] geometryOperands, final SpatialOperators spatial) {
        return new DefaultSpatialCapabilities(geometryOperands, spatial);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public IdCapabilities idCapabilities(final boolean eid, final boolean fid) {
        return new DefaultIdCapabilities(eid, fid);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FilterCapabilities capabilities(final String version,
            final ScalarCapabilities scalar, final SpatialCapabilities spatial,
            final TemporalCapabilities temporal, final IdCapabilities id) {
        return new DefaultFilterCapabilities(version, id, spatial, scalar, temporal);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalCapabilities temporalCapabilities(TemporalOperand[] temporalOperands, TemporalOperators temporal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
