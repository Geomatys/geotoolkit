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
package org.geotoolkit.wfs.xml.v200;

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
import org.geotoolkit.gml.xml.v321.DirectPositionType;
import org.geotoolkit.gml.xml.v321.PointType;
import org.geotoolkit.ogc.xml.v200.FilterType;
import org.geotoolkit.ogc.xml.v200.PropertyIsLikeType;
import org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType;
import org.geotoolkit.wfs.xml.*;
import org.apache.sis.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class WfsXMLBindingTest extends org.geotoolkit.test.TestBase {
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
            pool.recycle(unmarshaller);
        }
        if (marshaller != null) {
            pool.recycle(marshaller);
        }
    }

    @Test
    public void unmarshallingTest() throws JAXBException, FileNotFoundException {

        InputStream is = WfsXMLBindingTest.class.getResourceAsStream("/org/geotoolkit/wfs/v200/capabilities.xml");
        Object unmarshalled = unmarshaller.unmarshal(is);
        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }

        assertTrue(unmarshalled instanceof WFSCapabilitiesType);
        WFSCapabilitiesType result = (WFSCapabilitiesType) unmarshalled;

        assertTrue(result.getFeatureTypeList() != null);

        WFSCapabilitiesType expResult = new WFSCapabilitiesType();
        List<FeatureTypeType> featList = new ArrayList<>();
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

        is = WfsXMLBindingTest.class.getResourceAsStream("/org/geotoolkit/wfs/v200/capabilities.xml");
        Object obj = WFSBindingUtilities.unmarshall(is, WFSVersion.v200);
        assertTrue("was " + obj.getClass().getName(), obj instanceof WFSCapabilitiesType);
        result =(WFSCapabilitiesType) obj;
        assertEquals(expResult.getFeatureTypeList().getFeatureType(), result.getFeatureTypeList().getFeatureType());
        assertEquals(expResult.getFeatureTypeList(), result.getFeatureTypeList());

        String xml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n' +
                "<wfs:Transaction version=\"2.0.0\" service=\"WFS\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:ogc=\"http://www.opengis.net/fes/2.0\" " + '\n' +
                "          xmlns:wfs=\"http://www.opengis.net/wfs/2.0\" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\">" + '\n' +
                "    <wfs:Insert idgen=\"UseExisting\">" + '\n' +
                "    </wfs:Insert>" + '\n' +
                "</wfs:Transaction>";

        unmarshalled = unmarshaller.unmarshal(new StringReader(xml));

        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }
        assertTrue("was " + unmarshalled.getClass().getName(), unmarshalled instanceof TransactionType);
        TransactionType resultT = (TransactionType) unmarshalled;

        InsertType ins = new InsertType();
        //ins.setIdgen(IdentifierGenerationOptionType.USE_EXISTING);
        TransactionType expResultT = new TransactionType("WFS", "2.0.0", null, null, ins);

        assertEquals(expResultT, resultT);

        xml =   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n' +
                "<wfs:Transaction version=\"2.0.0\" service=\"WFS\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:fes=\"http://www.opengis.net/fes/2.0\" " + '\n' +
                "          xmlns:wfs=\"http://www.opengis.net/wfs/2.0\" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\">" + '\n' +
                "    <wfs:Delete typeName=\"gml:test\">" + '\n' +
                "    </wfs:Delete>" + '\n' +
                "</wfs:Transaction>";

        unmarshalled = unmarshaller.unmarshal(new StringReader(xml));

        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }

        assertTrue(unmarshalled instanceof TransactionType);
        resultT = (TransactionType) unmarshalled;

        DeleteType del = new DeleteType();
        del.setTypeName(new QName("http://www.opengis.net/gml/3.2", "test"));
        expResultT = new TransactionType("WFS", "2.0.0", null, null, del);

        assertEquals(expResultT, resultT);


    }

    @Test
    public void marshallingTest() throws JAXBException {

        WFSCapabilitiesType capa = new WFSCapabilitiesType();
        List<FeatureTypeType> featList = new ArrayList<>();
        List<String> otherSRS = Arrays.asList("urn:ogc:def:crs","crs:EPSG::32615","crs:EPSG::5773");
        WGS84BoundingBoxType bbox = new WGS84BoundingBoxType(29.8, -90.1, 30, -89.9);
        FeatureTypeType ft1 = new FeatureTypeType(new QName("http://www.opengis.net/ows-6/utds/0.3", "Building", "utds"), "", "urn:ogc:def:crs:EPSG::4979", otherSRS, Arrays.asList(bbox));
        featList.add(ft1);
        FeatureTypeListType featureList = new FeatureTypeListType(null, featList);
        capa.setFeatureTypeList(featureList);

        StringWriter sw = new StringWriter();
        marshaller.marshal(capa, sw);


        DeleteType del = null;
        TransactionType transac = new TransactionType("WFS", "1.1.0", null, AllSomeType.ALL, del);
        PropertyIsLikeType pis = new PropertyIsLikeType("NAME", "Ashton", "*", "?", "\\");
        FilterType filter = new FilterType(pis);
        DirectPositionType dp = new DirectPositionType(21400.0,2001368.0);
        PointType pt = new PointType(null, dp);
        pt.setSrsName("urn:ogc:def:crs:epsg:7.4:27582");
        PropertyType property = new PropertyType(new ValueReference(), pt);
        UpdateType update = new UpdateType(null, Arrays.asList(property), filter, new QName("http://www.opengis.net/gml", "NamedPlaces"), null);
        final ObjectFactory factory = new ObjectFactory();
        transac.getAbstractTransactionAction().add(factory.createUpdate(update));

        sw = new StringWriter();
        marshaller.marshal(transac, sw);

        final List<StoredQueryDescription> descriptions = new ArrayList<>();
        final ParameterExpressionType param = new ParameterExpressionType("param1", "parameter 1", "a test parameter", new QName("http://www.w3.org/2001/XMLSchema", "string", "xs"));
        final List<QName> types = Arrays.asList(new QName("http://test.com", "someType"));
        final QueryType query = new QueryType(filter, types, "2.0.0");
        final QueryExpressionTextType queryEx = new QueryExpressionTextType("urn:ogc:def:queryLanguage:OGC-WFS::WFS_QueryExpression", query, types);
        final StoredQueryDescriptionType des1 = new StoredQueryDescriptionType("query1", "title1", "abstract1", param, queryEx);
        descriptions.add(des1);
        final StoredQueries storesQueries = new StoredQueries(descriptions);

        sw = new StringWriter();
        marshaller.marshal(storesQueries, sw);

        //System.out.println(sw.toString());

        StoredQueryListItemType item = new StoredQueryListItemType("someid", Arrays.asList(new Title("some title")), null);
        ListStoredQueriesResponseType lsqr = new ListStoredQueriesResponseType(Arrays.asList(item));

        sw = new StringWriter();
        marshaller.marshal(lsqr, sw);

        System.out.println(sw.toString());

        item = new StoredQueryListItemType("someid", Arrays.asList(new Title("some title")),Arrays.asList(new QName("")));
        lsqr = new ListStoredQueriesResponseType(Arrays.asList(item));

        sw = new StringWriter();
        marshaller.marshal(lsqr, sw);

        System.out.println(sw.toString());
    }
}
