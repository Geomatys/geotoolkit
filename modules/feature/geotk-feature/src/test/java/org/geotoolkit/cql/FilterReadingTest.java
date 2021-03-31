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

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.measure.Quantity;
import org.apache.sis.cql.CQLException;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.measure.Units;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.temporal.object.TemporalUtilities;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.opengis.filter.DistanceOperator;
import org.opengis.filter.DistanceOperatorName;
import org.opengis.filter.Expression;
import org.opengis.filter.Filter;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.TemporalOperatorName;
import org.opengis.util.CodeList;

/**
 * Test reading CQL filters.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FilterReadingTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.00000001;
    private final FilterFactory2 FF = FilterUtilities.FF;
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
    private final Geometry baseGeometryPoint = GF.createPoint(
                new Coordinate(12.1, 28.9));


    @Test
    public void testNullFilter() throws CQLException {
        //this is not true cql but is since in commun use cases.
        String cql = "";
        Filter obj = CQL.parseFilter(cql);
        assertEquals(Filter.include(), obj);

        cql = "*";
        obj = CQL.parseFilter(cql);
        assertEquals(Filter.include(), obj);
    }

    @Test
    public void testAnd() throws CQLException {
        final String cql = "att1 = 15 AND att2 = 30 AND att3 = 50";
        final Filter obj = CQL.parseFilter(cql);
        assertTrue(obj instanceof Filter);
        final Filter filter = (Filter) obj;
        assertEquals(
                FF.and(
                UnmodifiableArrayList.<Filter<? super Object>>wrap(new Filter[] {(Filter)
                    FF.equal(FF.property("att1"), FF.literal(15)),
                    FF.equal(FF.property("att2"), FF.literal(30)),
                    FF.equal(FF.property("att3"), FF.literal(50))
                })),
                filter);
    }

    @Test
    public void testOr() throws CQLException {
        final String cql = "att1 = 15 OR att2 = 30 OR att3 = 50";
        final Filter obj = CQL.parseFilter(cql);
        assertTrue(obj instanceof Filter);
        final Filter filter = (Filter) obj;
        assertEquals(
                FF.or(
                UnmodifiableArrayList.<Filter<? super Object>>wrap(new Filter[] {(Filter)
                    FF.equal(FF.property("att1"), FF.literal(15)),
                    FF.equal(FF.property("att2"), FF.literal(30)),
                    FF.equal(FF.property("att3"), FF.literal(50))
                })),
                filter);
    }

    @Test
    public void testOrAnd1() throws CQLException {
        final String cql = "Title = 'VMAI' OR (Title ILIKE 'LO?Li' AND DWITHIN(BoundingBox, POINT(12.1 28.9), 10, meters))";
        final Filter obj = CQL.parseFilter(cql);
        assertTrue(obj instanceof Filter);
        final Filter filter = (Filter) obj;
        assertEquals(
                FF.or(
                    FF.equal(FF.property("Title"), FF.literal("VMAI")),
                    FF.and(
                        FF.like(FF.property("Title"), "LO?Li", '%', '_', '\\', false),
                        FF.dwithin(FF.property("BoundingBox"), FF.literal(baseGeometryPoint), 10, "meters")
                        )
                ),
                filter);
    }

    @Test
    public void testOrAnd2() throws CQLException {
        final Geometry geom =  GF.createPolygon(
                GF.createLinearRing(
                    new Coordinate[]{
                        new Coordinate(10, 40),
                        new Coordinate(20, 40),
                        new Coordinate(20, 30),
                        new Coordinate(10, 30),
                        new Coordinate(10, 40)
                    }),
                new LinearRing[0]
                );


        final String cql = "NOT (INTERSECTS(BoundingBox, ENVELOPE(10, 20, 40, 30)) OR CONTAINS(BoundingBox, POINT(12.1 28.9))) AND BBOX(BoundingBox, 10,20,30,40)";
        final Filter obj = CQL.parseFilter(cql);
        assertTrue(obj instanceof Filter);
        final Filter filter = (Filter) obj;
        assertEquals(
                FF.and(
                    FF.not(
                        FF.or(
                            FF.intersects(FF.property("BoundingBox"), FF.literal(geom)),
                            FF.contains(FF.property("BoundingBox"), FF.literal(baseGeometryPoint))
                            )
                    ),
                    FF.bbox("BoundingBox",10,20,30,40,"")
                ),
                filter);
    }

    @Test
    public void testNot() throws CQLException {
        final String cql = "NOT att = 15";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.not(FF.equal(FF.property("att"), FF.literal(15))), obj);
    }

    @Test
    public void testPropertyIsBetween() throws CQLException {
        final String cql = "att BETWEEN 15 AND 30";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.between(FF.property("att"), FF.literal(15), FF.literal(30)), obj);
    }

    @Test
    public void testIn() throws CQLException {
        final String cql = "att IN ( 15, 30, 'hello')";
        final Filter obj = CQL.parseFilter(cql);
        final LogicalOperator filter = (LogicalOperator) obj;
        assertEquals(FF.equal(FF.property("att"), FF.literal(15)), filter.getOperands().get(0));
        assertEquals(FF.equal(FF.property("att"), FF.literal(30)), filter.getOperands().get(1));
        assertEquals(FF.equal(FF.property("att"), FF.literal("hello")), filter.getOperands().get(2));
    }

    @Test
    public void testNotIn() throws CQLException {
        final String cql = "att NOT IN ( 15, 30, 'hello')";
        Filter obj = CQL.parseFilter(cql);
        obj = ((LogicalOperator<Object>) obj).getOperands().get(0);
        final LogicalOperator filter = (LogicalOperator) obj;
        assertEquals(FF.equal(FF.property("att"), FF.literal(15)), filter.getOperands().get(0));
        assertEquals(FF.equal(FF.property("att"), FF.literal(30)), filter.getOperands().get(1));
        assertEquals(FF.equal(FF.property("att"), FF.literal("hello")), filter.getOperands().get(2));
    }

    @Test
    public void testPropertyIsEqualTo1() throws CQLException {
        final String cql = "att=15";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.equal(FF.property("att"), FF.literal(15)), obj);
    }

    @Test
    public void testPropertyIsEqualTo2() throws CQLException {
        final String cql = "att = 15";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.equal(FF.property("att"), FF.literal(15)), obj);
    }

    @Test
    public void testPropertyIsNotEqualTo() throws CQLException {
        final String cql = "att <> 15";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.notEqual(FF.property("att"), FF.literal(15)), obj);
    }

    @Test
    public void testPropertyIsNotEqualTo2() throws CQLException {
        final String cql = "att <>'15'";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.notEqual(FF.property("att"), FF.literal("15")), obj);
    }

    @Test
    public void testPropertyIsGreaterThan() throws CQLException {
        final String cql = "att > 15";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.greater(FF.property("att"), FF.literal(15)), obj);
    }

    @Test
    public void testPropertyIsGreaterThanOrEqualTo() throws CQLException {
        final String cql = "att >= 15";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.greaterOrEqual(FF.property("att"), FF.literal(15)), obj);
    }

    @Test
    public void testPropertyIsLessThan() throws CQLException {
        final String cql = "att < 15";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.less(FF.property("att"), FF.literal(15)), obj);
    }

    @Test
    public void testPropertyIsLessThanOrEqualTo() throws CQLException {
        final String cql = "att <= 15";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.lessOrEqual(FF.property("att"), FF.literal(15)), obj);
    }

    @Test
    public void testPropertyIsLike() throws CQLException {
        final String cql = "att LIKE '%hello_'";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.like(FF.property("att"), "%hello_", '%', '_', '\\', true), obj);
    }

    @Test
    public void testPropertyIsNotLike() throws CQLException {
        final String cql = "att NOT LIKE '%hello_'";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.not(FF.like(FF.property("att"),"%hello_", '%', '_', '\\', true)), obj);
    }

    @Test
    public void testPropertyIsLikeInsensitive() throws CQLException {
        final String cql = "att ILIKE '%hello_'";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.like(FF.property("att"),"%hello_", '%', '_', '\\', false), obj);
    }

    @Test
    public void testPropertyIsNull() throws CQLException {
        final String cql = "att IS NULL";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.isNull(FF.property("att")), obj);
    }

    @Test
    public void testPropertyIsNotNull() throws CQLException {
        final String cql = "att IS NOT NULL";
        Filter obj = CQL.parseFilter(cql);
        obj = ((LogicalOperator<Object>) obj).getOperands().get(0);
        assertEquals(FF.isNull(FF.property("att")), obj);
    }

    @Test
    public void testBBOX1() throws CQLException {
        final String cql = "BBOX(\"att\" ,10, 20, 30, 40)";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.bbox(FF.property("att"), 10,20,30,40, null), obj);
    }

    @Test
    public void testBBOX2() throws CQLException {
        final String cql = "BBOX(\"att\" ,10, 20, 30, 40, 'CRS:84')";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.bbox(FF.property("att"), 10,20,30,40, "CRS:84"), obj);
    }

    @Test
    public void testBBOX3() throws CQLException {
        final String cql = "BBOX(att ,10, 20, 30, 40, 'CRS:84')";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.bbox(FF.property("att"), 10,20,30,40, "CRS:84"), obj);
    }

    @Test
    public void testBBOX4() throws CQLException {
        final String cql = "BBOX(geometry,-10,-20,10,20)";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(FF.bbox(FF.property("geometry"), -10,-20,10,20,null), obj);
    }

    private void verify(final CodeList<?> expected, final Filter<Object> filter) {
        assertEquals(expected, filter.getOperatorType());
        List<Expression<? super Object, ?>> expressions = filter.getExpressions();
        assertEquals(FF.property("att"), expressions.get(0));
        Literal<Object,?> literal = (Literal<Object,?>) expressions.get(1);
        Geometry value = (Geometry) literal.getValue();
        assertTrue(baseGeometry.equalsExact(value));
    }

    private void verify(final SpatialOperatorName expected, final String cql) throws CQLException {
        verify(expected, CQL.parseFilter(cql));
    }

    private void verify(final DistanceOperatorName expected, final String cql) throws CQLException {
        final Filter<Object> filter = CQL.parseFilter(cql);
        verify(expected, filter);
        Quantity distance = ((DistanceOperator) filter).getDistance();
        assertEquals(10, distance.getValue().doubleValue(), STRICT);
        assertEquals(Units.METRE, distance.getUnit());
    }

    @Test
    public void testBeyond() throws CQLException {
        verify(DistanceOperatorName.BEYOND,
                "BEYOND(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)), 10, meters)");
    }

    @Test
    public void testContains() throws CQLException {
        verify(SpatialOperatorName.CONTAINS,
                "CONTAINS(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))");
    }

    @Test
    public void testCrosses() throws CQLException {
        verify(SpatialOperatorName.CROSSES,
                "CROSSES(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))");
    }

    @Test
    public void testDisjoint() throws CQLException {
        verify(SpatialOperatorName.DISJOINT,
                "DISJOINT(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))");
    }

    @Test
    public void testDWithin() throws CQLException {
        verify(DistanceOperatorName.WITHIN,
                "DWITHIN(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)), 10, 'meters')");
    }

    @Test
    public void testDWithin2() throws CQLException {
        //there is an error in this syntax, meters is a literal so it should be writen 'meters"
        //but this writing is commun so we tolerate it
        final Filter<Object> filter = CQL.parseFilter("DWITHIN(BoundingBox, POINT(12.1 28.9), 10, meters)");
        Quantity distance = ((DistanceOperator) filter).getDistance();
        assertEquals(10, distance.getValue().doubleValue(), STRICT);
        assertEquals(Units.METRE, distance.getUnit());
    }

    @Test
    public void testEquals() throws CQLException {
        verify(SpatialOperatorName.EQUALS,
                "EQUALS(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))");
    }

    @Test
    public void testIntersects() throws CQLException {
        verify(SpatialOperatorName.INTERSECTS,
                "INTERSECTS(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))");
    }

    @Test
    public void testOverlaps() throws CQLException {
        verify(SpatialOperatorName.OVERLAPS,
                "OVERLAPS(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))");
    }

    @Test
    public void testTouches() throws CQLException {
        verify(SpatialOperatorName.TOUCHES,
                "TOUCHES(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))");
    }

    @Test
    public void testWithin() throws CQLException {
        verify(SpatialOperatorName.WITHIN,
                "WITHIN(\"att\" ,POLYGON((10 20, 30 40, 50 60, 10 20)))");
    }

    @Test
    public void testCombine1() throws CQLException {
        final String cql = "NOT att = 15 OR att BETWEEN 15 AND 30";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(
                FF.or(
                    FF.not(FF.equal(FF.property("att"), FF.literal(15))),
                    FF.between(FF.property("att"), FF.literal(15), FF.literal(30))
                ),
                obj);
    }

    @Test
    public void testCombine2() throws CQLException {
        final String cql = "(NOT att = 15) OR (att BETWEEN 15 AND 30)";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(
                FF.or(
                    FF.not(FF.equal(FF.property("att"), FF.literal(15))),
                    FF.between(FF.property("att"), FF.literal(15), FF.literal(30))
                ),
                obj);
    }

    @Test
    public void testCombine3() throws CQLException {
        final String cql = "(NOT att1 = 15) AND (att2 = 15 OR att3 BETWEEN 15 AND 30) AND (att4 BETWEEN 1 AND 2)";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(
                FF.and(
                    UnmodifiableArrayList.<Filter<? super Object>>wrap(new Filter[] {(Filter)
                        FF.not(FF.equal(FF.property("att1"), FF.literal(15))),
                        FF.or(
                            FF.equal(FF.property("att2"), FF.literal(15)),
                            FF.between(FF.property("att3"), FF.literal(15), FF.literal(30))
                        ),
                        FF.between(FF.property("att4"), FF.literal(1), FF.literal(2))
                    })
                ),
                obj);
    }

    @Test
    public void testCombine4() throws CQLException {
        final String cql = "(x+7) <= (y-9)";
        final Filter obj = CQL.parseFilter(cql);
        assertEquals(
                FF.lessOrEqual(
                    FF.add((Expression) FF.property("x"), FF.literal(7)),
                    FF.subtract((Expression) FF.property("y"), FF.literal(9))
                ),
                obj);
    }

    private void verify(final TemporalOperatorName expected, final String cql) throws CQLException, ParseException {
        final Filter<Object> filter = CQL.parseFilter(cql);
        assertEquals(expected, filter.getOperatorType());
        List<Expression<? super Object, ?>> expressions = filter.getExpressions();
        assertEquals(FF.property("att"), expressions.get(0));
        Literal<Object,?> literal = (Literal<Object,?>) expressions.get(1);
        Date value = (Date) literal.getValue();
        assertEquals(TemporalUtilities.parseDate("2012-03-21T05:42:36Z"), value);
    }

    @Test
    public void testAfter() throws CQLException, ParseException {
        verify(TemporalOperatorName.AFTER, "att AFTER 2012-03-21T05:42:36Z");
    }

    @Test
    public void testAnyInteracts() throws CQLException, ParseException {
        verify(TemporalOperatorName.ANY_INTERACTS, "att ANYINTERACTS 2012-03-21T05:42:36Z");
    }

    @Test
    public void testBefore() throws CQLException, ParseException {
        verify(TemporalOperatorName.BEFORE, "att BEFORE 2012-03-21T05:42:36Z");
    }

    @Test
    public void testBegins() throws CQLException, ParseException {
        verify(TemporalOperatorName.BEGINS, "att BEGINS 2012-03-21T05:42:36Z");
    }

    @Test
    public void testBegunBy() throws CQLException, ParseException {
        verify(TemporalOperatorName.BEGUN_BY, "att BEGUNBY 2012-03-21T05:42:36Z");
    }

    @Test
    public void testDuring() throws CQLException, ParseException {
        verify(TemporalOperatorName.DURING, "att DURING 2012-03-21T05:42:36Z");
    }

    @Test
    public void testEndedBy() throws CQLException, ParseException {
        verify(TemporalOperatorName.ENDED_BY, "att ENDEDBY 2012-03-21T05:42:36Z");
    }

    @Test
    public void testEnds() throws CQLException, ParseException {
        verify(TemporalOperatorName.ENDS, "att ENDS 2012-03-21T05:42:36Z");
    }

    @Test
    public void testMeets() throws CQLException, ParseException {
        verify(TemporalOperatorName.MEETS, "att MEETS 2012-03-21T05:42:36Z");
    }

    @Test
    public void testMetBy() throws CQLException, ParseException {
        verify(TemporalOperatorName.MET_BY, "att METBY 2012-03-21T05:42:36Z");
    }

    @Test
    public void testOverlappedBy() throws CQLException, ParseException {
        verify(TemporalOperatorName.OVERLAPPED_BY, "att OVERLAPPEDBY 2012-03-21T05:42:36Z");
    }

    @Test
    public void testTcontains() throws CQLException, ParseException {
        verify(TemporalOperatorName.CONTAINS, "att TCONTAINS 2012-03-21T05:42:36Z");
    }

    @Test
    public void testTequals() throws CQLException, ParseException {
        verify(TemporalOperatorName.EQUALS, "att TEQUALS 2012-03-21T05:42:36Z");
    }

    @Test
    public void testToverlaps() throws CQLException, ParseException {
        verify(TemporalOperatorName.OVERLAPS, "att TOVERLAPS 2012-03-21T05:42:36Z");
    }
}
