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
package org.geotoolkit.gml.v311;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.datatype.Duration;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.time.Instant;

import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.geotoolkit.gml.xml.v311.DirectPositionListType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.gml.xml.v311.LineStringSegmentType;
import org.geotoolkit.gml.xml.v311.ObjectFactory;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.gml.xml.v311.TimePositionType;
import org.geotoolkit.gml.xml.v311.TimeInstantType;
import org.apache.sis.xml.MarshallerPool;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Assertions.assertXmlEquals;

import org.opengis.temporal.Period;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class GmlXMLBindingTest {

    /**
     * GML namespace for this class.
     */
    private static final String GML = "http://www.opengis.net/gml";

    private static MarshallerPool pool;
    private Marshaller   marshaller;
    private Unmarshaller unmarshaller;
    private static final ObjectFactory FACTORY = new ObjectFactory();

    @BeforeClass
    public static void setUpClass() throws Exception {
        pool = GMLMarshallerPool.getInstance();
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
        EnvelopeType env = new EnvelopeType("bound-1", lower, upper, "urn:ogc:def:crs:EPSG:6.8:4283");

        StringWriter sw = new StringWriter();
        marshaller.marshal(FACTORY.createEnvelope(env), sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 2).trim();

        String expResult = "<gml:Envelope xmlns:gml=\"" + GML + '"' +
                           " srsName=\"urn:ogc:def:crs:EPSG:6.8:4283\">" + '\n' +
                           "  <gml:lowerCorner>-30.711 134.196</gml:lowerCorner>" + '\n' +
                           "  <gml:upperCorner>-30.702 134.205</gml:upperCorner>" + '\n' +
                           "</gml:Envelope>" + '\n' ;

        assertXmlEquals(expResult, result, "xmlns:*");

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
        result = result.substring(result.indexOf("?>") + 2).trim();

        expResult = "<gml:LineStringSegment xmlns:gml=\"" + GML + "\">\n" +
                    "  <gml:posList>1.0 1.1 1.2</gml:posList>" + '\n' +
                    "</gml:LineStringSegment>" + '\n' ;
        assertXmlEquals(expResult, result, "xmlns:*");

        ls = new LineStringSegmentType();
        DirectPositionType pos1 = new DirectPositionType(Arrays.asList(1.1, 1.2));
        DirectPositionType pos2 = new DirectPositionType(Arrays.asList(2.3, 48.1));
        ls.getPos().add(pos1);
        ls.getPos().add(pos2);

        sw = new StringWriter();
        marshaller.marshal(FACTORY.createLineStringSegment(ls), sw);

        result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 2).trim();

        expResult = "<gml:LineStringSegment xmlns:gml=\"" + GML + "\">\n" +
                    "  <gml:pos>1.1 1.2</gml:pos>" + '\n' +
                    "  <gml:pos>2.3 48.1</gml:pos>" + '\n' +
                    "</gml:LineStringSegment>" + '\n' ;
        assertXmlEquals(expResult, result, "xmlns:*");

    }

    @Test
    public void umarshallingTest() throws Exception {
        LineStringSegmentType expResult = new LineStringSegmentType();
        DirectPositionListType posList = new DirectPositionListType();
        posList.setValue(Arrays.asList(1.0, 1.1, 1.2));
        expResult.setPosList(posList);

        String xml = "<gml:LineStringSegment xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
                     "  <gml:posList>1.0 1.1 1.2</gml:posList>" + '\n' +
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
                    "  <gml:pos>1.1 1.2</gml:pos>" + '\n' +
                    "  <gml:pos>2.3 48.1</gml:pos>" + '\n' +
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
        TimePeriodType expResult = new TimePeriodType(null, new TimeInstantType(begin), new TimeInstantType(end));


        String xml = "<gml:TimePeriod xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
                     "  <gml:beginPosition>2002-08-01</gml:beginPosition>" + '\n' +
                     "  <gml:endPosition>2003-08-01</gml:endPosition>" + '\n' +
                     "</gml:TimePeriod>" + '\n' ;

        Object result = unmarshaller.unmarshal(new StringReader(xml));

        if (result instanceof JAXBElement) {
            result = ((JAXBElement) result).getValue();
        }
        assertPeriodEquals(expResult, (Period) result);

        end       = null;
        expResult = new TimePeriodType(null, new TimeInstantType(begin), new TimeInstantType(end));


        xml = "<gml:TimePeriod xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
              "  <gml:beginPosition>2002-08-01</gml:beginPosition>" + '\n' +
              "</gml:TimePeriod>" + '\n' ;

        result = unmarshaller.unmarshal(new StringReader(xml));

        if (result instanceof JAXBElement) {
            result = ((JAXBElement) result).getValue();
        }
        assertPeriodEquals(expResult, (Period) result);
    }

    private static void assertPeriodEquals(Period p1, Period p2) {
        assertEquals(getDate(p1.getBeginning()), getDate(p2.getBeginning()));
        assertEquals(getDate(p1.getEnding()),    getDate(p2.getEnding()));
    }

    private static Date getDate(Instant ins) {
        return (ins != null) ? Date.from(ins) : null;
    }
}
