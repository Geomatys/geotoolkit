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

import java.io.StringWriter;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeEntry;

//Junit dependencies
import org.geotoolkit.gml.xml.v311.ObjectFactory;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class GmlXMLBindingTest {

    private MarshallerPool pool;
    private Marshaller   marshaller;
    private static ObjectFactory FACTORY = new ObjectFactory();

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool("org.geotoolkit.gml.xml.v311:org.geotoolkit.internal.jaxb.geometry");
        marshaller = pool.acquireMarshaller();
    }

    @After
    public void tearDown() {
        if (marshaller != null) {
            pool.release(marshaller);
        }
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void marshallingTest() throws JAXBException {

        DirectPositionType lower = new DirectPositionType(-30.711, 134.196);
        DirectPositionType upper = new DirectPositionType(-30.702, 134.205);
        EnvelopeEntry env = new EnvelopeEntry("bound-1", lower, upper, "urn:ogc:def:crs:EPSG:6.8:4283");

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

    }
}
