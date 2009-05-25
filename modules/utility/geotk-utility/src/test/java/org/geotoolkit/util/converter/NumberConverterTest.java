/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Color;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests the various {@link NumberConverter} implementations.
 *
 * @author Justin Deoliveira (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 */
public final class NumberConverterTest {
    /**
     * Tests conversions to boolean values.
     * Adapted from Geotoolkit 2.5 {@code BooleanConverterFactoryTest}.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    public void testBoolean() throws NonconvertibleObjectException {
        final ObjectConverter<Number,Boolean> c = NumberConverter.Boolean.INSTANCE;
        assertEquals(Boolean.TRUE,  c.convert(Integer.valueOf(1)));
        assertEquals(Boolean.FALSE, c.convert(Integer.valueOf(0)));
        assertSame(c, serialize(c));
    }

    /**
     * Tests conversions to double values.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    public void testDouble() throws NonconvertibleObjectException {
        final ObjectConverter<Number,Double> c = NumberConverter.Double.INSTANCE;
        assertEquals(Double.valueOf(2.0), c.convert(Integer.valueOf(2)));
        assertEquals(Double.valueOf(0.5), c.convert(Float.valueOf(0.5f)));
        assertSame(c, serialize(c));
    }

    /**
     * Tests conversions to color values.
     * Adapted from Geotoolkit 2.5 {@code ColorConverterFactoryTest}.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    public void testColor() throws NonconvertibleObjectException {
        final ObjectConverter<Number,Color> c = NumberConverter.Color.INSTANCE;
        assertEquals("opaque red",    Color.RED,          c.convert(0xFF0000  ));
        assertEquals("no alpha",  new Color(0,0,255,255), c.convert(0x000000FF));
        assertEquals("255 alpha", new Color(0,0,255,255), c.convert(0xFF0000FF));
        assertEquals("1 alpha",   new Color(0,0,255,1),   c.convert(0x010000FF));
        assertSame(c, serialize(c));
    }
}
