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
package org.geotoolkit.ogcapi.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The vertical extent of the features/coverages in the collection, as defined by
 * OGC API - Environmental Data Retrieval (also used by OGC API - Coverages).
 * Interval and grid cell size values are kept as strings since the vertical axis
 * may use a recurring-interval syntax (e.g. {@code "R20/1000/-50"}) rather than
 * plain numbers.
 */
@JsonPropertyOrder({
    VerticalExtent.JSON_PROPERTY_INTERVAL,
    VerticalExtent.JSON_PROPERTY_VRS
})
public final class VerticalExtent extends DataTransferObject {

    public static final String JSON_PROPERTY_INTERVAL = "interval";
    private String[][] interval;

    public static final String JSON_PROPERTY_VRS = "vrs";
    private String vrs;

    public VerticalExtent() {
    }

    public VerticalExtent interval(String[][] interval) {
        this.interval = interval;
        return this;
    }

    /**
     * One or more vertical intervals that describe the vertical extent of the dataset.
     *
     * @return interval
     */
    @JsonProperty(JSON_PROPERTY_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String[][] getInterval() {
        return interval;
    }

    @JsonProperty(JSON_PROPERTY_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setInterval(String[][] interval) {
        this.interval = interval;
    }

    public VerticalExtent vrs(String vrs) {
        this.vrs = vrs;
        return this;
    }

    /**
     * Coordinate reference system of the coordinates in the vertical extent
     * (property {@code interval}).
     *
     * @return vrs
     */
    @JsonProperty(JSON_PROPERTY_VRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getVrs() {
        return vrs;
    }

    @JsonProperty(JSON_PROPERTY_VRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setVrs(String vrs) {
        this.vrs = vrs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VerticalExtent other = (VerticalExtent) o;
        return Objects.equals(this.interval, other.interval)
                && Objects.equals(this.vrs, other.vrs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, vrs);
    }

}
