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

// JTS dependencies
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

// J2SE dependencies
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

// Geotoolkit dependencies
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
import org.geotoolkit.ogc.xml.v110.GmlObjectIdType;
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
import org.geotoolkit.ogc.xml.v110.TouchesType;
import org.geotoolkit.ogc.xml.v110.UpperBoundaryType;
import org.geotoolkit.ogc.xml.v110.WithinType;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;

// GeoAPI dependencies
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
import org.opengis.geometry.Geometry;


/**
 * A factory used by a CQL parser to build filter. 
 * 
 * @author Guilhem Legal
 * @module pending
 */
public class FilterFactoryImpl implements FilterFactory2 {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.filter");
    
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    public FeatureId featureId(final String id) {
        return new FeatureIdType(id);
    }

    public GmlObjectId gmlObjectId(final String id) {
        return new GmlObjectIdType(id);
    }

    public And and(final Filter f, final Filter g) {
        return new AndType(f, g);
    }

    public And and(final List<Filter> f) {
        return new AndType(f);
    }

    public Or or(final Filter f, final Filter g) {
        return new OrType(f, g);
    }

    public Or or(final List<Filter> f) {
        return new OrType(f);
    }

    public Not not(final Filter f) {
        return new NotType(f);
    }

    public Id id(final Set<? extends Identifier> ids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PropertyName property(final String name) {
        return new PropertyNameType(name);
    }

    public PropertyIsBetween between(final Expression expr, Expression lower, Expression upper) {
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

    public PropertyIsEqualTo equals(final Expression expr1, final Expression expr2) {
        return new PropertyIsEqualToType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsEqualTo equal(final Expression expr1, final Expression expr2, final boolean matchCase) {
        return new PropertyIsEqualToType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsNotEqualTo notEqual(final Expression expr1, final Expression expr2) {
        return new PropertyIsNotEqualToType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsNotEqualTo notEqual(final Expression expr1, final Expression expr2, final boolean matchCase) {
        return new PropertyIsNotEqualToType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsGreaterThan greater(final Expression expr1, final Expression expr2) {
        return new PropertyIsGreaterThanType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsGreaterThan greater(final Expression expr1, final Expression expr2, final boolean matchCase) {
        return new PropertyIsGreaterThanType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsGreaterThanOrEqualTo greaterOrEqual(final Expression expr1, final Expression expr2) {
        return new PropertyIsGreaterThanOrEqualToType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsGreaterThanOrEqualTo greaterOrEqual(final Expression expr1, final Expression expr2, final boolean matchCase) {
        return new PropertyIsGreaterThanOrEqualToType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsLessThan less(final Expression expr1, final Expression expr2, final boolean matchCase) {
        return new PropertyIsLessThanType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsLessThan less(final Expression expr1, final Expression expr2) {
        return new PropertyIsLessThanType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsLessThanOrEqualTo lessOrEqual(final Expression expr1, final Expression expr2, final boolean matchCase) {
        return new PropertyIsLessThanOrEqualToType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsLessThanOrEqualTo lessOrEqual(final Expression expr1, final Expression expr2) {
        return new PropertyIsLessThanOrEqualToType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsLike like(final Expression expr, final String pattern) {
        return like(expr, pattern, "*", "?", "\\");
    }

    public PropertyIsLike like(final Expression expr, final String pattern, final boolean isMatchingCase) {
        return like(expr, pattern, "*", "?", "\\", isMatchingCase);
    }

    public PropertyIsLike like(final Expression expr, String pattern, final String wildcard, final String singleChar, final String escape) {
        //SQLBuilder add a white space at then end of the pattern we remove it
        if (pattern != null && pattern.lastIndexOf(' ') == pattern.length() -1)
            pattern = pattern.substring(0, pattern.length() -1);
        return new PropertyIsLikeType(expr, pattern, wildcard, singleChar, escape);
    }

    public PropertyIsLike like(final Expression expr, String pattern, final String wildcard, final String singleChar, final String escape, final boolean isMatchingCase) {
        //SQLBuilder add a white space at then end of the pattern we remove it
        if (pattern != null && pattern.lastIndexOf(' ') == pattern.length() -1)
            pattern = pattern.substring(0, pattern.length() -1);
        return new PropertyIsLikeType(expr, pattern, wildcard, singleChar, escape, isMatchingCase);
    }

    public PropertyIsNull isNull(final Expression expr) {
        return new PropertyIsNullType((PropertyNameType)expr);
    }

    public BBOX bbox(final String propertyName, final double minx, final double miny, final double maxx, final double maxy, String srs) {
        if (srs == null || srs.equals("")) {
            srs = "EPSG:4326";
        }
        return new BBOXType(propertyName, minx, miny, maxx, maxy, srs);
    }
    
    public BBOX bbox(final Expression geometry, final double minx, final double miny, final double maxx, final double maxy, String srs) {
        String propertyName = "";
        if (geometry instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry).getPropertyName();
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry.getClass().getSimpleName());
        }
        if (srs == null || srs.equals("")) {
            srs = "EPSG:4326";
        }
        return new BBOXType(propertyName, minx, miny, maxx, maxy, srs);
    }

    public BBOX bbox(final Expression geometry, final BoundingBox bounds) {
        String propertyName = "";
        String CRSName      = "";
        if (geometry instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry).getPropertyName();
        }
        if (bounds.getCoordinateReferenceSystem() != null) {
            CRSName = CRS.getDeclaredIdentifier(bounds.getCoordinateReferenceSystem());
        } else {
            CRSName = "EPSG:4326";
        }
        return new BBOXType(propertyName, bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), CRSName);
    }

    public Beyond beyond(final String propertyName, final Geometry geometry, final double distance, final String units) {
       
        return new BeyondType(propertyName, (AbstractGeometryType) geometry, distance, units);
    }

    public Beyond beyond(final Expression geometry1, final Expression geometry2, final double distance, String units) {
        String propertyName = "";
        if (geometry1 instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry1).getPropertyName();
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        
        // we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        
        // we formats the units (CQL parser add a white spce at the end)
        if (units.indexOf(' ') == units.length() -1)
            units = units.substring(0, units.length() - 1);
        
        return new BeyondType(propertyName, (AbstractGeometryType) geom, distance, units);
    }
    
    public DWithin dwithin(final String propertyName, final Geometry geometry, final double distance, final String units) {
        return new DWithinType(propertyName, (AbstractGeometryType) geometry, distance, units);
    }

    public DWithin dwithin(final Expression geometry1, final Expression geometry2, final double distance, String units) {
        String propertyName = "";
        
        // we get the propertyName
        if (geometry1 instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry1).getPropertyName();
        } else {
            throw new IllegalArgumentException("unexpected type instead of propertyNameType: " + geometry1.getClass().getSimpleName());
        }
        
        // we transform the JTS geometry into a GML geometry
        Object geom = null;
        if (geometry2 instanceof LiteralType) {
            geom = ((LiteralType)geometry2).getValue();
            geom = GeometryToGML(geom);
        }
        // we formats the units (CQL parser add a white spce at the end)
        if (units.indexOf(' ') == units.length() -1)
            units = units.substring(0, units.length() - 1);
        
        return new DWithinType(propertyName, (AbstractGeometryType) geom, distance, units);
    }
    
    public Contains contains(final String propertyName, final Geometry geometry) {
        return new ContainsType(propertyName, (AbstractGeometryType) geometry);
    }

    public Contains contains(final Expression geometry1, final Expression geometry2) {
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
    
    public Crosses crosses(final String propertyName, final Geometry geometry) {
        return new CrossesType(propertyName, (AbstractGeometryType) geometry);
    }

    public Crosses crosses(final Expression geometry1, final Expression geometry2) {
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
    
    public Disjoint disjoint(final String propertyName, final Geometry geometry) {
        return new DisjointType(propertyName, (AbstractGeometryType) geometry);
    }

    public Disjoint disjoint(final Expression geometry1, final Expression geometry2) {
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
    
    
    
    public Equals equals(final String propertyName, final Geometry geometry) {
        return new EqualsType(propertyName, (AbstractGeometryType) geometry);
    }

    public Equals equal(final Expression geometry1, final Expression geometry2) {
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

    public Intersects intersects(final String propertyName, final Geometry geometry) {
        return new IntersectsType(propertyName, (AbstractGeometryType) geometry);
    }

    public Intersects intersects(final Expression geometry1, final Expression geometry2) {
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
    
    public Overlaps overlaps(final String propertyName, final Geometry geometry) {
        return new OverlapsType(propertyName, (AbstractGeometryType) geometry);
    }

    public Overlaps overlaps(final Expression geometry1, final Expression geometry2) {
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
    
    public Touches touches(final String propertyName, final Geometry geometry) {
        return new TouchesType(propertyName, (AbstractGeometryType) geometry);
    }

    public Touches touches(final Expression propertyName1, final Expression geometry2) {
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
    
    public Within within(final String propertyName, final Geometry geometry) {
       return new WithinType(propertyName, (AbstractGeometryType) geometry);
    }
    
    public Within within(final Expression geometry1, final Expression geometry2) {
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


    public Add add(final Expression expr1, final Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Divide divide(final Expression expr1, final Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Multiply multiply(final Expression expr1, final Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Subtract subtract(final Expression expr1, final Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Function function(final String name, final Expression[] args) {
        return new FunctionType(name, args);
    }

    public Function function(final String name, final Expression arg1) {
        return new FunctionType(name, arg1);
    }

    public Function function(final String name, final Expression arg1, final Expression arg2) {
        return new FunctionType(name, arg1, arg2);
    }

    public Function function(final String name, final Expression arg1, final Expression arg2, final Expression arg3) {
         return new FunctionType(name, arg1, arg2, arg3);
    }

    public Literal literal(Object obj) {
        if (obj instanceof Date) {
            Date d = (Date) obj;
            synchronized(DATE_FORMAT) {
                obj = DATE_FORMAT.format(d);
            }
        }
        return new LiteralType(obj);
    }

    public Literal literal(final byte b) {
        return new LiteralType(b);
    }

    public Literal literal(final short s) {
        return new LiteralType(s);
    }

    public Literal literal(final int i) {
        return new LiteralType(i);
    }

    public Literal literal(final long l) {
        return new LiteralType(l);
    }

    public Literal literal(final float f) {
        return new LiteralType(f);
    }

    public Literal literal(final double d) {
        return new LiteralType(d);
    }

    public Literal literal(final char c) {
        return new LiteralType(c);
    }

    public Literal literal(final boolean b) {
        return new LiteralType(b);
    }

    public SortBy sort(final String propertyName, final SortOrder order) {
        return new SortPropertyType(propertyName, order);
    }

    public Operator operator(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SpatialOperator spatialOperator(final String name, final GeometryOperand[] geometryOperands) {
        return new SpatialOperatorType(name, geometryOperands);
    }

    public FunctionName functionName(final String name, final int nargs) {
        return new FunctionNameType(name, nargs);
    }

    public Functions functions(final FunctionName[] functionNames) {
       
        return new FunctionNamesType(Arrays.asList((FunctionNameType[])functionNames));
    }

    public SpatialOperators spatialOperators(final SpatialOperator[] spatialOperators) {
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

    public IdCapabilities idCapabilities(final boolean eid, final boolean fid) {
        return new IdCapabilitiesType(eid, fid);
    }

    public FilterCapabilities capabilities(final String version, final ScalarCapabilities scalar, final SpatialCapabilities spatial, final IdCapabilities id) {
        return new org.geotoolkit.ogc.xml.v110.FilterCapabilities(scalar, spatial, id);
    }

    public PropertyName property(final Name name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Transform a JTS geometric object into a GML marshallable object
     * @param geom
     * @return
     */
    public Object GeometryToGML(final Object geom) {
        Object result = null;
        if (geom instanceof Polygon) {
            Polygon p          = (Polygon) geom;
            Coordinate[] coord = p.getCoordinates();
            
            // an envelope
            if (coord.length == 5) {
                DirectPositionType lowerCorner = new DirectPositionType(coord[0].x, coord[1].y);
                DirectPositionType upperCorner = new DirectPositionType(coord[2].x, coord[0].y);
                result = new EnvelopeType(null, lowerCorner, upperCorner, "EPSG:4326");
            }
        } else if (geom instanceof Point){ 
            Point p = (Point) geom;
            Coordinate[] coord = p.getCoordinates();
            result = new PointType(null, new DirectPositionType(coord[0].x, coord[0].y));
            ((PointType)result).setSrsName("EPSG:4326");
        
        } else if (geom instanceof LineString){ 
            LineString ls = (LineString) geom;
            Coordinate[] coord = ls.getCoordinates();
            result = new LineStringType(new CoordinatesType(coord[0].x + "," + coord[0].y + " " + coord[1].x + "," + coord[1].y ));
            ((LineStringType)result).setSrsName("EPSG:4326");
            
        } else {
            LOGGER.severe("unable to create GML geometry with: " + geom.getClass().getSimpleName());
        }
        return result;
    }
}
