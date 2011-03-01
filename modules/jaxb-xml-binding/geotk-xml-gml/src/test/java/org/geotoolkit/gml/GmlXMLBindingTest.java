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
package org.geotoolkit.gml;

import java.io.StringReader;
import javax.xml.bind.Unmarshaller;
import java.util.Arrays;
import javax.xml.datatype.Duration;
import java.io.StringWriter;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.gml.xml.v311.DirectPositionListType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;

//Junit dependencies
import org.geotoolkit.gml.xml.v311.LineStringSegmentType;
import org.geotoolkit.gml.xml.v311.ObjectFactory;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.gml.xml.v311.TimePositionType;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class GmlXMLBindingTest {

    private static MarshallerPool pool;
    private Marshaller   marshaller;
    private Unmarshaller unmarshaller;
    private static ObjectFactory FACTORY = new ObjectFactory();

    @BeforeClass
    public static void setUpClass() throws Exception {
        pool = new MarshallerPool("org.geotoolkit.gml.xml.v311:org.geotoolkit.internal.jaxb.geometry");
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
            pool.release(marshaller);
        }
        if (unmarshaller != null) {
            pool.release(unmarshaller);
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
        EnvelopeType env = new EnvelopeType("bound-1", lower, upper, "urn:ogc:def:crs:EPSG:6.8:4283");

        StringWriter sw = new StringWriter();
        marshaller.marshal(FACTORY.createEnvelope(env), sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
       

        String expResult = "<gml:Envelope srsName=\"urn:ogc:def:crs:EPSG:6.8:4283\" gml:id=\"bound-1\">" + '\n' +
                           "    <gml:lowerCorner>-30.711 134.196</gml:lowerCorner>" + '\n' +
                           "    <gml:upperCorner>-30.702 134.205</gml:upperCorner>" + '\n' +
                           "</gml:Envelope>" + '\n' ;
        assertEquals(expResult, result);

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
        //we remove the xmlmns
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");

        expResult = "<gml:LineStringSegment>" + '\n' +
                    "    <gml:posList>1.0 1.1 1.2</gml:posList>" + '\n' +
                    "</gml:LineStringSegment>" + '\n' ;
        assertEquals(expResult, result);

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
        //we remove the xmlmns
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");

        expResult = "<gml:LineStringSegment>" + '\n' +
                    "    <gml:pos>1.1 1.2</gml:pos>" + '\n' +
                    "    <gml:pos>2.3 48.1</gml:pos>" + '\n' +
                    "</gml:LineStringSegment>" + '\n' ;
        assertEquals(expResult, result);

    }

    @Test
    public void umarshallingTest() throws Exception {
        LineStringSegmentType expResult = new LineStringSegmentType();
        DirectPositionListType posList = new DirectPositionListType();
        posList.setValue(Arrays.asList(1.0, 1.1, 1.2));
        expResult.setPosList(posList);

        String xml = "<gml:LineStringSegment xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
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


        xml       = "<gml:LineStringSegment xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
                    "    <gml:pos>1.1 1.2</gml:pos>" + '\n' +
                    "    <gml:pos>2.3 48.1</gml:pos>" + '\n' +
                    "</gml:LineStringSegment>" + '\n' ;
        
        result = unmarshaller.unmarshal(new StringReader(xml));

        if (result instanceof JAXBElement) {
            result = ((JAXBElement)result).getValue();
        }
        assertEquals(expResult, result);
    }
}
