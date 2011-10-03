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
package org.geotoolkit.wfs.xml.v100;

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
import org.geotoolkit.gml.xml.v212.CoordType;
import org.geotoolkit.gml.xml.v212.PointType;
import org.geotoolkit.ogc.xml.v100.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v100.FilterType;
import org.geotoolkit.ogc.xml.v100.PropertyIsLikeType;

//Junit dependencies
import org.geotoolkit.wfs.xml.WFSMarshallerPool;
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
        pool         = WFSMarshallerPool.getInstanceV100();
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

        InputStream is = WfsXMLBindingTest.class.getResourceAsStream("/org/geotoolkit/wfs/v100/capabilities.xml");
        Object unmarshalled = unmarshaller.unmarshal(is);
        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }

        assertTrue(unmarshalled instanceof WFSCapabilitiesType);
        WFSCapabilitiesType result = (WFSCapabilitiesType) unmarshalled;

        assertTrue(result.getFeatureTypeList() != null);

        WFSCapabilitiesType expResult = new WFSCapabilitiesType();
        List<FeatureTypeType> featList = new ArrayList<FeatureTypeType>();
        LatLongBoundingBoxType bbox = new LatLongBoundingBoxType(99038.3, 6.00684, 1.24244, 7.15024);
        FeatureTypeType ft1 = new FeatureTypeType(new QName("http://www.opengis.net/wfs","Zones_de_protection_speciale"), "Zones de protection speciale", "EPSG:2154", Arrays.asList(bbox));
        featList.add(ft1);

        FeatureTypeType ft2 = new FeatureTypeType(new QName("http://www.opengis.net/wfs","Sites_d_importance_communautaire"), "Sites importance communautaire", "EPSG:2154", Arrays.asList(bbox));
        featList.add(ft2);

        

        FeatureTypeListType featureList = new FeatureTypeListType(null, featList);
        expResult.setFeatureTypeList(featureList);

        assertEquals(expResult.getFeatureTypeList().getFeatureType(), result.getFeatureTypeList().getFeatureType());
        assertEquals(expResult.getFeatureTypeList(), result.getFeatureTypeList());
        /*assertEquals(expResult.getOperationsMetadata(), result.getOperationsMetadata());
        assertEquals(expResult.getFilterCapabilities(), result.getFilterCapabilities());
        assertEquals(expResult, result);*/

        // TEST with WFSBindingUtilities

        is = WfsXMLBindingTest.class.getResourceAsStream("/org/geotoolkit/wfs/v100/capabilities.xml");
        
        result = ((JAXBElement<WFSCapabilitiesType>) unmarshaller.unmarshal(is)).getValue();
        assertEquals(expResult.getFeatureTypeList().getFeatureType(), result.getFeatureTypeList().getFeatureType());
        assertEquals(expResult.getFeatureTypeList(), result.getFeatureTypeList());
        
        String xml = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n' +
                "<wfs:Transaction version=\"1.0.0\" service=\"WFS\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" " + '\n' +
                "          xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\">" + '\n' +
                "    <wfs:Insert>" + '\n' +
                "    </wfs:Insert>" + '\n' +
                "</wfs:Transaction>";

        unmarshalled = unmarshaller.unmarshal(new StringReader(xml));
        
        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }
        assertTrue("was no transaction but " + unmarshalled, unmarshalled instanceof TransactionType);
        TransactionType resultT = (TransactionType) unmarshalled;
        
        InsertElementType ins = new InsertElementType();
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
        
        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }
        
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
        
        LatLongBoundingBoxType bbox = new LatLongBoundingBoxType(29.8, -90.1, 30, -89.9);
        FeatureTypeType ft1 = new FeatureTypeType(new QName("http://www.opengis.net/ows-6/utds/0.3", "Building", "utds"), "", "urn:ogc:def:crs:EPSG::4979", Arrays.asList(bbox));
        featList.add(ft1);
        FeatureTypeListType featureList = new FeatureTypeListType(null, featList);
        capa.setFeatureTypeList(featureList);

        StringWriter sw = new StringWriter();
        marshaller.marshal(capa, sw);


        DeleteElementType del = null;
        TransactionType transac = new TransactionType("WFS", "1.1.0", null, AllSomeType.ALL, del);
        PropertyIsLikeType pis = new PropertyIsLikeType("NAME", "Ashton", "*", "?", "\\");
        org.geotoolkit.ogc.xml.v100.ObjectFactory factory = new org.geotoolkit.ogc.xml.v100.ObjectFactory();
        final JAXBElement<? extends ComparisonOpsType> jbPis = factory.createPropertyIsLike(pis);
        FilterType filter = new FilterType(null, jbPis, null, null);
        CoordType dp = new CoordType(21400.0,2001368.0);
        PointType pt = new PointType(dp);
        pt.setSrsName("urn:ogc:def:crs:epsg:7.4:27582");
        PropertyType property = new PropertyType("the_geom", pt);
        UpdateElementType update = new UpdateElementType(Arrays.asList(property), filter, new QName("http://www.opengis.net/gml", "NamedPlaces"));
        transac.getInsertOrUpdateOrDelete().add(update);

        //marshaller.marshal(transac, System.out);
    }
}
