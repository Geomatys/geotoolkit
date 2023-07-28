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
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.test.xml.DocumentComparator;
import org.geotoolkit.sos.xml.SOSMarshallerPool;

import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.gml.xml.v321.DirectPositionType;
import org.geotoolkit.gml.xml.v321.EnvelopeType;
import org.geotoolkit.gml.xml.v321.TimePeriodType;

//Junit dependencies
import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
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
        result = result.substring(result.indexOf("?>") + 2).trim();

        String expResult = """
                           <sos:InsertObservationResponse xmlns:sos="http://www.opengis.net/sos/2.0">
                             <sos:observation>new-id</sos:observation>
                           </sos:InsertObservationResponse>
                           """ ;
        ExtendedDOMComparator comparator = new ExtendedDOMComparator(expResult, result);
        comparator.compare();

        final GetObservationType go = new GetObservationType();
        go.getExtension().add("responseMode: out-of-band");

        sw = new StringWriter();
        marshaller.marshal(go, sw);

        result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 2).trim();

        expResult = """
                    <sos:GetObservation xmlns:sos="http://www.opengis.net/sos/2.0" xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema">
                      <swes:extension xsi:type="xs:string">responseMode: out-of-band</swes:extension>
                    </sos:GetObservation>
                    """;
        comparator = new ExtendedDOMComparator(expResult, result);
        comparator.compare();

        ObservationOfferingType offeringType = new ObservationOfferingType("off-1",
                                                                           "off-1",
                                                                           "offering:1",
                                                                           "some description",
                                                                            new EnvelopeType(new DirectPositionType(1.0, 2.0), new DirectPositionType(1.0, 2.0), "EPSG:4326"),
                                                                            new TimePeriodType("t-id", "2000-01-01T00:00:00",  "2001-01-01T00:00:00"),
                                                                            "proc_001",
                                                                            Arrays.asList("obsProp1", "obsProp2"),
                                                                            Arrays.asList("feat1", "feat2"),
                                                                            Arrays.asList("http://www.opengis.net/om/2.0"),
                                                                            Arrays.asList("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation",
                                                                                          "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement"),
                                                                            Arrays.asList("http://www.opengis.net/sensorML/1.0.0",
                                                                                          "http://www.opengis.net/sensorML/1.0.1"));
        sw = new StringWriter();
        marshaller.marshal(offeringType, sw);

        result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 2).trim();

        expResult = """
                    <sos:ObservationOffering swes:id="off-1" xmlns:sos="http://www.opengis.net/sos/2.0" xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml/3.2">
                        <swes:description>some description</swes:description>
                        <swes:identifier>off-1</swes:identifier>
                        <swes:name>offering:1</swes:name>
                        <swes:procedure>proc_001</swes:procedure>
                        <swes:procedureDescriptionFormat>http://www.opengis.net/sensorML/1.0.0</swes:procedureDescriptionFormat>
                        <swes:procedureDescriptionFormat>http://www.opengis.net/sensorML/1.0.1</swes:procedureDescriptionFormat>
                        <swes:observableProperty>obsProp1</swes:observableProperty>
                        <swes:observableProperty>obsProp2</swes:observableProperty>
                        <swes:relatedFeature>
                            <swes:FeatureRelationship>
                                <swes:target xlink:href="feat1"/>
                            </swes:FeatureRelationship>
                        </swes:relatedFeature>
                        <swes:relatedFeature>
                            <swes:FeatureRelationship>
                                <swes:target xlink:href="feat2"/>
                            </swes:FeatureRelationship>
                        </swes:relatedFeature>
                        <sos:phenomenonTime>
                            <gml:TimePeriod gml:id="time-off-1">
                                <gml:beginPosition>2000-01-01T00:00:00</gml:beginPosition>
                                <gml:endPosition>2001-01-01T00:00:00</gml:endPosition>
                            </gml:TimePeriod>
                        </sos:phenomenonTime>
                        <sos:responseFormat>http://www.opengis.net/om/2.0</sos:responseFormat>
                        <sos:observationType>http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation</sos:observationType>
                        <sos:observationType>http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement</sos:observationType>
                    </sos:ObservationOffering>
                    """;

        comparator = new ExtendedDOMComparator(expResult, result);
        comparator.compare();
    }

    @Test
    public void umarshallingTest() throws Exception {
       final InsertObservationResponseType expResult = new InsertObservationResponseType(Arrays.asList("new-id"));

        String xml = """
                     <sos:InsertObservationResponse xmlns:sos="http://www.opengis.net/sos/2.0">
                       <sos:observation>new-id</sos:observation>
                     </sos:InsertObservationResponse>
                     """ ;

        Object result = unmarshaller.unmarshal(new StringReader(xml));

        if (result instanceof JAXBElement) {
            result = ((JAXBElement)result).getValue();
        }
        assertEquals(expResult, result);

    }

    /**
     * TODO move to utilities modules
     */
    public static class ExtendedDOMComparator extends DocumentComparator {

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
