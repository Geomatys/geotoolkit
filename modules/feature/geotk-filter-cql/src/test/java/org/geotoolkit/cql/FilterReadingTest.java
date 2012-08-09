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
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;

/**
 * Test reading CQL filters.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FilterReadingTest {
    
    private final FilterFactory2 FF = new DefaultFilterFactory2();
    
    @Ignore
    @Test
    public void testNullFilter() throws CQLException {
        throw new UnsupportedOperationException("Null filter not supported in CQL.");
    }

    @Ignore
    @Test
    public void testExcludeFilter() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testIncludeFilter() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testAnd() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testId() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testNot() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testOr() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testPropertyIsBetween() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public void testPropertyIsNull() throws CQLException {
        final String cql = "att IS NULL";
        final Object obj = CQL.parseFilter(cql);        
        assertTrue(obj instanceof PropertyIsNull);
        final PropertyIsNull filter = (PropertyIsNull) obj;
        assertEquals(FF.isNull(FF.property("att")), filter);   
    }

    @Ignore
    @Test
    public void testBBOX() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testBeyond() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testContains() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testCrosses() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testDisjoint() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testDWithin() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testEquals() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testIntersects() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testOverlaps() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testTouches() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Ignore
    @Test
    public void testWithin() throws CQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
        
}
