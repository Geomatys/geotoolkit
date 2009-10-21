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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;

//Junit dependencies
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class WfsXMLBindingTest {
    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool("org.geotoolkit.wfs.xml.v110");
        unmarshaller = pool.acquireUnmarshaller();
        marshaller   = pool.acquireMarshaller();
    }

    @After
    public void tearDown() {
        if (unmarshaller != null) {
            pool.release(unmarshaller);
        }
        if (marshaller != null) {
            pool.release(marshaller);
        }
    }

    @Test
    public void unmarshallingTest() throws JAXBException, FileNotFoundException {

        InputStream is = WfsXMLBindingTest.class.getResourceAsStream("/org/constellation/wfs/v110/capabilities.xml");
        Object unmarshalled = unmarshaller.unmarshal(is);
        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }

        assertTrue(unmarshalled instanceof WFSCapabilitiesType);
        WFSCapabilitiesType result = (WFSCapabilitiesType) unmarshalled;

        assertTrue(result.getFeatureTypeList() != null);

        WFSCapabilitiesType expResult = new WFSCapabilitiesType();
        List<FeatureTypeType> featList = new ArrayList<FeatureTypeType>();
        List<String> otherSRS = Arrays.asList("urn:ogc:def:crs","crs:EPSG::32615","crs:EPSG::5773");
        WGS84BoundingBoxType bbox = new WGS84BoundingBoxType(29.8, -90.1, 30, -89.9);
        FeatureTypeType ft1 = new FeatureTypeType(new QName("http://www.opengis.net/ows-6/utds/0.3", "Building", "utds"), "", "urn:ogc:def:crs:EPSG::4979", otherSRS, Arrays.asList(bbox));
        featList.add(ft1);
        FeatureTypeListType featureList = new FeatureTypeListType(null, featList);
        expResult.setFeatureTypeList(featureList);

        //assertEquals(expectedResult, result);

    }

    @Test
    public void marshallingTest() throws JAXBException {

        WFSCapabilitiesType capa = new WFSCapabilitiesType();
        List<FeatureTypeType> featList = new ArrayList<FeatureTypeType>();
        List<String> otherSRS = Arrays.asList("urn:ogc:def:crs","crs:EPSG::32615","crs:EPSG::5773");
        WGS84BoundingBoxType bbox = new WGS84BoundingBoxType(29.8, -90.1, 30, -89.9);
        FeatureTypeType ft1 = new FeatureTypeType(new QName("http://www.opengis.net/ows-6/utds/0.3", "Building", "utds"), "", "urn:ogc:def:crs:EPSG::4979", otherSRS, Arrays.asList(bbox));
        featList.add(ft1);
        FeatureTypeListType featureList = new FeatureTypeListType(null, featList);
        capa.setFeatureTypeList(featureList);

        StringWriter sw = new StringWriter();
        marshaller.marshal(capa, sw);

        //logger.info(sw.toString());
    }
}
