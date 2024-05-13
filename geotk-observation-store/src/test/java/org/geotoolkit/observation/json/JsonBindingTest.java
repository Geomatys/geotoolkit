/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.observation.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static junit.framework.Assert.assertTrue;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.internal.storage.geojson.JSONComparator;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.observation.OMUtils;
import org.geotoolkit.observation.model.ComplexResult;
import org.geotoolkit.observation.model.CompositePhenomenon;
import org.geotoolkit.observation.model.Field;
import org.geotoolkit.observation.model.FieldType;
import org.geotoolkit.observation.model.MeasureResult;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.Procedure;
import org.geotoolkit.observation.model.Result;
import org.geotoolkit.observation.model.SamplingFeature;
import org.geotoolkit.observation.model.TextEncoderProperties;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.object.DefaultTemporalPosition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.metadata.quality.Element;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import org.opengis.temporal.IndeterminateValue;

/**
 *
 *  @author Guilhem Legal (geomatys)
 */
public class JsonBindingTest {

    private ObjectMapper mapper;

    @Before
    public void before() {
        mapper = ObservationJsonUtils.getMapper();
    }

    @Test
    public void readWriteMeasurementTest() throws Exception {
        Procedure procedure = new Procedure("proc-001");
        var i = new DefaultInstant(Collections.singletonMap(NAME_KEY, "st-time"), Instant.parse("2001-01-01T01:00:00Z"));

        GeometryFactory gf = new GeometryFactory();
        Point pt = gf.createPoint(new Coordinate(65400.0, 1731368.0));
        JTS.setCRS(pt, CRS.forCode("EPSG:27582"));

        SamplingFeature featureOfInterest = new SamplingFeature("station-001",
                                                                "10972X0137-PONT",
                                                                "Point d'eau BSSS",
                                                                Collections.EMPTY_MAP,
                                                                "urn:-sandre:object:bdrhf:123X",
                                                                pt);

        Map<String, Object> phenProperties = new HashMap<>();
        phenProperties.put("number", 1.2);
        phenProperties.put("names", Arrays.asList("p1", "phen1"));
        Phenomenon phen = new Phenomenon("phen-1", "urn:phen:1", "urn:phen:1", "phenomenon number 1", phenProperties);

        Element quality = OMUtils.createQualityElement("qFlag", null, FieldType.TEXT, "ok");
        List<Element> qualities = Arrays.asList(quality);

        Field field = new Field(1, FieldType.QUANTITY, "field_1", "field 1", "first field", "m");
        Result mresult = new MeasureResult(field, 2.0);
        Map<String, Object> properties = new HashMap<>();
        properties.put("type", "timeseries");
        properties.put("names", Arrays.asList("n1", "n2"));

        Observation expected = new Observation("obs-001",
                                          "urn:obs:001",
                                          "an observation",
                                          "obs def",
                                          "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation",
                                          procedure,
                                          i,
                                          featureOfInterest,
                                          phen,
                                          qualities,
                                          mresult,
                                          properties);

        StringWriter sw = new StringWriter();
        mapper.writeValue(sw, expected);

        String result = sw.toString();

        String expectedJSON = IOUtilities.toString(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/measurement.json"));
        compareJSON(expectedJSON, result);

        // READ TEST
        Observation readResult = mapper.readValue(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/measurement.json"), Observation.class);

        Assert.assertEquals(expected, readResult);
    }

    @Test
    public void readWriteObservationTest() throws Exception {
        Procedure procedure = new Procedure("proc-001");
        DefaultPeriod p = new DefaultPeriod(Collections.singletonMap(NAME_KEY, "p-time"),
                new DefaultInstant(Collections.singletonMap(NAME_KEY, "st-time"), Instant.parse("2001-01-01T01:00:00Z")),
                new DefaultInstant(Collections.singletonMap(NAME_KEY, "en-time"), Instant.parse("2001-01-01T02:00:00Z")));

        GeometryFactory gf = new GeometryFactory();
        Point pt = gf.createPoint(new Coordinate(65400.0, 1731368.0));
        JTS.setCRS(pt, CRS.forCode("EPSG:27582"));

        SamplingFeature featureOfInterest = new SamplingFeature("station-001",
                                                                "10972X0137-PONT",
                                                                "Point d'eau BSSS",
                                                                Collections.EMPTY_MAP,
                                                                "urn:-sandre:object:bdrhf:123X",
                                                                pt);

        Phenomenon phen = new Phenomenon("phen-1", "urn:phen:1", "urn:phen:1", "phenomenon number 1", Collections.EMPTY_MAP);

        Element quality = OMUtils.createQualityElement("qFlag", null, FieldType.QUANTITY, 2.3);
        List<Element> qualities = Arrays.asList(quality);

        Field field1 = new Field(1, FieldType.TIME,     "field_1", "field 1", "first field", null);
        Field field2 = new Field(2, FieldType.BOOLEAN,  "field_2", "field 2", "second field", null);
        Field field3 = new Field(3, FieldType.QUANTITY, "field_3", "field 3", "third field",  "m");

        Result mresult = new ComplexResult(Arrays.asList(field1, field2, field3),
                                           TextEncoderProperties.DEFAULT_ENCODING,
                                           "2001-01-01T02:00:00Z,true,6.5@@2001-01-01T03:00:00Z,false,7.5@@",
                                           2);
        Map<String, Object> properties = new HashMap<>();
        properties.put("type", "timeseries");

        Observation expected = new Observation("obs-001",
                                          "urn:obs:001",
                                          "an observation",
                                          "obs def",
                                          "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation",
                                          procedure,
                                          p,
                                          featureOfInterest,
                                          phen,
                                          qualities,
                                          mresult,
                                          properties);

        StringWriter sw = new StringWriter();
        mapper.writeValue(sw, expected);

        String result = sw.toString();
        String expectedJSON = IOUtilities.toString(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/observation.json"));
        compareJSON(expectedJSON, result);

        // READ TEST
        Observation readResult = mapper.readValue(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/observation.json"), Observation.class);

        Assert.assertEquals(expected, readResult);
    }

    @Test
    public void readWritePhenomenonTest() throws Exception {

        Phenomenon expected = new Phenomenon("phen-1", "urn:phen:1", "urn:phen:1", "phenomenon number 1", Collections.EMPTY_MAP);

        StringWriter sw = new StringWriter();
        mapper.writeValue(sw, expected);

        String result = sw.toString();
        String expectedJSON = IOUtilities.toString(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/phenomenon.json"));
        compareJSON(expectedJSON, result);

        // READ TEST
        Phenomenon readResult = mapper.readValue(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/phenomenon.json"), Phenomenon.class);

        Assert.assertEquals(expected, readResult);
    }

    @Test
    public void readWriteCompositePhenomenonTest() throws Exception {

        Phenomenon phen1 = new Phenomenon("phen-1", "urn:phen:1", "urn:phen:1", "phenomenon number 1", Collections.EMPTY_MAP);
        Phenomenon phen2 = new Phenomenon("phen-2", "urn:phen:2", "urn:phen:2", "phenomenon number 2", Collections.EMPTY_MAP);

        CompositePhenomenon expected = new CompositePhenomenon("compo-phen", "urn:phen:compo", "urn:phen:compo", "composite phenomenon number 1", Collections.EMPTY_MAP, Arrays.asList(phen1, phen2));

        StringWriter sw = new StringWriter();
        mapper.writeValue(sw, expected);

        String result = sw.toString();
        String expectedJSON = IOUtilities.toString(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/composite-phenomenon.json"));
        compareJSON(expectedJSON, result);

        // READ TEST
        Phenomenon readResult = mapper.readValue(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/composite-phenomenon.json"), Phenomenon.class);

        Assert.assertEquals(expected, readResult);

    }

    @Test
    public void readWriteOfferingTest() throws Exception {
        DefaultPeriod p = new DefaultPeriod(Collections.singletonMap(NAME_KEY, "p-time"),
                new DefaultInstant(Collections.singletonMap(NAME_KEY, "st-time"), Instant.parse("2001-01-01T01:00:00Z")),
                new DefaultInstant(Collections.singletonMap(NAME_KEY, "en-time"),
                        new DefaultTemporalPosition(
                                CommonCRS.Temporal.JULIAN.crs(),
                                IndeterminateValue.NOW)));

        GeneralEnvelope area =  new GeneralEnvelope(CommonCRS.defaultGeographic());
        area.setRange(0, 5, 10);
        area.setRange(1, 5, 10);
        Offering expected = new Offering("offering-1",
                                         "offering 1",
                                         "offering number 1",
                                         new HashMap<>(),
                                        area,
                                        Arrays.asList("EPSG:4326"),
                                        p,
                                        "urn:ogc:object:sensor:GEOM:1",
                                        Arrays.asList("urn:phen:1", "urn:phen:2"),
                                        Arrays.asList("station-001", "station-002", "station-006"));

        StringWriter sw = new StringWriter();
        mapper.writeValue(sw, expected);

        String result = sw.toString();
        String expectedJSON = IOUtilities.toString(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/offering.json"));
        compareJSON(expectedJSON, result);

        // READ TEST
        Offering readResult = mapper.readValue(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/offering.json"), Offering.class);

        Assert.assertEquals(expected, readResult);
    }

    @Test
    public void readWriteSamplingFeatureTest() throws Exception {

        GeometryFactory gf = new GeometryFactory();
        Point pt = gf.createPoint(new Coordinate(65400.0, 1731368.0));
        JTS.setCRS(pt, CRS.forCode("EPSG:27582"));

        SamplingFeature expected = new SamplingFeature("station-001",
                                                       "10972X0137-PONT",
                                                       "Point d'eau BSSS",
                                                       Collections.EMPTY_MAP,
                                                       "urn:-sandre:object:bdrhf:123X",
                                                       pt);

        StringWriter sw = new StringWriter();
        mapper.writeValue(sw, expected);

        String result = sw.toString();
        String expectedJSON = IOUtilities.toString(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/sampling-feature.json"));
        compareJSON(expectedJSON, result);

        // READ TEST
        SamplingFeature readResult = mapper.readValue(JsonBindingTest.class.getResourceAsStream("/org/geotoolkit/observation/json/sampling-feature.json"), SamplingFeature.class);

        Assert.assertEquals(expected, readResult);

    }

    public static void compareJSON(String expected, String result) throws JsonProcessingException {
        JSONComparator comparator = new JSONComparator();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedNode = mapper.readTree(expected);
        JsonNode resultNode = mapper.readTree(result);

        boolean eq = expectedNode.equals(comparator, resultNode);

        StringBuilder sb = new StringBuilder("expected:\n");
        sb.append(expected).append("\nbut was:\n");
        sb.append(result);
        assertTrue(sb.toString(), eq);
    }
}
