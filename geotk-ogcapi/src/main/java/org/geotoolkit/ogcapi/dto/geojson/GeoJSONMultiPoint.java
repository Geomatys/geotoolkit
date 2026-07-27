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
package org.geotoolkit.ogcapi.dto.geojson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * GeoJSONMultiPoint
 */
@JsonPropertyOrder({
    GeoJSONMultiPoint.PROPERTY_TYPE,
    GeoJSONMultiPoint.PROPERTY_BBOX,
    GeoJSONMultiPoint.PROPERTY_COORDINATES
})
public class GeoJSONMultiPoint extends GeoJSONGeometry {

    public static final String PROPERTY_COORDINATES = "coordinates";
    private List<List<Double>> coordinates = new ArrayList<>();

    public GeoJSONMultiPoint() {
    }

    @Override
    public String getType() {
        return TYPE_MULTIPOINT;
    }

    /**
     * Get coordinates
     *
     * @return coordinates
     */
    @JsonProperty(PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Return true if this GeoJSON_MultiPoint object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoJSONMultiPoint geoJSONMultiPoint = (GeoJSONMultiPoint) o;
        return super.equals(0)
                && Objects.equals(this.coordinates, geoJSONMultiPoint.coordinates);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(coordinates);
    }

}
