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

import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class StringToDoubleArrayConverterTest {

    @Test
    public void testConversion() throws NonconvertibleObjectException  {
        
        final WPSObjectConverter converter = WPSConverterRegistry.getInstance().getConverter(String.class, double[].class);
        
        double[] expected = new double[] { 10.5, 5.55, 90.4, 6.0, 70.0};
        double[] output = (double[])converter.convert("10.5, 5.55, 90.4, 6, 70.0", null);
        assertArrayEquals(expected, output, 0.0);
        
        output = (double[])converter.convert("   ", null);
        assertArrayEquals(new double[0], output, 0.0);
        
        boolean fail = false;
        try {
            output = (double[])converter.convert("Some random text", null);
        } catch (NonconvertibleObjectException ex) {
            fail = true;
        }
        assertTrue(fail);
    }
}
