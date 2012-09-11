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
import java.util.Collections;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Not;

/**
 * Test writing in CQL filters.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FilterWritingTest {
    
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
    public void testExcludeFilter() throws CQLException {
        final Filter filter = Filter.EXCLUDE;
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("1=0", cql);    
    }

    @Test
    public void testIncludeFilter() throws CQLException {
        final Filter filter = Filter.INCLUDE;
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("1=1", cql);    
    }

    @Test
    public void testAnd() throws CQLException {
        final Filter filter = FF.and(
                UnmodifiableArrayList.wrap((Filter)
                    FF.equals(FF.property("att1"), FF.literal(15)),
                    FF.equals(FF.property("att2"), FF.literal(30)),
                    FF.equals(FF.property("att3"), FF.literal(50))
                ));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("(att1 = 15 AND att2 = 30 AND att3 = 50)", cql);
    }

    @Test
    public void testOr() throws CQLException {
        final Filter filter = FF.or(
                UnmodifiableArrayList.wrap((Filter)
                    FF.equals(FF.property("att1"), FF.literal(15)),
                    FF.equals(FF.property("att2"), FF.literal(30)),
                    FF.equals(FF.property("att3"), FF.literal(50))
                ));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("(att1 = 15 OR att2 = 30 OR att3 = 50)", cql);
    }
    
    @Test
    public void testId() throws CQLException {
        final Filter filter = FF.id(Collections.singleton(new DefaultFeatureId("test-1")));
        try{
            final String cql = CQL.write(filter);
            fail("ID filter does not exist in CQL");
        }catch(UnsupportedOperationException ex){
            //ok
        }
    }

    @Test
    public void testNot() throws CQLException {
        final Filter filter = FF.not(FF.equals(FF.property("att"), FF.literal(15)));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("NOT att = 15", cql);
    }

    @Test
    public void testPropertyIsBetween() throws CQLException {
        final Filter filter = FF.between(FF.property("att"), FF.literal(15), FF.literal(30));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("att BETWEEN 15 AND 30", cql);
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

    @Test
    public void testBBOX() throws CQLException {
        final Filter filter = FF.bbox(FF.property("att"), 10,20,30,40, null);
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("BBOX(att,10.0,30.0,20.0,40.0)", cql);
    }

    @Test
    public void testBeyond() throws CQLException {
        final Filter filter = FF.beyond(FF.property("att"), FF.literal(baseGeometry), 0, "");
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("BEYOND(att,POLYGON ((10 20, 30 40, 50 60, 10 20)))", cql);
    }

    @Test
    public void testContains() throws CQLException {
        final Filter filter = FF.contains(FF.property("att"), FF.literal(baseGeometry));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("CONTAINS(att,POLYGON ((10 20, 30 40, 50 60, 10 20)))", cql);
    }

    @Test
    public void testCrosses() throws CQLException {
        final Filter filter = FF.crosses(FF.property("att"), FF.literal(baseGeometry));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("CROSS(att,POLYGON ((10 20, 30 40, 50 60, 10 20)))", cql);
    }

    @Test
    public void testDisjoint() throws CQLException {
        final Filter filter = FF.disjoint(FF.property("att"), FF.literal(baseGeometry));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("DISJOINT(att,POLYGON ((10 20, 30 40, 50 60, 10 20)))", cql);
    }

    @Test
    public void testDWithin() throws CQLException {
        final Filter filter = FF.dwithin(FF.property("att"), FF.literal(baseGeometry), 0, "");
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("DWITHIN(att,POLYGON ((10 20, 30 40, 50 60, 10 20)))", cql);
    }

    @Test
    public void testEquals() throws CQLException {
        final Filter filter = FF.equal(FF.property("att"), FF.literal(baseGeometry));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("EQUALS(att,POLYGON ((10 20, 30 40, 50 60, 10 20)))", cql);
    }

    @Test
    public void testIntersects() throws CQLException {
        final Filter filter = FF.intersects(FF.property("att"), FF.literal(baseGeometry));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("INTERSECT(att,POLYGON ((10 20, 30 40, 50 60, 10 20)))", cql);
    }

    @Test
    public void testOverlaps() throws CQLException {
        final Filter filter = FF.overlaps(FF.property("att"), FF.literal(baseGeometry));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("OVERLAP(att,POLYGON ((10 20, 30 40, 50 60, 10 20)))", cql);
    }

    @Test
    public void testTouches() throws CQLException {
        final Filter filter = FF.touches(FF.property("att"), FF.literal(baseGeometry));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("TOUCH(att,POLYGON ((10 20, 30 40, 50 60, 10 20)))", cql);
    }

    @Test
    public void testWithin() throws CQLException {
        final Filter filter = FF.within(FF.property("att"), FF.literal(baseGeometry));
        final String cql = CQL.write(filter);
        assertNotNull(cql);
        assertEquals("WITHIN(att,POLYGON ((10 20, 30 40, 50 60, 10 20)))", cql);
    }
    
}
