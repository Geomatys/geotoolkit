/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.cql;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.filter.And;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
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
import org.opengis.filter.expression.Literal;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;

/**
 * Test reading CQL filters.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FilterReadingTest {
    
    private final FilterFactory2 FF = new DefaultFilterFactory2();
    private final GeometryFactory GF = new GeometryFactory();
    private final Geometry baseGeometry = GF.createPolygon(
                GF.createLinearRing(
                    new Coordinate[]{
                        new Coordinate(10, 20),
                        new Coordinate(30, 40),
                        new Coordinate(50, 60),
                        new Coordinate(10, 20)
                    }),
                new LinearRing[0]
                );
    
    
    @Test
    public void testNullFilter() throws CQLException {
        //this is not true cql but is since in commun use cases.
        String cql = "";
        Object obj = CQL.parseFilter(cql);
        assertEquals(Filter.INCLUDE,obj);
        
        cql = "*";
        obj = CQL.parseFilter(cql);
        assertEquals(Filter.INCLUDE,obj);
    }

    @Test
    public void testAnd() throws CQLException {
        final String cql = "att1 = 15 AND att2 = 30 AND att3 = 50";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Filter);
        final Filter filter = (Filter) obj;
        assertEquals(
                FF.and(
                UnmodifiableArrayList.wrap((Filter)
                    FF.and(
                        UnmodifiableArrayList.wrap((Filter)
                            FF.equals(FF.property("att1"), FF.literal(15)),
                            FF.equals(FF.property("att2"), FF.literal(30))
                        )),
                    FF.equals(FF.property("att3"), FF.literal(50))
                )),
                filter);     
    }
    
    @Test
    public void testOr() throws CQLException {
        final String cql = "att1 = 15 OR att2 = 30 OR att3 = 50";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Filter);
        final Filter filter = (Filter) obj;
        assertEquals(
                FF.or(
                UnmodifiableArrayList.wrap((Filter)
                    FF.or(
                    UnmodifiableArrayList.wrap((Filter)
                        FF.equals(FF.property("att1"), FF.literal(15)),
                        FF.equals(FF.property("att2"), FF.literal(30))
                    )),
                    FF.equals(FF.property("att3"), FF.literal(50))
                )),
                filter);     
    }

    @Test
    public void testNot() throws CQLException {
        final String cql = "NOT att = 15";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Not);
        final Not filter = (Not) obj;
        assertEquals(FF.not(FF.equals(FF.property("att"), FF.literal(15))), filter);    
    }

    @Test
    public void testPropertyIsBetween() throws CQLException {
        final String cql = "att BETWEEN 15 AND 30";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsBetween);
        final PropertyIsBetween filter = (PropertyIsBetween) obj;
        assertEquals(FF.between(FF.property("att"), FF.literal(15), FF.literal(30)), filter);                
    }
    
    @Test
    public void testIn() throws CQLException {
        final String cql = "att IN ( 15, 30, 'hello')";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Or);
        final Or filter = (Or) obj;
        assertEquals(FF.equals(FF.property("att"), FF.literal(15)), filter.getChildren().get(0));  
        assertEquals(FF.equals(FF.property("att"), FF.literal(30)), filter.getChildren().get(1)); 
        assertEquals(FF.equals(FF.property("att"), FF.literal("hello")), filter.getChildren().get(2));               
    }
    
    @Test
    public void testNotIn() throws CQLException {
        final String cql = "att NOT IN ( 15, 30, 'hello')";
        Object obj = CQL.parseFilter(cql);
        obj = ((Not)obj).getFilter();
        assertTrue(obj instanceof Or);
        final Or filter = (Or) obj;
        assertEquals(FF.equals(FF.property("att"), FF.literal(15)), filter.getChildren().get(0));  
        assertEquals(FF.equals(FF.property("att"), FF.literal(30)), filter.getChildren().get(1)); 
        assertEquals(FF.equals(FF.property("att"), FF.literal("hello")), filter.getChildren().get(2));               
    }

    @Test
    public void testPropertyIsEqualTo() throws CQLException {
        final String cql = "att = 15";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsEqualTo);
        final PropertyIsEqualTo filter = (PropertyIsEqualTo) obj;
        assertEquals(FF.equals(FF.property("att"), FF.literal(15)), filter);                
    }

    @Test
    public void testPropertyIsNotEqualTo() throws CQLException {
        final String cql = "att <> 15";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsNotEqualTo);
        final PropertyIsNotEqualTo filter = (PropertyIsNotEqualTo) obj;
        assertEquals(FF.notEqual(FF.property("att"), FF.literal(15)), filter);   
    }

    @Test
    public void testPropertyIsGreaterThan() throws CQLException {
        final String cql = "att > 15";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsGreaterThan);
        final PropertyIsGreaterThan filter = (PropertyIsGreaterThan) obj;
        assertEquals(FF.greater(FF.property("att"), FF.literal(15)), filter);   
    }

    @Test
    public void testPropertyIsGreaterThanOrEqualTo() throws CQLException {
        final String cql = "att >= 15";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsGreaterThanOrEqualTo);
        final PropertyIsGreaterThanOrEqualTo filter = (PropertyIsGreaterThanOrEqualTo) obj;
        assertEquals(FF.greaterOrEqual(FF.property("att"), FF.literal(15)), filter);   
    }

    @Test
    public void testPropertyIsLessThan() throws CQLException {
        final String cql = "att < 15";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsLessThan);
        final PropertyIsLessThan filter = (PropertyIsLessThan) obj;
        assertEquals(FF.less(FF.property("att"), FF.literal(15)), filter);   
    }

    @Test
    public void testPropertyIsLessThanOrEqualTo() throws CQLException {
        final String cql = "att <= 15";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsLessThanOrEqualTo);
        final PropertyIsLessThanOrEqualTo filter = (PropertyIsLessThanOrEqualTo) obj;
        assertEquals(FF.lessOrEqual(FF.property("att"), FF.literal(15)), filter);   
    }

    @Test
    public void testPropertyIsLike() throws CQLException {
        final String cql = "att LIKE '%hello?'";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsLike);
        final PropertyIsLike filter = (PropertyIsLike) obj;
        assertEquals(FF.like(FF.property("att"),"%hello?"), filter);   
    }

    @Test
    public void testPropertyIsNotLike() throws CQLException {
        final String cql = "att NOT LIKE '%hello?'";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Not);
        final Not filter = (Not) obj;
        assertEquals(FF.not(FF.like(FF.property("att"),"%hello?")), filter);   
    }
    
    @Test
    public void testPropertyIsNull() throws CQLException {
        final String cql = "att IS NULL";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsNull);
        final PropertyIsNull filter = (PropertyIsNull) obj;
        assertEquals(FF.isNull(FF.property("att")), filter);   
    }
    
    @Test
    public void testPropertyIsNotNull() throws CQLException {
        final String cql = "att IS NOT NULL";
        Object obj = CQL.parseFilter(cql);
        obj = ((Not)obj).getFilter();
        assertTrue(obj instanceof PropertyIsNull);
        final PropertyIsNull filter = (PropertyIsNull) obj;
        assertEquals(FF.isNull(FF.property("att")), filter);   
    }

    @Test
    public void testBBOX1() throws CQLException {
        final String cql = "BBOX(\"att\" ,10, 20, 30, 40)";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof BBOX);
        final BinarySpatialOperator filter = (BBOX) obj;
        assertEquals(FF.bbox(FF.property("att"), 10,20,30,40, null), filter);   
    }
    
    @Test
    public void testBBOX2() throws CQLException {
        final String cql = "BBOX(\"att\" ,10, 20, 30, 40, 'CRS:84')";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof BBOX);
        final BBOX filter = (BBOX) obj;
        assertEquals(FF.bbox(FF.property("att"), 10,20,30,40, "CRS:84"), filter);   
    }

    @Test
    public void testBeyond() throws CQLException {
        final String cql = "BEYOND(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Beyond);
        final Beyond filter = (Beyond) obj;
                
        assertEquals(FF.property("att"), filter.getExpression1());
        assertTrue(filter.getExpression2() instanceof Literal);
        assertTrue( ((Literal)filter.getExpression2()).getValue() instanceof Geometry);
        final Geometry filtergeo = (Geometry) ((Literal)filter.getExpression2()).getValue();
        assertTrue(baseGeometry.equalsExact(filtergeo)); 
    }

    @Test
    public void testContains() throws CQLException {
        final String cql = "CONTAINS(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Contains);
        final Contains filter = (Contains) obj;
                
        assertEquals(FF.property("att"), filter.getExpression1());
        assertTrue(filter.getExpression2() instanceof Literal);
        assertTrue( ((Literal)filter.getExpression2()).getValue() instanceof Geometry);
        final Geometry filtergeo = (Geometry) ((Literal)filter.getExpression2()).getValue();
        assertTrue(baseGeometry.equalsExact(filtergeo)); 
    }

    @Test
    public void testCrosses() throws CQLException {
        final String cql = "CROSS(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Crosses);
        final Crosses filter = (Crosses) obj;
                
        assertEquals(FF.property("att"), filter.getExpression1());
        assertTrue(filter.getExpression2() instanceof Literal);
        assertTrue( ((Literal)filter.getExpression2()).getValue() instanceof Geometry);
        final Geometry filtergeo = (Geometry) ((Literal)filter.getExpression2()).getValue();
        assertTrue(baseGeometry.equalsExact(filtergeo)); 
    }

    @Test
    public void testDisjoint() throws CQLException {
        final String cql = "DISJOINT(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Disjoint);
        final Disjoint filter = (Disjoint) obj;
                
        assertEquals(FF.property("att"), filter.getExpression1());
        assertTrue(filter.getExpression2() instanceof Literal);
        assertTrue( ((Literal)filter.getExpression2()).getValue() instanceof Geometry);
        final Geometry filtergeo = (Geometry) ((Literal)filter.getExpression2()).getValue();
        assertTrue(baseGeometry.equalsExact(filtergeo)); 
    }

    @Test
    public void testDWithin() throws CQLException {
        final String cql = "DWITHIN(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof DWithin);
        final DWithin filter = (DWithin) obj;
                
        assertEquals(FF.property("att"), filter.getExpression1());
        assertTrue(filter.getExpression2() instanceof Literal);
        assertTrue( ((Literal)filter.getExpression2()).getValue() instanceof Geometry);
        final Geometry filtergeo = (Geometry) ((Literal)filter.getExpression2()).getValue();
        assertTrue(baseGeometry.equalsExact(filtergeo)); 
    }

    @Test
    public void testEquals() throws CQLException {
        final String cql = "EQUALS(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Equals);
        final Equals filter = (Equals) obj;
                
        assertEquals(FF.property("att"), filter.getExpression1());
        assertTrue(filter.getExpression2() instanceof Literal);
        assertTrue( ((Literal)filter.getExpression2()).getValue() instanceof Geometry);
        final Geometry filtergeo = (Geometry) ((Literal)filter.getExpression2()).getValue();
        assertTrue(baseGeometry.equalsExact(filtergeo)); 
    }

    @Test
    public void testIntersects() throws CQLException {
        final String cql = "INTERSECT(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Intersects);
        final Intersects filter = (Intersects) obj;
                
        assertEquals(FF.property("att"), filter.getExpression1());
        assertTrue(filter.getExpression2() instanceof Literal);
        assertTrue( ((Literal)filter.getExpression2()).getValue() instanceof Geometry);
        final Geometry filtergeo = (Geometry) ((Literal)filter.getExpression2()).getValue();
        assertTrue(baseGeometry.equalsExact(filtergeo)); 
    }

    @Test
    public void testOverlaps() throws CQLException {
        final String cql = "OVERLAP(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Overlaps);
        final Overlaps filter = (Overlaps) obj;
                
        assertEquals(FF.property("att"), filter.getExpression1());
        assertTrue(filter.getExpression2() instanceof Literal);
        assertTrue( ((Literal)filter.getExpression2()).getValue() instanceof Geometry);
        final Geometry filtergeo = (Geometry) ((Literal)filter.getExpression2()).getValue();
        assertTrue(baseGeometry.equalsExact(filtergeo)); 
    }

    @Test
    public void testTouches() throws CQLException {
        final String cql = "TOUCH(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Touches);
        final Touches filter = (Touches) obj;
                
        assertEquals(FF.property("att"), filter.getExpression1());
        assertTrue(filter.getExpression2() instanceof Literal);
        assertTrue( ((Literal)filter.getExpression2()).getValue() instanceof Geometry);
        final Geometry filtergeo = (Geometry) ((Literal)filter.getExpression2()).getValue();
        assertTrue(baseGeometry.equalsExact(filtergeo)); 
    }

    @Test
    public void testWithin() throws CQLException {
        final String cql = "WITHIN(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Within);
        final Within filter = (Within) obj;
                
        assertEquals(FF.property("att"), filter.getExpression1());
        assertTrue(filter.getExpression2() instanceof Literal);
        assertTrue( ((Literal)filter.getExpression2()).getValue() instanceof Geometry);
        final Geometry filtergeo = (Geometry) ((Literal)filter.getExpression2()).getValue();
        assertTrue(baseGeometry.equalsExact(filtergeo)); 
    }
    
    @Test
    public void testCombine1() throws CQLException {
        final String cql = "NOT att = 15 OR att BETWEEN 15 AND 30";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Or);
        final Or filter = (Or) obj;
        assertEquals(
                FF.or(
                    FF.not(FF.equals(FF.property("att"), FF.literal(15))),
                    FF.between(FF.property("att"), FF.literal(15), FF.literal(30))
                ),
                filter
                );    
    }

    @Test
    public void testCombine2() throws CQLException {
        final String cql = "(NOT att = 15) OR (att BETWEEN 15 AND 30)";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof Or);
        final Or filter = (Or) obj;
        assertEquals(
                FF.or(
                    FF.not(FF.equals(FF.property("att"), FF.literal(15))),
                    FF.between(FF.property("att"), FF.literal(15), FF.literal(30))
                ),
                filter
                );               
    }
    
    @Test
    public void testCombine3() throws CQLException {
        final String cql = "(NOT att1 = 15) AND (att2 = 15 OR att3 BETWEEN 15 AND 30) AND (att4 BETWEEN 1 AND 2)";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof And);
        final And filter = (And) obj;
        assertEquals(
                FF.and(
                    FF.and(
                        UnmodifiableArrayList.wrap(
                            FF.not(FF.equals(FF.property("att1"), FF.literal(15))),
                            FF.or(
                                FF.equals(FF.property("att2"), FF.literal(15)),
                                FF.between(FF.property("att3"), FF.literal(15), FF.literal(30))
                            )
                        )
                    ),
                    FF.between(FF.property("att4"), FF.literal(1), FF.literal(2))
                ),
                filter
                );               
    }
    
    @Test
    public void testCombine4() throws CQLException {
        final String cql = "(x+7) <= (y-9)";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsLessThanOrEqualTo);
        final PropertyIsLessThanOrEqualTo filter = (PropertyIsLessThanOrEqualTo) obj;
        assertEquals(
                FF.lessOrEqual(
                    FF.add(FF.property("x"), FF.literal(7)),
                    FF.subtract(FF.property("y"), FF.literal(9))
                ),
                filter
                );               
    }
    
}
