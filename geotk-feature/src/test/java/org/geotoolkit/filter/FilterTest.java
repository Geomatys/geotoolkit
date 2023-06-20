/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter;

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.junit.Test;

import org.opengis.filter.Filter;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.filter.ValueReference;
import static org.geotoolkit.filter.FilterTestConstants.*;
import org.opengis.feature.Feature;
import org.opengis.filter.LogicalOperator;
import static org.junit.Assert.*;
import static org.apache.sis.test.Assertions.assertSerializedEquals;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FilterTest extends org.geotoolkit.test.TestBase {
    @Test
    public void testId() {
        List<Filter<Object>> ids = new ArrayList<>();
        ids.add(FF.resourceId("dummyid"));
        ids.add(FF.resourceId("dummyid2"));
        ids.add(FF.resourceId("dummyid45"));

        LogicalOperator<Object> id = FF.or(ids);

        //test serialize
        assertSerializedEquals(id);

        assertFalse(id.test(CANDIDATE_1));

        ids.add(FF.resourceId("testFeatureType.1"));
        id = FF.or(ids);

        assertTrue(id.test(CANDIDATE_1));
    }

    /**
     * Test identifier match for not string types.
     */
    @Test
    public void testId2() {
        List<Filter<Object>> ids = new ArrayList<>();
        ids.add(FF.resourceId("13"));
        ids.add(FF.resourceId("42"));

        LogicalOperator<Object> id = FF.or(ids);

        //test against string identifier
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("StringFT");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        final Feature feat1 = ftb.build().newInstance();
        feat1.setPropertyValue(AttributeConvention.IDENTIFIER, "13");
        assertTrue(id.test(feat1));

        //test against long identifier
        ftb = new FeatureTypeBuilder();
        ftb.setName("LongFT");
        ftb.addAttribute(Long.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        final Feature feat2 = ftb.build().newInstance();
        feat2.setPropertyValue(AttributeConvention.IDENTIFIER, 42l);
        assertTrue(id.test(feat2));
    }

    @Test
    public void testLiteral(){
        Literal literal;
        literal = FF.literal(RIGHT_GEOMETRY);
        //we do not serialize on geometries, JTS does not provide an hashcode
        assertEquals(RIGHT_GEOMETRY, literal.getValue());

        assertNotNull(FF.literal(true));
        assertNotNull(FF.literal("a text string"));
        assertNotNull(FF.literal('x'));
        assertNotNull(FF.literal(122));
        assertNotNull(FF.literal(45.56d));

        assertSerializedEquals(FF.literal(true));
        assertSerializedEquals(FF.literal("a text string"));
        assertSerializedEquals(FF.literal('x'));
        assertSerializedEquals(FF.literal(122));
        assertSerializedEquals(FF.literal(45.56d));
    }

    @Test
    public void testNot(){
        Filter filter = FF.equal(FF.property("testString"), FF.literal("test string data"));
        assertTrue(filter.test(CANDIDATE_1));

        //test serialize
        assertSerializedEquals(filter);

        Filter not = FF.not(filter);
        assertFalse(not.test(CANDIDATE_1));

        //test serialize
        assertSerializedEquals(not);
    }

    @Test
    public void testIsLike(){
        ValueReference testAttribute = FF.property("testString");

        Filter filter = FF.like(testAttribute, "test*", '*', '.', '!', true);
        assertTrue(filter.test(CANDIDATE_1));
        assertSerializedEquals(filter); //test serialize

        // Test for false positive.
        filter = FF.like(testAttribute, "cows*", '*', '.', '!', true);
        assertFalse(filter.test(CANDIDATE_1));

        // Test we don't match if single character is missing
        filter = FF.like(testAttribute, "test*a.", '*', '.', '!', true);
        assertFalse(filter.test(CANDIDATE_1));

        // Test we do match if the single char is there
        filter = FF.like(testAttribute, "test*dat.", '*', '.', '!', true);
        assertTrue(filter.test(CANDIDATE_1));
    }

    @Test
    public void testIsNull(){
        Filter filter = FF.isNull(FF.property("testNull"));
        assertTrue(filter.test(CANDIDATE_1));

        filter = FF.isNull(FF.property("testString"));
        assertFalse(filter.test(CANDIDATE_1));
        assertSerializedEquals(filter); //test serialize
    }

    @Test
    public void testPropertyName(){
        Expression exp = FF.property("testString");
        assertEquals(exp.apply(CANDIDATE_1), "test string data");
        assertSerializedEquals(exp); //test serialize
    }
}
