/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
import java.util.Collection;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.test.TestBase;
import org.geotoolkit.xml.IdentifierMap;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.xml.IdentifierSpace.*;


/**
 * Tests {@link IdentifierMapAdapter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.18
 */
public strictfp class IdentifierMapAdapterTest extends TestBase {
    /**
     * Creates the {@link IdentifierMapAdapter} instance to test for the given identifiers.
     * Subclasses will override this method.
     *
     * @param  identifiers The identifiers to wrap in an {@code IdentifierMapAdapter}.
     * @return The {@code IdentifierMapAdapter} to test.
     */
    IdentifierMapAdapter create(final Collection<Identifier> identifiers) {
        return new IdentifierMapAdapter(identifiers);
    }

    /**
     * Asserts that the content of the given map is equals to the given content, represented
     * as a string. Subclasses can override this method in order to alter the expected string.
     *
     * @param  expected The expected content.
     * @return The map to compare with the expected content.
     */
    void assertMapEquals(final String expected, final Map<Citation,String> map) {
        assertEquals(expected, map.toString());
    }

    /**
     * Returns a string representation of the given {@code href} value.
     * The default implementation returns the value unchanged.
     */
    String toHRefString(final String href) {
        return href;
    }

    /**
     * Tests read and write operations on an {@link IdentifierMapAdapter}, using a well-formed
     * identifier collection (no null values, no duplicated authorities).
     * <p>
     * This test does not use the {@link IdentifierMap}-specific API.
     */
    @Test
    public void testGetAndPut() {
        final List<Identifier> identifiers = new ArrayList<>();
        final Map<Citation,String> map = create(identifiers);
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        identifiers.add(new IdentifierMapEntry(ID,   "myID"));
        identifiers.add(new IdentifierMapEntry(UUID, "myUUID"));
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
        assertMapEquals("{gml:id=“myID”, gco:uuid=“myUUID”}", map);

        assertEquals("myUUID", map.put(UUID, "myNewUUID"));
        assertFalse (map.containsValue("myUUID"));
        assertTrue  (map.containsValue("myNewUUID"));
        assertMapEquals("{gml:id=“myID”, gco:uuid=“myNewUUID”}", map);
        assertEquals(2, map.size());
        assertEquals(2, identifiers.size());

        assertNull  (map.put(HREF, "myHREF"));
        assertTrue  (map.containsValue("myHREF"));
        assertTrue  (map.containsKey(HREF));
        assertMapEquals("{gml:id=“myID”, gco:uuid=“myNewUUID”, xlink:href=“myHREF”}", map);
        assertEquals(3, map.size());
        assertEquals(3, identifiers.size());

        assertEquals("myNewUUID", map.remove(UUID));
        assertFalse (map.containsValue("myNewUUID"));
        assertFalse (map.containsKey(UUID));
        assertMapEquals("{gml:id=“myID”, xlink:href=“myHREF”}", map);
        assertEquals(2, map.size());
        assertEquals(2, identifiers.size());

        assertTrue  (map.values().remove(toHRefString("myHREF")));
        assertFalse (map.containsValue("myHREF"));
        assertFalse (map.containsKey(HREF));
        assertMapEquals("{gml:id=“myID”}", map);
        assertEquals(1, map.size());
        assertEquals(1, identifiers.size());

        assertTrue  (map.keySet().remove(ID));
        assertFalse (map.containsValue("myID"));
        assertFalse (map.containsKey(ID));
        assertMapEquals("{}", map);
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
        final List<Identifier> identifiers = new ArrayList<>();
        final IdentifierMap map = create(identifiers);
        final String myID = "myID";
        final java.util.UUID myUUID = java.util.UUID.fromString("a1eb6e53-93db-4942-84a6-d9e7fb9db2c7");
        final URI myURI = URI.create("http://mylink");
        map.putSpecialized(ID,   myID);
        map.putSpecialized(UUID, myUUID);
        map.putSpecialized(HREF, myURI);
        assertMapEquals("{gml:id=“myID”, gco:uuid=“a1eb6e53-93db-4942-84a6-d9e7fb9db2c7”, xlink:href=“http://mylink”}", map);
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
        final List<Identifier> identifiers = new ArrayList<>();
        final IdentifierMap map = create(identifiers);
        map.put(ID,   "myID");
        map.put(UUID, "a1eb6e53-93db-4942-84a6-d9e7fb9db2c7");
        map.put(HREF, "http://mylink");
        assertMapEquals("{gml:id=“myID”, gco:uuid=“a1eb6e53-93db-4942-84a6-d9e7fb9db2c7”, xlink:href=“http://mylink”}", map);
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
        final List<Identifier> identifiers = new ArrayList<>();
        identifiers.add(new IdentifierMapEntry(ID,   "myID1"));
        identifiers.add(new IdentifierMapEntry(UUID, "myUUID"));
        identifiers.add(new IdentifierMapEntry(ID,   "myID2"));

        final IdentifierMap map = create(identifiers);
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
        assertSame(ID,   assertSerializable(ID));
        assertSame(UUID, assertSerializable(UUID));
        assertSame(HREF, assertSerializable(HREF));

        final List<Identifier> identifiers = new ArrayList<>();
        final Map<Citation,String> map = create(identifiers);
        identifiers.add(new IdentifierMapEntry(ID,   "myID"));
        identifiers.add(new IdentifierMapEntry(UUID, "myUUID"));

        final Map<Citation,String> copy = assertSerializable(map);
        assertNotSame(map, copy);
        assertEquals(2, copy.size());
    }
}
