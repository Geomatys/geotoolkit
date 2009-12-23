/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.referencing.datum;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import javax.measure.unit.SI;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.internal.jaxb.referencing.datum.SecondDefiningParameter;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests the XML marshalling/unmarshalling of a few objects related to datum.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 */
public final class DatumMarshallingTest {
    /**
     * The pool to use for fetching marshallers and ummarshallers.
     */
    private static MarshallerPool pool;

    /**
     * Creates the pool of marshallers/unmarshallers. It is created only once
     * before the first test, and reused for every tests in this class.
     *
     * @throws JAXBException If an error occured while creating the pool.
     */
    @BeforeClass
    public static void createPool() throws JAXBException {
        pool = new MarshallerPool(Collections.singletonMap(
                MarshallerPool.ROOT_NAMESPACE_KEY, Namespaces.GMD), SecondDefiningParameter.class);
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
     * @throws JAXBException If an error occured during the marshalling process.
     * @throws IOException Should never happen since we are writing to a buffer.
     */
    @Test
    public void testMarshalling() throws JAXBException, IOException {
        final SecondDefiningParameter p = new SecondDefiningParameter(DefaultEllipsoid.SPHERE, false);
        final Marshaller marshaller = pool.acquireMarshaller();
        final StringWriter writer = new StringWriter();
        marshaller.marshal(p, writer);
        pool.release(marshaller);
        writer.close();
        final String xml = writer.toString();
        assertMultilinesEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<gml:SecondDefiningParameter xmlns:gml=\"http://www.opengis.net/gml\">\n" +
            "  <gml:semiMinorAxis uom=\"../uom/gmxUom.xsd#xpointer(//*[@gml:id='m'])\">6371000.0</gml:semiMinorAxis>\n" +
            "</gml:SecondDefiningParameter>", xml);
    }

    /**
     * Creates a {@link SecondDefiningParameter} from a XML tree.
     *
     * @throws JAXBException If an error occured during the unmarshalling process.
     * @throws IOException Should never happen since we are reading from a buffer.
     */
    @Test
    public void testUnmarshalling() throws JAXBException, IOException {
        final String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<gml:SecondDefiningParameter xmlns:gml=\"http://www.opengis.net/gml\">\n" +
            "  <gml:semiMinorAxis uom=\"../uom/gmxUom.xsd#xpointer(//*[@gml:id='m'])\">6371000.0</gml:semiMinorAxis>\n" +
            "</gml:SecondDefiningParameter>";
        final StringReader reader = new StringReader(xml);
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final Object object = unmarshaller.unmarshal(reader);
        pool.release(unmarshaller);
        reader.close();
        assertTrue(object instanceof SecondDefiningParameter);
        final SecondDefiningParameter sdp = (SecondDefiningParameter) object;
        assertEquals(6371000.0, sdp.measure.value, 0);
        assertEquals(SI.METRE,  sdp.measure.unit);
    }
}
