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
import java.text.ParseException;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.temporal.object.TemporalUtilities;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.temporal.Duration;

/**
 * Test reading CQL expressions.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ExpressionReadingTest {
    
    private final FilterFactory2 FF = new DefaultFilterFactory2();
    private final GeometryFactory GF = new GeometryFactory();
    
    @Test
    public void testPropertyName1() throws CQLException{        
        final String cql = "geom";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof PropertyName);
        final PropertyName expression = (PropertyName) obj;
        assertEquals("geom", expression.getPropertyName());                
    }
        
    @Test
    public void testPropertyName2() throws CQLException{        
        final String cql = "\"geom\"";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof PropertyName);
        final PropertyName expression = (PropertyName) obj;
        assertEquals("geom", expression.getPropertyName());                
    }
    
    @Test
    public void testPropertyName3() throws CQLException{        
        final String cql = "ùth{e_$uglY^_pr@perté";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof PropertyName);
        final PropertyName expression = (PropertyName) obj;
        assertEquals("ùth{e_$uglY^_pr@perté", expression.getPropertyName());                
    }
    
    @Test
    public void testInteger() throws CQLException{        
        final String cql = "15";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals(Integer.valueOf(15), expression.getValue());                
    }
    
    @Test
    public void testNegativeInteger() throws CQLException{        
        final String cql = "-15";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals(Integer.valueOf(-15), expression.getValue());                
    }
    
    @Test
    public void testDecimal1() throws CQLException{
        final String cql = "3.14";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals(Double.valueOf(3.14), expression.getValue());                
    }
    
    @Test
    public void testDecimal2() throws CQLException{
        final String cql = "9e-1";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals(Double.valueOf(9e-1), expression.getValue());                
    }
    
    @Test
    public void testNegativeDecimal() throws CQLException{        
        final String cql = "-3.14";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals(Double.valueOf(-3.14), expression.getValue());                
    }
    
    @Test
    public void testText() throws CQLException{
        final String cql = "'hello world'";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals("hello world", expression.getValue());                
    }
    
    @Test
    public void testDate() throws CQLException, ParseException{
        //dates are expected to be formated in ISO 8601 : yyyy-MM-dd'T'HH:mm:ss'Z'
        final String cql = "2012-03-21T05:42:36Z";
        final Object obj = CQL.parseExpression(cql);
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals(TemporalUtilities.parseDate("2012-03-21T05:42:36Z"), expression.getValue());
    }
    
    @Test
    public void testDuration() throws CQLException, ParseException{
        final String cql = "P7Y6M5D4H3M2S";
        final Object obj = CQL.parseExpression(cql);
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        final long duration = (Long) expression.getValue();
        
        assertEquals(236966582000l, duration);
    }
    
    @Test
    public void testDuration2() throws CQLException, ParseException{
        final String cql = "T4H3M2S";
        final Object obj = CQL.parseExpression(cql);
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        final long duration = (Long) expression.getValue();
        
        assertEquals(14582000,duration);
    }
    
    @Test
    public void testAddition() throws CQLException{
        final String cql = "3 + 2";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Add);
        final Add expression = (Add) obj;
        assertEquals(FF.add(FF.literal(3), FF.literal(2)), expression);                
    }
    
    @Test
    public void testSubstract() throws CQLException{
        final String cql = "3 - 2";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Subtract);
        final Subtract expression = (Subtract) obj;
        assertEquals(FF.subtract(FF.literal(3), FF.literal(2)), expression);                
    }
    
    @Test
    public void testMultiply() throws CQLException{
        final String cql = "3 * 2";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Multiply);
        final Multiply expression = (Multiply) obj;
        assertEquals(FF.multiply(FF.literal(3), FF.literal(2)), expression);                
    }
    
    @Test
    public void testDivide() throws CQLException{
        final String cql = "3 / 2";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Divide);
        final Divide expression = (Divide) obj;
        assertEquals(FF.divide(FF.literal(3), FF.literal(2)), expression);                
    }
    
    @Test
    public void testFunction1() throws CQLException{
        final String cql = "max(\"att\",15)";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Function);
        final Function expression = (Function) obj;
        assertEquals(FF.function("max",FF.property("att"), FF.literal(15)), expression);                
    }
    
    @Test
    public void testFunction2() throws CQLException{
        final String cql = "min(\"att\",cos(3.14))";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Function);
        final Function expression = (Function) obj;
        assertEquals(FF.function("min",FF.property("att"), FF.function("cos",FF.literal(3.14d))), expression);                
    }
    
    @Test
    public void testGeometryPoint() throws CQLException{
        final String cql = "POINT(15 30)";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;        
        final Geometry geom =  GF.createPoint(new Coordinate(15, 30));
        assertTrue(geom.equals((Geometry)expression.getValue()));  
    }
    
    @Test
    public void testGeometryMPoint() throws CQLException{
        final String cql = "MULTIPOINT(15 30, 45 60)";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;        
        final Geometry geom =  GF.createMultiPoint(
                new Coordinate[]{
                    new Coordinate(15, 30),
                    new Coordinate(45, 60)
                });
        assertTrue(geom.equals((Geometry)expression.getValue()));  
    }
    
    @Test
    public void testGeometryLineString() throws CQLException{
        final String cql = "LINESTRING(10 20, 30 40, 50 60)";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;        
        final Geometry geom =  GF.createLineString(
                new Coordinate[]{
                    new Coordinate(10, 20),
                    new Coordinate(30, 40),
                    new Coordinate(50, 60)
                });
        assertTrue(geom.equals((Geometry)expression.getValue()));  
    }
    
    @Test
    public void testGeometryMLineString() throws CQLException{
        final String cql = "MULTILINESTRING((10 20, 30 40, 50 60),(70 80, 90 100, 110 120))";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;        
        final Geometry geom =  GF.createMultiLineString(
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
        assertTrue(geom.equals((Geometry)expression.getValue()));  
    }
    
    @Test
    public void testGeometryPolygon() throws CQLException{
        final String cql = "POLYGON((10 20, 30 40, 50 60, 10 20), (70 80, 90 100, 110 120, 70 80))";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;        
        final Geometry geom =  GF.createPolygon(
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
        assertTrue(geom.equals((Geometry)expression.getValue()));  
    }
    
    @Test
    public void testGeometryMPolygon() throws CQLException{
        final String cql = "MULTIPOLYGON("
                + "((10 20, 30 40, 50 60, 10 20), (70 80, 90 100, 110 120, 70 80)),"
                + "((11 21, 31 41, 51 61, 11 21), (71 81, 91 101, 111 121, 71 81))"
                + ")";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;        
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
        assertTrue(geom.equals((Geometry)expression.getValue()));  
    }
    
    @Test
    public void testCombine1() throws CQLException{
        final String cql = "((3*1)+(2-6))/4";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Divide);
        final Divide expression = (Divide) obj;
        assertEquals(
                FF.divide(
                    FF.add(
                        FF.multiply(FF.literal(3), FF.literal(1)),
                        FF.subtract(FF.literal(2), FF.literal(6))
                        ), 
                    FF.literal(4))
                , expression);                
    }
    
    @Test
    public void testCombine2() throws CQLException{
        final String cql = "3*1+2/4";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Add);
        final Add rootAdd = (Add) obj;
        
        assertEquals(
                    FF.add(
                        FF.multiply(FF.literal(3), FF.literal(1)),
                        FF.divide(FF.literal(2), FF.literal(4))
                        )
                , rootAdd);     
                    
    }
    
    @Test
    public void testCombine3() throws CQLException{
        final String cql = "3*max(val,15)+2/4";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Add);
        final Add rootAdd = (Add) obj;
        
        assertEquals(
                    FF.add(
                        FF.multiply(
                            FF.literal(3), 
                            FF.function("max", FF.property("val"),FF.literal(15))
                        ),
                        FF.divide(FF.literal(2), FF.literal(4))
                        )
                , rootAdd);     
                    
    }
    
    @Test
    public void testCombine4() throws CQLException{
        final String cql = "3 * max ( val , 15 ) + 2 / 4";
        final Object obj = CQL.parseExpression(cql);        
        assertTrue(obj instanceof Add);
        final Add rootAdd = (Add) obj;
        
        assertEquals(
                    FF.add(
                        FF.multiply(
                            FF.literal(3), 
                            FF.function("max", FF.property("val"),FF.literal(15))
                        ),
                        FF.divide(FF.literal(2), FF.literal(4))
                        )
                , rootAdd);     
       
    }
    
}
