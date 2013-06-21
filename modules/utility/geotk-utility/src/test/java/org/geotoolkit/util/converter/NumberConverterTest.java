/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
import java.math.BigDecimal;

import org.junit.*;
import static org.apache.sis.test.Assert.*;


/**
 * Tests the various {@link NumberConverter} implementations.
 *
 * @author Justin Deoliveira (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 2.4
 */
public final strictfp class NumberConverterTest {
    /**
     * Tests conversions to boolean values.
     * Adapted from GeoTools 2.5 {@code BooleanConverterFactoryTest}.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    public void testBoolean() throws NonconvertibleObjectException {
        final ObjectConverter<Number,Boolean> c = NumberConverter.Boolean.INSTANCE;
        assertEquals(Boolean.TRUE,  c.convert(Integer.valueOf(1)));
        assertEquals(Boolean.FALSE, c.convert(Integer.valueOf(0)));
        assertSame(c, assertSerializedEquals(c));
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
        assertSame(c, assertSerializedEquals(c));
    }

    /**
     * Tests conversions to big decimal.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    public void testBigDecimal() throws NonconvertibleObjectException {
        final ObjectConverter<Number,BigDecimal> c = NumberConverter.BigDecimal.INSTANCE;
        assertEquals(BigDecimal.valueOf(2),   c.convert(Integer.valueOf(2)));
        assertEquals(BigDecimal.valueOf(0.5), c.convert(Float.valueOf(0.5f)));
        assertSame(c, assertSerializedEquals(c));
    }

    /**
     * Tests conversions to color values.
     * Adapted from GeoTools 2.5 {@code ColorConverterFactoryTest}.
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
        assertSame(c, assertSerializedEquals(c));
    }

    /**
     * Tests conversions to comparable objects. Should returns the object unchanged
     * since all {@link Number} subclasses are comparable.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    public void testComparable() throws NonconvertibleObjectException {
        @SuppressWarnings("rawtypes")
        final ObjectConverter<Number,Comparable<?>> c = NumberConverter.Comparable.INSTANCE;
        assertEquals(2,   c.convert(2  ));
        assertEquals(2.5, c.convert(2.5));
        assertSame(c, assertSerializedEquals(c));
    }
}
