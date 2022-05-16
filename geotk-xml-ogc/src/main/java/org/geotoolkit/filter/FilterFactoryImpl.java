/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.filter;

import org.geotoolkit.filter.capability.TemporalOperand;
import org.geotoolkit.filter.capability.ArithmeticOperators;
import org.geotoolkit.filter.capability.Functions;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.CoordinatesType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.ogc.xml.v110.AndType;
import org.geotoolkit.ogc.xml.v110.ArithmeticOperatorsType;
import org.geotoolkit.ogc.xml.v110.BBOXType;
import org.geotoolkit.ogc.xml.v110.BeyondType;
import org.geotoolkit.ogc.xml.v110.ComparisonOperatorsType;
import org.geotoolkit.ogc.xml.v110.ContainsType;
import org.geotoolkit.ogc.xml.v110.CrossesType;
import org.geotoolkit.ogc.xml.v110.DWithinType;
import org.geotoolkit.ogc.xml.v110.DisjointType;
import org.geotoolkit.ogc.xml.v110.EqualsType;
import org.geotoolkit.ogc.xml.v110.ExpressionType;
import org.geotoolkit.ogc.xml.v110.FeatureIdType;
import org.geotoolkit.ogc.xml.v110.FunctionNameType;
import org.geotoolkit.ogc.xml.v110.FunctionNamesType;
import org.geotoolkit.ogc.xml.v110.FunctionType;
import org.geotoolkit.ogc.xml.v110.IdCapabilitiesType;
import org.geotoolkit.ogc.xml.v110.IntersectsType;
import org.geotoolkit.ogc.xml.v110.LiteralType;
import org.geotoolkit.ogc.xml.v110.LowerBoundaryType;
import org.geotoolkit.ogc.xml.v110.NotType;
import org.geotoolkit.ogc.xml.v110.ObjectFactory;
import org.geotoolkit.ogc.xml.v110.OrType;
import org.geotoolkit.ogc.xml.v110.OverlapsType;
import org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.v110.PropertyIsEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsGreaterThanOrEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsGreaterThanType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLessThanOrEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLessThanType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.v110.PropertyIsNotEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsNullType;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.ogc.xml.v110.ScalarCapabilitiesType;
import org.geotoolkit.ogc.xml.v110.SortPropertyType;
import org.geotoolkit.ogc.xml.v110.SpatialCapabilitiesType;
import org.geotoolkit.ogc.xml.v110.SpatialOperatorType;
import org.geotoolkit.ogc.xml.v110.SpatialOperatorsType;
import org.geotoolkit.ogc.xml.v110.TemporalCapabilitiesType;
import org.geotoolkit.ogc.xml.v110.TimeAfterType;
import org.geotoolkit.ogc.xml.v110.TimeAnyInteractsType;
import org.geotoolkit.ogc.xml.v110.TimeBeforeType;
import org.geotoolkit.ogc.xml.v110.TimeBeginsType;
import org.geotoolkit.ogc.xml.v110.TimeBegunByType;
import org.geotoolkit.ogc.xml.v110.TimeContainsType;
import org.geotoolkit.ogc.xml.v110.TimeDuringType;
import org.geotoolkit.ogc.xml.v110.TimeEndedByType;
import org.geotoolkit.ogc.xml.v110.TimeEndsType;
import org.geotoolkit.ogc.xml.v110.TimeEqualsType;
import org.geotoolkit.ogc.xml.v110.TimeMeetsType;
import org.geotoolkit.ogc.xml.v110.TimeMetByType;
import org.geotoolkit.ogc.xml.v110.TimeOverlappedByType;
import org.geotoolkit.ogc.xml.v110.TimeOverlapsType;
import org.geotoolkit.ogc.xml.v110.TouchesType;
import org.geotoolkit.ogc.xml.v110.UpperBoundaryType;
import org.geotoolkit.ogc.xml.v110.WithinType;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.filter.capability.ComparisonOperators;
import org.geotoolkit.ogc.xml.FilterXmlFactory;
import org.opengis.filter.Filter;
import org.opengis.filter.MatchAction;
import org.geotoolkit.filter.capability.FilterCapabilities;
import org.geotoolkit.filter.capability.FunctionName;
import org.geotoolkit.filter.capability.TemporalOperators;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.DistanceOperator;
import org.opengis.filter.capability.GeometryOperand;
import org.geotoolkit.filter.capability.DefaultIdCapabilities;
import org.geotoolkit.filter.capability.Operator;
import org.geotoolkit.filter.capability.ScalarCapabilities;
import org.geotoolkit.filter.capability.SpatialCapabilities;
import org.geotoolkit.filter.capability.SpatialOperators;
import org.geotoolkit.filter.capability.TemporalCapabilities;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.Expression;
import org.opengis.filter.LikeOperator;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.NilOperator;
import org.opengis.filter.NullOperator;
import org.opengis.filter.ResourceId;
import org.opengis.filter.SortOrder;
import org.opengis.filter.SortProperty;
import org.opengis.filter.TemporalOperator;
import org.opengis.filter.ValueReference;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;


/**
 * A factory used by a CQL parser to build filter.
 *
 * @author Guilhem Legal
 * @module
 */
public class FilterFactoryImpl extends FilterFactory2 {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.filter");

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public ResourceId<Object>resourceId(final String id) {
        return new FeatureIdType(id);
    }

    @Override
    public LogicalOperator<Object> and(final Filter<Object> f, final Filter<Object> g) {
        final List<Filter<Object>> filterList = new ArrayList<>();
        boolean factorized = false;
        // factorize OR filter
        if (g.getOperatorType() == LogicalOperatorName.AND) {
            factorized = true;
            filterList.addAll(((LogicalOperator<Object>)g).getOperands());
        } else {
            filterList.add(g);
        }
        if (f.getOperatorType() == LogicalOperatorName.AND) {
            factorized = true;
            filterList.addAll(((LogicalOperator<Object>)f).getOperands());
        } else {
            filterList.add(f);
        }
        if (factorized) {
            return new AndType(filterList.toArray());
        } else {
            return new AndType(f, g);
        }
    }

    @Override
    public LogicalOperator<Object> and(final Collection<? extends Filter<Object>> f) {
        return new AndType(f.toArray());
    }

    @Override
    public LogicalOperator<Object> or(final Filter<Object> f, final Filter<Object> g) {
        final List<Filter<Object>> filterList = new ArrayList<>();
        boolean factorized = false;
        // factorize OR filter
        if (g.getOperatorType() == LogicalOperatorName.OR) {
            factorized = true;
            filterList.addAll(((LogicalOperator<Object>)g).getOperands());
        } else {
            filterList.add(g);
        }
        if (f.getOperatorType() == LogicalOperatorName.OR) {
            factorized = true;
            filterList.addAll(((LogicalOperator<Object>)f).getOperands());
        } else {
            filterList.add(f);
        }
        if (factorized) {
            return new OrType(filterList.toArray());
        } else {
            return new OrType(f, g);
        }
    }

    @Override
    public LogicalOperator<Object> or(final Collection<? extends Filter<Object>> filterList) {
        return new OrType(filterList.toArray());
    }

    @Override
    public LogicalOperator<Object> not(final Filter<Object> f) {
        return new NotType(f);
    }

    @Override
    public ValueReference<Object,Object> property(final String name) {
        return new PropertyNameType(name);
    }

    @Override
    public BetweenComparisonOperator<Object> between(final Expression<Object,?> expr, Expression<Object,?> lower, Expression<Object,?> upper) {
        if (lower instanceof LiteralType) {
            lower = new LowerBoundaryType((LiteralType)lower);
        }
        if (upper instanceof LiteralType) {
            upper = new UpperBoundaryType((LiteralType)upper);
        }
        if (expr instanceof PropertyNameType) {
            ObjectFactory factory = new ObjectFactory();
            return new PropertyIsBetweenType( factory.createPropertyName((PropertyNameType) expr),
                                              (LowerBoundaryType) lower,
                                              (UpperBoundaryType) upper);
        } else {
            return new PropertyIsBetweenType( (ExpressionType)    expr,
                                              (LowerBoundaryType) lower,
                                              (UpperBoundaryType) upper);
        }
    }

    @Override
    public BinaryComparisonOperator<Object> equal(final Expression<Object,?> expr1, final Expression<Object,?> expr2) {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsEqualToType(lit, propName, null);
    }

    @Override
    public BinaryComparisonOperator<Object> equal(final Expression<Object,?> expr1, final Expression<Object,?> expr2,
            final boolean matchCase,final MatchAction action)
    {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsEqualToType(lit, propName, matchCase);
    }

    @Override
    public BinaryComparisonOperator<Object> notEqual(final Expression<Object,?> expr1, final Expression<Object,?> expr2) {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsNotEqualToType(lit, propName, null);
    }

    @Override
    public BinaryComparisonOperator<Object> notEqual(final Expression<Object,?> expr1, final Expression<Object,?> expr2,
            final boolean matchCase,final MatchAction action)
    {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsNotEqualToType(lit, propName, matchCase);
    }

    @Override
    public BinaryComparisonOperator<Object> greater(final Expression<Object,?> expr1, final Expression<Object,?> expr2) {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsGreaterThanType(lit, propName, null);
    }

    @Override
    public BinaryComparisonOperator<Object> greater(final Expression<Object,?> expr1, final Expression<Object,?> expr2, final boolean matchCase, final MatchAction action) {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsGreaterThanType(lit, propName, matchCase);
    }

    @Override
    public BinaryComparisonOperator<Object> greaterOrEqual(final Expression<Object,?> expr1, final Expression<Object,?> expr2) {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsGreaterThanOrEqualToType(lit, propName, null);
    }

    @Override
    public BinaryComparisonOperator<Object> greaterOrEqual(final Expression<Object,?> expr1, final Expression<Object,?> expr2, final boolean matchCase, final MatchAction action) {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsGreaterThanOrEqualToType(lit, propName, matchCase);
    }

    @Override
    public BinaryComparisonOperator<Object> less(final Expression<Object,?> expr1, final Expression<Object,?> expr2, final boolean matchCase, final MatchAction action) {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsLessThanType(lit, propName, matchCase);
    }

    @Override
    public BinaryComparisonOperator<Object> less(final Expression<Object,?> expr1, final Expression<Object,?> expr2) {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsLessThanType(lit, propName, null);
    }

    @Override
    public BinaryComparisonOperator<Object> lessOrEqual(final Expression<Object,?> expr1, final Expression<Object,?> expr2, final boolean matchCase, final MatchAction action) {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsLessThanOrEqualToType(lit, propName, matchCase);
    }

    @Override
    public BinaryComparisonOperator<Object> lessOrEqual(final Expression<Object,?> expr1, final Expression<Object,?> expr2) {
        LiteralType lit           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
        }
        if (expr1 instanceof LiteralType) {
            lit = (LiteralType) expr1;
        } else if (expr2 instanceof LiteralType) {
            lit = (LiteralType) expr2;
        }
        return new PropertyIsLessThanOrEqualToType(lit, propName, null);
    }

    @Override
    public LikeOperator<Object> like(final Expression<Object,?> expr, final String pattern) {
        return like(expr, pattern, '*', '?', '\\');
    }

    public LikeOperator<Object> like(final Expression<Object,?> expr, final String pattern, final boolean isMatchingCase) {
        return like(expr, pattern, '*', '?', '\\', isMatchingCase);
    }

    public LikeOperator<Object> like(final Expression<Object,?> expr, String pattern, final char wildcard, final char singleChar, final char escape) {
        //SQLBuilder add a white space at then end of the pattern we remove it
        if (pattern != null && pattern.lastIndexOf(' ') == pattern.length() -1) {
            pattern = pattern.substring(0, pattern.length() -1);
        }
        return new PropertyIsLikeType(expr, pattern, String.valueOf(wildcard), String.valueOf(singleChar), String.valueOf(escape));
    }

    @Override
    public LikeOperator<Object> like(final Expression<Object,?> expr, String pattern, final char wildcard, final char singleChar, final char escape, final boolean isMatchingCase) {
        //SQLBuilder add a white space at then end of the pattern we remove it
        if (pattern != null && pattern.lastIndexOf(' ') == pattern.length() -1) {
            pattern = pattern.substring(0, pattern.length() -1);
        }
        return new PropertyIsLikeType(expr, pattern, String.valueOf(wildcard), String.valueOf(singleChar), String.valueOf(escape), isMatchingCase);
    }

    @Override
    public NullOperator<Object> isNull(final Expression<Object,?> expr) {
        return new PropertyIsNullType((PropertyNameType)expr);
    }

    @Override
    public BinarySpatialOperator<Object> bbox(final String propertyName, final double minx, final double miny, final double maxx, final double maxy, String srs) {
        if (srs == null || srs.equals("")) {
            srs = "CRS:84"; // default CRS used is normally EPSG 4326 but most of the implementation use this one by default
        }
        return new BBOXType(propertyName, minx, miny, maxx, maxy, srs);
    }

    @Override
    public BinarySpatialOperator<Object> bbox(final Expression geometry, final double minx, final double miny, final double maxx, final double maxy, String srs) {
        String propertyName = "";
        if (geometry instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry).getXPath();
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry.getClass().getSimpleName());
        }
        if (srs == null || srs.equals("")) {
            srs = "CRS:84"; // default CRS used is normally EPSG 4326 but most of the implementation use this one by default
        }
        return new BBOXType(propertyName, minx, miny, maxx, maxy, srs);
    }

    @Override
    public BinarySpatialOperator<Object> bbox(final Expression geometry, final Envelope bounds) {
        String propertyName = "";
        final String CRSName;
        if (geometry instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry).getXPath();
        }
        if (bounds.getCoordinateReferenceSystem() != null) {
            CRSName = IdentifiedObjects.getIdentifierOrName(bounds.getCoordinateReferenceSystem());
        } else {
            CRSName = "CRS:84";
        }
        return new BBOXType(propertyName, bounds.getMinimum(0), bounds.getMinimum(1),
                                          bounds.getMaximum(0), bounds.getMaximum(1), CRSName);
    }

    @Override
    public DistanceOperator<Object> beyond(Expression<? super Object, ? extends Object> geometry1, Expression<? super Object, ? extends Object> geometry2, Quantity<Length> distance) {
        String propertyName = "";
        if (geometry1 instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry1).getXPath();
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        // we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        String units = null;
        if (distance != null && distance.getUnit() != null) {
            units = distance.getUnit().getName();
        }
        Double dist = null;
        if (distance != null && distance.getValue()!= null) {
            dist = distance.getValue().doubleValue();
        }
        return new BeyondType(propertyName, (AbstractGeometryType) geom, dist, units);
    }


    @Override
    public DistanceOperator<Object> beyond(final String propertyName, final Geometry geometry, final double distance, final String units) {
        return new BeyondType(propertyName, (AbstractGeometryType) geometry, distance, units);
    }

    @Override
    public DistanceOperator<Object> beyond(final Expression geometry1, final Expression geometry2, final double distance, String units) {
        String propertyName = "";
        if (geometry1 instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry1).getXPath();
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        // we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        // we formats the units (CQL parser add a white space at the end)
        if (units.indexOf(' ') == units.length() -1) {
            units = units.substring(0, units.length() - 1);
        }
        return new BeyondType(propertyName, (AbstractGeometryType) geom, distance, units);
    }

    @Override
    public DistanceOperator<Object> within(Expression<? super Object, ? extends Object> geometry1, Expression<? super Object, ? extends Object> geometry2, Quantity<Length> distance) {
        String propertyName = "";

        // we get the propertyName
        if (geometry1 instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry1).getXPath();
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        // we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        String units = null;
        if (distance != null && distance.getUnit() != null) {
            units = distance.getUnit().getName();
        }
        Double dist = null;
        if (distance != null && distance.getValue()!= null) {
            dist = distance.getValue().doubleValue();
        }

        return new DWithinType(propertyName, (AbstractGeometryType) geom, dist, units);
    }

    @Override
    public DistanceOperator dwithin(final String propertyName, final Geometry geometry, final double distance, final String units) {
        return new DWithinType(propertyName, (AbstractGeometryType) geometry, distance, units);
    }

    @Override
    public DistanceOperator<Object> dwithin(final Expression geometry1, final Expression geometry2, final double distance, String units) {
        String propertyName = "";

        // we get the propertyName
        if (geometry1 instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry1).getXPath();
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        // we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        // we formats the units (CQL parser add a white space at the end)
        if (units.indexOf(' ') == units.length() -1) {
            units = units.substring(0, units.length() - 1);
        }
        return new DWithinType(propertyName, (AbstractGeometryType) geom, distance, units);
    }

    @Override
    public BinarySpatialOperator<Object> contains(final String propertyName, final Geometry geometry) {
        return new ContainsType(propertyName, (AbstractGeometryType) geometry);
    }

    @Override
    public BinarySpatialOperator<Object> contains(final Expression<Object,?> geometry1, final Expression<Object,?> geometry2) {
        // we get the propertyName
        PropertyNameType propertyName = null;
        if (geometry1 instanceof PropertyNameType) {
            propertyName = (PropertyNameType)geometry1;
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        // we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        return new ContainsType(propertyName, geom);
    }

    @Override
    public BinarySpatialOperator<Object> crosses(final String propertyName, final Geometry geometry) {
        return new CrossesType(propertyName, (AbstractGeometryType) geometry);
    }

    @Override
    public BinarySpatialOperator<Object> crosses(final Expression<Object,?> geometry1, final Expression<Object,?> geometry2) {
        PropertyNameType propertyName = null;
        if (geometry1 instanceof PropertyNameType) {
            propertyName = (PropertyNameType)geometry1;
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        // we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        return new CrossesType(propertyName, geom);
    }

    @Override
    public BinarySpatialOperator<Object> disjoint(final String propertyName, final Geometry geometry) {
        return new DisjointType(propertyName, (AbstractGeometryType) geometry);
    }

    @Override
    public BinarySpatialOperator<Object> disjoint(final Expression<Object,?> geometry1, final Expression<Object,?> geometry2) {
        PropertyNameType propertyName = null;
        if (geometry1 instanceof PropertyNameType) {
            propertyName = (PropertyNameType)geometry1;
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        // we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        return new DisjointType(propertyName,  geom);
    }

    @Override
    public BinarySpatialOperator<Object> equals(final String propertyName, final Geometry geometry) {
        return new EqualsType(propertyName, (AbstractGeometryType) geometry);
    }

    @Override
    public BinarySpatialOperator<Object> equals(final Expression<Object,?> geometry1, final Expression<Object,?> geometry2) {
        PropertyNameType propertyName = null;
        if (geometry1 instanceof PropertyNameType) {
            propertyName = (PropertyNameType)geometry1;
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        // we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        return new EqualsType(propertyName, geom);
    }

    @Override
    public BinarySpatialOperator<Object> intersects(final String propertyName, final Geometry geometry) {
        return new IntersectsType(propertyName, (AbstractGeometryType) geometry);
    }

    @Override
    public BinarySpatialOperator<Object> intersects(final Expression<Object,?> geometry1, final Expression<Object,?> geometry2) {
        PropertyNameType propertyName = null;
        if (geometry1 instanceof PropertyNameType) {
            propertyName = (PropertyNameType)geometry1;
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        //we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        return new IntersectsType(propertyName, geom);
    }

    @Override
    public BinarySpatialOperator<Object> overlaps(final String propertyName, final Geometry geometry) {
        return new OverlapsType(propertyName, (AbstractGeometryType) geometry);
    }

    @Override
    public BinarySpatialOperator<Object> overlaps(final Expression<Object,?> geometry1, final Expression<Object,?> geometry2) {
        PropertyNameType propertyName = null;
        if (geometry1 instanceof PropertyNameType) {
            propertyName = (PropertyNameType)geometry1;
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
         //we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        return new OverlapsType(propertyName, geom);
    }

    @Override
    public BinarySpatialOperator<Object> touches(final String propertyName, final Geometry geometry) {
        return new TouchesType(propertyName, (AbstractGeometryType) geometry);
    }

    @Override
    public BinarySpatialOperator<Object> touches(final Expression<Object,?> propertyName1, final Expression<Object,?> geometry2) {
        PropertyNameType propertyName = null;
        if (propertyName1 instanceof PropertyNameType) {
            propertyName = (PropertyNameType)propertyName1;
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + propertyName1.getClass().getSimpleName());
        }
        //we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        return new TouchesType(propertyName, geom);
    }

    @Override
    public BinarySpatialOperator<Object> within(final String propertyName, final Geometry geometry) {
       return new WithinType(propertyName, (AbstractGeometryType) geometry);
    }

    @Override
    public BinarySpatialOperator<Object> within(final Expression<Object,?> geometry1, final Expression<Object,?> geometry2) {
        PropertyNameType propertyName = null;
        if (geometry1 instanceof PropertyNameType) {
            propertyName = (PropertyNameType)geometry1;
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        //we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        return new WithinType(propertyName, geom);
    }


    @Override
    public Expression<Object,?> add(final Expression expr1, final Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Expression<Object,?> divide(final Expression expr1, final Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Expression<Object,?> multiply(final Expression expr1, final Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Expression<Object,?> subtract(final Expression expr1, final Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Expression<Object,?> function(final String name, final Expression[] args) {
        return new FunctionType(name, args);
    }

    public Expression<Object,?> function(final String name, final Expression<Object,?> arg1) {
        return new FunctionType(name, arg1);
    }

    public Expression<Object,?> function(final String name, final Expression<Object,?> arg1, final Expression<Object,?> arg2) {
        return new FunctionType(name, arg1, arg2);
    }

    public Expression<Object,?> function(final String name, final Expression<Object,?> arg1, final Expression<Object,?> arg2, final Expression<Object,?> arg3) {
         return new FunctionType(name, arg1, arg2, arg3);
    }

    @Override
    public Literal literal(Object obj) {
        if (obj instanceof Date) {
            Date d = (Date) obj;
            synchronized(DATE_FORMAT) {
                obj = DATE_FORMAT.format(d);
            }
        }
        return new LiteralType(obj);
    }

    @Override
    public Literal literal(final byte b) {
        return new LiteralType(b);
    }

    @Override
    public Literal literal(final short s) {
        return new LiteralType(s);
    }

    @Override
    public Literal literal(final int i) {
        return new LiteralType(i);
    }

    @Override
    public Literal literal(final long l) {
        return new LiteralType(l);
    }

    @Override
    public Literal literal(final float f) {
        return new LiteralType(f);
    }

    @Override
    public Literal literal(final double d) {
        return new LiteralType(d);
    }

    @Override
    public Literal literal(final char c) {
        return new LiteralType(c);
    }

    @Override
    public Literal literal(final boolean b) {
        return new LiteralType(b);
    }

    @Override
    public SortProperty sort(final ValueReference propertyName, final SortOrder order) {
        return new SortPropertyType(propertyName.getXPath(), order);
    }

    public org.geotoolkit.filter.capability.SpatialOperator spatialOperator(final String name, final GeometryOperand[] geometryOperands) {
        return new SpatialOperatorType(name, geometryOperands);
    }

    public FunctionName functionName(final String name, final int nargs) {
        return new FunctionNameType(name, nargs);
    }

    public Functions functions(final FunctionName[] functionNames) {
        return new FunctionNamesType(Arrays.asList((FunctionNameType[])functionNames));
    }

    public SpatialOperators spatialOperators(final org.geotoolkit.filter.capability.SpatialOperator[] spatialOperators) {
       return new SpatialOperatorsType( spatialOperators );
    }

    public ComparisonOperators comparisonOperators(final Operator[] comparisonOperators) {
        return new ComparisonOperatorsType(comparisonOperators);
    }

    public ArithmeticOperators arithmeticOperators(final boolean simple, final Functions functions) {
         return new ArithmeticOperatorsType(simple, functions);
    }

    public ScalarCapabilities scalarCapabilities(final ComparisonOperators comparison, final ArithmeticOperators arithmetic, final boolean logical) {
        return new ScalarCapabilitiesType(comparison, arithmetic, logical);
    }

    public SpatialCapabilities spatialCapabilities(final GeometryOperand[] geometryOperands, final SpatialOperators spatial) {
        return new SpatialCapabilitiesType(geometryOperands, spatial);
    }

    public DefaultIdCapabilities idCapabilities(final boolean eid, final boolean fid) {
        return new IdCapabilitiesType(eid, fid);
    }

    public FilterCapabilities capabilities(final String version, final ScalarCapabilities scalar, final SpatialCapabilities spatial, final DefaultIdCapabilities id) {
        return FilterXmlFactory.buildFilterCapabilities(version, scalar, spatial, id, null, null);
    }

    /**
     * Transform a JTS geometric object into a GML marshallable object
     */
    public Object GeometryToGML(final Object geom) {
        Object result = null;
        if (geom instanceof Polygon) {
            final Polygon p          = (Polygon) geom;
            final Coordinate[] coord = p.getCoordinates();

            // an envelope
            if (coord.length == 5) {
                final DirectPositionType lowerCorner = new DirectPositionType(coord[0].y, coord[0].x);
                final DirectPositionType upperCorner = new DirectPositionType(coord[2].y, coord[2].x);
                result = new EnvelopeType(null, lowerCorner, upperCorner, "EPSG:4326");
            }
        } else if (geom instanceof Point){
            final Point p = (Point) geom;
            final Coordinate[] coord = p.getCoordinates();
            result = new PointType(null, new DirectPositionType(coord[0].x, coord[0].y, coord[0].z));
            ((PointType)result).setSrsName("EPSG:4326");
        } else if (geom instanceof LineString){
            final LineString ls = (LineString) geom;
            final Coordinate[] coord = ls.getCoordinates();
            result = new LineStringType(new CoordinatesType(coord[0].x + "," + coord[0].y + " " + coord[1].x + "," + coord[1].y ));
            ((LineStringType)result).setSrsName("EPSG:4326");
        } else {
            LOGGER.severe("unable to create GML geometry with: " + geom.getClass().getSimpleName());
        }
        return result;
    }

    @Override
    public NilOperator<Object> isNil(final Expression<Object,?> exprsn, String reason) {
        throw new UnsupportedOperationException("Not supported in filter v110.");
    }

    @Override
    public TemporalOperator<Object>after(final Expression<Object,?> expr1, final Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeAfterType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> anyInteracts(final Expression<Object,?> expr1, final Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeAnyInteractsType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> before(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeBeforeType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> begins(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeBeginsType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> begunBy(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeBegunByType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> during(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeDuringType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> ends(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeEndsType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> endedBy(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeEndedByType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> meets(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeMeetsType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> metBy(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeMetByType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> overlappedBy(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeOverlappedByType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> tcontains(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeContainsType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> tequals(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeEqualsType(propName.getXPath(), temporal);
    }

    @Override
    public TemporalOperator<Object> toverlaps(final Expression<Object,?> expr1, final  Expression<Object,?> expr2) {
        Object temporal           = null;
        PropertyNameType propName = null;
        if (expr1 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr1;
            temporal = expr2;
        } else if (expr2 instanceof PropertyNameType) {
            propName = (PropertyNameType) expr2;
            temporal = expr1;
        }
        return new TimeOverlapsType(propName.getXPath(), temporal);
    }

    public TemporalCapabilities temporalCapabilities(final TemporalOperand[] tos, final TemporalOperators to) {
        return new TemporalCapabilitiesType(tos, to);
    }

    public FilterCapabilities capabilities(final String string, final ScalarCapabilities sc, final SpatialCapabilities sc1, final TemporalCapabilities tc, final DefaultIdCapabilities ic) {
        return FilterXmlFactory.buildFilterCapabilities("1.1.0", sc, sc1, ic, tc, null);
    }
}
