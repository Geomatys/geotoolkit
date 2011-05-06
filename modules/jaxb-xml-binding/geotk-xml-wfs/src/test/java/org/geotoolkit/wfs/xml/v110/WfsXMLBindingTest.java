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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLikeType;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;

//Junit dependencies
import org.geotoolkit.wfs.xml.WFSBindingUtilities;
import org.geotoolkit.wfs.xml.WFSMarshallerPool;
import org.geotoolkit.wfs.xml.WFSVersion;
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
        pool         = WFSMarshallerPool.getInstance();
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

        FeatureTypeType ft2 = new FeatureTypeType(new QName("http://www.opengis.net/ows-6/utds/0.3", "AircraftTransportationComplex", "utds"), "", "urn:ogc:def:crs:EPSG::4979", otherSRS, Arrays.asList(bbox));
        featList.add(ft2);

        FeatureTypeType ft3 = new FeatureTypeType(new QName("http://www.opengis.net/ows-6/utds/0.3", "Fence", "utds"), "", "urn:ogc:def:crs:EPSG::4979", otherSRS, Arrays.asList(bbox));
        featList.add(ft3);

        FeatureTypeListType featureList = new FeatureTypeListType(null, featList);
        expResult.setFeatureTypeList(featureList);

        assertEquals(expResult.getFeatureTypeList().getFeatureType(), result.getFeatureTypeList().getFeatureType());
        assertEquals(expResult.getFeatureTypeList(), result.getFeatureTypeList());
        /*assertEquals(expResult.getOperationsMetadata(), result.getOperationsMetadata());
        assertEquals(expResult.getFilterCapabilities(), result.getFilterCapabilities());
        assertEquals(expResult, result);*/

        // TEST with WFSBindingUtilities

        is = WfsXMLBindingTest.class.getResourceAsStream("/org/constellation/wfs/v110/capabilities.xml");
        result = WFSBindingUtilities.unmarshall(is, WFSVersion.v110);
        assertEquals(expResult.getFeatureTypeList().getFeatureType(), result.getFeatureTypeList().getFeatureType());
        assertEquals(expResult.getFeatureTypeList(), result.getFeatureTypeList());
        
        String xml = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n' +
                "<wfs:Transaction version=\"1.1.0\" service=\"WFS\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" " + '\n' +
                "          xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\">" + '\n' +
                "    <wfs:Insert idgen=\"UseExisting\">" + '\n' +
                "    </wfs:Insert>" + '\n' +
                "</wfs:Transaction>";

        unmarshalled = unmarshaller.unmarshal(new StringReader(xml));
        
        assertTrue(unmarshalled instanceof TransactionType);
        TransactionType resultT = (TransactionType) unmarshalled;
        
        InsertElementType ins = new InsertElementType();
        ins.setIdgen(IdentifierGenerationOptionType.USE_EXISTING);
        TransactionType expResultT = new TransactionType("WFS", "1.1.0", null, null, ins);
        
        assertEquals(expResultT, resultT);
        
        xml = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n' +
                "<wfs:Transaction version=\"1.1.0\" service=\"WFS\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" " + '\n' +
                "          xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\">" + '\n' +
                "    <wfs:Delete typeName=\"gml:test\">" + '\n' +
                "    </wfs:Delete>" + '\n' +
                "</wfs:Transaction>";

        unmarshalled = unmarshaller.unmarshal(new StringReader(xml));
        
        assertTrue(unmarshalled instanceof TransactionType);
        resultT = (TransactionType) unmarshalled;
        
        DeleteElementType del = new DeleteElementType();
        del.setTypeName(new QName("http://www.opengis.net/gml", "test"));
        expResultT = new TransactionType("WFS", "1.1.0", null, null, del);
        
        assertEquals(expResultT, resultT);
        
        
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


        DeleteElementType del = null;
        TransactionType transac = new TransactionType("WFS", "1.1.0", null, AllSomeType.ALL, del);
        PropertyIsLikeType pis = new PropertyIsLikeType("NAME", "Ashton", "*", "?", "\\");
        FilterType filter = new FilterType(pis);
        DirectPositionType dp = new DirectPositionType(21400.0,2001368.0);
        PointType pt = new PointType(null, dp);
        pt.setSrsName("urn:ogc:def:crs:epsg:7.4:27582");
        PropertyType property = new PropertyType(new QName("the_geom"), new ValueType(pt));
        UpdateElementType update = new UpdateElementType(Arrays.asList(property), filter, new QName("http://www.opengis.net/gml", "NamedPlaces"), null);
        transac.getInsertOrUpdateOrDelete().add(update);

        //marshaller.marshal(transac, System.out);
    }
}
