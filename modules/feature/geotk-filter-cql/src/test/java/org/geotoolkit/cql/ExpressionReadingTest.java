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
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

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
    
}
