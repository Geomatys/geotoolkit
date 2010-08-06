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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.FeaturePropertyType;
import org.geotoolkit.gml.xml.v311.PointPropertyType;
import org.geotoolkit.gml.xml.v311.PointType;

//Junit dependencies
import org.geotoolkit.sampling.xml.v100.SamplingPointEntry;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class SamplingXMLBindingTest {

    private Logger logger = Logging.getLogger("org.constellation.metadata.fra");
    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool(
                "org.geotoolkit.sampling.xml.v100:" +
                "org.geotoolkit.observation.xml.v100:" +
                "org.geotoolkit.gml.xml.v311:" +
                "org.geotoolkit.internal.jaxb.geometry");
        unmarshaller = pool.acquireUnmarshaller();
        marshaller   = pool.acquireMarshaller();
    }

    @After
    public void tearDown() throws Exception {
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
    public void marshallingTest() throws JAXBException {

        DirectPositionType pos = new DirectPositionType("urn:ogc:crs:espg:4326", 2, Arrays.asList(3.2, 6.5));
        PointType location = new PointType("point-ID", pos);
        SamplingPointEntry sp = new SamplingPointEntry("samplingID-007", "urn:sampling:test:007", "a sampling Test", new FeaturePropertyType(""), new PointPropertyType(location));

        StringWriter sw = new StringWriter();
        marshaller.marshal(sp, sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:swe=\"http://www.opengis.net/swe/1.0.1\"", "");
        result = result.replace(" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\"", "");
        result = result.replace(" xmlns:om=\"http://www.opengis.net/om/1.0\"", "");

        String expResult = "<sampling:SamplingPoint gml:id=\"samplingID-007\">" + '\n' +
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
        logger.finer("RESULT:" + result);
        assertEquals(expResult, result);
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
        SamplingPointEntry result =  (SamplingPointEntry) jb.getValue();

        DirectPositionType pos = new DirectPositionType("urn:ogc:crs:espg:4326", 2, Arrays.asList(3.2, 6.5));
        PointType location = new PointType("point-ID", pos);
        SamplingPointEntry expResult = new SamplingPointEntry("samplingID-007", "urn:sampling:test:007", "a sampling Test", new FeaturePropertyType(""), new PointPropertyType(location));

        assertEquals(expResult.getPosition(), result.getPosition());
        assertEquals(expResult, result);

    }

}
