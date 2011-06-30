/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.test.TestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.xml.IdentifierSpace.*;


/**
 * Test {@link IdentifierMapAdapter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.18
 */
public final class IdentifierMapAdapterTest extends TestBase {
    /**
     * Tests read and write operations on an {@link IdentifierMap}, using a well-formed
     * identifier collection (no null values, no duplicated authorities).
     */
    @Test
    public void testReadWrite() {
        final List<Identifier> identifiers = new ArrayList<Identifier>();
        final Map<Citation,String> map = new IdentifierMapAdapter(identifiers);
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        identifiers.add(new IdentifierEntry(ID, "myID"));
        identifiers.add(new IdentifierEntry(UUID, "myUUID"));
        assertFalse (map.isEmpty());
        assertEquals(2, map.size());
        assertEquals(2, identifiers.size());
        assertTrue  (map.containsKey(ID));
        assertTrue  (map.containsKey(UUID));
        assertFalse (map.containsKey(HREF));
        assertEquals("myID",   map.get(ID));
        assertEquals("myUUID", map.get(UUID));
        assertNull  (          map.get(HREF));
        assertTrue  (map.containsValue("myID"));
        assertTrue  (map.containsValue("myUUID"));
        assertFalse (map.containsValue("myHREF"));
        assertEquals("{gml:id=myID, gco:uuid=myUUID}", map.toString());

        assertEquals("myUUID", map.put(UUID, "myNewUUID"));
        assertFalse (map.containsValue("myUUID"));
        assertTrue  (map.containsValue("myNewUUID"));
        assertEquals("{gml:id=myID, gco:uuid=myNewUUID}", map.toString());
        assertEquals(2, map.size());
        assertEquals(2, identifiers.size());

        assertNull  (map.put(HREF, "myHREF"));
        assertTrue  (map.containsValue("myHREF"));
        assertTrue  (map.containsKey(HREF));
        assertEquals("{gml:id=myID, gco:uuid=myNewUUID, xlink:href=myHREF}", map.toString());
        assertEquals(3, map.size());
        assertEquals(3, identifiers.size());

        assertEquals("myNewUUID", map.remove(UUID));
        assertFalse (map.containsValue("myNewUUID"));
        assertFalse (map.containsKey(UUID));
        assertEquals("{gml:id=myID, xlink:href=myHREF}", map.toString());
        assertEquals(2, map.size());
        assertEquals(2, identifiers.size());

        assertTrue  (map.values().remove("myHREF"));
        assertFalse (map.containsValue("myHREF"));
        assertFalse (map.containsKey(HREF));
        assertEquals("{gml:id=myID}", map.toString());
        assertEquals(1, map.size());
        assertEquals(1, identifiers.size());

        assertTrue  (map.keySet().remove(ID));
        assertFalse (map.containsValue("myID"));
        assertFalse (map.containsKey(ID));
        assertEquals("{}", map.toString());
        assertEquals(0, map.size());
        assertEquals(0, identifiers.size());
    }
}
