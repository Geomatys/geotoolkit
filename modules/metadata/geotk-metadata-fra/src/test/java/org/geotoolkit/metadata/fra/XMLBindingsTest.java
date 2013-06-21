/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.fra;

import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.XML;
import org.geotoolkit.test.TestData;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.junit.*;

import static org.apache.sis.test.Assert.*;
import static org.apache.sis.test.TestUtilities.getSingleton;


/**
 * Testing class for JAXB annotations on a metadata file with French profile.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.17
 *
 * @since 3.00
 */
public final strictfp class XMLBindingsTest {
    /**
     * An XML file representing a reference system tree.
     */
    private static final String RESOURCE_FILE = "DirectReferenceSystem.xml";

    /**
     * Ensures that the marshalling process of a {@link DefaultMetadata} produces
     * an XML document which complies with the one expected.
     *
     * @throws IOException if an error occurred while reading the resource file.
     * @throws JAXBException if the marshalling process fails.
     */
    @Test
    public void marshallingTest() throws IOException, JAXBException {
        final DefaultMetadata metadata = new DefaultMetadata();
        final FRA_DirectReferenceSystem refSys = new FRA_DirectReferenceSystem(
                new DefaultCitation(getSingleton(Citations.EPSG.getCitedResponsibleParties())), null, "4326");
        metadata.setReferenceSystemInfo(Arrays.asList(refSys));

        String expected = TestData.readText(this, RESOURCE_FILE);
        String actual = XML.marshal(metadata);
        assertXmlEquals(expected, actual, "xmlns:*", "xsi:schemaLocation");
    }

    /**
     * Ensures that the unmarshalling process of a {@link DefaultMetadata} stored in an XML
     * document produces an object containing all the information.
     *
     * @throws JAXBException if the unmarshalling process fails.
     * @throws IOException if an error occurred while reading the resource file.
     */
    @Test
    public void unmarshallingTest() throws JAXBException, IOException {
        final DefaultMetadata result;
        try (InputStream in = TestData.openStream(this, RESOURCE_FILE)) {
            result = (DefaultMetadata) XML.unmarshal(in);
        }
        final FRA_DirectReferenceSystem refSys = new FRA_DirectReferenceSystem(
                new DefaultCitation(getSingleton(Citations.EPSG.getCitedResponsibleParties())), null, "4326");

        final DefaultMetadata expected = new DefaultMetadata();
        expected.setReferenceSystemInfo(Arrays.asList(refSys));

        if (false) { // TODO: disabled for now
            assertEquals(expected, result);
        }
    }
}
