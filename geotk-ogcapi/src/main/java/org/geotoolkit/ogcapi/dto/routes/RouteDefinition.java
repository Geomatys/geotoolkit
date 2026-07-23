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
package org.geotoolkit.ogcapi.dto.routes;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONMultiPoint;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONMultiPolygon;
import org.geotoolkit.ogcapi.dto.processes.Execute;

/**
 * Request body of {@code POST /routes}, aligned with the OGC API - Processes
 * execute request: each route parameter is stored as an {@code inputs.<name>.value}
 * entry, per the spec's explicit Processes-execution alignment.
 *
 * @author Quentin BIALOTA (Geomatys)
 * @see <a href="https://docs.ogc.org/DRAFTS/21-000.html">OGC API - Routes - Part 1: Core (draft)</a>
 */
public final class RouteDefinition extends Execute {

    public enum WhenType {
        DEPARTURE, ARRIVAL
    }

    public RouteDefinition name(String name) {
        putInputsItem("name", Map.of("value", name));
        return this;
    }

    public RouteDefinition waypoints(GeoJSONMultiPoint waypoints) {
        putInputsItem("waypoints", Map.of("value", waypoints));
        return this;
    }

    public RouteDefinition mode(String mode) {
        putInputsItem("mode", Map.of("value", mode));
        return this;
    }

    public RouteDefinition spatialConstraints(GeoJSONMultiPolygon spatialConstraints) {
        putInputsItem("spatial_constraints", Map.of("value", spatialConstraints));
        return this;
    }

    public RouteDefinition when(OffsetDateTime time, WhenType type) {
        putInputsItem("when", Map.of(
                "value", Map.of(type.name().toLowerCase(), time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))));
        return this;
    }

}
