/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.internal.jaxb;

import java.util.UUID;
import java.util.Date;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link UUIDs}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.13
 */
public final class UUIDsTest {
    /**
     * Tests the creation of a new UUID, and performs an object lookup for that UUID.
     */
    @Test
    public void testGetOrCreateUUID() {
        final UUIDs map = UUIDs.DEFAULT;
        final Date object = new Date();
        assertNull("The object should have no initial UUID.", map.getUUID(object));

        final UUID uuid = map.getOrCreateUUID(object);
        assertNotNull("Should have created a new UUID.", uuid);
        assertSame("Should find the UUID.", uuid, map.getUUID(object));
        assertSame("Should not create another UUID for the same object.", uuid, map.getOrCreateUUID(object));
        assertSame("Should be able to get the object from the UUID.", object, map.lookup(uuid));

        object.setTime(0);
        assertSame("Should find the UUID.", uuid, map.getUUID(object));
        assertSame("Should be able to get the object from the UUID.", object, map.lookup(uuid));

        try {
            map.setUUID(object, uuid);
            fail("It should not be allowed to reassing an UUID.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
        }
    }

    /**
     * Tests explicit setting of a UUID.
     */
    @Test
    public void testSetUUID() {
        final UUIDs map = UUIDs.DEFAULT;
        final Date object = new Date();
        assertNull("The object should have no initial UUID.", map.getUUID(object));

        final UUID uuid = UUID.randomUUID();
        map.setUUID(object, uuid);
        assertSame("Should find the UUID.", uuid, map.getUUID(object));
        assertSame("Should be able to get the object from the UUID.", object, map.lookup(uuid));
        assertSame("Should not create another UUID for the same object.", uuid, map.getOrCreateUUID(object));

        object.setTime(0);
        assertSame("Should find the UUID.", uuid, map.getUUID(object));
        assertSame("Should be able to get the object from the UUID.", object, map.lookup(uuid));

        try {
            map.setUUID(object, uuid);
            fail("It should not be allowed to reassing an UUID.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
        }
    }
}
