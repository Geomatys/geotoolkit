/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.inputs.literal;

import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class StringToLongArrayConverterTest {

    @Test
    public void testConversion() throws UnconvertibleObjectException  {

        final WPSObjectConverter<String, long[]> converter = WPSConverterRegistry.getInstance().getConverter(String.class, long[].class);

        long[] expected = new long[] {10, 5, 90, 6, 7000000000l};
        long[] output = converter.apply("10, 5, 90, 6, 7000000000");
        assertArrayEquals(expected, output);

        output = converter.apply("   ");
        assertArrayEquals(new long[0], output);

        try {
            output = converter.apply("Some random text");
            fail("Long parser should not be able to read random text.");
        } catch (UnconvertibleObjectException ex) {
            // expected behavior
        }

        try {
            output = converter.apply("10.0, 5.6, 90, 6, 70");
            fail("Long parser should not be able to read float values.");
        } catch (UnconvertibleObjectException ex) {
            // expected behavior
        }
    }
}
