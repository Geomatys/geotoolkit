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

import java.util.Arrays;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;

import static java.lang.Double.NaN;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class StringToDoubleArrayConverterTest extends org.geotoolkit.test.TestBase {

    final WPSObjectConverter<String, double[]> converter = WPSConverterRegistry.getInstance().getConverter(String.class, double[].class);

    @Test
    public void testConversion() throws UnconvertibleObjectException  {
        double[] expected = new double[] { 10.5, 5.55, 90.4, 6.0, 70.0};
        double[] output = converter.convert("10.5, 5.55, 90.4, 6, 70.0", null);
        assertArrayEquals(expected, output, 0.0);

        output = converter.convert("   ", null);
        assertArrayEquals(new double[0], output, 0.0);

        boolean fail = false;
        try {
            output = converter.convert("Some random text", null);
        } catch (UnconvertibleObjectException ex) {
            fail = true;
        }
        assertTrue(fail);
    }

    @Test
    public void testInvalidInput() {
        final String[] inputs = {
                "4.&",
                "_",
                "%ù;zpgnoabj",
                "2.78, 5.07, .43, 4.4.5" // last token contains an extra decimal part
        };

        for (String input : inputs) {
            try {
                final double[] output = converter.convert(input, null);
                fail(String.format(
                        "Input should not be convertible.%nInput: %s%nResulted in: %s",
                        input, output == null ? "null" : Arrays.toString(output)
                ));
            } catch (UnconvertibleObjectException e) {
                // Expected behavior
            }
        }
    }

    @Test
    public void testMultiLineIsSupported() {
        final double[] result = converter.convert("4.4\n3.3", null);
        assertArrayEquals(new double[]{4.4, 3.3}, result, 1e-1);
    }

    @Test
    public void testSemiColonSeparator() {
        final double[] result = converter.convert("4.4;3.3", null);
        assertArrayEquals(new double[]{4.4, 3.3}, result, 1e-1);
    }

    @Test
    public void testSpaceOrTabSeparator() {
        final double[] result = converter.convert("4.4 3.3\t10000", null);
        assertArrayEquals(new double[]{4.4, 3.3, 10_000}, result, 1e-1);
    }

    @Test
    public void testEmptyTokenIsConvertedToNaN() {
        final double[] result = converter.convert("3.12, 4.14, , .11, , ,,7.4,,5.6", null);
        assertArrayEquals(new double[]{3.12, 4.14, NaN, .11, NaN, NaN, NaN, 7.4,NaN, 5.6}, result, 1e-2);
    }
}
