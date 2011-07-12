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
import javax.xml.bind.JAXBException;

import org.opengis.metadata.Identifier;

import org.geotoolkit.xml.XML;
import org.geotoolkit.xml.IdentifierMap;
import org.geotoolkit.xml.IdentifierSpace;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.internal.jaxb.IdentifierMapAdapter;
import org.geotoolkit.test.Depend;
import org.geotoolkit.util.Strings;

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
     * Tests the identifier map, which handles ISBN and ISSN codes in a special way.
     */
    @Test
    public void testIdentifierMap() {
        final DefaultCitation citation = new DefaultCitation();
        final IdentifierMap identifierMap = citation.getIdentifierMap();
        final Collection<Identifier> identifiers = ((IdentifierMapAdapter) identifierMap).identifiers;
        assertTrue("Expected an initially empty set of identifiers.", identifiers.isEmpty());

        citation.setISBN("MyISBN");
        assertEquals("MyISBN", citation.getISBN());
        assertEquals("ISBN code shall be included in the set of identifiers.", 1, identifiers.size());
        assertEquals("{ISBN=“MyISBN”}", identifierMap.toString());

        assertNull(citation.getISSN());
        citation.setIdentifiers(Arrays.asList(
                new DefaultIdentifier(Citations.OGC,  "MyOGC"),
                new DefaultIdentifier(Citations.EPSG, "MyEPSG"),
                new DefaultIdentifier(Citations.ISSN, "MyISSN")));

        assertEquals(4, identifiers.size());
        assertEquals("MyISBN", citation.getISBN());
        assertNull("The current implementation of setIdentifiers(Collection) does not perform "
                + "any special processing for ISBN and ISSN codes yet. However we may revisit "
                + "this policy in a future version.", citation.getISSN());
        assertEquals("{OGC=“MyOGC”, EPSG=“MyEPSG”, ISSN=“MyISSN”, ISBN=“MyISBN”}", identifierMap.toString());
    }

    /**
     * Ensures that the identifier collection, when marshalled, does not include the ISBN
     * and ID codes.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testIdentifiersMarshalling() throws JAXBException {
        final DefaultCitation citation = new DefaultCitation();
        citation.setISBN("MyISBN");
        citation.setISSN("MyISSN");
        final IdentifierMap map = citation.getIdentifierMap();
        citation.setIdentifiers(Arrays.asList(
                new DefaultIdentifier(Citations.OGC,  "MyOGC"),
                new DefaultIdentifier(Citations.EPSG, "MyEPSG")));
        assertNull(map.put(IdentifierSpace.ID, "MyID"));
        assertEquals("{OGC=“MyOGC”, EPSG=“MyEPSG”, gml:id=“MyID”, ISBN=“MyISBN”, ISSN=“MyISSN”}", map.toString());
        final String xml = XML.marshal(citation);
        /*
         * We don't compare the full XML tree, since this is a bit tedious.
         * Just ensures that "gmd:id" is an attribute (not an element) and
         * that ISBN is declared has its own element.
         */
        String previous = null;
        int foundID=0, foundOGC=0, foundEPSG=0, foundISBN=0, foundISSN=0;
        for (String line : Strings.getLinesFromMultilines(xml)) {
            line = line.trim();
            if (line.contains("MyID"))   {foundID++;}
            if (line.contains("MyOGC"))  {assertEquals("Wrong parent element.", "<gmd:code>", previous); foundOGC++;}
            if (line.contains("MyEPSG")) {assertEquals("Wrong parent element.", "<gmd:code>", previous); foundEPSG++;}
            if (line.contains("MyISBN")) {assertEquals("Wrong parent element.", "<gmd:ISBN>", previous); foundISBN++;}
            if (line.contains("MyISSN")) {assertEquals("Wrong parent element.", "<gmd:ISSN>", previous); foundISSN++;}
            previous = line;
        }
        assertEquals("MyID",   0, foundID);
        assertEquals("MyOGC",  1, foundOGC);
        assertEquals("MyEPSG", 1, foundEPSG);
        assertEquals("MyISBN", 1, foundISBN);
        assertEquals("MyISSN", 1, foundISSN);
    }

    /**
     * Ensures that a mandatory element is marshaled even if empty.
     *
     * @throws JAXBException Should never happen.
     *
     * @todo Disabled for now, because it doesn't seem to work.
     */
    @Test
    @Ignore
    public void testMandatoryPropertyMarshaling() throws JAXBException {
        final DefaultCitation citation = new DefaultCitation();
        citation.getDates().add(new DefaultCitationDate());
        String xml = XML.marshal(citation);
        assertTrue(xml, xml.contains("<gmd:date>"));

        citation.setDates(null);
        xml = XML.marshal(citation);
        assertTrue(xml, xml.contains("<gmd:date/>"));
    }
}
