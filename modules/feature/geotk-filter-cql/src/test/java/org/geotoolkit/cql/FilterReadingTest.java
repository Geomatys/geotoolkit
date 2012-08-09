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
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import static org.junit.Assert.*;
import org.junit.Ignore;
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

/**
 * Test reading CQL filters.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FilterReadingTest {
    
    private final FilterFactory2 FF = new DefaultFilterFactory2();
    
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
                    FF.equals(FF.property("att1"), FF.literal(15)),
                    FF.and(
                    UnmodifiableArrayList.wrap((Filter)
                        FF.equals(FF.property("att2"), FF.literal(30)),
                        FF.equals(FF.property("att3"), FF.literal(50))
                    ))
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
                    FF.equals(FF.property("att1"), FF.literal(15)),
                    FF.or(
                    UnmodifiableArrayList.wrap((Filter)
                        FF.equals(FF.property("att2"), FF.literal(30)),
                        FF.equals(FF.property("att3"), FF.literal(50))
                    ))
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
