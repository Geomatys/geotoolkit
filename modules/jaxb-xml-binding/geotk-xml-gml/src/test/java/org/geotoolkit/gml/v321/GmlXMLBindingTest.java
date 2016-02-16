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
package org.geotoolkit.gml.v321;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.Duration;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.sis.test.XMLComparator;

import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.geotoolkit.gml.xml.v321.DirectPositionListType;
import org.geotoolkit.gml.xml.v321.DirectPositionType;
import org.geotoolkit.gml.xml.v321.EnvelopeType;
import org.geotoolkit.gml.xml.v321.LineStringSegmentType;
import org.geotoolkit.gml.xml.v321.ObjectFactory;
import org.geotoolkit.gml.xml.v321.TimePeriodType;
import org.geotoolkit.gml.xml.v321.TimePositionType;
import org.apache.sis.xml.MarshallerPool;

//Junit dependencies
import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Node;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class GmlXMLBindingTest extends org.geotoolkit.test.TestBase {

    private static MarshallerPool pool;
    private Marshaller   marshaller;
    private Unmarshaller unmarshaller;
    private static ObjectFactory FACTORY = new ObjectFactory();

    @BeforeClass
    public static void setUpClass() throws Exception {
        pool = GMLMarshallerPool.getInstance();
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
     *
     * @throws JAXBException
     */
    @Test
    public void marshallingTest() throws Exception {

        DirectPositionType lower = new DirectPositionType(-30.711, 134.196);
        DirectPositionType upper = new DirectPositionType(-30.702, 134.205);
        EnvelopeType env = new EnvelopeType(lower, upper, "urn:ogc:def:crs:EPSG:6.8:4283");

        StringWriter sw = new StringWriter();
        marshaller.marshal(FACTORY.createEnvelope(env), sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);

        String expResult = "<gml:Envelope xmlns:gml=\"http://www.opengis.net/gml/3.2\" srsName=\"urn:ogc:def:crs:EPSG:6.8:4283\" >" + '\n' +
                           "    <gml:lowerCorner>-30.711 134.196</gml:lowerCorner>" + '\n' +
                           "    <gml:upperCorner>-30.702 134.205</gml:upperCorner>" + '\n' +
                           "</gml:Envelope>" + '\n' ;
        XMLComparator comparator = new XMLComparator(expResult, result){
            @Override
            protected strictfp void compareNames(Node expected, Node actual) {
                final String[] exArray = expected.getNodeName().split(":");
                final String[] acArray = actual.getNodeName().split(":");
                assertEquals(exArray.length, acArray.length);
                assertEquals(exArray[exArray.length-1], acArray[acArray.length-1]);
            }
        };
        comparator.ignoredAttributes.add("http://www.w3.org/2000/xmlns:*");
        comparator.compare();

        Duration d1 = javax.xml.datatype.DatatypeFactory.newInstance().newDuration("P2D");

        TimePeriodType tp = new TimePeriodType(d1);

        marshaller.marshal(FACTORY.createTimePeriod(tp), sw);


        TimePositionType tpos = new TimePositionType("2002-08-15");
        tp = new TimePeriodType(tpos);

        marshaller.marshal(FACTORY.createTimePeriod(tp), sw);
        //System.out.println(sw.toString());

        LineStringSegmentType ls = new LineStringSegmentType();
        DirectPositionListType posList = new DirectPositionListType();
        posList.setValue(Arrays.asList(1.0, 1.1, 1.2));
        ls.setPosList(posList);

        sw = new StringWriter();
        marshaller.marshal(FACTORY.createLineStringSegment(ls), sw);

        result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);

        expResult = "<gml:LineStringSegment xmlns:gml=\"http://www.opengis.net/gml/3.2\">" + '\n' +
                    "    <gml:posList>1.0 1.1 1.2</gml:posList>" + '\n' +
                    "</gml:LineStringSegment>" + '\n' ;
        comparator = new XMLComparator(expResult, result){
            @Override
            protected strictfp void compareNames(Node expected, Node actual) {
                final String[] exArray = expected.getNodeName().split(":");
                final String[] acArray = actual.getNodeName().split(":");
                assertEquals(exArray.length, acArray.length);
                assertEquals(exArray[exArray.length-1], acArray[acArray.length-1]);
            }
        };
        comparator.ignoredAttributes.add("http://www.w3.org/2000/xmlns:*");
        comparator.compare();

        ls = new LineStringSegmentType();
        DirectPositionType pos1 = new DirectPositionType(Arrays.asList(1.1, 1.2));
        DirectPositionType pos2 = new DirectPositionType(Arrays.asList(2.3, 48.1));
        ls.getPos().add(pos1);
        ls.getPos().add(pos2);

        sw = new StringWriter();
        marshaller.marshal(FACTORY.createLineStringSegment(ls), sw);

        result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);

        expResult = "<gml:LineStringSegment xmlns:gml=\"http://www.opengis.net/gml/3.2\">" + '\n' +
                    "    <gml:pos>1.1 1.2</gml:pos>" + '\n' +
                    "    <gml:pos>2.3 48.1</gml:pos>" + '\n' +
                    "</gml:LineStringSegment>" + '\n' ;
        comparator = new XMLComparator(expResult, result){
            @Override
            protected strictfp void compareNames(Node expected, Node actual) {
                final String[] exArray = expected.getNodeName().split(":");
                final String[] acArray = actual.getNodeName().split(":");
                assertEquals(exArray.length, acArray.length);
                assertEquals(exArray[exArray.length-1], acArray[acArray.length-1]);
            }
        };
        comparator.ignoredAttributes.add("http://www.w3.org/2000/xmlns:*");
        comparator.compare();

    }

    @Test
    public void umarshallingTest() throws Exception {
        LineStringSegmentType expResult = new LineStringSegmentType();
        DirectPositionListType posList = new DirectPositionListType();
        posList.setValue(Arrays.asList(1.0, 1.1, 1.2));
        expResult.setPosList(posList);

        String xml = "<gml:LineStringSegment xmlns:gml=\"http://www.opengis.net/gml/3.2\">" + '\n' +
                     "    <gml:posList>1.0 1.1 1.2</gml:posList>" + '\n' +
                     "</gml:LineStringSegment>" + '\n' ;

        Object result = unmarshaller.unmarshal(new StringReader(xml));

        if (result instanceof JAXBElement) {
            result = ((JAXBElement)result).getValue();
        }
        assertEquals(expResult, result);


        expResult = new LineStringSegmentType();
        DirectPositionType pos1 = new DirectPositionType(Arrays.asList(1.1, 1.2));
        DirectPositionType pos2 = new DirectPositionType(Arrays.asList(2.3, 48.1));
        expResult.getPos().add(pos1);
        expResult.getPos().add(pos2);


        xml       = "<gml:LineStringSegment xmlns:gml=\"http://www.opengis.net/gml/3.2\">" + '\n' +
                    "    <gml:pos>1.1 1.2</gml:pos>" + '\n' +
                    "    <gml:pos>2.3 48.1</gml:pos>" + '\n' +
                    "</gml:LineStringSegment>" + '\n' ;

        result = unmarshaller.unmarshal(new StringReader(xml));

        if (result instanceof JAXBElement) {
            result = ((JAXBElement)result).getValue();
        }
        assertEquals(expResult, result);
    }

    @Test
    public void timePeriodUmarshallingTest() throws Exception {
        TimePositionType begin = new TimePositionType("2002-08-01");
        TimePositionType end = new TimePositionType("2003-08-01");
        TimePeriodType expResult = new TimePeriodType(begin, end);


        String xml = "<gml:TimePeriod xmlns:gml=\"http://www.opengis.net/gml/3.2\">" + '\n' +
                     "    <gml:beginPosition>2002-08-01</gml:beginPosition>" + '\n' +
                     "    <gml:endPosition>2003-08-01</gml:endPosition>" + '\n' +
                     "</gml:TimePeriod>" + '\n' ;

        Object result = unmarshaller.unmarshal(new StringReader(xml));

        if (result instanceof JAXBElement) {
            result = ((JAXBElement)result).getValue();
        }
        assertEquals(expResult, result);

        end = null;
        expResult = new TimePeriodType(begin, end);


        xml = "<gml:TimePeriod xmlns:gml=\"http://www.opengis.net/gml/3.2\">" + '\n' +
              "    <gml:beginPosition>2002-08-01</gml:beginPosition>" + '\n' +
              "</gml:TimePeriod>" + '\n' ;

        result = unmarshaller.unmarshal(new StringReader(xml));

        if (result instanceof JAXBElement) {
            result = ((JAXBElement)result).getValue();
        }
        assertEquals(expResult, result);

    }
}
