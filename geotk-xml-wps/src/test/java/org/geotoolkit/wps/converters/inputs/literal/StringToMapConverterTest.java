/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test converter between string and map.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class StringToMapConverterTest extends org.geotoolkit.test.TestBase {
    @Test
    public void testConvert() {
        final WPSObjectConverter converter = WPSConverterRegistry.getInstance().getConverter(String.class, Map.class);
        final String strMap = "{\"keyMap\":\"myValue\",\"10,20,30,40\":\"100\",\"keyInt\":2,\"keyDouble\":10.5,\"keyExtent\":[-180.0, -90.0, 180.0, 90.0]}";

        final Map convertedMap = (Map)converter.convert(strMap, null);
        assertNotNull(convertedMap);
        assertEquals("myValue", convertedMap.get("keyMap"));
        assertEquals("100",     convertedMap.get("10,20,30,40"));
        assertEquals(2,         convertedMap.get("keyInt"));
        assertEquals(10.5,      convertedMap.get("keyDouble"));

        final List<Double> extentValues = new ArrayList<>();
        extentValues.add(-180.0);
        extentValues.add(-90.0);
        extentValues.add(180.0);
        extentValues.add(90.0);
        assertEquals(extentValues, convertedMap.get("keyExtent"));
    }
}
