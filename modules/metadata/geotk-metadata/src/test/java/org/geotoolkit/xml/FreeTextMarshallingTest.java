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

import java.util.Locale;
import javax.xml.bind.JAXBException;
import org.opengis.metadata.citation.Citation;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.apache.sis.xml.XML;
import org.apache.sis.xml.Namespaces;
import org.geotoolkit.test.TestBase;

import org.junit.*;
import static org.apache.sis.test.Assert.*;


/**
 * Tests the XML marshalling of {@code FreeText}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-107">GEOTK-107</a>
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-152">GEOTK-152</a>
 * @see MetadataMarshallingTest#testTextGroup()
 *
 * @since 3.17
 */
public final strictfp class FreeTextMarshallingTest extends TestBase {
    /**
     * Returns the expected string.
     */
    private DefaultInternationalString getExpectedI18N() {
        final DefaultInternationalString i18n = new DefaultInternationalString();
        i18n.add(Locale.ENGLISH, "OpenSource Project");
        i18n.add(Locale.FRENCH,  "Projet OpenSource");
        i18n.add(Locale.ITALIAN, "Progetto OpenSource");
        return i18n;
    }

    /**
     * Tests parsing of a free text in an ISO 19139-compliant way.
     * The free text is wrapped inside a citation for marshalling
     * purpose, but only the free text is actually tested.
     *
     * @throws JAXBException If the XML in this test can not be parsed by JAXB.
     */
    @Test
    public void testStandard() throws JAXBException {
        final String expected =
            "<gmd:CI_Citation xmlns:gmd=\"" + Namespaces.GMD + "\" xmlns:gco=\"" + Namespaces.GCO + "\" xmlns:xsi=\"" + Namespaces.XSI + "\">\n" +
            "  <gmd:title xsi:type=\"gmd:PT_FreeText_PropertyType\">\n" +
            "    <gco:CharacterString>OpenSource Project</gco:CharacterString>\n" +
            "    <gmd:PT_FreeText>\n" +
            "      <gmd:textGroup>\n" +
            "        <gmd:LocalisedCharacterString locale=\"#locale-eng\">OpenSource Project</gmd:LocalisedCharacterString>\n" +
            "      </gmd:textGroup>\n" +
            "      <gmd:textGroup>\n" +
            "        <gmd:LocalisedCharacterString locale=\"#locale-ita\">Progetto OpenSource</gmd:LocalisedCharacterString>\n" +
            "      </gmd:textGroup>\n" +
            "      <gmd:textGroup>\n" +
            "        <gmd:LocalisedCharacterString locale=\"#locale-fra\">Projet OpenSource</gmd:LocalisedCharacterString>\n" +
            "      </gmd:textGroup>\n" +
            "    </gmd:PT_FreeText>\n" +
            "  </gmd:title>\n" +
            "</gmd:CI_Citation>\n";

        final Citation citation = (Citation) XML.unmarshal(expected);
        assertEquals(getExpectedI18N(), citation.getTitle());
        final String actual = XML.marshal(citation);
        assertXmlEquals(expected, actual, "xmlns:*");
    }

    /**
     * Tests parsing of a free text in the legacy (pre-Geotk 3.17) format.
     * We continue to support this format for compatibility reason, but
     * also because it is more compact and closer to what we would expect
     * inside a {@code <textGroup>} node.
     *
     * @throws JAXBException If the XML in this test can not be parsed by JAXB.
     */
    @Test
    public void testLegacy() throws JAXBException {
        final String legacy =
            "<gmd:CI_Citation xmlns:gmd=\"" + Namespaces.GMD + "\" xmlns:gco=\"" + Namespaces.GCO + "\" xmlns:xsi=\"" + Namespaces.XSI + "\">\n" +
            "  <gmd:title xsi:type=\"gmd:PT_FreeText_PropertyType\">\n" +
            "    <gco:CharacterString>OpenSource Project</gco:CharacterString>\n" +
            "    <gmd:PT_FreeText>\n" +
            "      <gmd:textGroup>\n" +
            "        <gmd:LocalisedCharacterString locale=\"#locale-eng\">OpenSource Project</gmd:LocalisedCharacterString>\n" +
            "        <gmd:LocalisedCharacterString locale=\"#locale-ita\">Progetto OpenSource</gmd:LocalisedCharacterString>\n" +
            "        <gmd:LocalisedCharacterString locale=\"#locale-fra\">Projet OpenSource</gmd:LocalisedCharacterString>\n" +
            "      </gmd:textGroup>\n" +
            "    </gmd:PT_FreeText>\n" +
            "  </gmd:title>\n" +
            "</gmd:CI_Citation>\n";

        final Citation citation = (Citation) XML.unmarshal(legacy);
        assertEquals(getExpectedI18N(), citation.getTitle());
    }
}
