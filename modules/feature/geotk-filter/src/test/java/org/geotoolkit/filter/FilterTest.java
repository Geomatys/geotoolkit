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
import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FilterTest {

    
    public FilterTest() {
    }

    @Test
    public void testId() {
        Set<Identifier> ids = new HashSet<Identifier>();
        ids.add(FF.featureId("dummyid"));
        ids.add(FF.featureId("dummyid2"));
        ids.add(FF.featureId("dummyid45"));

        Id id = FF.id(ids);

        assertFalse(id.evaluate(FEATURE_1));

        ids.add(FF.featureId("testFeatureType.1"));
        id = FF.id(ids);

        assertTrue(id.evaluate(FEATURE_1));
    }

    @Test
    public void testLiteral(){
        Literal literal;
        literal = FF.literal(RIGHT_GEOMETRY);
        assertEquals(RIGHT_GEOMETRY, literal.getValue());

        assertNotNull(FF.literal(true));
        assertNotNull(FF.literal("a text string"));
        assertNotNull(FF.literal('x'));
        assertNotNull(FF.literal(122));
        assertNotNull(FF.literal(45.56d));
    }

    @Test
    public void testNot(){
        Filter filter = FF.equals(FF.property("testString"), FF.literal("test string data"));
        assertTrue(filter.evaluate(FEATURE_1));

        Not not = FF.not(filter);
        assertFalse(not.evaluate(FEATURE_1));
    }

    @Test
    public void testIsBetween(){
        
        PropertyName property = FF.property("testInteger");

        int ibefore = 12;
        int iafter = 250;
        PropertyIsBetween between = FF.between(property, FF.literal(ibefore), FF.literal(iafter));
        assertTrue(between.evaluate(FEATURE_1));

        ibefore = 112;
        iafter = 360;
        between = FF.between(property, FF.literal(ibefore), FF.literal(iafter));
        assertFalse(between.evaluate(FEATURE_1));

        property = FF.property("date");

        Date dbefore = new Date(DATE.getTime()-360000);
        Date dafter = new Date(DATE.getTime()+360000);

        between = FF.between(property, FF.literal(dbefore), FF.literal(dafter));
        assertTrue(between.evaluate(FEATURE_1));

        dbefore = new Date(DATE.getTime()+10000);
        dafter = new Date(DATE.getTime()+360000);

        between = FF.between(property, FF.literal(dbefore), FF.literal(dafter));
        assertFalse(between.evaluate(FEATURE_1));

        //test against strings
        between = FF.between(property, FF.literal("1850-09-01Z"), FF.literal("2210-11-01Z"));
        assertTrue(between.evaluate(FEATURE_1));

        between = FF.between(property, FF.literal("2150-09-01Z"), FF.literal("2210-11-01Z"));
        assertFalse(between.evaluate(FEATURE_1));

        //test against timestamp
        dbefore = new Timestamp(DATE.getTime()+10000);
        dafter = new Timestamp(DATE.getTime()+360000);

        between = FF.between(property, FF.literal(dbefore), FF.literal(dafter));
        assertFalse(between.evaluate(FEATURE_1));

        dbefore = new Timestamp(DATE.getTime()-360000);
        dafter = new Timestamp(DATE.getTime()+360000);

        between = FF.between(property, FF.literal(dbefore), FF.literal(dafter));
        assertTrue(between.evaluate(FEATURE_1));

        //test timestamp against string
        property = FF.property("datetime2");
        between = FF.between(property, FF.literal("1850-09-01Z"), FF.literal("2210-11-01Z"));
        assertTrue(between.evaluate(FEATURE_1));

        between = FF.between(property, FF.literal("2150-09-01Z"), FF.literal("2210-11-01Z"));
        assertFalse(between.evaluate(FEATURE_1));
        
    }

    @Test
    public void testIsLike(){

        PropertyName testAttribute = FF.property("testString");


        PropertyIsLike filter = FF.like(testAttribute, "test*", "*", ".", "!");
        assertTrue(filter.evaluate(FEATURE_1));

        // Test for false positive.
        filter = FF.like(testAttribute, "cows*", "*", ".", "!");
        assertFalse(filter.evaluate(FEATURE_1));

        // Test we don't match if single character is missing
        filter = FF.like(testAttribute, "test*a.", "*", ".", "!");
        assertFalse(filter.evaluate(FEATURE_1));

        // Test we do match if the single char is there
        filter = FF.like(testAttribute, "test*dat.", "*", ".", "!");
        assertTrue(filter.evaluate(FEATURE_1));


    }

    @Test
    public void testIsNull(){
        Filter filter = FF.isNull(FF.property("testNull"));
        assertTrue(filter.evaluate(FEATURE_1));

        filter = FF.isNull(FF.property("testString"));
        assertFalse(filter.evaluate(FEATURE_1));
    }

    @Test
    public void testPropertyName(){
        Expression exp = FF.property("testString");
        assertEquals(exp.evaluate(FEATURE_1), "test string data");
    }

}
