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

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBException;

import org.opengis.metadata.citation.Series;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.test.TestBase;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.metadata.iso.citation.DefaultSeries;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.util.LenientComparable;

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
        assertEquals("title", "A title", citation.getTitle().toString());
        final Series series = citation.getSeries();
        assertInstanceOf("Should have instantiated a proxy.", IdentifiedObject.class, series);
        assertEquals("href", "org:dummy", ((IdentifiedObject) series).getXLink().getHRef().toString());
        assertEquals("Series[{xlink=“XLink[type=\"simple\", href=\"org:dummy\"]”}]", series.toString());
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

    /**
     * Tests equality between objects of different class.
     *
     * @throws URISyntaxException Should never happen.
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testEquals() throws URISyntaxException, JAXBException {
        final XLink link = new XLink();
        link.setType(XLink.Type.SIMPLE);
        link.setHRef(new URI("org:dummy"));
        final DefaultSeries series = new DefaultSeries();
        if (false) {
            // Used only for manual test of XML marshalling.
            series.setName(new SimpleInternationalString("A name"));
        }
        series.setXLink(link);
        final DefaultCitation citation = new DefaultCitation();
        citation.setTitle(new SimpleInternationalString("A title"));
        citation.setSeries(series);
        String xml = XML.marshal(citation);
        assertEquals(xml.length() - "<gmd:CI_Series/>".length(),
                 (xml = xml.replace("<gmd:CI_Series/>", "")).length());
        /*
         * Now test equality with a new citation object in which the Series instance
         * is a proxy rather than a {@link DefaultSeries} instance.
         */
        final DefaultCitation parsed = (DefaultCitation) XML.unmarshal(xml);
        final LenientComparable proxy = (LenientComparable) parsed.getSeries();
        assertFalse(proxy instanceof DefaultSeries);
        assertFalse("Test equality using Proxy.equals(Object)",   proxy.equals(series));
        assertFalse("Test equality using AbstractMetadata",       series.equals(proxy));
        assertTrue ("Test equality using Proxy.equals(Object)",   proxy.equals(series,    ComparisonMode.BY_CONTRACT));
        assertTrue ("Test equality using AbstractMetadata",       series.equals(proxy,    ComparisonMode.BY_CONTRACT));
        assertTrue ("Test equality using Proxy.equals(Object)",   parsed.equals(citation, ComparisonMode.BY_CONTRACT));
        assertTrue ("Test equality using AbstractMetadata",       citation.equals(parsed, ComparisonMode.BY_CONTRACT));
        assertFalse("Those objects are not expected to be equal", proxy.equals(citation,  ComparisonMode.BY_CONTRACT));
        assertFalse("Those objects are not expected to be equal", series.equals(parsed,   ComparisonMode.BY_CONTRACT));
        /*
         * Using a non-null name, the series should not anymore be equal to the proxy.
         */
        series.setName(new SimpleInternationalString("A name"));
        assertFalse(proxy.equals(series, ComparisonMode.BY_CONTRACT));
        assertFalse(series.equals(proxy, ComparisonMode.BY_CONTRACT));
        series.setName(null);
        assertTrue(proxy.equals(series,  ComparisonMode.BY_CONTRACT));
        assertTrue(series.equals(proxy,  ComparisonMode.BY_CONTRACT));
    }
}
