/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.filter;

// JTS dependencies
import org.geotoolkit.gml.xml.v311.DirectPositionType;
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

// constellation dependencies
import org.geotoolkit.ogc.xml.ExpressionType;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.CoordinatesType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeEntry;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.ogc.xml.AndType;
import org.geotoolkit.ogc.xml.ArithmeticOperatorsType;
import org.geotoolkit.ogc.xml.BBOXType;
import org.geotoolkit.ogc.xml.BeyondType;
import org.geotoolkit.ogc.xml.ComparisonOperatorsType;
import org.geotoolkit.ogc.xml.ContainsType;
import org.geotoolkit.ogc.xml.CrossesType;
import org.geotoolkit.ogc.xml.DWithinType;
import org.geotoolkit.ogc.xml.DisjointType;
import org.geotoolkit.ogc.xml.EqualsType;
import org.geotoolkit.ogc.xml.FeatureIdType;
import org.geotoolkit.ogc.xml.FunctionNameType;
import org.geotoolkit.ogc.xml.FunctionNamesType;
import org.geotoolkit.ogc.xml.FunctionType;
import org.geotoolkit.ogc.xml.GmlObjectIdType;
import org.geotoolkit.ogc.xml.IdCapabilitiesType;
import org.geotoolkit.ogc.xml.IntersectsType;
import org.geotoolkit.ogc.xml.LiteralType;
import org.geotoolkit.ogc.xml.LowerBoundaryType;
import org.geotoolkit.ogc.xml.NotType;
import org.geotoolkit.ogc.xml.OrType;
import org.geotoolkit.ogc.xml.OverlapsType;
import org.geotoolkit.ogc.xml.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.PropertyIsEqualToType;
import org.geotoolkit.ogc.xml.PropertyIsGreaterThanOrEqualToType;
import org.geotoolkit.ogc.xml.PropertyIsGreaterThanType;
import org.geotoolkit.ogc.xml.PropertyIsLessThanOrEqualToType;
import org.geotoolkit.ogc.xml.PropertyIsLessThanType;
import org.geotoolkit.ogc.xml.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.PropertyIsNotEqualToType;
import org.geotoolkit.ogc.xml.PropertyIsNullType;
import org.geotoolkit.ogc.xml.PropertyNameType;
import org.geotoolkit.ogc.xml.ScalarCapabilitiesType;
import org.geotoolkit.ogc.xml.SortPropertyType;
import org.geotoolkit.ogc.xml.SpatialCapabilitiesType;
import org.geotoolkit.ogc.xml.SpatialOperatorType;
import org.geotoolkit.ogc.xml.SpatialOperatorsType;
import org.geotoolkit.ogc.xml.TouchesType;
import org.geotoolkit.ogc.xml.UpperBoundaryType;
import org.geotoolkit.ogc.xml.WithinType;

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
 */
public class FilterFactoryImpl implements FilterFactory2 {

    private final Logger logger = Logger.getLogger("org.geotoolkit.filter");
    
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public FeatureId featureId(String id) {
        return new FeatureIdType(id);
    }

    public GmlObjectId gmlObjectId(String id) {
        return new GmlObjectIdType(id);
    }

    public And and(Filter f, Filter g) {
        return new AndType(f, g);
    }

    public And and(List<Filter> f) {
        return new AndType(f);
    }

    public Or or(Filter f, Filter g) {
        return new OrType(f, g);
    }

    public Or or(List<Filter> f) {
        return new OrType(f);
    }

    public Not not(Filter f) {
        return new NotType(f);
    }

    public Id id(Set<? extends Identifier> ids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PropertyName property(String name) {
        return new PropertyNameType(name);
    }

    public PropertyIsBetween between(Expression expr, Expression lower, Expression upper) {
        return new PropertyIsBetweenType( (ExpressionType)    expr, 
                                          (LowerBoundaryType) lower, 
                                          (UpperBoundaryType) upper);
    }

    public PropertyIsEqualTo equals(Expression expr1, Expression expr2) {
        return new PropertyIsEqualToType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsEqualTo equal(Expression expr1, Expression expr2, boolean matchCase) {
        return new PropertyIsEqualToType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsNotEqualTo notEqual(Expression expr1, Expression expr2) {
        return new PropertyIsNotEqualToType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsNotEqualTo notEqual(Expression expr1, Expression expr2, boolean matchCase) {
        return new PropertyIsNotEqualToType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsGreaterThan greater(Expression expr1, Expression expr2) {
        return new PropertyIsGreaterThanType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsGreaterThan greater(Expression expr1, Expression expr2, boolean matchCase) {
        return new PropertyIsGreaterThanType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsGreaterThanOrEqualTo greaterOrEqual(Expression expr1, Expression expr2) {
        return new PropertyIsGreaterThanOrEqualToType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsGreaterThanOrEqualTo greaterOrEqual(Expression expr1, Expression expr2, boolean matchCase) {
        return new PropertyIsGreaterThanOrEqualToType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsLessThan less(Expression expr1, Expression expr2, boolean matchCase) {
        return new PropertyIsLessThanType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsLessThan less(Expression expr1, Expression expr2) {
        return new PropertyIsLessThanType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsLessThanOrEqualTo lessOrEqual(Expression expr1, Expression expr2, boolean matchCase) {
        return new PropertyIsLessThanOrEqualToType((LiteralType) expr2, (PropertyNameType) expr1, matchCase);
    }

    public PropertyIsLessThanOrEqualTo lessOrEqual(Expression expr1, Expression expr2) {
        return new PropertyIsLessThanOrEqualToType((LiteralType) expr2, (PropertyNameType) expr1, null);
    }

    public PropertyIsLike like(Expression expr, String pattern) {
        return like(expr, pattern, "*", "?", "\\");
    }

    public PropertyIsLike like(Expression expr, String pattern, boolean isMatchingCase) {
        return like(expr, pattern, "*", "?", "\\", isMatchingCase);
    }

    public PropertyIsLike like(Expression expr, String pattern, String wildcard, String singleChar, String escape) {
        //SQLBuilder add a white space at then end of the pattern we remove it
        if (pattern != null && pattern.lastIndexOf(' ') == pattern.length() -1)
            pattern = pattern.substring(0, pattern.length() -1);
        return new PropertyIsLikeType(expr, pattern, wildcard, singleChar, escape);
    }

    public PropertyIsLike like(Expression expr, String pattern, String wildcard, String singleChar, String escape, boolean isMatchingCase) {
        //SQLBuilder add a white space at then end of the pattern we remove it
        if (pattern != null && pattern.lastIndexOf(' ') == pattern.length() -1)
            pattern = pattern.substring(0, pattern.length() -1);
        return new PropertyIsLikeType(expr, pattern, wildcard, singleChar, escape, isMatchingCase);
    }

    public PropertyIsNull isNull(Expression expr) {
        return new PropertyIsNullType((PropertyNameType)expr);
    }

    public BBOX bbox(String propertyName, double minx, double miny, double maxx, double maxy, String srs) {
        if (srs == null || srs.equals("")) {
            srs = "EPSG:4326";
        }
        return new BBOXType(propertyName, minx, miny, maxx, maxy, srs);
    }
    
    public BBOX bbox(Expression geometry, double minx, double miny, double maxx, double maxy, String srs) {
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

    public BBOX bbox(Expression geometry, BoundingBox bounds) {
        String propertyName = "";
        String CRSName      = "";
        if (geometry instanceof PropertyNameType) {
            propertyName = ((PropertyNameType)geometry).getPropertyName();
        }
        if (bounds.getCoordinateReferenceSystem() != null) {
            CRSName = bounds.getCoordinateReferenceSystem().getName() + "";
        } else {
            CRSName = "EPSG:4326";
        }
        return new BBOXType(propertyName, bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), CRSName);
    }

    public Beyond beyond(String propertyName, Geometry geometry, double distance, String units) {
       
        return new BeyondType(propertyName, (AbstractGeometryType) geometry, distance, units);
    }

    public Beyond beyond(Expression geometry1, Expression geometry2, double distance, String units) {
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
    
    public DWithin dwithin(String propertyName, Geometry geometry, double distance, String units) {
        return new DWithinType(propertyName, (AbstractGeometryType) geometry, distance, units);
    }

    public DWithin dwithin(Expression geometry1, Expression geometry2, double distance, String units) {
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
    
    public Contains contains(String propertyName, Geometry geometry) {
        return new ContainsType(propertyName, (AbstractGeometryType) geometry);
    }

    public Contains contains(Expression geometry1, Expression geometry2) {
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
    
    public Crosses crosses(String propertyName, Geometry geometry) {
        return new CrossesType(propertyName, (AbstractGeometryType) geometry);
    }

    public Crosses crosses(Expression geometry1, Expression geometry2) {
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
    
    public Disjoint disjoint(String propertyName, Geometry geometry) {
        return new DisjointType(propertyName, (AbstractGeometryType) geometry);
    }

    public Disjoint disjoint(Expression geometry1, Expression geometry2) {
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
    
    
    
    public Equals equals(String propertyName, Geometry geometry) {
        return new EqualsType(propertyName, (AbstractGeometryType) geometry);
    }

    public Equals equal(Expression geometry1, Expression geometry2) {
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

    public Intersects intersects(String propertyName, Geometry geometry) {
        return new IntersectsType(propertyName, (AbstractGeometryType) geometry);
    }

    public Intersects intersects(Expression geometry1, Expression geometry2) {
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
    
    public Overlaps overlaps(String propertyName, Geometry geometry) {
        return new OverlapsType(propertyName, (AbstractGeometryType) geometry);
    }

    public Overlaps overlaps(Expression geometry1, Expression geometry2) {
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
    
    public Touches touches(String propertyName, Geometry geometry) {
        return new TouchesType(propertyName, (AbstractGeometryType) geometry);
    }

    public Touches touches(Expression propertyName1, Expression geometry2) {
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
    
    public Within within(String propertyName, Geometry geometry) {
       return new WithinType(propertyName, (AbstractGeometryType) geometry);
    }
    
    public Within within(Expression geometry1, Expression geometry2) {
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


    public Add add(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Divide divide(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Multiply multiply(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Subtract subtract(Expression expr1, Expression expr2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Function function(String name, Expression[] args) {
        return new FunctionType(name, args);
    }

    public Function function(String name, Expression arg1) {
        return new FunctionType(name, arg1);
    }

    public Function function(String name, Expression arg1, Expression arg2) {
        return new FunctionType(name, arg1, arg2);
    }

    public Function function(String name, Expression arg1, Expression arg2, Expression arg3) {
         return new FunctionType(name, arg1, arg2, arg3);
    }

    public Literal literal(Object obj) {
        if (obj instanceof Date) {
            Date d = (Date) obj;
            synchronized(dateFormat) {
                obj = dateFormat.format(d);
            }
        }
        return new LiteralType(obj);
    }

    public Literal literal(byte b) {
        return new LiteralType(b);
    }

    public Literal literal(short s) {
        return new LiteralType(s);
    }

    public Literal literal(int i) {
        return new LiteralType(i);
    }

    public Literal literal(long l) {
        return new LiteralType(l);
    }

    public Literal literal(float f) {
        return new LiteralType(f);
    }

    public Literal literal(double d) {
        return new LiteralType(d);
    }

    public Literal literal(char c) {
        return new LiteralType(c);
    }

    public Literal literal(boolean b) {
        return new LiteralType(b);
    }

    public SortBy sort(String propertyName, SortOrder order) {
        return new SortPropertyType(propertyName, order);
    }

    public Operator operator(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SpatialOperator spatialOperator(String name, GeometryOperand[] geometryOperands) {
        return new SpatialOperatorType(name, geometryOperands);
    }

    public FunctionName functionName(String name, int nargs) {
        return new FunctionNameType(name, nargs);
    }

    public Functions functions(FunctionName[] functionNames) {
       
        return new FunctionNamesType(Arrays.asList((FunctionNameType[])functionNames));
    }

    public SpatialOperators spatialOperators(SpatialOperator[] spatialOperators) {
       return new SpatialOperatorsType( spatialOperators );
    }

    public ComparisonOperators comparisonOperators(Operator[] comparisonOperators) {
        return new ComparisonOperatorsType(comparisonOperators);
    }

    public ArithmeticOperators arithmeticOperators(boolean simple, Functions functions) {
         return new ArithmeticOperatorsType(simple, functions);
    }

    public ScalarCapabilities scalarCapabilities(ComparisonOperators comparison, ArithmeticOperators arithmetic, boolean logical) {
        return new ScalarCapabilitiesType(comparison, arithmetic, logical);
    }

    public SpatialCapabilities spatialCapabilities(GeometryOperand[] geometryOperands, SpatialOperators spatial) {
        return new SpatialCapabilitiesType(geometryOperands, spatial);
    }

    public IdCapabilities idCapabilities(boolean eid, boolean fid) {
        return new IdCapabilitiesType(eid, fid);
    }

    public FilterCapabilities capabilities(String version, ScalarCapabilities scalar, SpatialCapabilities spatial, IdCapabilities id) {
        return new org.geotoolkit.ogc.xml.FilterCapabilities(scalar, spatial, id);
    }

    public PropertyName property(Name name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Transform a JTS geometric object into a GML marshallable object
     * @param geom
     * @return
     */
    public Object GeometryToGML(Object geom) {
        Object result = null;
        if (geom instanceof Polygon) {
            Polygon p          = (Polygon) geom;
            Coordinate[] coord = p.getCoordinates();
            
            // an envelope
            if (coord.length == 5) {
                DirectPositionType lowerCorner = new DirectPositionType(coord[0].x, coord[1].y);
                DirectPositionType upperCorner = new DirectPositionType(coord[2].x, coord[0].y);
                result = new EnvelopeEntry(null, lowerCorner, upperCorner, "EPSG:4326");
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
            logger.severe("unable to create GML geometry with: " + geom.getClass().getSimpleName());
        }
        return result;
    }
}
