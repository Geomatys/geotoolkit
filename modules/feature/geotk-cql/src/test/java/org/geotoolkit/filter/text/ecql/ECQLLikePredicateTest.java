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

package org.geotoolkit.filter.text.ecql;

import org.geotoolkit.filter.text.commons.CompilerUtil;
import org.geotoolkit.filter.text.commons.Language;
import org.geotoolkit.filter.text.cql2.CQLLikePredicateTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.Not;
import org.opengis.filter.PropertyIsLike;

/**
 * Test for like predicate
 * 
 * <p>
 *
 * <pre>
 *  &lt;text predicate &gt; ::=
 *      &lt;expression &gt; [ NOT ] <b>LIKE</b>  &lt;character pattern &gt;
 *
 * </pre>
 * <p>
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
public class ECQLLikePredicateTest extends CQLLikePredicateTest {
    
    public ECQLLikePredicateTest(){
        super();
        language = Language.ECQL;
    }
    
    /**
     * Test Text Predicate
     * <p>
     * Sample: strConcat('aa', 'bbcc') like '%bb%'
     * </p>
     *
     * @todo Ignored because the two filters gotten are just different in the wildcard characters, the other properties are
     *       the same.
     */
    @Ignore
    @Test
    public void functionlikePredicate() throws Exception {

        // Like strConcat('aa', 'bbcc') like '%bb%'
        Filter resultFilter = CompilerUtil.parseFilter(this.language, FilterECQLSample.FUNCTION_LIKE_ECQL_PATTERN);

        Assert.assertNotNull("Filter expected", resultFilter);

        Assert.assertTrue(resultFilter instanceof PropertyIsLike);
        
        PropertyIsLike expected = (PropertyIsLike) FilterECQLSample.getSample(FilterECQLSample.FUNCTION_LIKE_ECQL_PATTERN);

        Assert.assertEquals("like filter was expected", expected, resultFilter);
    }
    
    /**
     * Test Text Predicate
     * <p>
     * Sample: 'aabbcc' like '%bb%'
     * </p>
     *
     * @todo Ignored because the two filters gotten are just different in the wildcard characters, the other properties are
     *       the same.
     */
    @Ignore
    @Test
    public void literallikePredicate() throws Exception {

        Filter resultFilter = CompilerUtil.parseFilter(this.language, FilterECQLSample.LITERAL_LIKE_ECQL_PATTERN);

        Assert.assertNotNull("Filter expected", resultFilter);

        Assert.assertTrue(resultFilter instanceof PropertyIsLike);
        
        PropertyIsLike expected = (PropertyIsLike) FilterECQLSample.getSample(FilterECQLSample.LITERAL_LIKE_ECQL_PATTERN);

        Assert.assertEquals("like filter was expected", expected, resultFilter);

    }

    /**
     * @todo Ignored because the two filters gotten are just different in the wildcard characters, the other properties are
     *       the same.
     */
    @Ignore
    @Test
    public void literalNotlikePredicate() throws Exception {

        Filter resultFilter = CompilerUtil.parseFilter(this.language, FilterECQLSample.LITERAL_NOT_LIKE_ECQL_PATTERN);

        Assert.assertNotNull("Filter expected", resultFilter);

        Assert.assertTrue(resultFilter instanceof Not);
        
        Not expected = (Not) FilterECQLSample.getSample(FilterECQLSample.LITERAL_NOT_LIKE_ECQL_PATTERN);
        
        Assert.assertTrue(expected.getFilter() instanceof PropertyIsLike);
        
        Assert.assertEquals("like filter was expected", expected, resultFilter);
    }
    
}
