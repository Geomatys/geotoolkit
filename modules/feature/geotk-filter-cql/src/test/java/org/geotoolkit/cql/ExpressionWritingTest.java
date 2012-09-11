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
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.geotoolkit.filter.DefaultFilterFactory2;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;

/**
 * Test writing in CQL expressions.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ExpressionWritingTest {
    
    private final FilterFactory2 FF = new DefaultFilterFactory2();
    private final GeometryFactory GF = new GeometryFactory();
    
    @Test
    public void testPropertyName1() throws CQLException{
        final Expression exp = FF.property("geom");
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("geom", cql);              
    }
    
    @Test
    public void testPropertyName2() throws CQLException{        
        final Expression exp = FF.property("the geom");
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("\"the geom\"", cql);                   
    }
    
    @Test
    public void testInteger() throws CQLException{
        final Expression exp = FF.literal(15);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("15", cql);                     
    }
    
    @Test
    public void testNegativeInteger() throws CQLException{
        final Expression exp = FF.literal(-15);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("-15", cql);                     
    }
    
    @Test
    public void testDecimal1() throws CQLException{
        final Expression exp = FF.literal(3.14);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("3.14", cql);                                
    }
    
    @Test
    public void testDecimal2() throws CQLException{
        final Expression exp = FF.literal(9.0E-21);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("9.0E-21", cql);    
    }
    
    @Test
    public void testNegativeDecimal() throws CQLException{
        final Expression exp = FF.literal(-3.14);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("-3.14", cql);                     
    }
    
    @Test
    public void testText() throws CQLException{
        final Expression exp = FF.literal("hello world");
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("'hello world'", cql);             
    }
    
    @Test
    public void testAdd() throws CQLException{
        final Expression exp = FF.add(FF.literal(3),FF.literal(2));
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("3 + 2", cql);             
    }
    
    @Test
    public void testSubtract() throws CQLException{
        final Expression exp = FF.subtract(FF.literal(3),FF.literal(2));
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("3 - 2", cql);             
    }
    
    @Test
    public void testMultiply() throws CQLException{
        final Expression exp = FF.multiply(FF.literal(3),FF.literal(2));
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("3 * 2", cql);             
    }
    
    @Test
    public void testDivide() throws CQLException{
        final Expression exp = FF.divide(FF.literal(3),FF.literal(2));
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("3 / 2", cql);             
    }
    
    @Test
    public void testFunction1() throws CQLException{
        final Expression exp = FF.function("max",FF.property("att"), FF.literal(15));
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("max(att , 15)", cql);
    }
    
    @Test
    public void testFunction2() throws CQLException{
        final Expression exp = FF.function("min",FF.property("att"), FF.function("cos",FF.literal(3.14d)));
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("min(att , cos(3.14))", cql);
    }
    
    @Test
    public void testCombine1() throws CQLException{
        final Expression exp =  
                FF.divide(
                    FF.add(
                        FF.multiply(FF.literal(3), FF.literal(1)),
                        FF.subtract(FF.literal(2), FF.literal(6))
                        ), 
                    FF.literal(4));
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("3 * 1 + 2 - 6 / 4", cql);              
    }
    
    @Test
    public void testCombine2() throws CQLException{
        final Expression exp =  
                FF.add(
                        FF.multiply(FF.literal(3), FF.literal(1)),
                        FF.divide(FF.literal(2), FF.literal(4))
                        );
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("3 * 1 + 2 / 4", cql);        
                    
    }
    
    @Test
    public void testCombine3() throws CQLException{
        final Expression exp =  
                FF.add(
                        FF.multiply(
                            FF.literal(3), 
                            FF.function("max", FF.property("val"),FF.literal(15))
                        ),
                        FF.divide(FF.literal(2), FF.literal(4))
                        );
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("3 * max(val , 15) + 2 / 4", cql);
    }
        
    @Test
    public void testPoint() throws CQLException{
        final Geometry geom = GF.createPoint(new Coordinate(15, 30));
        final Expression exp = FF.literal(geom);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("POINT (15 30)", cql);
    }
    
    @Test
    public void testMPoint() throws CQLException{
        final Geometry geom = GF.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(15, 30),
                    new Coordinate(45, 60)
                });
        final Expression exp = FF.literal(geom);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("MULTIPOINT ((15 30), (45 60))", cql);
    }
    
    @Test
    public void testLineString() throws CQLException{
        final Geometry geom = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(10, 20),
                    new Coordinate(30, 40),
                    new Coordinate(50, 60)
                });
        final Expression exp = FF.literal(geom);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("LINESTRING (10 20, 30 40, 50 60)", cql);
    }
    
    @Test
    public void testMLineString() throws CQLException{
        final Geometry geom = GF.createMultiLineString(
                new LineString[]{
                    GF.createLineString(
                        new Coordinate[]{
                            new Coordinate(10, 20),
                            new Coordinate(30, 40),
                            new Coordinate(50, 60)
                        }),
                    GF.createLineString(
                        new Coordinate[]{
                            new Coordinate(70, 80),
                            new Coordinate(90, 100),
                            new Coordinate(110, 120)
                        })
                    }
                );
        final Expression exp = FF.literal(geom);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("MULTILINESTRING ((10 20, 30 40, 50 60), (70 80, 90 100, 110 120))", cql);
    }
    
    @Test
    public void testPolygon() throws CQLException{
        final Geometry geom = GF.createPolygon(
                GF.createLinearRing(
                    new Coordinate[]{
                        new Coordinate(10, 20),
                        new Coordinate(30, 40),
                        new Coordinate(50, 60),
                        new Coordinate(10, 20)
                    }),
                new LinearRing[]{
                    GF.createLinearRing(
                        new Coordinate[]{
                            new Coordinate(70, 80),
                            new Coordinate(90, 100),
                            new Coordinate(110, 120),
                            new Coordinate(70, 80)
                        })
                    }
                );
        final Expression exp = FF.literal(geom);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("POLYGON ((10 20, 30 40, 50 60, 10 20), (70 80, 90 100, 110 120, 70 80))", cql);
    }
    
    @Test
    public void testMPolygon() throws CQLException{
        final Polygon geom1 = GF.createPolygon(
                GF.createLinearRing(
                    new Coordinate[]{
                        new Coordinate(10, 20),
                        new Coordinate(30, 40),
                        new Coordinate(50, 60),
                        new Coordinate(10, 20)
                    }),
                new LinearRing[]{
                    GF.createLinearRing(
                        new Coordinate[]{
                            new Coordinate(70, 80),
                            new Coordinate(90, 100),
                            new Coordinate(110, 120),
                            new Coordinate(70, 80)
                        })
                    }
                );
        final Polygon geom2 = GF.createPolygon(
                GF.createLinearRing(
                    new Coordinate[]{
                        new Coordinate(11, 21),
                        new Coordinate(31, 41),
                        new Coordinate(51, 61),
                        new Coordinate(11, 21)
                    }),
                new LinearRing[]{
                    GF.createLinearRing(
                        new Coordinate[]{
                            new Coordinate(71, 81),
                            new Coordinate(91, 101),
                            new Coordinate(111, 121),
                            new Coordinate(71, 81)
                        })
                    }
                );
        final Geometry geom = GF.createMultiPolygon(new Polygon[]{geom1,geom2});
        final Expression exp = FF.literal(geom);
        final String cql = CQL.write(exp);
        assertNotNull(cql);
        assertEquals("MULTIPOLYGON (((10 20, 30 40, 50 60, 10 20), (70 80, 90 100, 110 120, 70 80)), ((11 21, 31 41, 51 61, 11 21), (71 81, 91 101, 111 121, 71 81)))", cql);
    }
    
}
