/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.filter.binarycomparison;

import java.util.Date;
import org.junit.Test;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;
import org.opengis.filter.MatchAction;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class BinaryComparisonTest extends org.geotoolkit.test.TestBase {

    public BinaryComparisonTest() {
    }

    @Test
    public void testIsEqualTo() {

        PropertyIsEqualTo filter;
        PropertyName property;

        Literal rightliteral = FF.literal(RIGHT_GEOMETRY);
        Literal falseliteral = FF.literal(WRONG_GEOMETRY);

        property = FF.property("testGeometry");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        rightliteral = FF.literal(true);
        falseliteral = FF.literal(false);

        property = FF.property("testBoolean");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        rightliteral = FF.literal('t');
        falseliteral = FF.literal('z');

        property = FF.property("testCharacter");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        rightliteral = FF.literal(new Byte("101"));
        falseliteral = FF.literal(new Byte("011"));

        property = FF.property("testByte");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        rightliteral = FF.literal(101);
        falseliteral = FF.literal(103);

        property = FF.property("testShort");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testInteger");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testLong");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testFloat");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testDouble");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        rightliteral = FF.literal(DATE);
        falseliteral = FF.literal(new Date(DATE.getTime() + 360000)); // Same day
        Literal falseliteral2 = FF.literal(new Date(DATE.getTime() + 25*60*60*1000)); // Other day

        property = FF.property("date");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1)); // Would be false if we took hours in account, but the filter intentionally ignores hours if a date is java.sql.Date.

        filter = FF.equals(property, falseliteral2);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("time");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime1");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime2");
        filter = FF.equals(property, rightliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equals(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));

        assertSerializedEquals(filter); //test serialize

    }

    @Test
    public void testIsGreaterThan() {

        PropertyIsGreaterThan filter;
        PropertyName property;

        Literal aboveLiteral = FF.literal('z');
        Literal equalLiteral = FF.literal('t');
        Literal underLiteral = FF.literal('c');

        property = FF.property("testCharacter");
        filter = FF.greater(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(new Byte("111"));
        equalLiteral = FF.literal(new Byte("101"));
        underLiteral = FF.literal(new Byte("001"));

        property = FF.property("testByte");
        filter = FF.greater(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(103);
        equalLiteral = FF.literal(101);
        underLiteral = FF.literal(21);

        property = FF.property("testShort");
        filter = FF.greater(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testInteger");
        filter = FF.greater(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testLong");
        filter = FF.greater(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testFloat");
        filter = FF.greater(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testDouble");
        filter = FF.greater(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(new Date(DATE.getTime()+360000));
        equalLiteral = FF.literal(DATE);
        underLiteral = FF.literal(new Date(DATE.getTime()-360000));

        property = FF.property("date");
        filter = FF.greater(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        //test against string dates
        Literal aboveStrLiteral = FF.literal("3021-05-25");
        Literal underStrLiteral = FF.literal("1850-11-01");
        property = FF.property("date");
        filter = FF.greater(property,aboveStrLiteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(property,underStrLiteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        //ensure the test in mad in primary on dates over string
        property = FF.property("date");
        filter = FF.greater(aboveStrLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underStrLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));



        property = FF.property("time");
        filter = FF.greater(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime1");
        filter = FF.greater(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.greater(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

//        property = FF.property("datetime2");
//        filter = FF.greater(aboveLiteral,property);
//        assertTrue(filter.evaluate(FEATURE_1));
//        filter = FF.greater(equalLiteral,property);
//        assertFalse(filter.evaluate(FEATURE_1));
//        filter = FF.greater(underLiteral,property);
//        assertFalse(filter.evaluate(FEATURE_1));

        assertSerializedEquals(filter); //test serialize

    }

    @Test
    public void testIsGreaterThanOrEqualTo() {

        PropertyIsGreaterThanOrEqualTo filter;
        PropertyName property;

        Literal aboveLiteral = FF.literal('z');
        Literal equalLiteral = FF.literal('t');
        Literal underLiteral = FF.literal('c');

        property = FF.property("testCharacter");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(new Byte("111"));
        equalLiteral = FF.literal(new Byte("101"));
        underLiteral = FF.literal(new Byte("001"));

        property = FF.property("testByte");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(103);
        equalLiteral = FF.literal(101);
        underLiteral = FF.literal(21);

        property = FF.property("testShort");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testInteger");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testLong");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testFloat");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("testDouble");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(new Date(DATE.getTime()+360000));
        equalLiteral = FF.literal(DATE);
        underLiteral = FF.literal(new Date(DATE.getTime()-360000));

        property = FF.property("date");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("time");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime1");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime2");
        filter = FF.greaterOrEqual(aboveLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.greaterOrEqual(underLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));

        assertSerializedEquals(filter); //test serialize

    }

    @Test
    public void testIsLessThan() {

        PropertyIsLessThan filter;
        PropertyName property;

        Literal aboveLiteral = FF.literal('z');
        Literal equalLiteral = FF.literal('t');
        Literal underLiteral = FF.literal('c');

        property = FF.property("testCharacter");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(new Byte("111"));
        equalLiteral = FF.literal(new Byte("101"));
        underLiteral = FF.literal(new Byte("001"));

        property = FF.property("testByte");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(103);
        equalLiteral = FF.literal(101);
        underLiteral = FF.literal(21);

        property = FF.property("testShort");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testInteger");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testLong");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testFloat");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testDouble");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(new Date(DATE.getTime()+360000));
        equalLiteral = FF.literal(DATE);
        underLiteral = FF.literal(new Date(DATE.getTime()-360000));

        property = FF.property("date");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("time");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime1");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime2");
        filter = FF.less(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.less(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        assertSerializedEquals(filter); //test serialize

    }

    @Test
    public void testIsLessThanOrEqualTo() {

        PropertyIsLessThanOrEqualTo filter;
        PropertyName property;

        Literal aboveLiteral = FF.literal('z');
        Literal equalLiteral = FF.literal('t');
        Literal underLiteral = FF.literal('c');

        property = FF.property("testCharacter");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(new Byte("111"));
        equalLiteral = FF.literal(new Byte("101"));
        underLiteral = FF.literal(new Byte("001"));

        property = FF.property("testByte");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(103);
        equalLiteral = FF.literal(101);
        underLiteral = FF.literal(21);

        property = FF.property("testShort");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testInteger");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testLong");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testFloat");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testDouble");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        aboveLiteral = FF.literal(new Date(DATE.getTime()+360000));
        equalLiteral = FF.literal(DATE);
        underLiteral = FF.literal(new Date(DATE.getTime()-360000));

        property = FF.property("date");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("time");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime1");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime2");
        filter = FF.lessOrEqual(aboveLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.lessOrEqual(equalLiteral,property);
        assertFalse(filter.evaluate(CANDIDATE_1));              // Needs to be assertTrue with JDK 10. TODO: investigate why!
        filter = FF.lessOrEqual(underLiteral,property);
        assertTrue(filter.evaluate(CANDIDATE_1));

        assertSerializedEquals(filter); //test serialize

    }

    @Test
    public void testIsNotEqualTo() {

        PropertyIsNotEqualTo filter;
        PropertyName property;

        Literal rightliteral = FF.literal(RIGHT_GEOMETRY);
        Literal falseliteral = FF.literal(WRONG_GEOMETRY);

        property = FF.property("testGeometry");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        rightliteral = FF.literal(true);
        falseliteral = FF.literal(false);

        property = FF.property("testBoolean");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        rightliteral = FF.literal('t');
        falseliteral = FF.literal('z');

        property = FF.property("testCharacter");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        rightliteral = FF.literal(new Byte("101"));
        falseliteral = FF.literal(new Byte("011"));

        property = FF.property("testByte");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        rightliteral = FF.literal(101);
        falseliteral = FF.literal(103);

        property = FF.property("testShort");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testInteger");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testLong");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testFloat");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("testDouble");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        rightliteral = FF.literal(DATE);
        falseliteral = FF.literal(new Date(DATE.getTime()+360000));
        Literal falseliteral2 = FF.literal(new Date(DATE.getTime()+ 25*60*60*1000));

        property = FF.property("date");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertFalse(filter.evaluate(CANDIDATE_1)); // Would be false if we took hours in account, but the filter intentionally ignores hours if a date is java.sql.Date.
        filter = FF.notEqual(property, falseliteral2);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("time");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime1");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        property = FF.property("datetime2");
        filter = FF.notEqual(property, rightliteral);
        assertFalse(filter.evaluate(CANDIDATE_1));
        filter = FF.notEqual(property, falseliteral);
        assertTrue(filter.evaluate(CANDIDATE_1));

        assertSerializedEquals(filter); //test serialize

    }

    @Test
    public void testMatchCase() {

        PropertyIsEqualTo filter;
        PropertyName property;

        Literal rightliteral = FF.literal("test STRING data");

        property = FF.property("testString");
        filter = FF.equal(property, rightliteral, false, MatchAction.ANY);
        assertTrue(filter.evaluate(CANDIDATE_1));
        filter = FF.equal(property, rightliteral, true, MatchAction.ANY);
        assertFalse(filter.evaluate(CANDIDATE_1));

        assertSerializedEquals(filter); //test serialize

    }

}
