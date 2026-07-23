/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.model.movingfeatures;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONPoint;
import org.geotoolkit.ogcapi.dto.movingfeatures.MovingFeature;
import org.geotoolkit.ogcapi.dto.movingfeatures.MovingFeatures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Check that {@link MovingFeature}/{@link MovingFeatures}, now extending {@link
 * org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeature}/{@link
 * org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeatureCollection}, still serialize MF-JSON's own
 * {@code time}/{@code features} under their expected shape rather than the inherited JSON-FG ones.
 *
 * @author Quentin Bialota (Geomatys)
 */
public class MovingFeaturesTest {

    @Test
    public void testMovingFeatureRoundTrip() throws JsonProcessingException {
        final GeoJSONPoint geometry = new GeoJSONPoint();
        geometry.setCoordinates(List.of(7.0, 50.0));

        final MovingFeature feature = new MovingFeature();
        feature.setId("mf-1");
        feature.setGeometry(geometry);
        feature.setProperties(Map.of("name", "boat"));
        feature.setTemporalExtent(List.of("2026-01-01T00:00:00Z", "2026-01-02T00:00:00Z"));
        feature.setCrs("urn:ogc:def:crs:OGC::CRS84");

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode tree = mapper.valueToTree(feature);
        Assertions.assertEquals("Feature", tree.get("type").asText());
        Assertions.assertEquals("Point", tree.get("geometry").get("type").asText());
        Assertions.assertTrue(tree.get("time").isArray(), "MF-JSON time must be the temporal-extent array, not the inherited JSON-FG time object");
        Assertions.assertEquals("2026-01-01T00:00:00Z", tree.get("time").get(0).asText());

        final MovingFeature roundTripped = mapper.readValue(mapper.writeValueAsString(feature), MovingFeature.class);
        Assertions.assertEquals(feature, roundTripped);
    }

    @Test
    public void testMovingFeaturesRoundTrip() throws JsonProcessingException {
        final GeoJSONPoint geometry = new GeoJSONPoint();
        geometry.setCoordinates(List.of(7.0, 50.0));

        final MovingFeature feature = new MovingFeature();
        feature.setId("mf-1");
        feature.setGeometry(geometry);

        final MovingFeatures collection = new MovingFeatures().features(List.of(feature));

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode tree = mapper.valueToTree(collection);
        Assertions.assertEquals("FeatureCollection", tree.get("type").asText());
        Assertions.assertEquals(1, tree.get("features").size());
        Assertions.assertEquals("mf-1", tree.get("features").get(0).get("id").asText());

        final MovingFeatures roundTripped = mapper.readValue(mapper.writeValueAsString(collection), MovingFeatures.class);
        Assertions.assertEquals(collection, roundTripped);
    }

}
