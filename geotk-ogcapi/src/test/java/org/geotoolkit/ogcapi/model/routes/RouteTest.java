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
package org.geotoolkit.ogcapi.model.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.List;
import org.geotoolkit.ogcapi.dto.common.Link;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONMultiPoint;
import org.geotoolkit.ogcapi.dto.routes.RouteDefinition;
import org.geotoolkit.ogcapi.dto.routes.RouteDefinition.WhenType;
import org.geotoolkit.ogcapi.dto.routes.RouteList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Quentin Bialota (Geomatys)
 */
public class RouteTest {

    /**
     * Check that {@link RouteDefinition} serializes its typed setters into the
     * {@code inputs.<name>.value} nesting expected by the OGC API - Processes execute alignment.
     */
    @Test
    public void testRouteDefinitionInputsNesting() throws JsonProcessingException {
        final GeoJSONMultiPoint waypoints = new GeoJSONMultiPoint();
        waypoints.setCoordinates(List.of(List.of(7.0, 50.0), List.of(8.0, 51.0)));

        final RouteDefinition definition = new RouteDefinition()
                .name("my route")
                .waypoints(waypoints)
                .mode("driving")
                .when(OffsetDateTime.parse("2026-01-01T10:00:00Z"), WhenType.DEPARTURE);

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode tree = mapper.valueToTree(definition);
        final JsonNode inputs = tree.get("inputs");

        Assertions.assertEquals("my route", inputs.get("name").get("value").asText());
        Assertions.assertEquals("driving", inputs.get("mode").get("value").asText());
        Assertions.assertEquals("MultiPoint", inputs.get("waypoints").get("value").get("type").asText());
        Assertions.assertEquals("2026-01-01T10:00:00Z", inputs.get("when").get("value").get("departure").asText());
    }

    /**
     * Serialize then deserialize a {@link RouteDefinition}, check round-trip equality.
     */
    @Test
    public void testRouteDefinitionRoundTrip() throws JsonProcessingException {
        final GeoJSONMultiPoint waypoints = new GeoJSONMultiPoint();
        waypoints.setCoordinates(List.of(List.of(7.0, 50.0), List.of(8.0, 51.0)));

        final RouteDefinition definition = new RouteDefinition()
                .name("my route")
                .waypoints(waypoints)
                .mode("driving");

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(definition);
        final RouteDefinition roundTripped = mapper.readValue(json, RouteDefinition.class);
        Assertions.assertEquals(definition, roundTripped);
    }

    /**
     * Serialize then deserialize a {@link RouteList}, check round-trip equality.
     */
    @Test
    public void testRouteListRoundTrip() throws JsonProcessingException {
        final RouteList list = new RouteList()
                .addLinksItem(new Link("https://example.com/routes/1", "item", null, null, null, null));

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(list);
        final RouteList roundTripped = mapper.readValue(json, RouteList.class);
        Assertions.assertEquals(list, roundTripped);
    }

}
