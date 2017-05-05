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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.junit.Test;

import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.Not;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.Identifier;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FilterTest extends org.geotoolkit.test.TestBase {


    public FilterTest() {
    }

    @Test
    public void testId() {
        Set<Identifier> ids = new HashSet<>();
        ids.add(FF.featureId("dummyid"));
        ids.add(FF.featureId("dummyid2"));
        ids.add(FF.featureId("dummyid45"));

        Id id = FF.id(ids);

        //test serialize
        assertSerializedEquals(id);

        assertFalse(id.evaluate(CANDIDATE_1));

        ids.add(FF.featureId("testFeatureType.1"));
        id = FF.id(ids);

        assertTrue(id.evaluate(CANDIDATE_1));
    }

    /**
     * Test identifier match for not string types.
     */
    @Test
    public void testId2() {
        Set<Identifier> ids = new HashSet<>();
        ids.add(FF.featureId("13"));
        ids.add(FF.featureId("42"));

        Id id = FF.id(ids);

        //test against string identifier
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("StringFT");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        final Feature feat1 = ftb.build().newInstance();
        feat1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "13");
        assertTrue(id.evaluate(feat1));

        //test against long identifier
        ftb = new FeatureTypeBuilder();
        ftb.setName("LongFT");
        ftb.addAttribute(Long.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        final Feature feat2 = ftb.build().newInstance();
        feat2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), 42l);
        assertTrue(id.evaluate(feat2));
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
        Filter filter = FF.equals(FF.property("testString"), FF.literal("test string data"));
        assertTrue(filter.evaluate(CANDIDATE_1));

        //test serialize
        assertSerializedEquals(filter);

        Not not = FF.not(filter);
        assertFalse(not.evaluate(CANDIDATE_1));

        //test serialize
        assertSerializedEquals(not);
    }

    @Test
    public void testIsBetween(){

        PropertyName property = FF.property("testInteger");

        int ibefore = 12;
        int iafter = 250;
        PropertyIsBetween between = FF.between(property, FF.literal(ibefore), FF.literal(iafter));
        assertTrue(between.evaluate(CANDIDATE_1));
        assertSerializedEquals(between); //test serialize

        ibefore = 112;
        iafter = 360;
        between = FF.between(property, FF.literal(ibefore), FF.literal(iafter));
        assertFalse(between.evaluate(CANDIDATE_1));

        property = FF.property("date");

        Date dbefore = new Date(DATE.getTime()-360000);
        Date dafter = new Date(DATE.getTime()+360000);

        between = FF.between(property, FF.literal(dbefore), FF.literal(dafter));
        assertTrue(between.evaluate(CANDIDATE_1));

        dbefore = new Date(DATE.getTime()+10000);
        dafter = new Date(DATE.getTime()+360000);

        between = FF.between(property, FF.literal(dbefore), FF.literal(dafter));
        assertFalse(between.evaluate(CANDIDATE_1));
        assertSerializedEquals(between); //test serialize

        //test against strings
        between = FF.between(property, FF.literal("1850-09-01Z"), FF.literal("2210-11-01Z"));
        assertTrue(between.evaluate(CANDIDATE_1));

        between = FF.between(property, FF.literal("2150-09-01Z"), FF.literal("2210-11-01Z"));
        assertFalse(between.evaluate(CANDIDATE_1));

        //test against timestamp
        dbefore = new Timestamp(DATE.getTime()+10000);
        dafter = new Timestamp(DATE.getTime()+360000);

        between = FF.between(property, FF.literal(dbefore), FF.literal(dafter));
        assertFalse(between.evaluate(CANDIDATE_1));

        dbefore = new Timestamp(DATE.getTime()-360000);
        dafter = new Timestamp(DATE.getTime()+360000);

        between = FF.between(property, FF.literal(dbefore), FF.literal(dafter));
        assertTrue(between.evaluate(CANDIDATE_1));
        assertSerializedEquals(between); //test serialize

        //test timestamp against string
        property = FF.property("datetime2");
        between = FF.between(property, FF.literal("1850-09-01Z"), FF.literal("2210-11-01Z"));
        assertTrue(between.evaluate(CANDIDATE_1));
        assertSerializedEquals(between); //test serialize

        between = FF.between(property, FF.literal("2150-09-01Z"), FF.literal("2210-11-01Z"));
        assertFalse(between.evaluate(CANDIDATE_1));

    }

    @Test
    public void testIsLike(){

        PropertyName testAttribute = FF.property("testString");


        PropertyIsLike filter = FF.like(testAttribute, "test*", "*", ".", "!");
        assertTrue(filter.evaluate(CANDIDATE_1));
        assertSerializedEquals(filter); //test serialize

        // Test for false positive.
        filter = FF.like(testAttribute, "cows*", "*", ".", "!");
        assertFalse(filter.evaluate(CANDIDATE_1));

        // Test we don't match if single character is missing
        filter = FF.like(testAttribute, "test*a.", "*", ".", "!");
        assertFalse(filter.evaluate(CANDIDATE_1));

        // Test we do match if the single char is there
        filter = FF.like(testAttribute, "test*dat.", "*", ".", "!");
        assertTrue(filter.evaluate(CANDIDATE_1));


    }

    @Test
    public void testIsNull(){
        Filter filter = FF.isNull(FF.property("testNull"));
        assertTrue(filter.evaluate(CANDIDATE_1));

        filter = FF.isNull(FF.property("testString"));
        assertFalse(filter.evaluate(CANDIDATE_1));
        assertSerializedEquals(filter); //test serialize
    }

    @Test
    public void testPropertyName(){
        Expression exp = FF.property("testString");
        assertEquals(exp.evaluate(CANDIDATE_1), "test string data");
        assertSerializedEquals(exp); //test serialize
    }

}
