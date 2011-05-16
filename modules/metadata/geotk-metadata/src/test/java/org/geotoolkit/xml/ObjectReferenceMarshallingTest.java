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
package org.geotoolkit.xml;

import javax.xml.bind.JAXBException;
import org.opengis.metadata.citation.Series;
import org.opengis.metadata.citation.Citation;
import org.geotoolkit.test.TestBase;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the XML marshalling of object having {@code xlink} attributes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-165">GEOTK-165</a>
 *
 * @since 3.18
 */
public final class ObjectReferenceMarshallingTest extends TestBase {
    /**
     * Tests a simple case.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testSimple() throws JAXBException {
        final String expected =
            "<gmd:CI_Citation xmlns:gmd=\"" + Namespaces.GMD + '"' +
                            " xmlns:gco=\"" + Namespaces.GCO + '"' +
                            " xmlns:xlink=\"" + Namespaces.XLINK + "\">\n" +
            "  <gmd:title>\n" +
            "    <gco:CharacterString>A title</gco:CharacterString>\n" +
            "  </gmd:title>\n" +
            "  <gmd:series xlink:href=\"org:dummy\">\n" +
            "    <gmd:CI_Series>\n" +
            "      <gmd:name>\n" +
            "        <gco:CharacterString>A series</gco:CharacterString>\n" +
            "      </gmd:name>\n" +
            "    </gmd:CI_Series>\n" +
            "  </gmd:series>\n" +
            "</gmd:CI_Citation>";
        final Citation citation = (Citation) XML.unmarshal(expected);
        assertEquals("title",  "A title",   citation.getTitle().toString());
        assertEquals("series", "A series",  citation.getSeries().getName().toString());
        assertEquals("href",   "org:dummy", ((IdentifiedObject) citation.getSeries()).getXLink().getHRef().toString());

        final String actual = XML.marshal(citation);
        assertDomEquals(expected, actual, "xmlns:*");
        assertEquals(citation, XML.unmarshal(actual));
    }

    /**
     * The same test than {@link #testSimple}, except that the {@code <gmd:CI_Series>}
     * element is empty, thus forcing the creation of a new, empty, element for storing
     * the {@code xlink} information.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testEmptyCreation() throws JAXBException {
        final String expected =
            "<gmd:CI_Citation xmlns:gmd=\"" + Namespaces.GMD + '"' +
                            " xmlns:gco=\"" + Namespaces.GCO + '"' +
                            " xmlns:xlink=\"" + Namespaces.XLINK + "\">\n" +
            "  <gmd:title>\n" +
            "    <gco:CharacterString>A title</gco:CharacterString>\n" +
            "  </gmd:title>\n" +
            "  <gmd:series xlink:href=\"org:dummy\"/>\n" +
            "</gmd:CI_Citation>";
        final Citation citation = (Citation) XML.unmarshal(expected);
        assertEquals("title",  "A title", citation.getTitle().toString());
        final Series series = citation.getSeries();
        assertEquals("href",   "org:dummy", ((IdentifiedObject) series).getXLink().getHRef().toString());
        assertEquals("Series[ObjectReference[type=\"simple\", href=\"org:dummy\"]]", series.toString());
        assertNull("All attributes are expected to be null.", series.getName());
        try {
            ((IdentifiedObject) series).setXLink(null);
            fail("The proxy instance should be unmodifiable.");
        } catch (UnsupportedOperationException e) {
            // This is the expected exception.
            assertTrue(e.getMessage().contains("Series"));
        }
        /*
         * Tests marshalling.
         */
        final String actual = XML.marshal(citation);
        assertDomEquals(expected, actual, "xmlns:*");
        assertEquals(citation, XML.unmarshal(actual));
    }
}
