/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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


import org.apache.sis.test.DependsOn;
import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link AnyConverter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 */
@DependsOn(SystemConverterTest.class)
public final strictfp class AnyConverterTest {
    /**
     * Tests the {@link AnyConverter#convert} method.
     *
     * @throws NonconvertibleObjectException Should never happen.
     */
    @Test
    public void testConvert() throws NonconvertibleObjectException {
        final AnyConverter c = new AnyConverter();
        assertNull("Expected no initial converter.", c.getLastConverter());

        assertEquals(2, c.convert(2, Number.class));
        assertNull("Expected no need for a converter.", c.getLastConverter());

        assertEquals(Integer.valueOf(2), c.convert("2", Integer.class));
        final ObjectConverter<?,?> last = c.getLastConverter();
        assertNotNull("Expected a new converter.", last);

        assertEquals(Integer.valueOf(4), c.convert("4", Integer.class));
        assertSame("Expected reuse of previous converter.", last, c.getLastConverter());

        assertEquals(Double .valueOf(8), c.convert("8", Double.class));
        assertNotSame("Expected a new converter.", last, c.getLastConverter());
    }

    /**
     * Tests the {@link AnyConverter#tryConvert} method.
     */
    @Test
    public void testTryConvert() {
        final AnyConverter c = new AnyConverter();
        assertNull("Expected no initial converter.", c.getLastConverter());

        assertEquals(2, c.tryConvert(2, Number.class));
        assertNull("Expected no need for a converter.", c.getLastConverter());

        assertEquals(Integer.valueOf(2), c.tryConvert("2", Integer.class));
        assertNotNull("Expected a new converter.", c.getLastConverter());

        assertEquals("2", c.tryConvert("2", System.class));
        assertNull("Expected no suitable converter.", c.getLastConverter());

        assertEquals("3", c.tryConvert("3", System.class));
        assertNull("Expected reuse of previous converter.", c.getLastConverter());

        assertEquals(Integer.valueOf(2), c.tryConvert("2", Integer.class));
        assertNotNull("Expected a new converter.", c.getLastConverter());
    }
}
