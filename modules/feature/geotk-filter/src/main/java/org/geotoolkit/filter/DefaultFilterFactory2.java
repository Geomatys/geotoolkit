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

import java.util.List;
import java.util.Set;

import org.geotoolkit.filter.binarycomparison.DefaultPropertyIsEqualTo;
import org.geotoolkit.filter.binarycomparison.DefaultPropertyIsGreaterThan;
import org.geotoolkit.filter.binarycomparison.DefaultPropertyIsGreaterThanOrEqualTo;
import org.geotoolkit.filter.binarycomparison.DefaultPropertyIsLessThan;
import org.geotoolkit.filter.binarycomparison.DefaultPropertyIsLessThanOrEqualTo;
import org.geotoolkit.filter.binarycomparison.DefaultPropertyIsNotEqualTo;
import org.geotoolkit.filter.binaryexpression.DefaultAdd;
import org.geotoolkit.filter.binaryexpression.DefaultDivide;
import org.geotoolkit.filter.binaryexpression.DefaultMultiply;
import org.geotoolkit.filter.binaryexpression.DefaultSubtract;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.filter.identity.DefaultGmlObjectId;
import org.geotoolkit.filter.binarylogic.DefaultAnd;
import org.geotoolkit.filter.binarylogic.DefaultOr;
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
import org.geotoolkit.filter.sort.DefaultSortBy;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.type.Name;
import org.opengis.filter.And;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
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
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.GmlObjectId;
import org.opengis.filter.identity.Identifier;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
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
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default implementation of a GeoAPI filterFactory.
 * All objects created by this factory are immutable.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultFilterFactory2 implements FilterFactory2{

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

        final DefaultBoundingBox env;

        if (srs == null || srs.trim().isEmpty()) {
            env = new DefaultBoundingBox(new double[]{minx, miny}, new double[]{maxx, maxy});
            return bbox(e, env);
        }

        CoordinateReferenceSystem crs = null;
        FactoryException firstException = null;
        try {
            crs = CRS.decode(srs);
        } catch (FactoryException ex) {
            firstException = ex;
        }

        //TODO : Datastore from geotools sucks, they dont even provide the authority name sometimes !!!
        // we are forced add the two next tests

        if(crs == null && !srs.startsWith("CRS:") && !srs.startsWith("EPSG:")){
            //we presume all epsg given are using the epsg authority
            //this is a necessity since the last geotools modules aren't correctly providing the authority
            final String test = "EPSG:"+srs;

            try {
                crs = CRS.decode(test);
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
                crs = CRS.decode(test);
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

        env = new DefaultBoundingBox(crs);
        env.setRange(0, minx, maxx);
        env.setRange(1, miny, maxy);

        return bbox(e,env);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BBOX bbox(final Expression e, final BoundingBox bounds) {
        if(e != null && !(e instanceof PropertyName)){
            throw new IllegalArgumentException("Expression expected to be a PropertyName, instead found a " + e.getClass());
        }
        return new DefaultBBox((PropertyName)e, new DefaultLiteral<BoundingBox>(bounds));
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
//  IDENTIFIERS
//
////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureId featureId(final String id) {
        return new DefaultFeatureId(id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GmlObjectId gmlObjectId(final String id) {
        return new DefaultGmlObjectId(id);
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
    public And and(final Filter filter1, final Filter filter2) {
        return new DefaultAnd(filter1, filter2);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public And and(final List<Filter> filters) {
        return new DefaultAnd(filters);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Or or(final Filter filter1, final Filter filter2) {
        return new DefaultOr(filter1, filter2);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Or or(final List<Filter> filters) {
        return new DefaultOr(filters);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Not not(final Filter filter) {
        return new DefaultNot(filter);
    }

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
    public PropertyName property(final Name name) {
        return property(name.getLocalPart());
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
    public PropertyIsBetween between(final Expression expr,
            final Expression lower, final Expression upper) {
        return new DefaultPropertyIsBetween(expr, lower, upper);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsEqualTo equals(final Expression expr1, final Expression expr2) {
        return equal(expr1,expr2,true);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsEqualTo equal(final Expression expr1,
            final Expression expr2, final boolean matchCase) {
        return new DefaultPropertyIsEqualTo(expr1, expr2, matchCase);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsNotEqualTo notEqual(final Expression expr1, final Expression expr2) {
        return notEqual(expr1, expr2,false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsNotEqualTo notEqual(final Expression expr1,
            final Expression expr2, final boolean matchCase) {
        return new DefaultPropertyIsNotEqualTo(expr1, expr2, matchCase);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsGreaterThan greater(final Expression expr1,
            final Expression expr2) {
        return greater(expr1,expr2,false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsGreaterThan greater(final Expression expr1,
            final Expression expr2, final boolean matchCase) {
        return new DefaultPropertyIsGreaterThan(expr1, expr2, matchCase);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsGreaterThanOrEqualTo greaterOrEqual(
            final Expression expr1, final Expression expr2) {
        return greaterOrEqual(expr1, expr2,false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsGreaterThanOrEqualTo greaterOrEqual(
            final Expression expr1, final Expression expr2, final boolean matchCase) {
        return new DefaultPropertyIsGreaterThanOrEqualTo(expr1, expr2, matchCase);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLessThan less(final Expression expr1, final Expression expr2) {
        return less(expr1, expr2, false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLessThan less(final Expression expr1,
            final Expression expr2, final boolean matchCase) {
        return new DefaultPropertyIsLessThan(expr1, expr2, matchCase);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLessThanOrEqualTo lessOrEqual(
            final Expression expr1, final Expression expr2) {
        return lessOrEqual(expr1, expr2, false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLessThanOrEqualTo lessOrEqual(final Expression expr1,
            final Expression expr2, final boolean matchCase) {
        return new DefaultPropertyIsLessThanOrEqualTo(expr1, expr2, matchCase);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLike like(final Expression expr, final String pattern) {
        return like(expr,pattern,"*","?","\\");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLike like(final Expression expr, final String pattern,
            final String wildcard, final String singleChar, final String escape) {
        return like(expr,pattern,wildcard,singleChar,escape,false);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLike like(final Expression expr, final String pattern,
            final String wildcard, final String singleChar,
            final String escape, final boolean matchCase) {
        return new DefaultPropertyIsLike(expr, pattern, wildcard, singleChar, escape, matchCase);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsNull isNull(final Expression expr) {
        return new DefaultPropertyIsNull(expr);
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
    public Add add(final Expression expr1, final Expression expr2) {
        return new DefaultAdd(expr1, expr2);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Divide divide(final Expression expr1, final Expression expr2) {
        return new DefaultDivide(expr1, expr2);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Multiply multiply(final Expression expr1, final Expression expr2) {
        return new DefaultMultiply(expr1, expr2);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Subtract subtract(final Expression expr1, final Expression expr2) {
        return new DefaultSubtract(expr1, expr2);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function function(final String name, final Expression ... parameters) {
        return org.geotoolkit.filter.function.Functions.function(name, null, parameters);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final Object obj) {
        if(obj instanceof Envelope && !(obj instanceof BoundingBox) ){
            //special case for envelopes to change them in JTS geometries
            return new DefaultEnvelopeLiteral((Envelope) obj);
        }else{
            return new DefaultLiteral<Object>(obj);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final byte b) {
        return new DefaultLiteral<Byte>(b);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final short s) {
        return new DefaultLiteral<Short>(s);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final int i) {
        return new DefaultLiteral<Integer>(i);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final long l) {
        return new DefaultLiteral<Long>(l);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final float f) {
        return new DefaultLiteral<Float>(f);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final double d) {
        return new DefaultLiteral<Double>(d);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final char c) {
        return new DefaultLiteral<Character>(c);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(final boolean b) {
        return new DefaultLiteral<Boolean>(b);
    }

////////////////////////////////////////////////////////////////////////////////
//
//  SORT BY
//
////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public SortBy sort(final String propertyName, final SortOrder order) {
        final PropertyName name = property(propertyName);
        return new DefaultSortBy(name,order);
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
            final IdCapabilities id) {
        return new DefaultFilterCapabilities(version, id, spatial, scalar);
    }

}
