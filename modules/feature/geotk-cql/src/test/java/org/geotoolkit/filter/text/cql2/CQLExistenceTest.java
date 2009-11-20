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


/**
 * Test Existence Predicate.
 * <p>
 * EXIST: evaluates as true for all record instances where the attribute_name is
 * a member of the record schema. DOES-NOT-EXIST: opposite to EXISTS
 * </p>
 * <p>
 *
 * <pre>
 *  &lt;existence_predicate &gt; ::=
 *          &lt;attribute_name &gt; EXISTS
 *      |   &lt;attribute_name &gt; DOES-NOT-EXIST
 * </pre>
 *
 * </p>
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
public class CQLExistenceTest {

    protected Language language;

    public CQLExistenceTest(){
        language = Language.CQL;
    }

    /**
     * Sample: attribute_name DOES-NOT-EXIST
     *
     * @throws CQLException
     */
    @Test
    public void attributeDoesNotExist() throws CQLException{
        // -------------------------------------------------------------
        // <attribute_name> DOES-NOT-EXIST
        // -------------------------------------------------------------
        Filter resultFilter = CompilerUtil.parseFilter(this.language,FilterCQLSample.ATTRIBUTE_NAME_DOES_NOT_EXIST);

        Assert.assertNotNull("Filter expected", resultFilter);

        Filter expected = FilterCQLSample.getSample(FilterCQLSample.ATTRIBUTE_NAME_DOES_NOT_EXIST);

        Assert.assertEquals(expected, resultFilter);
    }

    /**
     * Sample: attribute_name EXISTS
     */
    @Test
    public void attributeExist() throws Exception {

        // -------------------------------------------------------------
        // <attribute_name> EXISTS
        // TODO Exist function must be implemented in GeotoolKit
        // -------------------------------------------------------------
        Filter resultFilter = CompilerUtil.parseFilter(this.language,FilterCQLSample.ATTRIBUTE_NAME_EXISTS);

        Assert.assertNotNull("Filter expected", resultFilter);

        Assert.assertTrue(resultFilter instanceof PropertyIsEqualTo);

        PropertyIsEqualTo eqToResultFilter = (PropertyIsEqualTo) resultFilter;

        Filter expected = FilterCQLSample.getSample(FilterCQLSample.ATTRIBUTE_NAME_EXISTS);

        Assert.assertEquals(expected, eqToResultFilter);

        Assert.assertNotNull("implementation of function was expected", eqToResultFilter.getExpression1());
    }


}
