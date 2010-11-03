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


import org.geotoolkit.test.Commons;
import org.junit.Test;

import org.opengis.filter.expression.Function;

import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StringFunctionTest {

    
    public StringFunctionTest() {

    }

    @Test
    public void testTruncateFirst() {

        Function f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal(1112),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "112");

        f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal(998),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "998");

        f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal(72),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "72");

        f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal("1112"),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "112");

        f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal("998"),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "998");

        f = FF.function(StringFunctionFactory.TRUNCATE_FIRST, FF.literal("72"),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "72");
        Commons.serialize(f); //test serialize

    }

    @Test
    public void testTruncateLast() {

        Function f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal(1112),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "111");

        f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal(998),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "998");

        f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal(72),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "72");

        f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal("1112"),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "111");

        f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal("998"),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "998");

        f = FF.function(StringFunctionFactory.TRUNCATE_LAST, FF.literal("72"),FF.literal(3));
        assertEquals(f.evaluate(null, String.class), "72");
        Commons.serialize(f); //test serialize

    }


    
}
