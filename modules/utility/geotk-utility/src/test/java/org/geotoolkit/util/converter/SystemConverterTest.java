/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.converter;

import java.util.Date;
import java.sql.Timestamp;
import org.geotoolkit.test.Depend;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests a few conversions using the system converter.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(ConverterRegistryTest.class)
public class SystemConverterTest {
    /**
     * Tests conversions from strings to numbers.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testStringToNumber() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals(Short.valueOf((short) 300), system.converter(String.class, Short.class).convert("300"));
        assertEquals(Float.valueOf((float) 0.5), system.converter(String.class, Float.class).convert("0.5"));
    }

    /**
     * Tests conversions from numbers to strings.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testNumberToString() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals("12",  system.converter(Integer.class, String.class).convert(12));
        assertEquals("2.5", system.converter(Number.class,  String.class).convert(2.5));
    }

    /**
     * Tests conversions from numbers to numbers.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testNumberToNumber() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals(Float.valueOf(2.0f), system.converter(Integer.class, Float.class).convert(2));
        assertEquals(Float.valueOf(3.0f), system.converter(Number .class, Float.class).convert(3));
        assertEquals(Float.valueOf(0.5f), system.converter(Double .class, Float.class).convert(0.5));
        assertEquals(Float.valueOf(2.5f), system.converter(Number .class, Float.class).convert(2.5));
    }

    /**
     * Tests conversions from dates to timestamps.
     *
     * @throws NonconvertibleObjectException Should not happen.
     */
    @Test
    public void testDateToTimestamp() throws NonconvertibleObjectException {
        final ConverterRegistry system = ConverterRegistry.system();
        assertEquals(new Timestamp(2000), system.converter(Date.class, Timestamp.class).convert(new Date(2000)));
    }
}
