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

import java.util.Arrays;
import java.util.Locale;
import java.io.StringWriter;
import java.util.Collections;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;

import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.identification.TopicCategory;

import org.apache.sis.xml.XML;
import org.apache.sis.xml.Namespaces;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.util.CharSequences;
import org.geotoolkit.test.LocaleDependantTestBase;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the XML marshalling of {@code CodeList}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.19
 *
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-121">GEOTK-121</a>
 *
 * @since 3.17
 */
public final strictfp class CodeListMarshallingTest extends LocaleDependantTestBase {
    /**
     * Returns a XML string to use for testing purpose.
     *
     * @param baseURL The base URL of XML schemas.
     */
    private static String getResponsiblePartyXML(final String baseURL) {
        return "<gmd:CI_ResponsibleParty xmlns:gmd=\"" + Namespaces.GMD + "\">\n" +
               "  <gmd:role>\n" +
               "    <gmd:CI_RoleCode codeList=\"" + baseURL + "resources/Codelist/gmxCodelists.xml#CI_RoleCode\"" +
                    " codeListValue=\"principalInvestigator\">" + "Principal investigator</gmd:CI_RoleCode>\n" +
               "  </gmd:role>\n" +
               "</gmd:CI_ResponsibleParty>";
    }

    /**
     * Returns a XML string to use for testing purpose.
     *
     * @param baseURL The base URL of XML schemas.
     */
    private static String getCitationXML(final String baseURL, final String language, final String value) {
        return "<gmd:CI_Date xmlns:gmd=\"" + Namespaces.GMD + "\">\n" +
               "  <gmd:dateType>\n" +
               "    <gmd:CI_DateTypeCode codeList=\"" + baseURL + "resources/Codelist/gmxCodelists.xml#CI_DateTypeCode\"" +
                    " codeListValue=\"creation\" codeSpace=\"" + language + "\">" + value + "</gmd:CI_DateTypeCode>\n" +
               "  </gmd:dateType>\n" +
               "</gmd:CI_Date>";
    }

    /**
     * Marshals the given object using the given marshaller.
     */
    private static String marshal(final Marshaller marshaller, final Object object) throws JAXBException {
        final StringWriter output = new StringWriter();
        marshaller.marshal(object, output);
        return output.toString();
    }

    /**
     * Tests marshalling using the default URL.
     *
     * @throws JAXBException If an error occurred while marshalling the XML.
     */
    @Test
    public void testDefaultURL() throws JAXBException {
        final String expected = getResponsiblePartyXML("http://schemas.opengis.net/iso/19139/20070417/");
        final ResponsibleParty rp = (ResponsibleParty) XML.unmarshal(expected);
        assertEquals(Role.PRINCIPAL_INVESTIGATOR, rp.getRole());
        /*
         * Use the convenience method in order to avoid the effort of creating
         * our own MarshallerPool.
         */
        final String actual = XML.marshal(rp);
        assertDomEquals(expected, actual, "xmlns:*");
    }

    /**
     * Tests marshalling using the ISO URL.
     *
     * @throws JAXBException If an error occurred while marshalling the XML.
     */
    @Test
    public void testISO_URL() throws JAXBException {
        final String expected = getResponsiblePartyXML("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/");
        final ResponsibleParty rp = (ResponsibleParty) XML.unmarshal(expected);
        assertEquals(Role.PRINCIPAL_INVESTIGATOR, rp.getRole());
        /*
         * We have to create a MarshallerPool in order to apply the desired configuration.
         */
        final MarshallerPool pool = new MarshallerPool(null);
        final Marshaller marshaller = pool.acquireMarshaller();
        marshaller.setProperty(XML.SCHEMAS, Collections.singletonMap("gmd",
                "http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas")); // Intentionally omit trailing '/'.
        final String actual = marshal(marshaller, rp);
        pool.recycle(marshaller);
        assertDomEquals(expected, actual, "xmlns:*");
    }

    /**
     * Tests a code list localization.
     *
     * @throws JAXBException If an error occurred while marshalling the XML.
     */
    @Test
    public void testLocalization() throws JAXBException {
        final MarshallerPool pool = new MarshallerPool(null);
        final Marshaller marshaller = pool.acquireMarshaller();
        /*
         * First, test using the French locale.
         */
        marshaller.setProperty(XML.LOCALE, Locale.FRENCH);
        String expected = getCitationXML("http://schemas.opengis.net/iso/19139/20070417/", "fra", "Création");
        CitationDate ci = (CitationDate) XML.unmarshal(expected);
        assertEquals(DateType.CREATION, ci.getDateType());
        String actual = marshal(marshaller, ci);
        assertDomEquals(expected, actual, "xmlns:*");
        /*
         * Tests again using the Englisg locale.
         */
        marshaller.setProperty(XML.LOCALE, Locale.ENGLISH);
        expected = getCitationXML("http://schemas.opengis.net/iso/19139/20070417/", "eng", "Creation");
        ci = (CitationDate) XML.unmarshal(expected);
        assertEquals(DateType.CREATION, ci.getDateType());
        actual = marshal(marshaller, ci);
        assertDomEquals(expected, actual, "xmlns:*");

        pool.recycle(marshaller);
    }

    /**
     * Tests marshalling of a code list which is not in the list of standard codes.
     *
     * @throws JAXBException If an error occurred while marshalling the XML.
     */
    @Test
    public void testExtraCodes() throws JAXBException {
        final MarshallerPool pool = new MarshallerPool(null);
        final Marshaller marshaller = pool.acquireMarshaller();
        final DefaultDataIdentification id = new DefaultDataIdentification();
        id.setTopicCategories(Arrays.asList(
                TopicCategory.valueOf("oceans"), // New code
                TopicCategory.valueOf("OCEANS"), // Existing code with UML id="oceans"
                TopicCategory.valueOf("test"))); // New code

        final String xml = marshal(marshaller, id);
        pool.recycle(marshaller);

        // "OCEANS" is marshalled as "oceans" because is contains a UML id, which is lower-case.
        assertEquals(2, CharSequences.count(xml, "<gmd:MD_TopicCategoryCode>oceans</gmd:MD_TopicCategoryCode>"));
        assertEquals(0, CharSequences.count(xml, "<gmd:MD_TopicCategoryCode>OCEANS</gmd:MD_TopicCategoryCode>"));
        assertEquals(1, CharSequences.count(xml, "<gmd:MD_TopicCategoryCode>test</gmd:MD_TopicCategoryCode>"));
    }
}
