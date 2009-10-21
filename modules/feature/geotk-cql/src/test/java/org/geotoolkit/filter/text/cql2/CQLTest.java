/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.text.cql2;

import java.util.List;

import org.geotoolkit.filter.DefaultPropertyIsNull;
import org.geotoolkit.filter.function.other.PropertyExistsFunction;
import org.geotoolkit.filter.text.ecql.ECQLBetweenPredicateTest;
import org.geotoolkit.filter.text.ecql.ECQLBooleanValueExpressionTest;
import org.geotoolkit.filter.text.ecql.ECQLComparisonPredicateTest;
import org.geotoolkit.filter.text.ecql.ECQLExistenceTest;
import org.geotoolkit.filter.text.ecql.ECQLGeoOperationTest;
import org.geotoolkit.filter.text.ecql.ECQLLikePredicateTest;
import org.geotoolkit.filter.text.ecql.ECQLNullPredicateTest;
import org.geotoolkit.filter.text.ecql.ECQLTemporalPredicateTest;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.And;
import org.opengis.filter.Filter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.DistanceBufferOperator;


/**
 * CQL Test
 *
 * <p>
 * Test Common CQL language
 * </p>
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.5
 */
public class CQLTest {

    /**
     * Between predicate sample
     *
     * @see ECQLBetweenPredicateTest
     *
     * @throws CQLException
     */
    @Test
    public void betweenPredicate() throws CQLException{

        Filter filter = CQL.toFilter("QUANTITY BETWEEN 10 AND 20");

        Assert.assertTrue(filter instanceof PropertyIsBetween);
    }

    /**
     * Equals predicate sample
     *
     * @see ECQLComparisonPredicateTest
     *
     * @throws Exception
     */
    @Test
    public void comparisonPredicate() throws Exception{

        Filter filter;

        filter = CQL.toFilter("POP_RANK > 6");

        Assert.assertTrue(filter instanceof PropertyIsGreaterThan);
    }

    /**
     * GeoOperation predicate sample
     *
     * @see ECQLGeoOperationTest
     *
     * @throws CQLException
     */
    @Test
    public void geoOperationPredicate() throws CQLException{

        Filter filter;

        filter = CQL.toFilter("DISJOINT(the_geom, POINT(1 2))");

        Assert.assertTrue("Disjoint was expected", filter instanceof Disjoint);
    }

    @Test
    public void functionDwithinGeometry() throws Exception{
        Filter resultFilter;

        // DWITHIN
        resultFilter = CQL.toFilter(
                "DWITHIN(the_geom, POINT(1 2), 10, kilometers)");

        Assert.assertTrue(resultFilter instanceof DistanceBufferOperator);
    }

    /**
     * Temporal predicate sample
     *
     * @see ECQLTemporalPredicateTest
     *
     * @throws Exception
     */
    @Test
    public void temporalPredicate() throws Exception{

        Filter filter = CQL.toFilter("DATE BEFORE 2006-12-31T01:30:00Z");

        Assert.assertTrue( filter instanceof PropertyIsLessThan);
    }

    /**
     * And / Or / Not predicate
     * @throws Exception
     *
     * @see ECQLBooleanValueExpressionTest
     */
    @Test
    public void booleanPredicate() throws Exception{

        Filter  filter;

        // and sample
        filter = CQL.toFilter("QUANTITY < 10 AND QUANTITY < 2 ");

        Assert.assertTrue(filter instanceof And);

       // or sample
        filter = CQL.toFilter("QUANTITY < 10 OR QUANTITY < 2 ");

        Assert.assertTrue(filter instanceof Or);

        // not sample
        filter = CQL.toFilter("NOT QUANTITY < 10");

        Assert.assertTrue(filter instanceof Not);
    }

    /**
     * Like predicate sample
     *
     * @see ECQLLikePredicateTest
     *
     * @throws Exception
     */
    @Test
    public void likePredicate() throws Exception{

        Filter filter = CQL.toFilter("NAME like '%new%'");

        Assert.assertTrue(filter instanceof PropertyIsLike);
    }

    /**
     * Null predicate sample
     *
     * @see ECQLNullPredicateTest
     *
     * @throws Exception
     */
    @Test
    public void isNullPredicate() throws Exception {

        Filter filter = CQL.toFilter("SHAPE IS NULL");

        Assert.assertTrue(filter instanceof DefaultPropertyIsNull);


    }

    /**
     * Exist property predicate sample
     *
     * @see ECQLExistenceTest
     * @throws Exception
     */
    @Test
    public void existProperty() throws Exception{

        Filter resultFilter = CQL.toFilter("NAME EXISTS");

        Assert.assertTrue(resultFilter instanceof PropertyIsEqualTo);

        PropertyIsEqualTo eq = (PropertyIsEqualTo) resultFilter;

        Expression expr = eq.getExpression1() ;

        Assert.assertTrue(expr instanceof PropertyExistsFunction);

    }

    @Test
    public void expression() throws Exception{

        Expression expr = CQL.toExpression("QUANTITY + 1");

        Assert.assertTrue(expr instanceof Add);
    }

    @Test
    public void listOfPredicates() throws Exception{

        List<Filter> list = CQL.toFilterList("QUANTITY=1; YEAR<1963");

        Assert.assertTrue(list.size() == 2);

        Assert.assertTrue(list.get(0) instanceof PropertyIsEqualTo );

        Assert.assertTrue(list.get(1) instanceof PropertyIsLessThan );
    }
}
