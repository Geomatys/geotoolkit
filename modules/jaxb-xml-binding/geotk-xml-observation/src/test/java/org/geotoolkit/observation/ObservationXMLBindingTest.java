/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.observation;

import org.geotoolkit.observation.xml.v100.ProcessEntry;
import org.geotoolkit.observation.xml.v100.ObservationEntry;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.gml.xml.v311modified.DirectPositionType;
import org.geotoolkit.gml.xml.v311modified.PointType;
import org.geotoolkit.gml.xml.v311modified.TimePeriodType;
import org.geotoolkit.sampling.xml.v100.SamplingPointEntry;

//Junit dependencies
import org.geotoolkit.swe.xml.v101.PhenomenonEntry;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;
/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ObservationXMLBindingTest {

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool(
                "org.geotoolkit.sampling.xml.v100:" +
                "org.geotoolkit.observation.xml.v100:" +
                "org.geotoolkit.gml.xml.v311modified");
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

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void marshallingTest() throws JAXBException {

        DirectPositionType pos = new DirectPositionType("urn:ogc:crs:espg:4326", 2, Arrays.asList(3.2, 6.5));
        PointType location = new PointType("point-ID", pos);
        SamplingPointEntry sp = new SamplingPointEntry("samplingID-007", "urn:sampling:test:007", "a sampling Test", "urn:sampling:sampledFeature", location);

        PhenomenonEntry observedProperty = new PhenomenonEntry("phenomenon-007", "urn:OGC:phenomenon-007");
        ProcessEntry procedure = new ProcessEntry("urn:sensor:007");
        TimePeriodType samplingTime = new TimePeriodType("2007-01-01", "2008-09-09");
        ObservationEntry obs = new ObservationEntry("urn:Observation-007", "observation definition", sp, observedProperty, procedure, null, samplingTime);

        StringWriter sw = new StringWriter();
        marshaller.marshal(obs, sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:swe=\"http://www.opengis.net/swe/1.0.1\"", "");
        result = result.replace(" xmlns:sa=\"http://www.opengis.net/sa/1.0\"", "");
        result = result.replace(" xmlns:om=\"http://www.opengis.net/om/1.0\"", "");

        String expResult = "<om:Observation>" + '\n' +
                           "    <gml:name>urn:Observation-007</gml:name>" + '\n' +
                           "    <om:definition>observation definition</om:definition>" + '\n' +
                           "    <om:samplingTime>" + '\n' +
                           "        <gml:TimePeriod>" + '\n' +
                           "            <gml:beginPosition>2007-01-01</gml:beginPosition>" + '\n' +
                           "            <gml:endPosition>2008-09-09</gml:endPosition>" + '\n' +
                           "        </gml:TimePeriod>" + '\n' +
                           "    </om:samplingTime>" + '\n' +
                           "    <om:procedure xlink:href=\"urn:sensor:007\"/>" + '\n' +
                           "    <om:observedProperty>" + '\n' +
                           "        <swe:Phenomenon gml:id=\"phenomenon-007\">" + '\n' +
                           "            <gml:name>urn:OGC:phenomenon-007</gml:name>" + '\n' +
                           "        </swe:Phenomenon>" + '\n' +
                           "    </om:observedProperty>" + '\n' +
                           "    <om:featureOfInterest>" + '\n' + 
                           "        <sa:SamplingPoint gml:id=\"samplingID-007\">" + '\n' +
                           "            <gml:description>a sampling Test</gml:description>" + '\n' +
                           "            <gml:name>urn:sampling:test:007</gml:name>" + '\n' +
                           "            <gml:boundedBy>" + '\n' +
                           "                <gml:Null>not_bounded</gml:Null>" + '\n' +
                           "            </gml:boundedBy>" + '\n' +
                           "            <sa:sampledFeature>urn:sampling:sampledFeature</sa:sampledFeature>" + '\n' +
                           "            <sa:position gml:id=\"point-ID\">" + '\n' +
                           "                <gml:pos srsName=\"urn:ogc:crs:espg:4326\" srsDimension=\"2\">3.2 6.5</gml:pos>" + '\n' +
                           "            </sa:position>" + '\n' +
                           "        </sa:SamplingPoint>" + '\n' +
                           "    </om:featureOfInterest>" + '\n' +
                           "</om:Observation>\n";
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
                "<om:Observation xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:sa=\"http://www.opengis.net/sa/1.0\" " +
                " xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:swe=\"http://www.opengis.net/swe/1.0.1\">" + '\n' +
                "    <gml:name>urn:Observation-007</gml:name>" + '\n' +
                "    <om:definition>observation definition</om:definition>" + '\n' +
                "    <om:samplingTime>" + '\n' +
                "        <gml:TimePeriod>" + '\n' +
                "            <gml:beginPosition>2007-01-01</gml:beginPosition>" + '\n' +
                "            <gml:endPosition>2008-09-09</gml:endPosition>" + '\n' +
                "        </gml:TimePeriod>" + '\n' +
                "    </om:samplingTime>" + '\n' +
                "    <om:procedure xlink:href=\"urn:sensor:007\"/>" + '\n' +
                "    <om:observedProperty>" + '\n' +
                "        <swe:Phenomenon gml:id=\"phenomenon-007\">" + '\n' +
                "            <gml:name>urn:OGC:phenomenon-007</gml:name>" + '\n' +
                "        </swe:Phenomenon>" + '\n' +
                "    </om:observedProperty>" + '\n' +
                "    <om:featureOfInterest>" + '\n' +
                "        <sa:SamplingPoint gml:id=\"samplingID-007\">" + '\n' +
                "            <gml:description>a sampling Test</gml:description>" + '\n' +
                "            <gml:name>urn:sampling:test:007</gml:name>" + '\n' +
                "            <gml:boundedBy>" + '\n' +
                "                <gml:Null>not_bounded</gml:Null>" + '\n' +
                "            </gml:boundedBy>" + '\n' +
                "            <sa:sampledFeature>urn:sampling:sampledFeature</sa:sampledFeature>" + '\n' +
                "            <sa:position gml:id=\"point-ID\">" + '\n' +
                "                <gml:pos srsName=\"urn:ogc:crs:espg:4326\" srsDimension=\"2\">3.2 6.5</gml:pos>" + '\n' +
                "            </sa:position>" + '\n' +
                "        </sa:SamplingPoint>" + '\n' +
                "    </om:featureOfInterest>" + '\n' +
                "</om:Observation>\n";

        StringReader sr = new StringReader(xml);

        JAXBElement jb =  (JAXBElement) unmarshaller.unmarshal(sr);
        ObservationEntry result =  (ObservationEntry) jb.getValue();

        DirectPositionType pos = new DirectPositionType("urn:ogc:crs:espg:4326", 2, Arrays.asList(3.2, 6.5));
        PointType location = new PointType("point-ID", pos);
        SamplingPointEntry sp = new SamplingPointEntry("samplingID-007", "urn:sampling:test:007", "a sampling Test", "urn:sampling:sampledFeature", location);

        PhenomenonEntry observedProperty = new PhenomenonEntry("phenomenon-007", "urn:OGC:phenomenon-007");
        ProcessEntry procedure = new ProcessEntry("urn:sensor:007");
        TimePeriodType samplingTime = new TimePeriodType("2007-01-01", "2008-09-09");
        ObservationEntry expResult = new ObservationEntry("urn:Observation-007", "observation definition", sp, observedProperty, procedure, null, samplingTime);

        assertEquals(expResult.getFeatureOfInterest(), result.getFeatureOfInterest());
        assertEquals(expResult.getDefinition(), result.getDefinition());
        assertEquals(expResult.getName(), result.getName());
        assertEquals(expResult.getObservationMetadata(), result.getObservationMetadata());
        assertEquals(expResult.getObservedProperty(), result.getObservedProperty());
        assertEquals(expResult.getProcedure(), result.getProcedure());
        assertEquals(expResult.getProcedureParameter(), result.getProcedureParameter());
        assertEquals(expResult.getProcedureTime(), result.getProcedureTime());
        assertEquals(expResult.getPropertyFeatureOfInterest(), result.getPropertyFeatureOfInterest());
        assertEquals(expResult.getPropertyObservedProperty(), result.getPropertyObservedProperty());
        assertEquals(expResult.getQuality(), result.getQuality());
        assertEquals(expResult.getResult(), result.getResult());
        assertEquals(expResult.getSamplingTime(), result.getSamplingTime());
        assertEquals(expResult, result);

    }
}
