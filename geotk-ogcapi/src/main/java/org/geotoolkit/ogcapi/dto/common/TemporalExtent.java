/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.serializer.TemporalExtentIntervalSerializer;

/**
 * The temporal extent of the features in the collection.
 */
@JsonPropertyOrder({
    TemporalExtent.JSON_PROPERTY_INTERVAL,
    TemporalExtent.JSON_PROPERTY_TRS,
    TemporalExtent.JSON_PROPERTY_GRID
})
public final class TemporalExtent extends DataTransferObject {

    public static final String TRS_GREGORIAN = "http://www.opengis.net/def/uom/ISO-8601/0/Gregorian";

    public static final String JSON_PROPERTY_INTERVAL = "interval";
    @JsonSerialize(using = TemporalExtentIntervalSerializer.class)
    private OffsetDateTime[][] interval;

    /**
     * Coordinate reference system of the coordinates in the temporal extent
     * (property &#x60;interval&#x60;). The default reference system is the
     * Gregorian calendar. In the Core this is the only supported temporal
     * coordinate reference system. Extensions may support additional temporal
     * coordinate reference systems and add additional enum values.
     */
    public static final String JSON_PROPERTY_TRS = "trs";
    private String trs = TRS_GREGORIAN;

    public static final String JSON_PROPERTY_GRID = "grid";
    private Grid grid;

    public TemporalExtent() {
    }

    public TemporalExtent interval(OffsetDateTime[][] interval) {
        this.interval = interval;
        return this;
    }

    /**
     * One or more time intervals that describe the temporal extent of the
     * dataset. In the Core only a single time interval is supported. Extensions
     * may support multiple intervals. The first time interval describes the
     * overall temporal extent of the data. All subsequent time intervals
     * describe more precise time intervals, e.g., to identify clusters of data.
     * Clients only interested in the overall extent will only need to access
     * the first item in each array.
     *
     * @return interval
     */
    @JsonProperty(JSON_PROPERTY_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public OffsetDateTime[][] getInterval() {
        return interval;
    }

    @JsonProperty(JSON_PROPERTY_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setInterval(OffsetDateTime[][] interval) {
        this.interval = interval;
    }

    public TemporalExtent trs(String trs) {
        this.trs = trs;
        return this;
    }

    /**
     * Coordinate reference system of the coordinates in the temporal extent
     * (property &#x60;interval&#x60;). The default reference system is the
     * Gregorian calendar. In the Core this is the only supported temporal
     * coordinate reference system. Extensions may support additional temporal
     * coordinate reference systems and add additional enum values.
     *
     * @return trs
     */
    @JsonProperty(JSON_PROPERTY_TRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTrs() {
        return trs;
    }

    @JsonProperty(JSON_PROPERTY_TRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTrs(String trs) {
        this.trs = trs;
    }

    public TemporalExtent grid(Grid grid) {
        this.grid = grid;
        return this;
    }

    /**
     * Get grid
     *
     * @return grid
     */
    @JsonProperty(JSON_PROPERTY_GRID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Grid getGrid() {
        return grid;
    }

    @JsonProperty(JSON_PROPERTY_GRID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    /**
     * Return true if this extent_allOf_temporal object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemporalExtent extentAllOfTemporal = (TemporalExtent) o;
        return Objects.equals(this.interval, extentAllOfTemporal.interval)
                && Objects.equals(this.trs, extentAllOfTemporal.trs)
                && Objects.equals(this.grid, extentAllOfTemporal.grid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, trs, grid);
    }

}
