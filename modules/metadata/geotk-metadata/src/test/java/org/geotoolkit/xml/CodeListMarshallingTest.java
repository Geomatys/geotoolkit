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

import java.io.StringWriter;
import java.util.Collections;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.ResponsibleParty;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the XML marshalling of {@code CodeList}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-121">GEOTK-121</a>
 *
 * @since 3.17
 */
public final class CodeListMarshallingTest {
    /**
     * Returns a XML string to use for testing purpose.
     *
     * @param baseURL The base URL of XML schemas.
     */
    private static String getXML(final String baseURL) {
        return "<gmd:CI_ResponsibleParty xmlns:gmd=\"" + Namespaces.GMD + "\">\n" +
               "  <gmd:role>\n" +
               "    <gmd:CI_RoleCode codeList=\"" + baseURL + "resources/Codelist/gmxCodelists.xml#CI_RoleCode\" codeListValue=\"principalInvestigator\"/>\n" +
               "  </gmd:role>\n" +
               "</gmd:CI_ResponsibleParty>";
    }

    /**
     * Tests marshalling using the default URL.
     *
     * @throws JAXBException If an error occurred while unmarshalling the XML.
     */
    @Test
    public void testDefault() throws JAXBException {
        final String expected = getXML("http://schemas.opengis.net/iso/19139/20070417/");
        final ResponsibleParty rp = (ResponsibleParty) XML.unmarshal(expected);
        assertEquals(Role.PRINCIPAL_INVESTIGATOR, rp.getRole());
        /*
         * Use the convenience method in order to avoid the effort of creating
         * our own MarshallerPool.
         */
        final String actual = XML.marshal(rp);
        assertDomEquals(expected, actual, "xmlns:*", "xsi:schemaLocation");
    }

    /**
     * Tests marshalling using the ISO URL.
     *
     * @throws JAXBException If an error occurred while unmarshalling the XML.
     */
    @Test
    public void testISO() throws JAXBException {
        final String expected = getXML("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/");
        final ResponsibleParty rp = (ResponsibleParty) XML.unmarshal(expected);
        assertEquals(Role.PRINCIPAL_INVESTIGATOR, rp.getRole());
        /*
         * We have to create a MarshallerPool in order to apply the desired configuration.
         */
        final StringWriter output = new StringWriter();
        final MarshallerPool pool = new MarshallerPool(MarshallerPool.defaultClassesToBeBound());
        final Marshaller marshaller = pool.acquireMarshaller();
        marshaller.setProperty(XML.SCHEMAS, Collections.singletonMap("gmd",
                "http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas")); // Intentionally omit trailing '/'.
        marshaller.marshal(rp, output);
        pool.release(marshaller);
        final String actual = output.toString();
        assertDomEquals(expected, actual, "xmlns:*", "xsi:schemaLocation");
    }
}
