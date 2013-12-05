/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import javax.measure.unit.SI;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;

import org.apache.sis.xml.XML;
import org.apache.sis.xml.Namespaces;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.internal.jaxb.referencing.SecondDefiningParameter;

import org.junit.*;
import static org.apache.sis.test.Assert.*;


/**
 * Tests the XML marshalling/unmarshalling of a few objects related to datum.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 */
public final strictfp class DatumMarshallingTest {
    /**
     * The pool to use for fetching marshallers and ummarshallers.
     */
    private static MarshallerPool pool;

    /**
     * Creates the pool of marshallers/unmarshallers. It is created only once
     * before the first test, and reused for every tests in this class.
     *
     * @throws JAXBException If an error occurred while creating the pool.
     */
    @BeforeClass
    public static void createPool() throws JAXBException {
        pool = new MarshallerPool(JAXBContext.newInstance(SecondDefiningParameter.class),
                Collections.singletonMap(XML.DEFAULT_NAMESPACE, Namespaces.GMD));
    }

    /**
     * Releases the pool of marshallers/unmarshallers after every tests have been completed.
     */
    @AfterClass
    public static void disposePool() {
        pool = null;
    }

    /**
     * Generates a XML tree using the annotations on the {@link SecondDefiningParameter} class,
     * and writes it in a temporary buffer.
     *
     * @throws JAXBException If an error occurred during the marshalling process.
     * @throws IOException Should never happen since we are writing to a buffer.
     */
    @Test
    public void testMarshalling() throws JAXBException, IOException {
        final SecondDefiningParameter p = new SecondDefiningParameter(DefaultEllipsoid.SPHERE, false);
        final Marshaller marshaller = pool.acquireMarshaller();
        final String xml;
        try (StringWriter writer = new StringWriter()) {
            marshaller.marshal(p, writer);
            pool.recycle(marshaller);
            xml = writer.toString();
        }
        assertXmlEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<gml:SecondDefiningParameter xmlns:gml=\"http://www.opengis.net/gml/3.2\">\n" +
            "  <gml:semiMinorAxis uom=\"urn:ogc:def:uom:EPSG::9001\">6371000.0</gml:semiMinorAxis>\n" +
            "</gml:SecondDefiningParameter>", xml, "xmlns:*", "xsi:schemaLocation");
    }

    /**
     * Creates a {@link SecondDefiningParameter} from a XML tree.
     *
     * @throws JAXBException If an error occurred during the unmarshalling process.
     * @throws IOException Should never happen since we are reading from a buffer.
     */
    @Test
    public void testUnmarshalling() throws JAXBException, IOException {
        final String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<gml:SecondDefiningParameter xmlns:gml=\"http://www.opengis.net/gml/3.2\">\n" +
            "  <gml:semiMinorAxis uom=\"urn:ogc:def:uom:EPSG::9001\">6371000.0</gml:semiMinorAxis>\n" +
            "</gml:SecondDefiningParameter>";
        final Object object;
        try (StringReader reader = new StringReader(xml)) {
            final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
            object = unmarshaller.unmarshal(reader);
            pool.recycle(unmarshaller);
        }
        assertTrue(object instanceof SecondDefiningParameter);
        final SecondDefiningParameter sdp = (SecondDefiningParameter) object;
        assertEquals(6371000.0, sdp.measure.value, 0);
        assertEquals(SI.METRE,  sdp.measure.unit);
    }
}
