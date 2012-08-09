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
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

/**
 * Test writing in CQL filters.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FilterWritingTest {
    
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
        final Filter filter = FF.equals(FF.property("att"), FF.literal(15));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("att = 15", cql);                
    }

    @Test
    public void testPropertyIsNotEqualTo() throws CQLException {
        final Filter filter = FF.notEqual(FF.property("att"), FF.literal(15));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("att <> 15", cql);         
    }

    @Test
    public void testPropertyIsGreaterThan() throws CQLException {
        final Filter filter = FF.greater(FF.property("att"), FF.literal(15));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("att > 15", cql);       
    }

    @Test
    public void testPropertyIsGreaterThanOrEqualTo() throws CQLException {
        final Filter filter = FF.greaterOrEqual(FF.property("att"), FF.literal(15));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("att >= 15", cql);     
    }

    @Test
    public void testPropertyIsLessThan() throws CQLException {
        final Filter filter = FF.less(FF.property("att"), FF.literal(15));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("att < 15", cql);     
    }

    @Test
    public void testPropertyIsLessThanOrEqualTo() throws CQLException {
        final Filter filter = FF.lessOrEqual(FF.property("att"), FF.literal(15));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("att <= 15", cql);     
    }

    @Test
    public void testPropertyIsLike() throws CQLException {        
        final Filter filter = FF.like(FF.property("att"),"%hello");
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("upper(att) LIKE '%hello'", cql);
    }

    @Test
    public void testPropertyIsNull() throws CQLException {
        final Filter filter = FF.isNull(FF.property("att"));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("att IS NULL", cql);
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
