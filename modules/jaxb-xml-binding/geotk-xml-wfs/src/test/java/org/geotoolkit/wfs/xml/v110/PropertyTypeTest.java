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
package org.geotoolkit.wfs.xml.v110;

import java.io.StringReader;

//Junit dependencies
import java.io.StringWriter;
import java.util.Arrays;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.wfs.xml.WFSMarshallerPool;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class PropertyTypeTest {

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool         = WFSMarshallerPool.getInstance();
        marshaller   = pool.acquireMarshaller();
        unmarshaller = pool.acquireUnmarshaller();
    }

    @After
    public void tearDown() throws JAXBException {
        if (unmarshaller != null) {
            pool.release(unmarshaller);
        }
        if (marshaller != null) {
            pool.release(marshaller);
        }
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void getValueStringTest() throws JAXBException {

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<wfs:Transaction xmlns:wfs=\"http://www.opengis.net/wfs\" version=\"1.1.0\" service=\"WFS\" >" + '\n' +
        "    <wfs:Update>"                                                                              + '\n' +
        "        <wfs:Property>"                                                                        + '\n' +
        "            <wfs:Name>something</wfs:Name>"                                                    + '\n' +
        "            <wfs:Value>Jane</wfs:Value>"                                                       + '\n' +
        "        </wfs:Property>"                                                                       + '\n' +
        "    </wfs:Update>"                                                                             + '\n' +
        "</wfs:Transaction>"+ '\n';

        TransactionType result = (TransactionType) unmarshaller.unmarshal(new StringReader(xml));

        assertTrue(result.getInsertOrUpdateOrDelete().size() == 1);
        assertTrue(result.getInsertOrUpdateOrDelete().get(0) instanceof UpdateElementType);

        UpdateElementType update = (UpdateElementType) result.getInsertOrUpdateOrDelete().get(0);

        assertTrue(update.getProperty().size() == 1);

        PropertyType property = update.getProperty().get(0);

        assertEquals("Jane", property.getValue());

        xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<wfs:Transaction xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" version=\"1.1.0\" service=\"WFS\" >"             + '\n' +
        "    <wfs:Update>"                                                                           + '\n' +
        "        <wfs:Property>"                                                                     + '\n' +
        "            <wfs:Name>somthing</wfs:Name>"                                                  + '\n' +
        "            <wfs:Value xsi:type=\"xs:string\">Jane</wfs:Value>"                             + '\n' +
        "        </wfs:Property>"                                                                    + '\n' +
        "    </wfs:Update>"                                                                          + '\n' +
        "</wfs:Transaction>"+ '\n';

        result = (TransactionType) unmarshaller.unmarshal(new StringReader(xml));

        assertTrue(result.getInsertOrUpdateOrDelete().size() == 1);
        assertTrue(result.getInsertOrUpdateOrDelete().get(0) instanceof UpdateElementType);

        update = (UpdateElementType) result.getInsertOrUpdateOrDelete().get(0);

        assertTrue(update.getProperty().size() == 1);

        property = update.getProperty().get(0);

        assertEquals("Jane", property.getValue());
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void getValueGeometryNewTest() throws JAXBException {

        String xml =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<wfs:Transaction xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:gml=\"http://www.opengis.net/gml\" version=\"1.1.0\" service=\"WFS\" >" + '\n' +
        "    <wfs:Update>"                                                                              + '\n' +
        "        <wfs:Property>"                                                                        + '\n' +
        "            <wfs:Name>something</wfs:Name>"                                                       + '\n' +
        "            <wfs:Value>"                                                                       + '\n' +
        "               <gml:Point srsName=\"urn:ogc:def:crs:epsg:7.4:4326\" >"                         + '\n' +
        "                   <gml:pos>2.1 12.6</gml:pos>"                                                + '\n' +
        "               </gml:Point>"                                                                   + '\n' +
        "            </wfs:Value>"                                                                       + '\n' +
        "        </wfs:Property>"                                                                       + '\n' +
        "    </wfs:Update>"                                                                             + '\n' +
        "</wfs:Transaction>"                                                                            + '\n';

        TransactionType result = (TransactionType) unmarshaller.unmarshal(new StringReader(xml));

        assertTrue(result.getInsertOrUpdateOrDelete().size() == 1);
        assertTrue(result.getInsertOrUpdateOrDelete().get(0) instanceof UpdateElementType);

        UpdateElementType update = (UpdateElementType) result.getInsertOrUpdateOrDelete().get(0);

        assertTrue(update.getProperty().size() == 1);

        PropertyType property = update.getProperty().get(0);

        PointType pt = new PointType(null, new DirectPositionType(2.1, 12.6));
        pt.setSrsName("urn:ogc:def:crs:epsg:7.4:4326");
        assertEquals(pt, property.getValue());
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void setValueGeometryNewTest() throws JAXBException {
        TransactionType transac = new TransactionType();
        PointType pt = new PointType(null, new DirectPositionType(2.1, 12.6));
        pt.setSrsName("urn:ogc:def:crs:epsg:7.4:4326");
        PropertyType prop = new PropertyType(new QName("something"), new ValueType(pt));
        transac.getInsertOrUpdateOrDelete().add(new UpdateElementType(Arrays.asList(prop), null, null, null));
        StringWriter sw = new StringWriter();
        //marshaller.marshal(transac, System.out);
        marshaller.marshal(transac, sw);

        Object result  = unmarshaller.unmarshal(new StringReader(sw.toString()));

        assertEquals(transac, result);

        transac = new TransactionType();
        prop = new PropertyType(new QName("something"), new ValueType("jane"));
        transac.getInsertOrUpdateOrDelete().add(new UpdateElementType(Arrays.asList(prop), null, null, null));
        sw = new StringWriter();
        //marshaller.marshal(transac, System.out);
        marshaller.marshal(transac, sw);

        result  = unmarshaller.unmarshal(new StringReader(sw.toString()));

        assertEquals(transac, result);
    }
}
