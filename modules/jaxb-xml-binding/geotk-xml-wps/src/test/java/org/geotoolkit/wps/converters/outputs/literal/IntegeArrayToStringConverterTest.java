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

import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class IntegeArrayToStringConverterTest {

    @Test
    public void testConversion() throws UnconvertibleObjectException  {
        
        final WPSObjectConverter converter = WPSConverterRegistry.getInstance().getConverter(int[].class, String.class);
        
        int[] input = new int[] { 10, 5, 90, 6, 70};
        String output = (String)converter.convert(input, null);
        assertEquals("10,5,90,6,70", output);
        
        output = (String)converter.convert(new int[0], null);
        assertEquals("", output);
    }
}
