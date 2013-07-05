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
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.junit.*;
import static org.apache.sis.test.Assert.*;


/**
 * Tests the various {@link StringConverter} implementations.
 *
 * @author Justin Deoliveira (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 */
public final strictfp class StringConverterTest {
    /**
     * Tests conversions to boolean values.
     * Adapted from GeoTools 2.5 {@code BooleanConverterFactoryTest}.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    public void testBoolean() throws NonconvertibleObjectException {
        final ObjectConverter<String,Boolean> c = StringConverter.Boolean.INSTANCE;
        assertEquals(Boolean.TRUE,  c.convert("true"));
        assertEquals(Boolean.TRUE,  c.convert("1"));
        assertEquals(Boolean.FALSE, c.convert("false"));
        assertEquals(Boolean.FALSE, c.convert("0"));
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
        final ObjectConverter<String,Color> c = StringConverter.Color.INSTANCE;
        assertEquals(Color.RED, c.convert("#FF0000"));
        assertSame(c, assertSerializedEquals(c));
    }

    /**
     * Tests conversions to charset values.
     * Adapted from GeoTools 2.5 {@code CharsetConverterFactoryTest}.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    public void testCharset() throws NonconvertibleObjectException {
        final ObjectConverter<String,Charset> c = StringConverter.Charset.INSTANCE;
        Charset charset = c.convert("UTF-8");
        assertEquals("UTF-8", charset.displayName());
        try {
            c.convert("FOO");
            fail("Should not be convertible.");
        } catch (NonconvertibleObjectException e) {
            // This is the expected exception.
            assertTrue(e.getCause() instanceof UnsupportedCharsetException);
        }
        assertSame(c, assertSerializedEquals(c));
    }
}
