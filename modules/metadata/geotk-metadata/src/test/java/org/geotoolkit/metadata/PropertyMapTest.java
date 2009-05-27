/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.metadata;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.PresentationForm;

import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link PropertyMap}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final class PropertyMapTest {
    /**
     * Creates a map from a metadata object and verifies that the map contains
     * the expected values. Then remove values from this map.
     */
    @Test
    public void testMap() {
        final DefaultCitation citation = new DefaultCitation();
        final InternationalString title = new SimpleInternationalString("Undercurrent");
        citation.setTitle(title);
        citation.setISBN("9782505004509");

        final DefaultResponsibleParty author = new DefaultResponsibleParty();
        author.setIndividualName("Testsuya Toyoda");
        citation.getCitedResponsibleParties().add(author);

        final Map<String,Object> map = citation.asMap();
        assertEquals("Undercurrent", map.get("title").toString());
        assertEquals("9782505004509", map.get("ISBN"));
        assertEquals(Collections.singleton(author), map.get("citedResponsibleParties"));

        final Map<String,Object> expected = new HashMap<String,Object>();
        assertNull(expected.put("title", title));
        assertNull(expected.put("ISBN", "9782505004509"));
        assertNull(expected.put("citedResponsibleParties", Collections.singleton(author)));
        assertEquals(expected, map);
        /*
         * Removes a value.
         */
        assertEquals("9782505004509", expected.remove("ISBN"));
        assertEquals("9782505004509", map.remove("ISBN"));
        assertNull(citation.getISBN());
        assertEquals(expected, map);
        /*
         * Adds a value.
         */
        assertNull(expected.put("presentationForm", Collections.singleton(PresentationForm.DOCUMENT_HARDCOPY)));
        assertEquals(Collections.emptySet(), map.put("presentationForm", PresentationForm.DOCUMENT_HARDCOPY));
        assertEquals(Collections.singleton(PresentationForm.DOCUMENT_HARDCOPY), citation.getPresentationForm());
        assertEquals(expected, map);
        /*
         * Adds back the ISBN value.
         */
        assertNull(citation.getISBN());
        assertNull(expected.put("ISBN", "9782505004509"));
        assertNull(map.put("ISBN", "9782505004509"));
        assertEquals(expected, map);
        assertEquals("9782505004509", citation.getISBN());
        /*
         * Copies everything in a new citation using map.putAll(Map) and compare.
         */
        final DefaultCitation copy = new DefaultCitation();
        copy.asMap().putAll(expected);
        assertEquals(citation, copy);
    }
}
