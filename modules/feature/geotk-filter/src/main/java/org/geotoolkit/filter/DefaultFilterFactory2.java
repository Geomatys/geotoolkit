/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.filter.binarycomparison.DefaultPropertyIsNotEqualTo;
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
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 * Default implementation of a GeoAPI filterFactory.
 * All objects created by this factory are immutable.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultFilterFactory2 implements FilterFactory2{

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyName property(Name name) {
        return new DefaultPropertyName(name.getURI());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLike like(Expression expr, String pattern, String wildcard, String singleChar, String escape, boolean matchCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BBOX bbox(Expression e, double minx, double miny, double maxx, double maxy, String srs) {

        final DefaultBoundingBox env;
        try {
            env = new DefaultBoundingBox(CRS.decode(srs));
        } catch (NoSuchAuthorityCodeException ex) {
            throw new IllegalArgumentException("Could not fine CRS for srs " + srs, ex);
        } catch (FactoryException ex) {
            throw new IllegalArgumentException("Could not fine CRS for srs " + srs, ex);
        }
        env.setRange(0, minx, maxx);
        env.setRange(1, miny, maxy);

        return bbox(e,env);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BBOX bbox(Expression e, BoundingBox bounds) {
        if(!(e instanceof PropertyName)){
            throw new IllegalArgumentException("Expression expected to be a PropertyName, instead found a " + e.getClass());
        }
        return new DefaultBBox((PropertyName)e, new DefaultLiteral<Envelope>(bounds));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Beyond beyond(Expression left, Expression right, double distance, String units) {
        return new DefaultBeyond(left, right, distance, units);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Contains contains(Expression left, Expression right) {
        return new DefaultContains(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Crosses crosses(Expression left, Expression right) {
        return new DefaultCrosses(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Disjoint disjoint(Expression left, Expression right) {
        return new DefaultDisjoint(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DWithin dwithin(Expression left, Expression right, double distance, String units) {
        return new DefaultDWithin(left, right, distance, units);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Equals equal(Expression left, Expression right) {
        return new DefaultEquals(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Intersects intersects(Expression left, Expression right) {
        return new DefaultIntersect(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Overlaps overlaps(Expression left, Expression right) {
        return new DefaultOverlaps(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Touches touches(Expression left, Expression right) {
        return new DefaultTouches(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Within within(Expression left, Expression right) {
        return new DefaultWithin(left, right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureId featureId(String id) {
        return new DefaultFeatureId(id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GmlObjectId gmlObjectId(String id) {
        return new DefaultGmlObjectId(id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public And and(Filter filter1, Filter filter2) {
        return new DefaultAnd(filter1, filter2);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public And and(List<Filter> filters) {
        return new DefaultAnd(filters);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Or or(Filter filter1, Filter filter2) {
        return new DefaultOr(filter1, filter2);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Or or(List<Filter> filters) {
        return new DefaultOr(filters);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Not not(Filter filter) {
        return new DefaultNot(filter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Id id(Set<? extends Identifier> ids) {
        return new DefaultId(ids);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyName property(String name) {
        return new DefaultPropertyName(name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsBetween between(Expression expr, Expression lower, Expression upper) {
        return new DefaultPropertyIsBetween(expr, lower, upper);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsEqualTo equals(Expression expr1, Expression expr2) {
        return equal(expr1,expr2,true);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsEqualTo equal(Expression expr1, Expression expr2, boolean matchCase) {
        return new DefaultPropertyIsEqualTo(expr1, expr2, matchCase);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsNotEqualTo notEqual(Expression expr1, Expression expr2) {
        return notEqual(expr1, expr2,false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsNotEqualTo notEqual(Expression expr1, Expression expr2, boolean matchCase) {
        return new DefaultPropertyIsNotEqualTo(expr1, expr2, matchCase);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsGreaterThan greater(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsGreaterThan greater(Expression expr1, Expression expr2, boolean matchCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsGreaterThanOrEqualTo greaterOrEqual(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsGreaterThanOrEqualTo greaterOrEqual(Expression expr1, Expression expr2, boolean matchCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLessThan less(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLessThan less(Expression expr1, Expression expr2, boolean matchCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLessThanOrEqualTo lessOrEqual(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLessThanOrEqualTo lessOrEqual(Expression expr1, Expression expr2, boolean matchCase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLike like(Expression expr, String pattern) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsLike like(Expression expr, String pattern, String wildcard, String singleChar, String escape) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyIsNull isNull(Expression expr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BBOX bbox(String propertyName, double minx, double miny, double maxx, double maxy, String srs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Beyond beyond(String propertyName, Geometry geometry, double distance, String units) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Contains contains(String propertyName, Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Crosses crosses(String propertyName, Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Disjoint disjoint(String propertyName, Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DWithin dwithin(String propertyName, Geometry geometry, double distance, String units) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Equals equals(String propertyName, Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Intersects intersects(String propertyName, Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Overlaps overlaps(String propertyName, Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Touches touches(String propertyName, Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Within within(String propertyName, Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Add add(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Divide divide(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Multiply multiply(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Subtract subtract(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function function(String name, Expression[] args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function function(String name, Expression arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function function(String name, Expression arg1, Expression arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function function(String name, Expression arg1, Expression arg2, Expression arg3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(Object obj) {
        return new DefaultLiteral<Object>(obj);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(byte b) {
        return new DefaultLiteral<Byte>(b);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(short s) {
        return new DefaultLiteral<Short>(s);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(int i) {
        return new DefaultLiteral<Integer>(i);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(long l) {
        return new DefaultLiteral<Long>(l);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(float f) {
        return new DefaultLiteral<Float>(f);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(double d) {
        return new DefaultLiteral<Double>(d);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(char c) {
        return new DefaultLiteral<Character>(c);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal literal(boolean b) {
        return new DefaultLiteral<Boolean>(b);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SortBy sort(String propertyName, SortOrder order) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Operator operator(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SpatialOperator spatialOperator(String name, GeometryOperand[] geometryOperands) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FunctionName functionName(String name, int nargs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Functions functions(FunctionName[] functionNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SpatialOperators spatialOperators(SpatialOperator[] spatialOperators) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ComparisonOperators comparisonOperators(Operator[] comparisonOperators) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ArithmeticOperators arithmeticOperators(boolean simple, Functions functions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ScalarCapabilities scalarCapabilities(ComparisonOperators comparison, ArithmeticOperators arithmetic, boolean logical) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SpatialCapabilities spatialCapabilities(GeometryOperand[] geometryOperands, SpatialOperators spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public IdCapabilities idCapabilities(boolean eid, boolean fid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FilterCapabilities capabilities(String version, ScalarCapabilities scalar, SpatialCapabilities spatial, IdCapabilities id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
