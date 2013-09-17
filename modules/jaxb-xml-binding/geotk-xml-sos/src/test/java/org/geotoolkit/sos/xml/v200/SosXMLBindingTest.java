/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sos.xml.v200;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.test.XMLComparator;
import org.geotoolkit.sos.xml.SOSMarshallerPool;

import org.apache.sis.xml.MarshallerPool;

//Junit dependencies
import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class SosXMLBindingTest {

    private static MarshallerPool pool;
    private Marshaller   marshaller;
    private Unmarshaller unmarshaller;
    private static ObjectFactory FACTORY = new ObjectFactory();

    @BeforeClass
    public static void setUpClass() throws Exception {
        pool = SOSMarshallerPool.getInstance();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws JAXBException {
        marshaller = pool.acquireMarshaller();
        unmarshaller = pool.acquireUnmarshaller();
    }

    @After
    public void tearDown() {
        if (marshaller != null) {
            pool.recycle(marshaller);
        }
        if (unmarshaller != null) {
            pool.recycle(unmarshaller);
        }
    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void marshallingTest() throws Exception {

        final InsertObservationResponseType response = new InsertObservationResponseType(Arrays.asList("new-id"));

        StringWriter sw = new StringWriter();
        marshaller.marshal(FACTORY.createInsertObservationResponse(response), sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);

        String expResult = "<sos:InsertObservationResponse xmlns:sos=\"http://www.opengis.net/sos/2.0\">\n" +
                           "  <sos:observation>new-id</sos:observation>\n" +
                           "</sos:InsertObservationResponse>\n" ;
        final ExtendedDOMComparator comparator = new ExtendedDOMComparator(expResult, result);
        comparator.compare();

    }

    @Test
    public void umarshallingTest() throws Exception {
       final InsertObservationResponseType expResult = new InsertObservationResponseType(Arrays.asList("new-id"));

        String xml = "<sos:InsertObservationResponse xmlns:sos=\"http://www.opengis.net/sos/2.0\">\n" +
                     "  <sos:observation>new-id</sos:observation>\n" +
                     "</sos:InsertObservationResponse>\n" ;

        Object result = unmarshaller.unmarshal(new StringReader(xml));

        if (result instanceof JAXBElement) {
            result = ((JAXBElement)result).getValue();
        }
        assertEquals(expResult, result);

    }

    /**
     * TODO move to utilities modules
     */
    public static class ExtendedDOMComparator extends XMLComparator {

        public ExtendedDOMComparator(final Object expected, final Object actual) throws IOException, ParserConfigurationException, SAXException {
            super(expected, actual);
            ignoredAttributes.add("http://www.w3.org/2000/xmlns:*");
        }

        /**
         * Compares the names and namespaces of the given node.
         *
         * Exclude the prefix from comparison
         *
         * @param expected The node having the expected name and namespace.
         * @param actual The node to compare.
         */
        @Override
        protected void compareNames(final Node expected, final Node actual) {
            assertPropertyEquals("namespace", expected.getNamespaceURI(), actual.getNamespaceURI(), expected, actual);
            String expectedNodeName = expected.getNodeName();
            int i = expectedNodeName.indexOf(':');
            if (i != -1) {
                expectedNodeName = expectedNodeName.substring(i + 1);
            }
            String actualNodeName   = actual.getNodeName();
            i = actualNodeName.indexOf(':');
            if (i != -1) {
                actualNodeName = actualNodeName.substring(i + 1);
            }
            assertPropertyEquals("name",      expectedNodeName,     actualNodeName,     expected, actual);
        }

    }
}
