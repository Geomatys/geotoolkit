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
package org.geotoolkit.metadata.iso.citation;

import java.util.Arrays;
import java.util.Collection;

import org.opengis.metadata.Identifier;

import org.geotoolkit.test.Depend;
import org.geotoolkit.xml.IdentifierMap;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.internal.jaxb.IdentifierMapAdapter;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link DefaultCitation}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
@Depend(DefaultCitationDateTest.class)
public final class DefaultCitationTest {
    /**
     * Tests the ISBN identifier, which is handled in a special way.
     */
    @Test
    public void testISBN() {
        final DefaultCitation citation = new DefaultCitation();
        final IdentifierMap identifierMap = citation.getIdentifierMap();
        final Collection<Identifier> identifiers = ((IdentifierMapAdapter) identifierMap).identifiers;
        assertTrue("Expected an initially empty set of identifiers.", identifiers.isEmpty());

        citation.setISBN("MyISBN");
        assertEquals("ISBN code shall be included in the set of identifiers.", 1, identifiers.size());
        assertEquals("MyISBN", citation.getISBN());
        assertNull(citation.getISSN());
        assertEquals("{ISBN=“MyISBN”}", identifierMap.toString());

        citation.setIdentifiers(Arrays.asList(
                new DefaultIdentifier(Citations.OGC,  "MyOGC"),
                new DefaultIdentifier(Citations.EPSG, "MyEPSG"),
                new DefaultIdentifier(Citations.ISSN, "MyISSN")));

        assertEquals(4, identifiers.size());
        assertEquals("MyISBN", citation.getISBN());
        if (false) {
            // Following test is disabled for now, because our current implementation
            // does not yet have the capability to convert Strings to arbitrary types.
            assertEquals("MyISSN", citation.getISSN());
        }
        assertEquals("{OGC=“MyOGC”, EPSG=“MyEPSG”, ISSN=“MyISSN”, ISBN=“MyISBN”}", identifierMap.toString());
    }
}
