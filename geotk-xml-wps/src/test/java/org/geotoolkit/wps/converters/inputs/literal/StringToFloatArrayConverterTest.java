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
public class StringToFloatArrayConverterTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testConversion() throws UnconvertibleObjectException  {

        final WPSObjectConverter converter = WPSConverterRegistry.getInstance().getConverter(String.class, float[].class);

        float[] expected = new float[] { 10.5f, 5.55f, 90.4f, 6.0f, 70.0f};
        float[] output = (float[])converter.convert("10.5, 5.55, 90.4, 6, 70.0", null);
        assertArrayEquals(expected, output, 0.0f);

        output = (float[])converter.convert("   ", null);
        assertArrayEquals(new float[0], output, 0.0f);

        boolean fail = false;
        try {
            output = (float[])converter.convert("Some random text", null);
        } catch (UnconvertibleObjectException ex) {
            fail = true;
        }
        assertTrue(fail);
    }
}
