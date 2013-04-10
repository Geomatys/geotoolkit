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

import javax.xml.bind.JAXBException;
import org.opengis.metadata.citation.Series;
import org.opengis.metadata.citation.Citation;
import org.apache.sis.xml.NilObject;
import org.apache.sis.xml.NilReason;
import org.geotoolkit.test.TestBase;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the XML marshalling of object having {@code nilReason} attribute.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-149">GEOTK-149</a>
 *
 * @since 3.18
 */
public final strictfp class NilReasonMarshallingTest extends TestBase {
    /**
     * Tests a simple case for a missing data.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testMissing() throws JAXBException {
        final String expected =
            "<gmd:CI_Citation xmlns:gmd=\"" + Namespaces.GMD + '"' +
                            " xmlns:gco=\"" + Namespaces.GCO + '"' +
                            " xmlns:xlink=\"" + Namespaces.XLINK + "\">\n" +
            "  <gmd:title>\n" +
            "    <gco:CharacterString>A title</gco:CharacterString>\n" +
            "  </gmd:title>\n" +
            "  <gmd:series gco:nilReason=\"missing\"/>\n" +
            "</gmd:CI_Citation>";

        final Citation citation = (Citation) XML.unmarshal(expected);
        assertEquals("title", "A title", citation.getTitle().toString());

        final Series series = citation.getSeries();
        assertInstanceOf("Should have instantiated a proxy.", NilObject.class, series);

        final org.apache.sis.xml.NilReason reason = ((NilObject) series).getNilReason();
        assertSame("nilReason", NilReason.MISSING, reason);
        assertNull("NilReason.explanation", reason.getOtherExplanation());
        assertNull("NilReason.URI",         reason.getURI());

        assertEquals("Series[missing]", series.toString());
        assertNull("All attributes are expected to be null.", series.getName());

        final String actual = XML.marshal(citation);
        assertDomEquals(expected, actual, "xmlns:*");
        assertEquals(citation, XML.unmarshal(actual));
    }

    /**
     * Tests a case where the nil reason is specified by an other reason.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testOther() throws JAXBException {
        final String expected =
            "<gmd:CI_Citation xmlns:gmd=\"" + Namespaces.GMD + '"' +
                            " xmlns:gco=\"" + Namespaces.GCO + '"' +
                            " xmlns:xlink=\"" + Namespaces.XLINK + "\">\n" +
            "  <gmd:title>\n" +
            "    <gco:CharacterString>A title</gco:CharacterString>\n" +
            "  </gmd:title>\n" +
            "  <gmd:series gco:nilReason=\"other:myReason\"/>\n" +
            "</gmd:CI_Citation>";

        final Citation citation = (Citation) XML.unmarshal(expected);
        assertEquals("title", "A title", citation.getTitle().toString());

        final Series series = citation.getSeries();
        assertInstanceOf("Should have instantiated a proxy.", NilObject.class, series);

        final org.apache.sis.xml.NilReason reason = ((NilObject) series).getNilReason();
        assertEquals("NilReason.explanation", "myReason", reason.getOtherExplanation());
        assertNull("NilReason.URI", reason.getURI());

        assertEquals("Series[other:myReason]", series.toString());
        assertNull("All attributes are expected to be null.", series.getName());

        final String actual = XML.marshal(citation);
        assertDomEquals(expected, actual, "xmlns:*");
        assertEquals(citation, XML.unmarshal(actual));
    }

    /**
     * Tests a case where the nil reason is specified by a URI.
     *
     * @throws JAXBException Should never happen.
     */
    @Test
    public void testURI() throws JAXBException {
        final String expected =
            "<gmd:CI_Citation xmlns:gmd=\"" + Namespaces.GMD + '"' +
                            " xmlns:gco=\"" + Namespaces.GCO + '"' +
                            " xmlns:xlink=\"" + Namespaces.XLINK + "\">\n" +
            "  <gmd:title>\n" +
            "    <gco:CharacterString>A title</gco:CharacterString>\n" +
            "  </gmd:title>\n" +
            "  <gmd:series gco:nilReason=\"http://www.myreason.org\"/>\n" +
            "</gmd:CI_Citation>";

        final Citation citation = (Citation) XML.unmarshal(expected);
        assertEquals("title", "A title", citation.getTitle().toString());

        final Series series = citation.getSeries();
        assertInstanceOf("Should have instantiated a proxy.", NilObject.class, series);

        final org.apache.sis.xml.NilReason reason = ((NilObject) series).getNilReason();
        assertNull("NilReason.explanation", reason.getOtherExplanation());
        assertEquals("NilReason.URI", "http://www.myreason.org", String.valueOf(reason.getURI()));

        assertEquals("Series[http://www.myreason.org]", series.toString());
        assertNull("All attributes are expected to be null.", series.getName());

        final String actual = XML.marshal(citation);
        assertDomEquals(expected, actual, "xmlns:*");
        assertEquals(citation, XML.unmarshal(actual));
    }
}
