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
package org.geotoolkit.sampling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.FeatureCollectionType;
import org.geotoolkit.gml.xml.v311.FeaturePropertyType;
import org.geotoolkit.gml.xml.v311.PointType;

//Junit dependencies
import org.geotoolkit.sampling.xml.v100.ObjectFactory;
import org.geotoolkit.sampling.xml.v100.SamplingPointType;
import javax.xml.bind.JAXBContext;
import org.apache.sis.xml.MarshallerPool;
import org.junit.*;
import org.xml.sax.SAXException;

import static org.apache.sis.test.MetadataAssert.*;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class SamplingXMLBindingTest extends org.geotoolkit.test.TestBase {

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool(JAXBContext.newInstance(
                "org.geotoolkit.sampling.xml.v100:" +
                "org.geotoolkit.observation.xml.v100:" +
                "org.geotoolkit.gml.xml.v311:" +
                "org.apache.sis.internal.jaxb.geometry"), null);
        unmarshaller = pool.acquireUnmarshaller();
        marshaller   = pool.acquireMarshaller();
    }

    @After
    public void tearDown() throws Exception {
        if (unmarshaller != null) {
            pool.recycle(unmarshaller);
        }
        if (marshaller != null) {
            pool.recycle(marshaller);
        }
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void marshallingTest() throws JAXBException, IOException, ParserConfigurationException, SAXException {

        final DirectPositionType pos = new DirectPositionType("urn:ogc:crs:espg:4326", 2, Arrays.asList(3.2, 6.5));
        final PointType location = new PointType("point-ID", pos);
        final SamplingPointType sp = new SamplingPointType("samplingID-007", "urn:sampling:test:007", "a sampling Test", new FeaturePropertyType(""), location);

        StringWriter sw = new StringWriter();
        marshaller.marshal(sp, sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 2).trim();

        String expResult = "<sampling:SamplingPoint gml:id=\"samplingID-007\"" +
                                    " xmlns:sampling=\"http://www.opengis.net/sampling/1.0\"" +
                                    " xmlns:xlink=\"http://www.w3.org/1999/xlink\"" +
                                    " xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
                           "    <gml:description>a sampling Test</gml:description>" + '\n' +
                           "    <gml:name>urn:sampling:test:007</gml:name>" + '\n' +
                           "    <gml:boundedBy>" + '\n' +
                           "        <gml:Null>not_bounded</gml:Null>" + '\n' +
                           "    </gml:boundedBy>" + '\n' +
                           "    <sampling:sampledFeature xlink:href=\"\"/>" + '\n' +
                           "    <sampling:position>" + '\n' +
                           "        <gml:Point gml:id=\"point-ID\">" + '\n' +
                           "            <gml:pos srsName=\"urn:ogc:crs:espg:4326\" srsDimension=\"2\">3.2 6.5</gml:pos>" + '\n' +
                           "        </gml:Point>" + '\n' +
                           "    </sampling:position>" + '\n' +
                           "</sampling:SamplingPoint>" + '\n' ;
        assertXmlEquals(expResult, result, "xmlns:*");

        final ObjectFactory facto = new ObjectFactory();
        FeatureCollectionType collection = new FeatureCollectionType();
        List<FeaturePropertyType> featProps = new ArrayList<FeaturePropertyType>();
        featProps.add(new FeaturePropertyType(facto.createSamplingPoint(sp)));
        collection.getFeatureMember().addAll(featProps);

        sw = new StringWriter();
        marshaller.marshal(collection, sw);

        result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 2).trim();

        expResult =        "<gml:FeatureCollection" +
                                    " xmlns:sampling=\"http://www.opengis.net/sampling/1.0\"" +
                                    " xmlns:xlink=\"http://www.w3.org/1999/xlink\"" +
                                    " xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
                           "    <gml:featureMember>" + '\n' +
                           "        <sampling:SamplingPoint gml:id=\"samplingID-007\">" + '\n' +
                           "            <gml:description>a sampling Test</gml:description>" + '\n' +
                           "            <gml:name>urn:sampling:test:007</gml:name>" + '\n' +
                           "            <gml:boundedBy>" + '\n' +
                           "                <gml:Null>not_bounded</gml:Null>" + '\n' +
                           "            </gml:boundedBy>" + '\n' +
                           "            <sampling:sampledFeature xlink:href=\"\"/>" + '\n' +
                           "            <sampling:position>" + '\n' +
                           "                <gml:Point gml:id=\"point-ID\">" + '\n' +
                           "                    <gml:pos srsName=\"urn:ogc:crs:espg:4326\" srsDimension=\"2\">3.2 6.5</gml:pos>" + '\n' +
                           "                </gml:Point>" + '\n' +
                           "            </sampling:position>" + '\n' +
                           "        </sampling:SamplingPoint>" + '\n' +
                           "    </gml:featureMember>" + '\n' +
                           "</gml:FeatureCollection>" + '\n' ;
        assertXmlEquals(expResult, result, "xmlns:*");
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void UnmarshalingTest() throws JAXBException {

        /*
         * Test Unmarshalling spatial filter.
         */

       String xml =
       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
       "<sa:SamplingPoint xmlns:sa=\"http://www.opengis.net/sampling/1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:gml=\"http://www.opengis.net/gml\" gml:id=\"samplingID-007\">" + '\n' +
       "    <gml:description>a sampling Test</gml:description>" + '\n' +
       "    <gml:name>urn:sampling:test:007</gml:name>" + '\n' +
       "   <gml:boundedBy>" + '\n' +
       "        <gml:Null>not_bounded</gml:Null>" + '\n' +
       "    </gml:boundedBy>" + '\n' +
       "    <sa:sampledFeature xlink:href=\"\"/>" + '\n' +
       "    <sa:position>" + '\n' +
       "        <gml:Point gml:id=\"point-ID\">" + '\n' +
       "            <gml:pos srsName=\"urn:ogc:crs:espg:4326\" srsDimension=\"2\">3.2 6.5</gml:pos>" + '\n' +
       "        </gml:Point>" + '\n' +
       "    </sa:position>" + '\n' +
       "</sa:SamplingPoint>" + '\n' ;

        StringReader sr = new StringReader(xml);

        JAXBElement jb =  (JAXBElement) unmarshaller.unmarshal(sr);
        final SamplingPointType result =  (SamplingPointType) jb.getValue();

        final DirectPositionType pos = new DirectPositionType("urn:ogc:crs:espg:4326", 2, Arrays.asList(3.2, 6.5));
        final PointType location = new PointType("point-ID", pos);
        final SamplingPointType expResult = new SamplingPointType("samplingID-007", "urn:sampling:test:007", "a sampling Test", new FeaturePropertyType(""), location);

        assertEquals(expResult.getPosition(), result.getPosition());
        assertEquals(expResult.getName(), result.getName());
        assertEquals(expResult, result);

         xml =             "<gml:FeatureCollection xmlns:sampling=\"http://www.opengis.net/sampling/1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
                           "    <gml:featureMember>" + '\n' +
                           "        <sampling:SamplingPoint gml:id=\"samplingID-007\">" + '\n' +
                           "            <gml:description>a sampling Test</gml:description>" + '\n' +
                           "            <gml:name>urn:sampling:test:007</gml:name>" + '\n' +
                           "            <gml:boundedBy>" + '\n' +
                           "                <gml:Null>not_bounded</gml:Null>" + '\n' +
                           "            </gml:boundedBy>" + '\n' +
                           "            <sampling:sampledFeature xlink:href=\"\"/>" + '\n' +
                           "            <sampling:position>" + '\n' +
                           "                <gml:Point gml:id=\"point-ID\">" + '\n' +
                           "                    <gml:pos srsName=\"urn:ogc:crs:espg:4326\" srsDimension=\"2\">3.2 6.5</gml:pos>" + '\n' +
                           "                </gml:Point>" + '\n' +
                           "            </sampling:position>" + '\n' +
                           "        </sampling:SamplingPoint>" + '\n' +
                           "    </gml:featureMember>" + '\n' +
                           "</gml:FeatureCollection>" + '\n' ;
        sr = new StringReader(xml);

        Object obj  =   ((JAXBElement) unmarshaller.unmarshal(sr)).getValue();

        final ObjectFactory facto = new ObjectFactory();
        FeatureCollectionType collection = new FeatureCollectionType();
        List<FeaturePropertyType> featProps = new ArrayList<FeaturePropertyType>();
        featProps.add(new FeaturePropertyType(facto.createSamplingPoint(expResult)));
        collection.getFeatureMember().addAll(featProps);

        assertEquals(collection, obj);

    }

}
