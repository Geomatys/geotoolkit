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

import org.geotoolkit.filter.text.commons.Language;
import org.geotoolkit.filter.text.cql2.CQLNullPredicateTest;
import org.junit.Test;
import org.opengis.filter.Filter;


/**
 * Test ECQL Null Predicate:
 * <p>
 *
 * <pre>
 * &lt;null predicate &gt; ::=  &lt;expression &gt; IS [ NOT ] NULL
 * </pre>
 *
 * </p>
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
public class ECQLNullPredicateTest extends CQLNullPredicateTest {

    public ECQLNullPredicateTest(){
        super();
        language = Language.ECQL;
    }

    /**
     * Sample: centroid( the_geom ) IS NOT NULL
     */
    @Test
    public void functionIsNull() throws Exception {

        final String samplePredicate = FilterECQLSample.FUNCTION_IS_NULL;

        Filter expected = FilterECQLSample.getSample(samplePredicate);

        testNullPredicate(samplePredicate, expected);
    }

    /**
     * Sample: centroid( the_geom ) IS NOT NULL
     */
    @Test
    public void functionIsNotNull() throws Exception {

        final String samplePredicate = FilterECQLSample.FUNCTION_IS_NOT_NULL;

        Filter expected = FilterECQLSample.getSample(samplePredicate);

        testNullPredicate(samplePredicate, expected);
    }

}
