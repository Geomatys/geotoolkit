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

import org.geotoolkit.filter.DefaultFilterFactory2;
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

/**
 * Test reading CQL expressions.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ExpressionReadingTest {
    
    private final FilterFactory2 FF = new DefaultFilterFactory2();
    
    @Test
    public void testPropertyName1() throws CQLException{        
        final String cql = "geom";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof PropertyName);
        final PropertyName expression = (PropertyName) obj;
        assertEquals("geom", expression.getPropertyName());                
    }
    
    @Test
    public void testPropertyName2() throws CQLException{        
        final String cql = "\"geom\"";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof PropertyName);
        final PropertyName expression = (PropertyName) obj;
        assertEquals("geom", expression.getPropertyName());                
    }
    
    @Test
    public void testInteger() throws CQLException{        
        final String cql = "15";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals(Integer.valueOf(15), expression.getValue());                
    }
    
    @Test
    public void testDecimal1() throws CQLException{
        final String cql = "3.14";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals(Double.valueOf(3.14), expression.getValue());                
    }
    
    @Test
    public void testDecimal2() throws CQLException{
        final String cql = "9e-1";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals(Double.valueOf(9e-1), expression.getValue());                
    }
    
    @Test
    public void testText() throws CQLException{
        final String cql = "'hello world'";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof Literal);
        final Literal expression = (Literal) obj;
        assertEquals("hello world", expression.getValue());                
    }
    
    @Test
    public void testAddition() throws CQLException{
        final String cql = "3 + 2";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof Add);
        final Add expression = (Add) obj;
        assertEquals(FF.add(FF.literal(3), FF.literal(2)), expression);                
    }
    
    @Test
    public void testSubstract() throws CQLException{
        final String cql = "3 - 2";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof Subtract);
        final Subtract expression = (Subtract) obj;
        assertEquals(FF.subtract(FF.literal(3), FF.literal(2)), expression);                
    }
    
    @Test
    public void testMultiply() throws CQLException{
        final String cql = "3 * 2";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof Multiply);
        final Multiply expression = (Multiply) obj;
        assertEquals(FF.multiply(FF.literal(3), FF.literal(2)), expression);                
    }
    
    @Test
    public void testDivide() throws CQLException{
        final String cql = "3 / 2";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof Divide);
        final Divide expression = (Divide) obj;
        assertEquals(FF.divide(FF.literal(3), FF.literal(2)), expression);                
    }
    
    @Test
    public void testFunction1() throws CQLException{
        final String cql = "max(\"att\",15)";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof Function);
        final Function expression = (Function) obj;
        assertEquals(FF.function("max",FF.property("att"), FF.literal(15)), expression);                
    }
    
    @Test
    public void testFunction2() throws CQLException{
        final String cql = "min(\"att\",cos(3.14))";
        final Object obj = CQL.read(cql);        
        assertTrue(obj instanceof Function);
        final Function expression = (Function) obj;
        assertEquals(FF.function("min",FF.property("att"), FF.function("cos",FF.literal(3.14d))), expression);                
    }
    
}
