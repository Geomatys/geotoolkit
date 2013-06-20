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
package org.geotoolkit.xml;

import java.util.UUID;
import java.net.URI;
import java.net.URISyntaxException;
import java.lang.reflect.Proxy;
import javax.xml.bind.JAXBException;

import org.opengis.metadata.citation.Series;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.test.TestBase;
import org.apache.sis.xml.XLink;
import org.apache.sis.xml.IdentifierSpace;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.LenientComparable;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.metadata.iso.citation.DefaultSeries;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.internal.jaxb.gco.ObjectIdentification;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the XML marshalling of object having {@code xlink} or {@code uuid} attributes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-165">GEOTK-165</a>
 *
 * @since 3.18
 */
public final strictfp class ObjectReferenceMarshallingTest extends TestBase {
    /**
     * Tests a simple case using {@code xlink} attribute.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testSimpleXLink() throws JAXBException {
        checkXLink(false,
            "<gmd:CI_Citation xmlns:gmd=\""   + Namespaces.GMD + '"' +
                            " xmlns:gco=\""   + Namespaces.GCO + '"' +
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
            "</gmd:CI_Citation>");
    }

    /**
     * Unmarshall the given XML, checks its property, marshall it and compare DOM.
     */
    private static void checkXLink(final boolean isProxy, final String xml) throws JAXBException {
        final Citation citation = (Citation) XML.unmarshal(xml);
        assertEquals("title",  "A title", citation.getTitle().toString());

        final Series series = citation.getSeries();
        assertInstanceOf("Expected IdentifiedObject", IdentifiedObject.class, series);
        assertEquals("isProxy", isProxy, Proxy.isProxyClass(series.getClass()));
        if (isProxy) {
            assertNull("series", series.getName());
            assertEquals("Series[{xlink=XLink[type=\"simple\", href=\"org:dummy\"]}]", series.toString());
        } else {
            assertEquals("series", "A series", series.getName().toString());
        }
        final IdentifierMap map = ((IdentifiedObject) series).getIdentifierMap();
        assertEquals("href",   "org:dummy", map.get(IdentifierSpace.HREF));
        assertNull  ("uuid",                map.get(IdentifierSpace.UUID));

        final String actual = XML.marshal(citation);
        assertDomEquals(xml, actual, "xmlns:*");
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
    public void testEmptyXLink() throws JAXBException {
        checkXLink(true,
            "<gmd:CI_Citation xmlns:gmd=\"" + Namespaces.GMD + '"' +
                            " xmlns:gco=\"" + Namespaces.GCO + '"' +
                            " xmlns:xlink=\"" + Namespaces.XLINK + "\">\n" +
            "  <gmd:title>\n" +
            "    <gco:CharacterString>A title</gco:CharacterString>\n" +
            "  </gmd:title>\n" +
            "  <gmd:series xlink:href=\"org:dummy\"/>\n" +
            "</gmd:CI_Citation>");
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
        assertNull(series.getIdentifierMap().putSpecialized(IdentifierSpace.XLINK, link));
        final DefaultCitation citation = new DefaultCitation();
        citation.setTitle(new SimpleInternationalString("A title"));
        citation.setSeries(series);
        /*
         * The citation should be:
         *
         * <gmd:series xlink:href="org:dummy">
         *   <gmd:CI_Series/>
         * </gmd:series>
         *
         * Removes the <gmd:CI_Series/> and unmarshall. We should now have a
         * proxy for an empty Series rather than a DefaultSeries instance.
         */
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

    /**
     * Tests a case having a {@code uuid} attribute.
     *
     * @throws JAXBException Should never happen.
     *
     * @since 3.19
     */
    @Test
    public void testUUID() throws JAXBException {
        final String uuid = "f8f5fcb1-d57b-4013-b3a4-4eaa40df6dcf";
        String xml =
            "<gmd:CI_Citation xmlns:gmd=\""   + Namespaces.GMD + '"' +
                            " xmlns:gco=\""   + Namespaces.GCO + '"' +
                            " xmlns:xlink=\"" + Namespaces.XLINK + "\">\n" +
            "  <gmd:title>\n" +
            "    <gco:CharacterString>A title</gco:CharacterString>\n" +
            "  </gmd:title>\n" +
            "  <gmd:series>\n" +
            "    <gmd:CI_Series uuid=\"" + uuid + "\">\n" +
            "      <gmd:name>\n" +
            "        <gco:CharacterString>A series</gco:CharacterString>\n" +
            "      </gmd:name>\n" +
            "    </gmd:CI_Series>\n" +
            "  </gmd:series>\n" +
            "</gmd:CI_Citation>";

        final Citation citation = (Citation) XML.unmarshal(xml);
        final Series series = citation.getSeries();
        assertFalse("Unexpected proxy", Proxy.isProxyClass(series.getClass()));
        assertInstanceOf("Expected IdentifiedObject", IdentifiedObject.class, series);
        final IdentifierMap map = ((IdentifiedObject) series).getIdentifierMap();

        assertEquals("title",  "A title",   citation.getTitle().toString());
        assertEquals("series", "A series",  series.getName().toString());
        assertNull  ("href",                map.get(IdentifierSpace.HREF));
        assertEquals(uuid,   String.valueOf(map.get(IdentifierSpace.UUID)));
        assertSame("As a consequence of the XML unmarshalling, the series should "
                + "now be registered in our global object-UUID mapping.",
                series, ObjectIdentification.UUIDs.lookup(UUID.fromString(uuid)));

        final String actual = XML.marshal(citation);
        assertDomEquals(xml, actual, "xmlns:*");
        assertEquals(citation, XML.unmarshal(actual));
        /*
         * Tests again using a reference to the above series.
         */
        xml =
            "<gmd:CI_Citation xmlns:gmd=\""   + Namespaces.GMD + '"' +
                            " xmlns:gco=\""   + Namespaces.GCO + '"' +
                            " xmlns:xlink=\"" + Namespaces.XLINK + "\">\n" +
            "  <gmd:title>\n" +
            "    <gco:CharacterString>A title</gco:CharacterString>\n" +
            "  </gmd:title>\n" +
            "  <gmd:series uuidref=\"" + uuid + "\"/>\n" +
            "</gmd:CI_Citation>";

        final Citation fromUUID = (Citation) XML.unmarshal(xml);
        assertSame("The series should have been found in our global object-UUID map.", series, fromUUID.getSeries());
        assertEquals(citation, fromUUID);
    }
}
