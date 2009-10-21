/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.filter.text.commons.CompilerUtil;
import org.geotoolkit.filter.text.commons.Language;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.spatial.BinarySpatialOperator;


/**
 * Literal parser test
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
public class CQLLiteralTest {
    protected Language language;

    public CQLLiteralTest(){
        language = Language.CQL;
    }

    /**
     * Tests Geometry Literals
     * <p>
     *
     * <pre>
     *  &lt;geometry literal &gt; :=
     *          &lt;Point Tagged Text &gt;
     *      |   &lt;LineString Tagged Text &gt;
     *      |   &lt;Polygon Tagged Text &gt;
     *      |   &lt;MultiPoint Tagged Text &gt;
     *      |   &lt;MultiLineString Tagged Text &gt;
     *      |   &lt;MultiPolygon Tagged Text &gt;
     *      |   &lt;GeometryCollection Tagged Text &gt;
     *      |   &lt;Envelope Tagged Text &gt;
     * </pre>
     *
     * </p>
     *
     */
    @Test
    public void geometryLiterals() throws Exception {
        BinarySpatialOperator result;
        Literal geom;

        // Point
        result = (BinarySpatialOperator) CompilerUtil.parseFilter(this.language, "WITHIN(ATTR1, POINT(1 2))");

        geom = (Literal) ((language.equals(Language.ECQL)) ? result.getExpression1() : result.getExpression2());

        Assert.assertNotNull(geom.getValue());
        Assert.assertTrue(geom.getValue() instanceof com.vividsolutions.jts.geom.Point);

        // LineString
        result = (BinarySpatialOperator) CompilerUtil.parseFilter(this.language,"WITHIN(ATTR1, LINESTRING(1 2, 10 15))");

        geom = (Literal) ((language.equals(Language.ECQL)) ? result.getExpression1() : result.getExpression2());

        Assert.assertNotNull(geom.getValue());
        Assert.assertTrue(geom.getValue() instanceof com.vividsolutions.jts.geom.LineString);

        // Polygon
        result = (BinarySpatialOperator) CompilerUtil.parseFilter(this.language,
                "WITHIN(ATTR1, POLYGON((1 2, 15 2, 15 20, 15 21, 1 2)))");

        geom = (Literal) ((language.equals(Language.ECQL)) ? result.getExpression1() : result.getExpression2());

        Assert.assertNotNull(geom.getValue());
        Assert.assertTrue(geom.getValue() instanceof com.vividsolutions.jts.geom.Polygon);

        // MultiPoint
        result = (BinarySpatialOperator) CompilerUtil.parseFilter(this.language,
                "WITHIN(ATTR1, MULTIPOINT( (1 2), (15 2), (15 20), (15 21), (1 2) ))");

        geom = (Literal) ((language.equals(Language.ECQL)) ? result.getExpression1() : result.getExpression2());

        Assert.assertNotNull(geom.getValue());
        Assert.assertTrue(geom.getValue() instanceof com.vividsolutions.jts.geom.MultiPoint);

        // MultiLineString
        result = (BinarySpatialOperator) CompilerUtil.parseFilter(this.language,
                "WITHIN(ATTR1, MULTILINESTRING((10 10, 20 20),(15 15,30 15)) )");

        geom = (Literal) ((language.equals(Language.ECQL)) ? result.getExpression1() : result.getExpression2());

        Assert.assertNotNull(geom.getValue());
        Assert.assertTrue(geom.getValue() instanceof com.vividsolutions.jts.geom.MultiLineString);

        // MultiPolygon
        result = (BinarySpatialOperator) CompilerUtil.parseFilter(this.language,
                "WITHIN(ATTR1, MULTIPOLYGON( ((10 10, 10 20, 20 20, 20 15, 10 10)),((60 60, 70 70, 80 60, 60 60 )) ) )");

        geom = (Literal) ((language.equals(Language.ECQL)) ? result.getExpression1() : result.getExpression2());

        Assert.assertNotNull(geom.getValue());
        Assert.assertTrue(geom.getValue() instanceof com.vividsolutions.jts.geom.MultiPolygon);

        // ENVELOPE
        result = (BinarySpatialOperator) CompilerUtil.parseFilter(this.language,
                "WITHIN(ATTR1, ENVELOPE( 10, 20, 30, 40) )");

        geom = (Literal) ((language.equals(Language.ECQL)) ? result.getExpression1() : result.getExpression2());

        Assert.assertNotNull(geom.getValue());
        Assert.assertTrue(geom.getValue() instanceof com.vividsolutions.jts.geom.Polygon);
    }

    /**
     * Test error at geometry literal
     * @throws CQLException
     */
    @Test(expected=CQLException.class)
    public void geometryLiteralsError() throws CQLException {

        final String filterError = "WITHIN(ATTR1, POLYGON(1 2, 10 15), (10 15, 1 2)))";
        CompilerUtil.parseFilter(this.language,filterError);
    }


    /**
     * <pre>
     * &lt;character string literal&gt; ::= &lt;quote&gt; [ {&lt;character representation&lt;} ]  &lt;quote&gt;
     * &lt;character representation&gt; ::=
     *                  &lt;nonquote character&gt;
     *          |       &lt;quote symbol&gt;
     * &lt;quote symbol&gt; ::=  &lt;quote&gt; &lt;quote&gt;
     * </pre>
     */
    @Test
    public void characterStringLiteral() throws Exception {

        PropertyIsEqualTo eqFilter;

        // space check
        final String strWithSpace = "ALL PRACTICES";
        Filter filterWithSpace = CompilerUtil.parseFilter(language, "practice='" + strWithSpace + "'");
        Assert.assertNotNull(filterWithSpace);
        Assert.assertTrue(filterWithSpace instanceof PropertyIsEqualTo);

        eqFilter = (PropertyIsEqualTo) filterWithSpace;
        Expression spacesLiteral = eqFilter.getExpression2();
        Assert.assertEquals(strWithSpace, spacesLiteral.toString());

        // empty string ''
        Filter emptyFilter = CompilerUtil.parseFilter(language, "MAJOR_WATERSHED_SYSTEM = ''");

        Assert.assertNotNull(emptyFilter);
        Assert.assertTrue(emptyFilter instanceof PropertyIsEqualTo);

        eqFilter = (PropertyIsEqualTo) emptyFilter;
        Expression emptyLiteral = eqFilter.getExpression2();
        Assert.assertEquals("", emptyLiteral.toString());

        // character string without quote
        final String expectedWithout = "ab";

        Filter filterWithoutQuote = CompilerUtil.parseFilter(language, "MAJOR_WATERSHED_SYSTEM = '"
                + expectedWithout + "'");

        Assert.assertNotNull(filterWithoutQuote);
        Assert.assertTrue(filterWithoutQuote instanceof PropertyIsEqualTo);

        eqFilter = (PropertyIsEqualTo) filterWithoutQuote;
        Expression actualWhithoutQuote = eqFilter.getExpression2();
        Assert.assertEquals(expectedWithout, actualWhithoutQuote.toString());

        // <quote symbol>
        final String expected = "cde'' fg";

        Filter filter = CQL.toFilter("MAJOR_WATERSHED_SYSTEM = '" + expected
                + "'");

        Assert.assertNotNull(filter);
        Assert.assertTrue(filter instanceof PropertyIsEqualTo);

        eqFilter = (PropertyIsEqualTo) filter;
        Expression actual = eqFilter.getExpression2();
        Assert.assertEquals(expected.replaceAll("''", "'"), actual.toString());

    }

    @Test(expected = CQLException.class)
    public void badCharacterString() throws CQLException {

        String cqlExpression = "A=\"2\""; //should be simple quote
        CQL.toFilter(cqlExpression);
    }

    @Test
    public void doubleLiteral() throws Exception{

        final String expected = "4.20082008E4";

        Expression expr = CompilerUtil.parseExpression(language, expected);

        Literal doubleLiteral = (Literal) expr;
        Double actual = (Double) doubleLiteral.getValue();

        Assert.assertEquals(Double.parseDouble(expected), actual.doubleValue(), 8);
    }
}
