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
package org.geotoolkit.metadata;

import org.apache.sis.metadata.TypeValuePolicy;
import org.apache.sis.metadata.ValueExistencePolicy;
import org.apache.sis.metadata.KeyNamePolicy;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.extent.GeographicDescription;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.Commons;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.extent.AbstractGeographicExtent;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicDescription;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link TypeMap} and {@link PropertyMap}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
@Depend(MetadataStandard.class)
public final strictfp class PropertyMapTest {
    /**
     * Tests {@link TestMap} on a well known metadata type.
     */
    @Test
    public void testTypeMap() {
        final MetadataStandard standard = MetadataStandard.ISO_19115;
        final KeyNamePolicy keyNames = KeyNamePolicy.JAVABEANS_PROPERTY;
        Map<String, Class<?>> types;

        types = standard.asTypeMap(DefaultCitation.class, TypeValuePolicy.PROPERTY_TYPE, keyNames);
        assertEquals(InternationalString.class, types.get("title"));
        assertEquals(Collection.class,          types.get("alternateTitles"));

        types = standard.asTypeMap(DefaultCitation.class, TypeValuePolicy.ELEMENT_TYPE, keyNames);
        assertEquals(InternationalString.class, types.get("title"));
        assertEquals(InternationalString.class, types.get("alternateTitles"));

        types = standard.asTypeMap(DefaultCitation.class, TypeValuePolicy.DECLARING_INTERFACE, keyNames);
        assertEquals(Citation.class, types.get("title"));
        assertEquals(Citation.class, types.get("alternateTitles"));

        types = standard.asTypeMap(DefaultCitation.class, TypeValuePolicy.DECLARING_CLASS, keyNames);
        assertEquals(DefaultCitation.class, types.get("title"));
        assertEquals(DefaultCitation.class, types.get("alternateTitles"));

        /*
         * Tests declaring classes/interfaces again, now with metadata having a class hierarchy.
         */
        types = standard.asTypeMap(DefaultGeographicDescription.class, TypeValuePolicy.DECLARING_INTERFACE, keyNames);
        assertEquals(GeographicDescription.class, types.get("geographicIdentifier"));
        assertEquals(GeographicExtent.class,      types.get("inclusion"));

        types = standard.asTypeMap(DefaultGeographicDescription.class, TypeValuePolicy.DECLARING_CLASS, keyNames);
        assertEquals(DefaultGeographicDescription.class, types.get("geographicIdentifier"));
        assertEquals(AbstractGeographicExtent.class,     types.get("inclusion"));
    }

    /**
     * Creates a map from a metadata object and verifies that the map contains
     * the expected values. Then remove values from this map.
     */
    @Test
    public void testPropertyMap() {
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
        assertEquals(Collections.singletonList(author), map.get("citedResponsibleParties"));

        final Map<String,Object> expected = new HashMap<String,Object>();
        assertNull(expected.put("title", title));
        assertNull(expected.put("ISBN", "9782505004509"));
        assertNull(expected.put("citedResponsibleParties", Collections.singletonList(author)));
        assertNull(expected.put("identifiers", Collections.singletonList(Commons.getSingleton(citation.getIdentifiers()))));
        assertEquals(expected, map);
        /*
         * Removes a value.
         */
        assertEquals("9782505004509", expected.remove("ISBN"));
        assertEquals("9782505004509", map.remove("ISBN"));
        assertNotNull(expected.remove("identifiers"));
        assertNull(citation.getISBN());
        assertEquals(expected, map);
        /*
         * Adds a value.
         */
        assertNull(expected.put("presentationForm", Collections.singleton(PresentationForm.DOCUMENT_HARDCOPY)));
        assertEquals(Collections.emptySet(), map.put("presentationForm", PresentationForm.DOCUMENT_HARDCOPY));
        assertEquals(Collections.singleton(PresentationForm.DOCUMENT_HARDCOPY), citation.getPresentationForms());
        assertEquals(expected, map);
        /*
         * Adds back the ISBN value.
         */
        assertNull(citation.getISBN());
        assertNull(expected.put("ISBN", "9782505004509"));
        assertNull(map.put("ISBN", "9782505004509"));
        assertNull(expected.put("identifiers", Collections.singletonList(Commons.getSingleton(citation.getIdentifiers()))));
        assertEquals(expected, map);
        assertEquals("9782505004509", citation.getISBN());
        /*
         * Copies everything in a new citation using map.putAll(Map) and compare.
         */
        final DefaultCitation copy = new DefaultCitation();
        copy.asMap().putAll(expected);
        assertEquals(citation, copy);
        /*
         * Tries different kind of views.
         */
        Map<String,Object> m2;
        final MetadataStandard s = MetadataStandard.ISO_19115;
        m2 = s.asMap(citation, ValueExistencePolicy.NON_EMPTY, KeyNamePolicy.JAVABEANS_PROPERTY);
        assertFalse("Null values should be excluded.", m2.containsKey("alternateTitles"));
        assertEquals(map, m2);

        m2 = s.asMap(citation, ValueExistencePolicy.ALL, KeyNamePolicy.JAVABEANS_PROPERTY);
        assertTrue ("Null values should be included.", m2.containsKey("alternateTitles"));
        assertTrue ("'m2' should be a larger map than 'map'.", m2.entrySet().containsAll(map.entrySet()));
        assertFalse("'m2' should be a larger map than 'map'.", map.entrySet().containsAll(m2.entrySet()));
    }
}
