/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.factory;

import java.awt.RenderingHints;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.media.jai.JAI;

import org.junit.*;

import static org.apache.sis.test.Assert.*;


/**
 * Tests {@link Hints}.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD)
 * @version 3.05
 *
 * @since 2.4
 */
public final strictfp class HintsTest {
    /**
     * Makes sures that J2SE 1.4 assertions are enabled.
     */
    @Test
    public void testAssertionEnabled() {
        assertTrue("Assertions not enabled.", Hints.class.desiredAssertionStatus());
    }

    /**
     * Tests the {@link Hints#nameOf} static method.
     */
    @Test
    public void testNameOf() {
        assertEquals("CS_FACTORY",        Hints.nameOf(Hints.CS_FACTORY));
        assertEquals("KEY_INTERPOLATION", Hints.nameOf(RenderingHints.KEY_INTERPOLATION));
        assertEquals("KEY_TILE_CACHE",    Hints.nameOf(JAI.KEY_TILE_CACHE));
    }

    /**
     * Tests the serialization of a key.
     */
    @Test
    public void testKeySerialization() {
        assertSame(Hints.CS_FACTORY,    assertSerializedEquals(Hints.CS_FACTORY));
        assertSame(Hints.DATUM_FACTORY, assertSerializedEquals(Hints.DATUM_FACTORY));
    }

    /**
     * Tests the removal of keys from a hashmap. Required for {@link FactoryRegistry} working.
     */
    @Test
    public void testRemoval() {
        final Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        assertFalse(hints.isEmpty());

        Map<RenderingHints.Key,Object> map = new HashMap<>();
        assertNull(map.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.FALSE));
        map = Collections.unmodifiableMap(map);
        assertFalse(map.isEmpty());

        final Hints remaining = hints.clone();
        assertTrue(remaining.keySet().removeAll(map.keySet()));
        assertTrue(remaining.isEmpty());
    }

    /**
     * Tests the {@link Hints#toString} method.
     */
    public void testToString() {
        final Hints hints = new Hints();

        assertNull(hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.FALSE));
        assertEquals("FORCE_LONGITUDE_FIRST_AXIS_ORDER = false", hints.toString().trim());

        assertEquals(hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE), Boolean.FALSE);
        assertEquals("FORCE_LONGITUDE_FIRST_AXIS_ORDER = true", hints.toString().trim());

        assertEquals(hints.remove(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER), Boolean.TRUE);
        assertEquals("", hints.toString().trim());
    }

    /**
     * Tests addition of system-wide defaults.
     */
    @Test
    public void testSystemDefaults(){
        assertTrue(new Hints().isEmpty());
        try {
            assertNull(Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.FALSE));
            final Hints hints = new Hints();
            assertFalse(hints.isEmpty());
            assertEquals(1, hints.size());

            final Object value = hints.get(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER);
            assertTrue(value instanceof Boolean);
            assertFalse(((Boolean) value).booleanValue());

            assertEquals(hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE), Boolean.FALSE);
            assertEquals(hints.remove(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER), Boolean.TRUE);
            assertTrue(hints.isEmpty());
        } finally {
            assertNotNull(Hints.removeSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));
        }
        assertTrue(new Hints().isEmpty());
    }
}
