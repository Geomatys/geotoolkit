/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
import java.io.StringWriter;
import java.io.BufferedReader;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.test.TestData;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.metadata.iso.DefaultMetaData;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Testing class for JAXB annotations on a metadata file with french profile.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 */
public final class XMLBindingsTest {
    /**
     * Pool of marshallers/unmarshallers.
     */
    private static MarshallerPool pool;

    /**
     * Unmarshaller for a {@link DefaultMetaData} object.
     */
    private Unmarshaller unmarshaller;

    /**
     * Marshaller for a {@link DefaultMetaData} object.
     */
    private Marshaller marshaller;

    /**
     * An XML file representing a reference system tree.
     */
    private static final String RESOURCE_FILE = "DirectReferenceSystem.xml";

    /**
     * Instanciates the {@link MarshallerPool} with the classes whished.
     *
     * @throws JAXBException if the creation of the {@link MarshallerPool} fails.
     */
    @BeforeClass
    public static void setupPool() throws JAXBException {
        pool = new MarshallerPool("", DefaultMetaData.class);
    }

    /**
     * Releases some marshaller/unmarshaller that might have not already been returned to the
     * pool.
     */
    @After
    public void releaseMarshallers() {
        if (marshaller != null) {
            pool.release(marshaller);
            marshaller = null;
        }
        if (unmarshaller != null) {
            pool.release(unmarshaller);
            unmarshaller = null;
        }
    }

    /**
     * Skips the two first lines, because the xlmns are not always in the same order.
     */
    private static String skipHeader(final String xml) {
        return xml.substring(xml.indexOf('\n', xml.indexOf('\n') + 1) + 1);
    }

    /**
     * Ensures that the marshalling process of a {@link DefaultMetaData} produces
     * an XML document which complies with the one expected.
     *
     * @throws JAXBException if the marshalling process fails.
     * @throws IOException if an error occured while reading the resource file.
     */
    @Test
    public void marshallingTest() throws JAXBException, IOException {
        final DefaultMetaData metadata = new DefaultMetaData();
        final FRA_DirectReferenceSystem refSys = new FRA_DirectReferenceSystem(
                new DefaultCitation(DefaultResponsibleParty.EPSG), null, "4326");
        metadata.setReferenceSystemInfo(Arrays.asList(refSys));

        final StringWriter sw = new StringWriter();
        marshaller = pool.acquireMarshaller();
        marshaller.marshal(metadata, sw);

        final BufferedReader in = TestData.openReader(this, RESOURCE_FILE);
        final StringBuilder out = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            out.append(line).append('\n');
        }
        in.close();

        String expected = out.toString();
        String actual   = sw.toString();
        expected = skipHeader(expected);
        actual   = skipHeader(actual);

        assertMultilinesEquals(expected, actual);
    }

    /**
     * Ensures that the unmarshalling process of a {@link DefaultMetaData} stored in an XML
     * document produces an object containing all the information.
     *
     * @throws JAXBException if the unmarshalling process fails.
     * @throws IOException if an error occured while reading the resource file.
     */
    @Test
    public void UnmarshallingTest() throws JAXBException, IOException {
        final InputStream in = TestData.openStream(this, RESOURCE_FILE);
        unmarshaller = pool.acquireUnmarshaller();
        final DefaultMetaData result = (DefaultMetaData) unmarshaller.unmarshal(in);
        in.close();

        final FRA_DirectReferenceSystem refSys = new FRA_DirectReferenceSystem(
                new DefaultCitation(DefaultResponsibleParty.EPSG), null, "4326");

        final DefaultMetaData expected = new DefaultMetaData();
        expected.setReferenceSystemInfo(Arrays.asList(refSys));

        if (false) { // TODO: disabled for now
            assertEquals(expected, result);
        }
    }
}
