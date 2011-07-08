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

import java.net.URI;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.test.Commons;
import org.geotoolkit.test.TestBase;
import org.geotoolkit.xml.IdentifierMap;

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
     * Tests read and write operations on an {@link IdentifierMapAdapter}, using a well-formed
     * identifier collection (no null values, no duplicated authorities).
     * <p>
     * This test does not use the {@link IdentifierMap}-specific API.
     */
    @Test
    public void testGetAndPut() {
        final List<Identifier> identifiers = new ArrayList<Identifier>();
        final Map<Citation,String> map = IdentifierMapAdapter.create(Identifier.class, identifiers);
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
        assertEquals("{gml:id=“myID”, gco:uuid=“myUUID”}", map.toString());

        assertEquals("myUUID", map.put(UUID, "myNewUUID"));
        assertFalse (map.containsValue("myUUID"));
        assertTrue  (map.containsValue("myNewUUID"));
        assertEquals("{gml:id=“myID”, gco:uuid=“myNewUUID”}", map.toString());
        assertEquals(2, map.size());
        assertEquals(2, identifiers.size());

        assertNull  (map.put(HREF, "myHREF"));
        assertTrue  (map.containsValue("myHREF"));
        assertTrue  (map.containsKey(HREF));
        assertEquals("{gml:id=“myID”, gco:uuid=“myNewUUID”, xlink:href=“myHREF”}", map.toString());
        assertEquals(3, map.size());
        assertEquals(3, identifiers.size());

        assertEquals("myNewUUID", map.remove(UUID));
        assertFalse (map.containsValue("myNewUUID"));
        assertFalse (map.containsKey(UUID));
        assertEquals("{gml:id=“myID”, xlink:href=“myHREF”}", map.toString());
        assertEquals(2, map.size());
        assertEquals(2, identifiers.size());

        assertTrue  (map.values().remove("myHREF"));
        assertFalse (map.containsValue("myHREF"));
        assertFalse (map.containsKey(HREF));
        assertEquals("{gml:id=“myID”}", map.toString());
        assertEquals(1, map.size());
        assertEquals(1, identifiers.size());

        assertTrue  (map.keySet().remove(ID));
        assertFalse (map.containsValue("myID"));
        assertFalse (map.containsKey(ID));
        assertEquals("{}", map.toString());
        assertEquals(0, map.size());
        assertEquals(0, identifiers.size());
    }

    /**
     * Tests write operations on an {@link IdentifierMap} using specific API.
     *
     * @since 3.19
     */
    @Test
    public void testPutSpecialized() {
        final List<Identifier> identifiers = new ArrayList<Identifier>();
        final IdentifierMap map = IdentifierMapAdapter.create(Identifier.class, identifiers);
        final String myID = "myID";
        final java.util.UUID myUUID = java.util.UUID.fromString("a1eb6e53-93db-4942-84a6-d9e7fb9db2c7");
        final URI myURI = URI.create("http://mylink");
        map.putSpecialized(ID,   myID);
        map.putSpecialized(UUID, myUUID);
        map.putSpecialized(HREF, myURI);
        assertEquals("{gml:id=“myID”, gco:uuid=“a1eb6e53-93db-4942-84a6-d9e7fb9db2c7”, xlink:href=“http://mylink”}", map.toString());
        assertSame(myID,   map.getSpecialized(ID));
        assertSame(myUUID, map.getSpecialized(UUID));
        assertSame(myURI,  map.getSpecialized(HREF));
        assertEquals("myID",                                 map.get(ID));
        assertEquals("a1eb6e53-93db-4942-84a6-d9e7fb9db2c7", map.get(UUID));
        assertEquals("http://mylink",                        map.get(HREF));
    }

    /**
     * Tests read operations on an {@link IdentifierMap} using specific API.
     *
     * @since 3.19
     */
    @Test
    public void testGetSpecialized() {
        final List<Identifier> identifiers = new ArrayList<Identifier>();
        final IdentifierMap map = IdentifierMapAdapter.create(Identifier.class, identifiers);
        map.put(ID,   "myID");
        map.put(UUID, "a1eb6e53-93db-4942-84a6-d9e7fb9db2c7");
        map.put(HREF, "http://mylink");
        assertEquals("{gml:id=“myID”, gco:uuid=“a1eb6e53-93db-4942-84a6-d9e7fb9db2c7”, xlink:href=“http://mylink”}", map.toString());
        assertEquals("myID",                                 map.get(ID));
        assertEquals("a1eb6e53-93db-4942-84a6-d9e7fb9db2c7", map.get(UUID));
        assertEquals("http://mylink",                        map.get(HREF));
        assertEquals("myID",                                                            map.getSpecialized(ID));
        assertEquals(URI.create("http://mylink"),                                       map.getSpecialized(HREF));
        assertEquals(java.util.UUID.fromString("a1eb6e53-93db-4942-84a6-d9e7fb9db2c7"), map.getSpecialized(UUID));
    }

    /**
     * Tests the handling of duplicated authorities.
     *
     * @since 3.19
     */
    @Test
    public void testDuplicatedAuthorities() {
        final List<Identifier> identifiers = new ArrayList<Identifier>();
        identifiers.add(new IdentifierEntry(ID,   "myID1"));
        identifiers.add(new IdentifierEntry(UUID, "myUUID"));
        identifiers.add(new IdentifierEntry(ID,   "myID2"));

        final IdentifierMap map = IdentifierMapAdapter.create(Identifier.class, identifiers);
        assertEquals("Duplicated authorities shall be filtered.", 2, map.size());
        assertEquals("Duplicated authorities shall still exist.", 3, identifiers.size());
        assertEquals("myID1",  map.get(ID));
        assertEquals("myUUID", map.get(UUID));

        final Iterator<Citation> it = map.keySet().iterator();
        assertTrue(it.hasNext());
        assertSame(ID, it.next());
        it.remove();
        assertTrue(it.hasNext());
        assertSame(UUID, it.next());
        assertFalse("Duplicated authority shall have been removed.", it.hasNext());

        assertEquals(1, identifiers.size());
        assertEquals(1, map.size());
    }

    /**
     * Tests serialization.
     *
     * @since 3.19
     */
    @Test
    public void testSerialization() {
        assertSame(ID,   Commons.serialize(ID));
        assertSame(UUID, Commons.serialize(UUID));
        assertSame(HREF, Commons.serialize(HREF));

        final List<Identifier> identifiers = new ArrayList<Identifier>();
        final Map<Citation,String> map = IdentifierMapAdapter.create(Identifier.class, identifiers);
        identifiers.add(new IdentifierEntry(ID, "myID"));
        identifiers.add(new IdentifierEntry(UUID, "myUUID"));

        final Map<Citation,String> copy = Commons.serialize(map);
        assertNotSame(map, copy);
        assertEquals(2, copy.size());
    }
}
