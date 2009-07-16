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
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.PropertyName;

/**
 * Test case for Like predicate
 * 
 * <p>
 * <pre>
 *  &lt;text predicate &gt; ::=
 *      &lt;attribute name &gt; [ NOT ] LIKE  &lt;character pattern &gt;
 *      For example:
 *      attribute like '%contains_this%'
 *      attribute like 'begins_with_this%'
 *      attribute like '%ends_with_this'
 *      attribute like 'd_ve' will match 'dave' or 'dove'
 *      attribute not like '%will_not_contain_this%'
 *      attribute not like 'will_not_begin_with_this%'
 *      attribute not like '%will_not_end_with_this'
 * </pre>
 *
 * </p>
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @since 2.5
 */
public class CQLLikePredicateTest {

    protected Language language;
    
    public CQLLikePredicateTest(){
        language = Language.CQL;
    }

    /**
     * Test Text Predicate
     * <p>
     *
     * <pre>
     *  &lt;text predicate &gt; ::=
     *      &lt;attribute name &gt; [ NOT ] <b>LIKE</b>  &lt;character pattern &gt;
     *      For example:
     *      attribute like '%contains_this%'
     *      attribute like 'begins_with_this%'
     *      attribute like '%ends_with_this'
     *      attribute like 'd_ve' will match 'dave' or 'dove'
     * </pre>
     *
     * </p>
     */
    @Test
    public void likePredicate() throws Exception {

        // Like
        Filter resultFilter = CompilerUtil.parseFilter(this.language, FilterCQLSample.LIKE_FILTER);

        Assert.assertNotNull("Filter expected", resultFilter);

        Filter expected = FilterCQLSample.getSample(FilterCQLSample.LIKE_FILTER);

        Assert.assertEquals("like filter was expected", expected, resultFilter);

    }
    
    /**
     * Test Text Predicate
     * <p>
     *
     * <pre>
     *  &lt;text predicate &gt; ::=
     *      &lt;attribute name &gt; <b>[ NOT ] LIKE</b>  &lt;character pattern &gt;
     *      For example:
     *      attribute not like '%will_not_contain_this%'
     *      attribute not like 'will_not_begin_with_this%'
     *      attribute not like '%will_not_end_with_this'
     * </pre>
     *
     * </p>
     */
    @Test
    public void notLikePredicate() throws Exception{
        // not Like
        Filter resultFilter = CompilerUtil.parseFilter(this.language,FilterCQLSample.NOT_LIKE_FILTER);

        Assert.assertNotNull("Filter expected", resultFilter);

        Filter expected = FilterCQLSample.getSample(FilterCQLSample.NOT_LIKE_FILTER);

        Assert.assertEquals("like filter was expected", expected, resultFilter);
        
    }
    
    /**
     * Test Attribute
     * <p>
     *
     * <pre>
     *  &lt;attribute name &gt; ::=
     *          &lt;simple attribute name &gt;
     *      |    &lt;compound attribute name &gt;
     *  &lt;simple attribute name &gt; ::=  &lt;identifier &gt;
     *  &lt;compound attribute name &gt; ::=  &lt;identifier &gt; &lt;period &gt; [{ &lt;identifier &gt; &lt;period &gt;}...] &lt;simple attribute name &gt;
     *  &lt;identifier &gt; ::=  &lt;identifier start [ {  &lt;colon &gt; |  &lt;identifier part &gt; }... ]
     *  &lt;identifier start &gt; ::=  &lt;simple Latin letter &gt;
     *  &lt;identifier part &gt; ::=  &lt;simple Latin letter &gt; |  &lt;digit &gt;
     * </pre>
     *
     * </p>
     */
    @Test
    public void compoundAttribute() throws CQLException {
        // Simple attribute name
        testAttribute("startPart");

        testAttribute("startpart:part1:part2");

        // Compound attribute name
        testAttribute("s11:p12:p13.s21:p22.s31:p32");

        testAttribute(
            "gmd:MD_Metadata.gmd:identificationInfo.gmd:MD_DataIdentification.gmd:abstract");
    }

    private void testAttribute(final String attSample) throws CQLException {
        PropertyIsLike result;
        PropertyName attResult = null;

        String expected = attSample.replace('.', '/');

        result = (PropertyIsLike) CompilerUtil.parseFilter(this.language, attSample + " LIKE 'abc%'");

        attResult = (PropertyName) result.getExpression();

        Assert.assertEquals(expected, attResult.getPropertyName());
    }    
    
}
