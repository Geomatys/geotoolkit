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
package org.geotoolkit.observation;

import org.geotoolkit.observation.xml.v100.ProcessEntry;
import org.geotoolkit.observation.xml.v100.ObservationEntry;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.FeaturePropertyType;
import org.geotoolkit.gml.xml.v311.PointPropertyType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.gml.xml.v311.UnitOfMeasureEntry;
import org.geotoolkit.observation.xml.v100.MeasureEntry;
import org.geotoolkit.observation.xml.v100.MeasurementEntry;
import org.geotoolkit.observation.xml.v100.ObservationCollectionEntry;
import org.geotoolkit.sampling.xml.v100.SamplingPointEntry;

//Junit dependencies
import org.geotoolkit.swe.xml.v101.AnyScalarPropertyType;
import org.geotoolkit.swe.xml.v101.DataArrayEntry;
import org.geotoolkit.swe.xml.v101.DataArrayPropertyType;
import org.geotoolkit.swe.xml.v101.PhenomenonEntry;
import org.geotoolkit.swe.xml.v101.SimpleDataRecordEntry;
import org.geotoolkit.swe.xml.v101.Text;
import org.geotoolkit.swe.xml.v101.TextBlockEntry;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class ObservationXMLBindingTest {

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool(
                "org.geotoolkit.sampling.xml.v100:" +
                "org.geotoolkit.swe.xml.v101:" +
                "org.geotoolkit.observation.xml.v100:" +
                "org.geotoolkit.gml.xml.v311");
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
        PointType location     = new PointType("point-ID", pos);
        SamplingPointEntry sp  = new SamplingPointEntry("samplingID-007", "urn:sampling:test:007", "a sampling Test", new FeaturePropertyType(""), new PointPropertyType(location));

        PhenomenonEntry observedProperty = new PhenomenonEntry("phenomenon-007", "urn:OGC:phenomenon-007");
        ProcessEntry procedure           = new ProcessEntry("urn:sensor:007");
        TimePeriodType samplingTime      = new TimePeriodType("2007-01-01", "2008-09-09");
        samplingTime.setId("t1");

        TextBlockEntry encoding            = new TextBlockEntry("encoding-001", ",", "@@", ".");
        List<AnyScalarPropertyType> fields = new ArrayList<AnyScalarPropertyType>();
        AnyScalarPropertyType field        = new AnyScalarPropertyType("text-field-001", new Text("urn:something", "some value"));
        fields.add(field);
        SimpleDataRecordEntry record       = new SimpleDataRecordEntry(fields);
        DataArrayEntry array               = new DataArrayEntry("array-001", 1, record, encoding, "somevalue");
        DataArrayPropertyType arrayProp    = new DataArrayPropertyType(array);
        ObservationEntry obs = new ObservationEntry("urn:Observation-007", "observation definition", sp, observedProperty, procedure, arrayProp, samplingTime);

        StringWriter sw = new StringWriter();
        marshaller.marshal(obs, sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:swe=\"http://www.opengis.net/swe/1.0.1\"", "");
        result = result.replace(" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\"", "");
        result = result.replace(" xmlns:om=\"http://www.opengis.net/om/1.0\"", "");

        String expResult = "<om:Observation>" + '\n' +
                           "    <gml:name>urn:Observation-007</gml:name>" + '\n' +
                           "    <om:samplingTime>" + '\n' +
                           "        <gml:TimePeriod gml:id=\"t1\">" + '\n' +
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
                           "    </om:featureOfInterest>" + '\n' +
                           "    <om:result xsi:type=\"swe:DataArrayPropertyType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + '\n' +
                           "        <swe:DataArray gml:id=\"array-001\">" + '\n' +
                           "            <swe:elementCount>" + '\n' +
                           "                <swe:Count>" + '\n' +
                           "                    <swe:value>1</swe:value>" + '\n' +
                           "                </swe:Count>" + '\n' +
                           "            </swe:elementCount>" + '\n' +
                           "            <swe:elementType name=\"array-001\">" + '\n' +
                           "                <swe:SimpleDataRecord>" + '\n' +
                           "                    <swe:field name=\"text-field-001\">" + '\n' +
                           "                        <swe:Text definition=\"urn:something\">" + '\n' +
                           "                            <swe:value>some value</swe:value>" + '\n' +
                           "                        </swe:Text>" + '\n' +
                           "                    </swe:field>" + '\n' +
                           "                </swe:SimpleDataRecord>" + '\n' +
                           "            </swe:elementType>" + '\n' +
                           "            <swe:encoding>" + '\n' +
                           "                <swe:TextBlock blockSeparator=\"@@\" decimalSeparator=\".\" tokenSeparator=\",\" id=\"encoding-001\"/>" + '\n' +
                           "            </swe:encoding>" + '\n' +
                           "            <swe:values>somevalue</swe:values>" + '\n' +
                           "        </swe:DataArray>" + '\n' +
                           "    </om:result>" + '\n' +
                           "</om:Observation>\n";
        assertEquals(expResult, result);


        UnitOfMeasureEntry uom  = new UnitOfMeasureEntry("m", "meters", "distance", null);
        MeasureEntry meas       = new MeasureEntry("result-1", uom, 7);
        MeasurementEntry measmt = new MeasurementEntry("urn:Observation-007", "observation definition", sp, observedProperty, procedure, meas, samplingTime);

        sw = new StringWriter();
        marshaller.marshal(measmt, sw);

        result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:swe=\"http://www.opengis.net/swe/1.0.1\"", "");
        result = result.replace(" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\"", "");
        result = result.replace(" xmlns:om=\"http://www.opengis.net/om/1.0\"", "");

        expResult =        "<om:Measurement>" + '\n' +
                           "    <gml:name>urn:Observation-007</gml:name>" + '\n' +
                           "    <om:samplingTime>" + '\n' +
                           "        <gml:TimePeriod gml:id=\"t1\">" + '\n' +
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
                           "    </om:featureOfInterest>" + '\n' +
                           "    <om:result xsi:type=\"om:Measure\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + '\n' +
                           "        <om:name>result-1</om:name>" + '\n' +
                           "        <om:uom id=\"m\">" + '\n' +
                           "            <gml:name>meters</gml:name>" + '\n' +
                           "            <gml:quantityType>distance</gml:quantityType>" + '\n' +
                           "        </om:uom>" + '\n' +
                           "        <om:value>7.0</om:value>" + '\n' +
                           "    </om:result>" + '\n' +
                           "</om:Measurement>\n";
        
        assertEquals(expResult, result);
        
        ObservationCollectionEntry collection = new ObservationCollectionEntry();
        collection.add(measmt);

        sw = new StringWriter();
        marshaller.marshal(collection, sw);

        result = sw.toString();
        //System.out.println(result);

        collection = new ObservationCollectionEntry();
        collection.add(obs.getTemporaryTemplate("temporaryName", samplingTime));

        sw = new StringWriter();
        marshaller.marshal(collection, sw);

        result = sw.toString();
        
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void UnmarshalingTest() throws JAXBException {

        /*
         * Test Unmarshalling observation
         */

        String xml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
                "<om:Observation xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\" " +
                " xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:swe=\"http://www.opengis.net/swe/1.0.1\">" + '\n' +
                "    <gml:name>urn:Observation-007</gml:name>" + '\n' +
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
                "        <sampling:SamplingPoint gml:id=\"samplingID-007\">" + '\n' +
                "            <gml:description>a sampling Test</gml:description>" + '\n' +
                "            <gml:name>urn:sampling:test:007</gml:name>" + '\n' +
                "            <gml:boundedBy>" + '\n' +
                "                <gml:Null>not_bounded</gml:Null>" + '\n' +
                "            </gml:boundedBy>" + '\n' +
                "            <sampling:sampledFeature xlink:href=\"urn:sampling:sampledFeature\"/>" + '\n' +
                "            <sampling:position gml:id=\"point-ID\">" + '\n' +
                "                <gml:Point gml:id=\"point-ID\">" + '\n' +
                "                   <gml:pos srsName=\"urn:ogc:crs:espg:4326\" srsDimension=\"2\">3.2 6.5</gml:pos>" + '\n' +
                "                </gml:Point>" + '\n' +
                "            </sampling:position>" + '\n' +
                "        </sampling:SamplingPoint>" + '\n' +
                "    </om:featureOfInterest>" + '\n' +
                "    <om:result xsi:type=\"swe:DataArrayPropertyType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + '\n' +
               "        <swe:DataArray gml:id=\"array-001\">" + '\n' +
               "            <swe:elementCount>" + '\n' +
               "                <swe:Count>" + '\n' +
               "                    <swe:value>1</swe:value>" + '\n' +
               "                </swe:Count>" + '\n' +
               "            </swe:elementCount>" + '\n' +
               "            <swe:elementType name=\"array-001\">" + '\n' +
               "                <swe:SimpleDataRecord>" + '\n' +
               "                    <swe:field name=\"text-field-001\">" + '\n' +
               "                        <swe:Text definition=\"urn:something\">" + '\n' +
               "                            <swe:value>some value</swe:value>" + '\n' +
               "                        </swe:Text>" + '\n' +
               "                    </swe:field>" + '\n' +
               "                </swe:SimpleDataRecord>" + '\n' +
               "            </swe:elementType>" + '\n' +
               "            <swe:encoding>" + '\n' +
               "                <swe:TextBlock blockSeparator=\"@@\" decimalSeparator=\".\" tokenSeparator=\",\" id=\"encoding-001\"/>" + '\n' +
               "            </swe:encoding>" + '\n' +
               "            <swe:values>somevalue</swe:values>" + '\n' +
               "        </swe:DataArray>" + '\n' +
               "    </om:result>" + '\n' +
                "</om:Observation>\n";

        StringReader sr = new StringReader(xml);

        JAXBElement jb =  (JAXBElement) unmarshaller.unmarshal(sr);
        ObservationEntry result =  (ObservationEntry) jb.getValue();

        DirectPositionType pos = new DirectPositionType("urn:ogc:crs:espg:4326", 2, Arrays.asList(3.2, 6.5));
        PointType location = new PointType("point-ID", pos);
        SamplingPointEntry sp = new SamplingPointEntry("samplingID-007", "urn:sampling:test:007", "a sampling Test", new FeaturePropertyType("urn:sampling:sampledFeature"), new PointPropertyType(location));

        PhenomenonEntry observedProperty = new PhenomenonEntry("phenomenon-007", "urn:OGC:phenomenon-007");
        ProcessEntry procedure = new ProcessEntry("urn:sensor:007");
        TimePeriodType samplingTime = new TimePeriodType("2007-01-01", "2008-09-09");

        TextBlockEntry encoding            = new TextBlockEntry("encoding-001", ",", "@@", ".");
        List<AnyScalarPropertyType> fields = new ArrayList<AnyScalarPropertyType>();
        AnyScalarPropertyType field        = new AnyScalarPropertyType("text-field-001", new Text("urn:something", "some value"));
        fields.add(field);
        SimpleDataRecordEntry record       = new SimpleDataRecordEntry(fields);
        DataArrayEntry array               = new DataArrayEntry("array-001", 1, record, encoding, "somevalue");
        DataArrayPropertyType arrayProp    = new DataArrayPropertyType(array);
        
        ObservationEntry expResult = new ObservationEntry("urn:Observation-007", null, sp, observedProperty, procedure, arrayProp, samplingTime);

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



        /*
         * Test Unmarshalling measurement
         */

        xml =  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
               "<om:Measurement xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\" " +
               " xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:swe=\"http://www.opengis.net/swe/1.0.1\">" + '\n' +
               "    <gml:name>urn:Observation-007</gml:name>" + '\n' +
               "    <om:samplingTime>" + '\n' +
               "        <gml:TimePeriod gml:id=\"t1\">" + '\n' +
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
               "        <sampling:SamplingPoint gml:id=\"samplingID-007\">" + '\n' +
               "            <gml:description>a sampling Test</gml:description>" + '\n' +
               "            <gml:name>urn:sampling:test:007</gml:name>" + '\n' +
               "            <gml:boundedBy>" + '\n' +
               "                <gml:Null>not_bounded</gml:Null>" + '\n' +
               "            </gml:boundedBy>" + '\n' +
               "            <sampling:sampledFeature xlink:href=\"urn:sampling:sampledFeature\"/>" + '\n' +
               "            <sampling:position gml:id=\"point-ID\">" + '\n' +
               "                <gml:Point gml:id=\"point-ID\">" + '\n' +
               "                    <gml:pos srsName=\"urn:ogc:crs:espg:4326\" srsDimension=\"2\">3.2 6.5</gml:pos>" + '\n' +
               "                </gml:Point>" + '\n' +
               "            </sampling:position>" + '\n' +
               "        </sampling:SamplingPoint>" + '\n' +
               "    </om:featureOfInterest>" + '\n' +
               "    <om:result xsi:type=\"om:Measure\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + '\n' +
               "        <om:name>result-1</om:name>" + '\n' +
               "        <om:uom id=\"m\">" + '\n' +
               "            <gml:name>meters</gml:name>" + '\n' +
               "            <gml:quantityType>distance</gml:quantityType>" + '\n' +
               "        </om:uom>" + '\n' +
               "        <om:value>7.0</om:value>" + '\n' +
               "    </om:result>" + '\n' +
               "</om:Measurement>\n";

        sr = new StringReader(xml);

        jb =  (JAXBElement) unmarshaller.unmarshal(sr);
        MeasurementEntry result2 =  (MeasurementEntry) jb.getValue();

        UnitOfMeasureEntry uom  = new UnitOfMeasureEntry("m", "meters", "distance", null);
        MeasureEntry meas       = new MeasureEntry("result-1", uom, 7);

        MeasurementEntry expResult2 = new MeasurementEntry("urn:Observation-007", null, sp, observedProperty, procedure, meas, samplingTime);

        assertEquals(expResult2.getFeatureOfInterest(), result2.getFeatureOfInterest());
        assertEquals(expResult2.getDefinition(), result2.getDefinition());
        assertEquals(expResult2.getName(), result2.getName());
        assertEquals(expResult2.getObservationMetadata(), result2.getObservationMetadata());
        assertEquals(expResult2.getObservedProperty(), result2.getObservedProperty());
        assertEquals(expResult2.getProcedure(), result2.getProcedure());
        assertEquals(expResult2.getProcedureParameter(), result2.getProcedureParameter());
        assertEquals(expResult2.getProcedureTime(), result2.getProcedureTime());
        assertEquals(expResult2.getPropertyFeatureOfInterest(), result2.getPropertyFeatureOfInterest());
        assertEquals(expResult2.getPropertyObservedProperty(), result2.getPropertyObservedProperty());
        assertEquals(expResult2.getQuality(), result2.getQuality());
        assertEquals(expResult2.getResult(), result2.getResult());
        assertEquals(expResult2.getSamplingTime(), result2.getSamplingTime());
        assertEquals(expResult2, result2);


        xml =  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
               "<om:ObservationCollection xmlns:swe=\"http://www.opengis.net/swe/1.0.1\" xmlns:sampling=\"http://www.opengis.net/sampling/1.0\" xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
               "    <gml:boundedBy>" + '\n' +
               "        <gml:Envelope srsName=\"urn:ogc:crs:espg:4326\">" + '\n' +
               "            <gml:lowerCorner>-180.0 -90.0</gml:lowerCorner>" + '\n' +
               "            <gml:upperCorner>180.0 90.0</gml:upperCorner>" + '\n' +
               "        </gml:Envelope>" + '\n' +
               "    </gml:boundedBy>" + '\n' +
               "    <om:member>" + '\n' +
               "        <om:Measurement>" + '\n' +
               "            <gml:name>urn:ogc:object:observationTemplate:SunSpot:0014.4F01.0000.2626-12</gml:name>" + '\n' +
               "            <om:samplingTime>" + '\n' +
               "                <gml:TimePeriod>" + '\n' +
               "                    <gml:beginPosition>2009-08-03 11:18:06</gml:beginPosition>" + '\n' +
               "                    <gml:endPosition indeterminatePosition=\"now\"></gml:endPosition>" + '\n' +
               "                </gml:TimePeriod>" + '\n' +
               "            </om:samplingTime>" + '\n' +
               "            <om:procedure xlink:href=\"urn:ogc:object:sensor:SunSpot:0014.4F01.0000.2626\"/>" + '\n' +
               "            <om:observedProperty>" + '\n' +
               "                <swe:Phenomenon gml:id=\"temperature\">" + '\n' +
               "                    <gml:name>urn:phenomenon:temperature</gml:name>" + '\n' +
               "                </swe:Phenomenon>" + '\n' +
               "            </om:observedProperty>" + '\n' +
               "            <om:featureOfInterest>" + '\n' +
               "                <sampling:SamplingPoint gml:id=\"sampling-point-001\">" + '\n' +
               "                    <gml:name>sampling-point-001</gml:name>" + '\n' +
               "                    <gml:boundedBy>" + '\n' +
               "                        <gml:Null>not_bounded</gml:Null>" + '\n' +
               "                    </gml:boundedBy>" + '\n' +
               "                <sampling:sampledFeature>sampling-point-001</sampling:sampledFeature>" + '\n' +
               "                    <sampling:position>" + '\n' +
               "                        <gml:Point gml:id=\"point-ID\">" + '\n' +
               "                            <gml:pos srsDimension=\"0\">0.0 0.0</gml:pos>" + '\n' +
               "                        </gml:Point>" + '\n' +
               "                    </sampling:position>" + '\n' +
               "                </sampling:SamplingPoint>" + '\n' +
               "            </om:featureOfInterest>" + '\n' +
               "            <om:result xsi:type=\"om:Measure\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + '\n' +
               "                <om:name>mesure-027</om:name>" + '\n' +
               "                <om:value>0.0</om:value>" + '\n' +
               "            </om:result>" + '\n' +
               "        </om:Measurement>" + '\n' +
               "    </om:member>" + '\n' +
               "</om:ObservationCollection>" + '\n';

        sr = new StringReader(xml);

        ObservationCollectionEntry result3 =  (ObservationCollectionEntry) unmarshaller.unmarshal(sr);

    }
}
