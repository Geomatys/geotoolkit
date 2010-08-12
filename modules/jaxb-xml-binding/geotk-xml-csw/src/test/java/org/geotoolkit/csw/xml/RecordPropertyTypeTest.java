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
package org.geotoolkit.csw.xml;

import java.io.StringReader;

//Junit dependencies
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.csw.xml.v202.RecordPropertyType;
import org.geotoolkit.csw.xml.v202.TransactionType;
import org.geotoolkit.csw.xml.v202.UpdateType;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class RecordPropertyTypeTest {

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = CSWMarshallerPool.getInstance();
        marshaller = pool.acquireMarshaller();
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
        "<csw:Transaction xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" >"             + '\n' +
        "    <csw:Update>"                                                                           + '\n' +
        "        <csw:RecordProperty>"                                                               + '\n' +
        "            <csw:Name>/csw:Record/dc:contributor</csw:Name>"                                + '\n' +
        "            <csw:Value>Jane</csw:Value>"                                                    + '\n' +
        "        </csw:RecordProperty>"                                                              + '\n' +
        "        <csw:Constraint version=\"1.1.0\">"                                                 + '\n' +
        "            <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>" + '\n' +
        "        </csw:Constraint>"                                                                  + '\n' +
        "    </csw:Update>"                                                                          + '\n' +
        "</csw:Transaction>"+ '\n';

        TransactionType result = (TransactionType) unmarshaller.unmarshal(new StringReader(xml));

        assertTrue(result.getInsertOrUpdateOrDelete().size() == 1);
        assertTrue(result.getInsertOrUpdateOrDelete().get(0) instanceof UpdateType);

        UpdateType update = (UpdateType) result.getInsertOrUpdateOrDelete().get(0);

        assertTrue(update.getRecordProperty().size() == 1);

        RecordPropertyType property = update.getRecordProperty().get(0);

        assertEquals("Jane", property.getValue());

        xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:Transaction xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" >"             + '\n' +
        "    <csw:Update>"                                                                           + '\n' +
        "        <csw:RecordProperty>"                                                               + '\n' +
        "            <csw:Name>/csw:Record/dc:contributor</csw:Name>"                                + '\n' +
        "            <csw:Value xsi:type=\"xs:string\">Jane</csw:Value>"                                                    + '\n' +
        "        </csw:RecordProperty>"                                                              + '\n' +
        "        <csw:Constraint version=\"1.1.0\">"                                                 + '\n' +
        "            <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>" + '\n' +
        "        </csw:Constraint>"                                                                  + '\n' +
        "    </csw:Update>"                                                                          + '\n' +
        "</csw:Transaction>"+ '\n';

        result = (TransactionType) unmarshaller.unmarshal(new StringReader(xml));

        assertTrue(result.getInsertOrUpdateOrDelete().size() == 1);
        assertTrue(result.getInsertOrUpdateOrDelete().get(0) instanceof UpdateType);

        update = (UpdateType) result.getInsertOrUpdateOrDelete().get(0);

        assertTrue(update.getRecordProperty().size() == 1);

        property = update.getRecordProperty().get(0);

        assertEquals("Jane", property.getValue());
    }

    @Test
    public void getValueComplexTypeTest() throws JAXBException {

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:Transaction xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:gco=\"http://www.isotc211.org/2005/gco\" verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" >" + '\n' +
        "    <csw:Update>"                                                                           + '\n' +
        "        <csw:RecordProperty>"                                                               + '\n' +
        "            <csw:Name>/csw:Record/dc:contributor</csw:Name>"                                + '\n' +
        "            <csw:Value>"                                                                    + '\n' +
        "                <gmd:EX_GeographicBoundingBox>"                                             + '\n' +
        "                    <gmd:extentTypeCode>"                                                   + '\n' +
        "                        <gco:Boolean>true</gco:Boolean>"                                    + '\n' +
        "                    </gmd:extentTypeCode>"                                                  + '\n' +
        "                    <gmd:westBoundLongitude>"                                               + '\n' +
        "                        <gco:Decimal>1.1667</gco:Decimal>"                                  + '\n' +
        "                    </gmd:westBoundLongitude>"                                              + '\n' +
        "                    <gmd:eastBoundLongitude>"                                               + '\n' +
        "                        <gco:Decimal>1.1667</gco:Decimal>"                                  + '\n' +
        "                    </gmd:eastBoundLongitude>"                                              + '\n' +
        "                    <gmd:southBoundLatitude>"                                               + '\n' +
        "                         <gco:Decimal>36.6</gco:Decimal>"                                   + '\n' +
        "                    </gmd:southBoundLatitude>"                                              + '\n' +
        "                    <gmd:northBoundLatitude>"                                               + '\n' +
        "                         <gco:Decimal>36.6</gco:Decimal>"                                   + '\n' +
        "                    </gmd:northBoundLatitude>"                                              + '\n' +
        "                </gmd:EX_GeographicBoundingBox>"                                            + '\n' +
        "            </csw:Value>"                                                                   + '\n' +
        "        </csw:RecordProperty>"                                                              + '\n' +
        "        <csw:Constraint version=\"1.1.0\">"                                                 + '\n' +
        "            <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>" + '\n' +
        "        </csw:Constraint>"                                                                  + '\n' +
        "    </csw:Update>"                                                                          + '\n' +
        "</csw:Transaction>"+ '\n';

        TransactionType result = (TransactionType) unmarshaller.unmarshal(new StringReader(xml));

        assertTrue(result.getInsertOrUpdateOrDelete().size() == 1);
        assertTrue(result.getInsertOrUpdateOrDelete().get(0) instanceof UpdateType);

        UpdateType update = (UpdateType) result.getInsertOrUpdateOrDelete().get(0);

        assertTrue(update.getRecordProperty().size() == 1);

        RecordPropertyType property = update.getRecordProperty().get(0);

        boolean launched = false;
        try {
            property.getValue();
        } catch (IllegalArgumentException ex) {
            launched = true;
        }

        assertTrue(launched);

        xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<csw:Transaction xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:gco=\"http://www.isotc211.org/2005/gco\" verboseResponse=\"false\" version=\"2.0.2\" service=\"CSW\" >" + '\n' +
        "    <csw:Update>"                                                                           + '\n' +
        "        <csw:RecordProperty>"                                                               + '\n' +
        "            <csw:Name>/csw:Record/dc:contributor</csw:Name>"                                + '\n' +
        "            <csw:Value xsi:type=\"gmd:EX_GeographicBoundingBox\" >"                         + '\n' +
        "                    <gmd:extentTypeCode>"                                                   + '\n' +
        "                        <gco:Boolean>true</gco:Boolean>"                                    + '\n' +
        "                    </gmd:extentTypeCode>"                                                  + '\n' +
        "                    <gmd:westBoundLongitude>"                                               + '\n' +
        "                        <gco:Decimal>1.1667</gco:Decimal>"                                  + '\n' +
        "                    </gmd:westBoundLongitude>"                                              + '\n' +
        "                    <gmd:eastBoundLongitude>"                                               + '\n' +
        "                        <gco:Decimal>1.1667</gco:Decimal>"                                  + '\n' +
        "                    </gmd:eastBoundLongitude>"                                              + '\n' +
        "                    <gmd:southBoundLatitude>"                                               + '\n' +
        "                         <gco:Decimal>36.6</gco:Decimal>"                                   + '\n' +
        "                    </gmd:southBoundLatitude>"                                              + '\n' +
        "                    <gmd:northBoundLatitude>"                                               + '\n' +
        "                         <gco:Decimal>36.6</gco:Decimal>"                                   + '\n' +
        "                    </gmd:northBoundLatitude>"                                              + '\n' +
        "            </csw:Value>"                                                                   + '\n' +
        "        </csw:RecordProperty>"                                                              + '\n' +
        "        <csw:Constraint version=\"1.1.0\">"                                                 + '\n' +
        "            <csw:CqlText>identifier='{8C71082D-5B3B-5F9D-FC40-F7807C8AB645}'</csw:CqlText>" + '\n' +
        "        </csw:Constraint>"                                                                  + '\n' +
        "    </csw:Update>"                                                                          + '\n' +
        "</csw:Transaction>"+ '\n';

        result = (TransactionType) unmarshaller.unmarshal(new StringReader(xml));

        assertTrue(result.getInsertOrUpdateOrDelete().size() == 1);
        assertTrue(result.getInsertOrUpdateOrDelete().get(0) instanceof UpdateType);

        update = (UpdateType) result.getInsertOrUpdateOrDelete().get(0);

        assertTrue(update.getRecordProperty().size() == 1);

        property = update.getRecordProperty().get(0);

        assertTrue(property.getValue() instanceof DefaultGeographicBoundingBox);

        DefaultGeographicBoundingBox expResult = new DefaultGeographicBoundingBox(1.1667, 1.1667, 36.6, 36.6);

        assertEquals(expResult, property.getValue());

    }
}
