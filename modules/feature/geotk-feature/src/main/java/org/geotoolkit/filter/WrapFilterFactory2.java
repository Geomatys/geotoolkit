/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
import org.opengis.filter.And;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.MatchAction;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNil;
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
import org.opengis.filter.capability.TemporalCapabilities;
import org.opengis.filter.capability.TemporalOperand;
import org.opengis.filter.capability.TemporalOperators;
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
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.AnyInteracts;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.OverlappedBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.filter.temporal.TOverlaps;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.util.GenericName;

/**
 * Wrap a filter factory.
 * Can be used as a base class to extend a factory capabilities.
 *
 * @author Johann Sorel (Geomatys)
 */
public class WrapFilterFactory2 implements FilterFactory2 {

    protected final FilterFactory2 factory;

    public WrapFilterFactory2(FilterFactory2 factory) {
        this.factory = factory;
    }

    @Override
    public PropertyName property(GenericName name) {
        return factory.property(name);
    }

    @Override
    public PropertyIsLike like(Expression expr, String pattern, String wildcard, String singleChar, String escape, boolean matchCase) {
        return factory.like(expr, pattern, wildcard, singleChar, escape, matchCase);
    }

    @Override
    public BBOX bbox(Expression geometry, double minx, double miny, double maxx, double maxy, String srs) {
        return factory.bbox(geometry, minx, miny, maxx, maxy, srs);
    }

    @Override
    public BBOX bbox(Expression geometry, Envelope bounds) {
        return factory.bbox(geometry, bounds);
    }

    @Override
    public Beyond beyond(Expression geometry1, Expression geometry2, double distance, String units) {
        return factory.beyond(geometry1, geometry2, distance, units);
    }

    @Override
    public Contains contains(Expression geometry1, Expression geometry2) {
        return factory.contains(geometry1, geometry2);
    }

    @Override
    public Crosses crosses(Expression geometry1, Expression geometry2) {
        return factory.crosses(geometry1, geometry2);
    }

    @Override
    public Disjoint disjoint(Expression geometry1, Expression geometry2) {
        return factory.disjoint(geometry1, geometry2);
    }

    @Override
    public DWithin dwithin(Expression geometry1, Expression geometry2, double distance, String units) {
        return factory.dwithin(geometry1, geometry2, distance, units);
    }

    @Override
    public Equals equal(Expression geometry1, Expression geometry2) {
        return factory.equal(geometry1, geometry2);
    }

    @Override
    public Intersects intersects(Expression geometry1, Expression geometry2) {
        return factory.intersects(geometry1, geometry2);
    }

    @Override
    public Overlaps overlaps(Expression geometry1, Expression geometry2) {
        return factory.overlaps(geometry1, geometry2);
    }

    @Override
    public Touches touches(Expression propertyName1, Expression geometry2) {
        return factory.touches(propertyName1, geometry2);
    }

    @Override
    public Within within(Expression geometry1, Expression geometry2) {
        return factory.within(geometry1, geometry2);
    }

    @Override
    public FeatureId featureId(String id) {
        return factory.featureId(id);
    }

    @Override
    public GmlObjectId gmlObjectId(String id) {
        return factory.gmlObjectId(id);
    }

    @Override
    public And and(Filter f, Filter g) {
        return factory.and(f,g);
    }

    @Override
    public And and(List<Filter> f) {
        return factory.and(f);
    }

    @Override
    public Or or(Filter f, Filter g) {
        return factory.or(f,g);
    }

    @Override
    public Or or(List<Filter> f) {
        return factory.or(f);
    }

    @Override
    public Not not(Filter f) {
        return factory.not(f);
    }

    @Override
    public Id id(Set<? extends Identifier> ids) {
        return factory.id(ids);
    }

    @Override
    public PropertyName property(String name) {
        return factory.property(name);
    }

    @Override
    public PropertyIsBetween between(Expression expr, Expression lower, Expression upper) {
        return factory.between(expr, lower, upper);
    }

    @Override
    public PropertyIsEqualTo equals(Expression expr1, Expression expr2) {
        return factory.equals(expr1, expr2);
    }

    @Override
    public PropertyIsEqualTo equal(Expression expr1, Expression expr2, boolean matchCase, MatchAction matchAction) {
        return factory.equal(expr1, expr2, matchCase, matchAction);
    }

    @Override
    public PropertyIsNotEqualTo notEqual(Expression expr1, Expression expr2) {
        return factory.notEqual(expr1, expr2);
    }

    @Override
    public PropertyIsNotEqualTo notEqual(Expression expr1, Expression expr2, boolean matchCase, MatchAction matchAction) {
        return factory.notEqual(expr1, expr2, matchCase, matchAction);
    }

    @Override
    public PropertyIsGreaterThan greater(Expression expr1, Expression expr2) {
        return factory.greater(expr1, expr2);
    }

    @Override
    public PropertyIsGreaterThan greater(Expression expr1, Expression expr2, boolean matchCase, MatchAction matchAction) {
        return factory.greater(expr1, expr2, matchCase, matchAction);
    }

    @Override
    public PropertyIsGreaterThanOrEqualTo greaterOrEqual(Expression expr1, Expression expr2) {
        return factory.greaterOrEqual(expr1, expr2);
    }

    @Override
    public PropertyIsGreaterThanOrEqualTo greaterOrEqual(Expression expr1, Expression expr2, boolean matchCase, MatchAction matchAction) {
        return factory.greaterOrEqual(expr1, expr2, matchCase, matchAction);
    }

    @Override
    public PropertyIsLessThan less(Expression expr1, Expression expr2) {
        return factory.less(expr1, expr2);
    }

    @Override
    public PropertyIsLessThan less(Expression expr1, Expression expr2, boolean matchCase, MatchAction matchAction) {
        return factory.less(expr1, expr2, matchCase, matchAction);
    }

    @Override
    public PropertyIsLessThanOrEqualTo lessOrEqual(Expression expr1, Expression expr2) {
        return factory.lessOrEqual(expr1, expr2);
    }

    @Override
    public PropertyIsLessThanOrEqualTo lessOrEqual(Expression expr1, Expression expr2, boolean matchCase, MatchAction matchAction) {
        return factory.lessOrEqual(expr1, expr2, matchCase, matchAction);
    }

    @Override
    public PropertyIsLike like(Expression expr, String pattern) {
        return factory.like(expr, pattern);
    }

    @Override
    public PropertyIsLike like(Expression expr, String pattern, String wildcard, String singleChar, String escape) {
        return factory.like(expr, pattern, wildcard, singleChar, escape);
    }

    @Override
    public PropertyIsNull isNull(Expression expr) {
        return factory.isNull(expr);
    }

    @Override
    public PropertyIsNil isNil(Expression expr) {
        return factory.isNil(expr);
    }

    @Override
    public BBOX bbox(String propertyName, double minx, double miny, double maxx, double maxy, String srs) {
        return factory.bbox(propertyName, minx, miny, maxx, maxy, srs);
    }

    @Override
    public Beyond beyond(String propertyName, Geometry geometry, double distance, String units) {
        return factory.beyond(propertyName, geometry, distance, units);
    }

    @Override
    public Contains contains(String propertyName, Geometry geometry) {
        return factory.contains(propertyName, geometry);
    }

    @Override
    public Crosses crosses(String propertyName, Geometry geometry) {
        return factory.crosses(propertyName, geometry);
    }

    @Override
    public Disjoint disjoint(String propertyName, Geometry geometry) {
        return factory.disjoint(propertyName, geometry);
    }

    @Override
    public DWithin dwithin(String propertyName, Geometry geometry, double distance, String units) {
        return factory.dwithin(propertyName, geometry, distance, units);
    }

    @Override
    public Equals equals(String propertyName, Geometry geometry) {
        return factory.equals(propertyName, geometry);
    }

    @Override
    public Intersects intersects(String propertyName, Geometry geometry) {
        return factory.intersects(propertyName, geometry);
    }

    @Override
    public Overlaps overlaps(String propertyName, Geometry geometry) {
        return factory.overlaps(propertyName, geometry);
    }

    @Override
    public Touches touches(String propertyName, Geometry geometry) {
        return factory.touches(propertyName, geometry);
    }

    @Override
    public Within within(String propertyName, Geometry geometry) {
        return factory.within(propertyName, geometry);
    }

    @Override
    public After after(Expression expr1, Expression expr2) {
        return factory.after(expr1, expr2);
    }

    @Override
    public AnyInteracts anyInteracts(Expression expr1, Expression expr2) {
        return factory.anyInteracts(expr1, expr2);
    }

    @Override
    public Before before(Expression expr1, Expression expr2) {
        return factory.before(expr1, expr2);
    }

    @Override
    public Begins begins(Expression expr1, Expression expr2) {
        return factory.begins(expr1, expr2);
    }

    @Override
    public BegunBy begunBy(Expression expr1, Expression expr2) {
        return factory.begunBy(expr1, expr2);
    }

    @Override
    public During during(Expression expr1, Expression expr2) {
        return factory.during(expr1, expr2);
    }

    @Override
    public Ends ends(Expression expr1, Expression expr2) {
        return factory.ends(expr1, expr2);
    }

    @Override
    public EndedBy endedBy(Expression expr1, Expression expr2) {
        return factory.endedBy(expr1, expr2);
    }

    @Override
    public Meets meets(Expression expr1, Expression expr2) {
        return factory.meets(expr1, expr2);
    }

    @Override
    public MetBy metBy(Expression expr1, Expression expr2) {
        return factory.metBy(expr1, expr2);
    }

    @Override
    public OverlappedBy overlappedBy(Expression expr1, Expression expr2) {
        return factory.overlappedBy(expr1, expr2);
    }

    @Override
    public TContains tcontains(Expression expr1, Expression expr2) {
        return factory.tcontains(expr1, expr2);
    }

    @Override
    public TEquals tequals(Expression expr1, Expression expr2) {
        return factory.tequals(expr1, expr2);
    }

    @Override
    public TOverlaps toverlaps(Expression expr1, Expression expr2) {
        return factory.toverlaps(expr1, expr2);
    }

    @Override
    public Add add(Expression expr1, Expression expr2) {
        return factory.add(expr1, expr2);
    }

    @Override
    public Divide divide(Expression expr1, Expression expr2) {
        return factory.divide(expr1, expr2);
    }

    @Override
    public Multiply multiply(Expression expr1, Expression expr2) {
        return factory.multiply(expr1, expr2);
    }

    @Override
    public Subtract subtract(Expression expr1, Expression expr2) {
        return factory.subtract(expr1, expr2);
    }

    @Override
    public Function function(String name, Expression... args) {
        return factory.function(name, args);
    }

    @Override
    public Literal literal(Object obj) {
        return factory.literal(obj);
    }

    @Override
    public Literal literal(byte b) {
        return factory.literal(b);
    }

    @Override
    public Literal literal(short s) {
        return factory.literal(s);
    }

    @Override
    public Literal literal(int i) {
        return factory.literal(i);
    }

    @Override
    public Literal literal(long l) {
        return factory.literal(l);
    }

    @Override
    public Literal literal(float f) {
        return factory.literal(f);
    }

    @Override
    public Literal literal(double d) {
        return factory.literal(d);
    }

    @Override
    public Literal literal(char c) {
        return factory.literal(c);
    }

    @Override
    public Literal literal(boolean b) {
        return factory.literal(b);
    }

    @Override
    public SortBy sort(String propertyName, SortOrder order) {
        return factory.sort(propertyName, order);
    }

    @Override
    public Operator operator(String name) {
        return factory.operator(name);
    }

    @Override
    public SpatialOperator spatialOperator(String name, GeometryOperand[] geometryOperands) {
        return factory.spatialOperator(name, geometryOperands);
    }

    @Override
    public FunctionName functionName(String name, int nargs) {
        return factory.functionName(name, nargs);
    }

    @Override
    public Functions functions(FunctionName[] functionNames) {
        return factory.functions(functionNames);
    }

    @Override
    public SpatialOperators spatialOperators(SpatialOperator[] spatialOperators) {
        return factory.spatialOperators(spatialOperators);
    }

    @Override
    public ComparisonOperators comparisonOperators(Operator[] comparisonOperators) {
        return factory.comparisonOperators(comparisonOperators);
    }

    @Override
    public ArithmeticOperators arithmeticOperators(boolean simple, Functions functions) {
        return factory.arithmeticOperators(simple, functions);
    }

    @Override
    public ScalarCapabilities scalarCapabilities(ComparisonOperators comparison, ArithmeticOperators arithmetic, boolean logical) {
        return factory.scalarCapabilities(comparison, arithmetic, logical);
    }

    @Override
    public SpatialCapabilities spatialCapabilities(GeometryOperand[] geometryOperands, SpatialOperators spatial) {
        return factory.spatialCapabilities(geometryOperands, spatial);
    }

    @Override
    public TemporalCapabilities temporalCapabilities(TemporalOperand[] temporalOperands, TemporalOperators temporal) {
        return factory.temporalCapabilities(temporalOperands, temporal);
    }

    @Override
    public IdCapabilities idCapabilities(boolean eid, boolean fid) {
        return factory.idCapabilities(eid, fid);
    }

    @Override
    public FilterCapabilities capabilities(String version, ScalarCapabilities scalar, SpatialCapabilities spatial, TemporalCapabilities temporal, IdCapabilities id) {
        return factory.capabilities(version, scalar, spatial, temporal, id);
    }

}
