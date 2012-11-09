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
package org.geotoolkit.wps.converters.outputs.literal;

import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class DoubleArrayToStringConverterTest {

    @Test
    public void testConversion() throws NonconvertibleObjectException  {
        
        final WPSObjectConverter converter = WPSConverterRegistry.getInstance().getConverter(double[].class, String.class);
        
        double[] input = new double[] { 10.5, 5.55, 90.4, 6.0, 70.0};
        String output = (String)converter.convert(input, null);
        assertEquals("10.5,5.55,90.4,6.0,70.0", output);
        
        output = (String)converter.convert(new double[0], null);
        assertEquals("", output);
    }
}
