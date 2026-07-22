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
package org.geotoolkit.ogcapi.model.edr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.geotoolkit.ogcapi.dto.coveragejson.Axes;
import org.geotoolkit.ogcapi.dto.coveragejson.Axis;
import org.geotoolkit.ogcapi.dto.coveragejson.CoverageJson;
import org.geotoolkit.ogcapi.dto.coveragejson.Domain;
import org.geotoolkit.ogcapi.dto.coveragejson.NdArray;
import org.geotoolkit.ogcapi.dto.coveragejson.Parameter;
import org.geotoolkit.ogcapi.dto.coveragejson.Unit;
import org.geotoolkit.ogcapi.dto.edr.DataQueries;
import org.geotoolkit.ogcapi.dto.edr.DataQuery;
import org.geotoolkit.ogcapi.dto.edr.DataQueryLink;
import org.geotoolkit.ogcapi.dto.edr.EdrCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Quentin Bialota (Geomatys)
 */
public class EdrTest {

    /**
     * Check null fields are not written.
     */
    @Test
    public void testNullValueAreNotWritten() throws JsonProcessingException {
        final EdrCollection collection = new EdrCollection();
        final String json = new ObjectMapper().writeValueAsString(collection);
        Assertions.assertEquals("{\"id\":null,\"links\":[],\"itemType\":\"feature\","
                + "\"crs\":[\"http://www.opengis.net/def/crs/OGC/1.3/CRS84\"],"
                + "\"storageCrs\":\"http://www.opengis.net/def/crs/OGC/1.3/CRS84\"}", json);
    }

    /**
     * Serialize then deserialize an {@link EdrCollection} with nested data queries, check
     * round-trip equality.
     */
    @Test
    public void testCollectionRoundTrip() throws JsonProcessingException {
        final DataQuery positionQuery = new DataQuery()
                .queryType("position")
                .outputFormats(List.of("CoverageJSON"));
        final DataQueryLink positionLink = new DataQueryLink().variables(positionQuery);
        positionLink.setHref("https://example.com/collections/temperature/position");
        positionLink.setRel("data");
        final DataQueries dataQueries = new DataQueries().position(positionLink);
        final Parameter parameter = new Parameter()
                .description("Air temperature")
                .unit(new Unit().symbol("°C"));

        final EdrCollection collection = new EdrCollection()
                .dataQueries(dataQueries)
                .putParameterNamesItem("temperature", parameter)
                .outputFormats(List.of("CoverageJSON", "GeoJSON"));
        collection.setId("temperature");

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(collection);
        final EdrCollection roundTripped = mapper.readValue(json, EdrCollection.class);
        Assertions.assertEquals(collection, roundTripped);
    }

    /**
     * Serialize then deserialize a {@link CoverageJson} position-query response, check round-trip
     * equality.
     */
    @Test
    public void testCoverageJsonRoundTrip() throws JsonProcessingException {
        final Axes axes = new Axes()
                .x(new Axis().values(List.of(-10.0)))
                .y(new Axis().values(List.of(50.0)))
                .t(new Axis().values(List.of("2026-01-01T00:00:00Z")));
        final Domain domain = new Domain().type("Domain").domainType("Point").axes(axes);
        final NdArray range = new NdArray()
                .type("NdArray")
                .dataType("float")
                .axisNames(List.of("t"))
                .shape(List.of(1))
                .values(List.of(12.5));

        final CoverageJson coverage = new CoverageJson()
                .type("Coverage")
                .domainType("Point")
                .domain(domain)
                .parameters(Map.of("temperature", new Parameter().description("Air temperature")))
                .ranges(Map.of("temperature", range));

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(coverage);
        final CoverageJson roundTripped = mapper.readValue(json, CoverageJson.class);
        Assertions.assertEquals(coverage, roundTripped);
    }

}
