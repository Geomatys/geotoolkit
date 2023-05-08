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
package org.geotoolkit.filter.function.string;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.apache.sis.test.Assertions.assertSerializedEquals;
import static org.geotoolkit.filter.FilterTestConstants.*;
import static org.geotoolkit.filter.function.string.StringFunctionFactory.CONCAT;

import org.opengis.filter.Expression;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class StringFunctionTest extends org.geotoolkit.test.TestBase {
    @Test
    public void testTruncateFirst() {
        Expression f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal(1112),FF.literal(3));
        assertEquals(f.apply(null), "112");

        f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal(998),FF.literal(3));
        assertEquals(f.apply(null), "998");

        f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal(72),FF.literal(3));
        assertEquals(f.apply(null), "72");

        f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal("1112"),FF.literal(3));
        assertEquals(f.apply(null), "112");

        f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal("998"),FF.literal(3));
        assertEquals(f.apply(null), "998");

        f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal("72"),FF.literal(3));
        assertEquals(f.apply(null), "72");
        assertSerializedEquals(f); //test serialize
    }

    @Test
    public void testTruncateLast() {
        Expression f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal(1112),FF.literal(3));
        assertEquals(f.apply(null), "111");

        f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal(998),FF.literal(3));
        assertEquals(f.apply(null), "998");

        f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal(72),FF.literal(3));
        assertEquals(f.apply(null), "72");

        f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal("1112"),FF.literal(3));
        assertEquals(f.apply(null), "111");

        f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal("998"),FF.literal(3));
        assertEquals(f.apply(null), "998");

        f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal("72"),FF.literal(3));
        assertEquals(f.apply(null), "72");
        assertSerializedEquals(f); //test serialize
    }

    @Test
    public void testStringConcat() {
        final Expression oneTwo = FF.function(CONCAT, FF.literal("1"), FF.literal("2"));
        assertEquals("12", oneTwo.apply(null));
        final Expression oneTwoThree = FF.function(CONCAT, oneTwo, FF.literal("3"));
        assertEquals("123", oneTwoThree.apply(null));
    }
}
